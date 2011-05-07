/* 
        Copyright 2011 Alberto Perez Alonso
 
        This file is part of AutoRecNotes.

    AutoRecNotes is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AutoRecNotes is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AutoRecNotes.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.apa42.autorecnotes;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class ServiceRecorder extends Service
{
	// for logs
	private final String CLASS_NAME = getClass().getName();

	
	// Static Data, Share Data
	private static ServiceRecorderUIUpdateListener static_UI_UPDATE_LISTENER;
	// 
	private TimerTask _timerTask = null;
	private Handler _handler = null;
	private Timer _timer = null;
	//
	private long _pref_TimeToRecord = ConfigAppValues.DEFAULT_TIMETORECORD;
	private long _timeRecording = 0;
	private long _notificationPeriod = 1000; //1sg
	private boolean _recording = false;
		
	private MediaRecorder _recorder = null;
	private String _fileName = null;
	MediaRecorder.OnErrorListener _recorderOnErrorListener = null;
	MediaRecorder.OnInfoListener _recorderOnInfoListener = null;

	
	@Override
	public IBinder onBind(Intent arg0)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onBind(Intent arg0)" );
		//
		return null;
	}

	@Override
	public void onCreate()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate()" );

		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate()" );
		//

		super.onDestroy();
		
		shutdown();
		
		// Check if user stops service or not=> From shared preferences
		// I know it's not the best way but I use sharedPreferences for other communications
		//
		if ( getStateFromPreferences(ConfigAppValues.SERVICE_CANCELED_BY_THE_USER) )
		{
			// Have to delete file
    		if ( null != _fileName )
    		{
    			// Check if can be delete
    			if ( Utils.canDeleteFile(_fileName) )
    			{
    				File file = new File(_fileName);
    				if ( file.delete() )
    				{
    					if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onDestroy()=>File Deleted" );
    				}
    				else
    				{
    					if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onDestroy()=>Can't delete file : " + _fileName);
    				}	
    			}
    			else
    			{
    				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onDestroy()=>Can't delete file : " + _fileName);
    			}
    		}
    		// Change the state,don't care about if the file has been deleted or not
    		saveStateToPreferences(ConfigAppValues.SERVICE_CANCELED_BY_THE_USER, false);
		}
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		// @@ VERY IMPORTANT
		// -----------------
		// This method is deprecated.
		// Implement onStartCommand(Intent, int, int) instead.
		// 
		// See: http://developer.android.com/reference/android/app/Service.html#onStartCommand%28android.content.Intent,%20int,%20int%29
		//
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate()" );
		//
		super.onStart(intent, startId);
		
		//
		getValuesFromPreferences();
		if ( !_recording )
		{
			prepareAndStartToRecord();
		}
		else
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "We're recording=> nothing to do");
		}
	}

	
	public static void setUpdateUIListener(ServiceRecorderUIUpdateListener updateListener)
	{
		static_UI_UPDATE_LISTENER = updateListener;
	}
	

	private void saveStateToPreferences(String stateString, boolean state)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "saveStateToPreferences("+stateString+","+state+")" );
		//
    	// It isn't necessary=> PreferenceManager.setDefaultValues(getApplication(), R.xml.preferences, false);
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
    	Editor editor = preferences.edit();
    	editor.putBoolean(stateString, state);
    	editor.commit();		
	}
	
	private boolean getStateFromPreferences(String stateString)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "getStateFromPreferences("+stateString+")" );
		//
    	// It isn't necessary=> PreferenceManager.setDefaultValues(getApplication(), R.xml.preferences, false);
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
    	return preferences.getBoolean(stateString, false);
	}
	

	
	private void getValuesFromPreferences()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "getValuesFromPreferences()" );
		//
    	// It isn't necessary=> PreferenceManager.setDefaultValues(getApplication(), R.xml.preferences, false);
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
    	String aux = preferences.getString(ConfigAppValues.PREF_KEY_TIMETORECORD_LIST, ConfigAppValues.PREF_KEY_TIMETORECORD_LIST_DEFAULT_VALUE);
    	try
    	{
    		_pref_TimeToRecord = Long.parseLong(aux);
    	}
    	catch (NumberFormatException e)
    	{
    		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "Fail to convert String to long (apply default value): " + e.getMessage());
    		// Apply default value
    		_pref_TimeToRecord = ConfigAppValues.DEFAULT_TIMETORECORD;    		
    	}
    	//
    	_recording = preferences.getBoolean(ConfigAppValues.PREF_KEY_RECORDING_STATE_FOR_SERVICE, false);
	}
	
	private void prepareAndStartToRecord()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "prepareAndStartToRecord()" );
		//
		// Create the handle to receive data and send to UI
		_handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
//				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "handler::handleMessage(Message msg)");
				// Send to UI
				if ( null != static_UI_UPDATE_LISTENER )
					static_UI_UPDATE_LISTENER.updateUI((int)_timeRecording);
			}
			
		};
		//
		_timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
//				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "timerTask::run()");
				// TODO Auto-generated method stub
				if ( _timeRecording == _pref_TimeToRecord )
				{
					// Finish the service
					if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "****timerTask::Stop My Self");
					stopSelf();
				}
				_handler.sendEmptyMessage(0); // @@ Send a message with the tick
				_timeRecording += _notificationPeriod;
			}
		};
		
		// Reset timer counter
		_timeRecording = 0;
		// Set the timer
		_timer = new Timer();
		// Set null to file Name
		_fileName = null;
		
		if ( prepareMediaRecorder() )
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "** START RECORDING");
			
			try
			{
				// Start Recording				
				_recording = true;
				saveStateToPreferences(ConfigAppValues.PREF_KEY_RECORDING_STATE_FOR_SERVICE,_recording);
				_recorder.start();
				// run
				_timer.scheduleAtFixedRate(_timerTask, 0, _notificationPeriod);
			}
			catch(IllegalStateException e)
			{
	    		if (ConfigAppValues.DEBUG) Log.e(this.getClass().getName(), "prepareAndStartToRecord FAILS: " + e.getMessage() );
				e.printStackTrace();
				// Get out of here;
				// Don't => stopSelf(); 	we're on start service...kill my self other way
				shutdown();
				// Update UI
				if ( null != static_UI_UPDATE_LISTENER )
					static_UI_UPDATE_LISTENER.updateUI(ConfigAppValues.SERVICE_RECORDER_STOP_NOMEDIARECORDER);
			}
		}
		else
		{
			// Get out of here;
			// Don't => stopSelf(); 	we're on start service...kill my self other way
			shutdown();
			// Update UI
			if ( null != static_UI_UPDATE_LISTENER )
				static_UI_UPDATE_LISTENER.updateUI(ConfigAppValues.SERVICE_RECORDER_STOP_NOMEDIARECORDER);
		}
		
		
	}
	
	private void shutdown()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "shutDown()");
		//
		if ( null != _recorder )
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "** STOP RECORDING");
			try
			{
				_recorder.stop();
				_recorder.release();
				_recorder = null;
			}
			catch(IllegalStateException e)
			{
	    		if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "shutdown Stop Recorder FAILS: " + e.getMessage() );
				e.printStackTrace();
			}
		}

		if (_timer != null)
		{
			_timer.cancel();
			_timer.purge();
			_timer = null;
		}
		_timerTask.cancel();
		_timerTask = null;
		_handler = null;
		
		
		_recording = false;
		saveStateToPreferences(ConfigAppValues.PREF_KEY_RECORDING_STATE_FOR_SERVICE, _recording);
	}

	
	private boolean prepareMediaRecorder()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "prepareMediaRecorder()" );
		
		boolean returned = false;
		
		if ( ! Utils.checkMediaStorage() )
			return returned;
		//
    	DateFormat fileNameDateFormat = new SimpleDateFormat(getResources().getString(R.string.filenameformat));
    	_fileName = Utils.giveMePathToStorage(this);
    	_fileName += ConfigAppValues.DOUBLE_SLASH;
    	try
    	{
    		if (null == _recorder)
    		{
        		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "** Creating new MediaRecorder()");			
        		_recorder = new MediaRecorder();
    		}
    		else
    		{
        		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "** Reset MediaRecorder()");
        		// _recorder.reset();  // Reset sometimes doesn't release correctly
    			_recorder.release();
    			_recorder = null;
    			_recorder = new MediaRecorder();
    		}

    		_recorderOnErrorListener = new MediaRecorder.OnErrorListener()
    		{
    			@Override
    			public void onError(MediaRecorder mr, int what, int extra)
    			{
    				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "MediaRecorder.OnErrorListener()");		
    				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "Error What : " + what + " Error Extra: " + extra);
    			}
    		};
    		_recorderOnInfoListener = new MediaRecorder.OnInfoListener()
    		{
    			@Override
    			public void onInfo(MediaRecorder mr, int what, int extra)
    			{
    				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "MediaRecorder.OnInfo()");				
    				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "Info What: " + what + " Info Extra: " +extra);
    			}
    		};
    		
    		_recorder.setOnErrorListener(_recorderOnErrorListener);
    		_recorder.setOnInfoListener(_recorderOnInfoListener);

    		// Don't change the order of sentences
	    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "==>setAudioSource(MediaRecorder.AudioSource.MIC);");
	    	_recorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
	    	
	    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "==>setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);");
	    	_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    	
	    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "==>setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);");
	    	_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);				

	    	_fileName += fileNameDateFormat.format(new Date()) + ConfigAppValues.FILE_EXTENSION;
	    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "==>setOutputFile("+_fileName+");");
	    	_recorder.setOutputFile(_fileName);
 	    	
	    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "==>Set done");
	    	
	    	
			_recorder.prepare();
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "==>prepare()  done");
			returned = true;

	    }
    	catch(IllegalStateException e)
    	{
    		if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "IllegalStateException->FAILS: " + e.getMessage() );
			e.printStackTrace();
    	}
    	catch (IOException e)
		{
    		if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "IOException->FAILS: " + e.getMessage() );
			e.printStackTrace();
		}
    	catch(Exception e)
    	{
    		if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "Exception->FAILS: " + e.getMessage() );
			e.printStackTrace();
    	}
    	
    	return returned;
	}
	
}
