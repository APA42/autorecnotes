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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AutoRecNotes extends Activity implements ServiceRecorderUIUpdateListener{
	// for logs
	private final String CLASS_NAME = getClass().getName();
	
	// Preferences and default values
	private boolean _pref_AlwaysRecord;
	private long _pref_TimeToRecord;
	// UI controls
	private ProgressBar _progressBar = null;
	private Button _btStartRecording = null;
	private View.OnClickListener _btStartRecording_Listener = null;
	private Button _btStopRecording = null;
	private View.OnClickListener _btStopRecording_Listener = null;
	private Button _btManageRecordedNotes = null;
	private View.OnClickListener _btManageRecordedNotes_Listener = null;
	// 
	private boolean _comeBackFromPreferences = false;
	private boolean _comebackFromManageRecordedNotes = false;
	private boolean _comebackFromAbout = false;
	// 	
	private boolean _recording = false;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate(Bundle savedInstanceState)" );
        //
		super.onCreate(savedInstanceState);
        setContentView(R.layout.autorecnotes);
        
        // Check storage card and create the directory to storage notes
       	if (!checkCreateFolderAppStorage())
       	{
       		// Can not save => exit
       		Toast.makeText(getApplicationContext(), getResources().getString(R.string.NO_ACCESS_TO_SD), Toast.LENGTH_SHORT).show();
       		finish();
       	}
        
        // Get UI controls
        _progressBar = (ProgressBar) findViewById(R.id.progressBarHorizontal);
        // Set color for progress bar
        _progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_red));
        // Buttons
        _btStartRecording = (Button) findViewById(R.id.btn_StartRecording);
        _btStopRecording = (Button) findViewById(R.id.btn_StopRecording);
        _btManageRecordedNotes = (Button) findViewById(R.id.btn_ManageRecorderNotes);

        // Listener
        _btStartRecording_Listener = new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (ConfigAppValues.DEBUG) Log.d(this.getClass().getName(), "_btStartRecording_Listener::onClick()");
		    	//
		    	if (!_recording)
		    		startRecording();
		    	else
		    		if (ConfigAppValues.DEBUG) Log.d(this.getClass().getName(), "*** we're recording=>no start again");
			}
		};
		
		_btStopRecording_Listener = new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (ConfigAppValues.DEBUG) Log.d(this.getClass().getName(), "_btStopRecording_Listener::onClick()");
		    	//
		    	if (_recording)
		    		stopRecordingService();
		    	else
		    		if (ConfigAppValues.DEBUG) Log.d(this.getClass().getName(), "*** we're NOT recording=>nothing to do");
		    	
			}
		};
		
		_btManageRecordedNotes_Listener = new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (ConfigAppValues.DEBUG) Log.d(this.getClass().getName(), "_btManageRecordedNotes_Listener::onClick()");
				//
				showManageRecordedActivity();
			}
		};
		// Bind the listeners
		_btStartRecording.setOnClickListener(_btStartRecording_Listener);
		_btStopRecording.setOnClickListener(_btStopRecording_Listener);
		_btManageRecordedNotes.setOnClickListener(_btManageRecordedNotes_Listener);

		// Bind UI Listener to Service
		ServiceRecorder.setUpdateUIListener(this);
    }
    
	@Override
	protected void onStart()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onStart()" );
		// TODO Auto-generated method stub
		super.onStart();
		//
	}
	@Override
	protected void onDestroy()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onDestroy()" );
    	//
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onPause()" );
    	//
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onRestart()" );
    	//
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onRestoreInstanceState(Bundle savedInstanceState)" );
    	//
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onSaveInstanceState(Bundle outState)" );
    	//
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onStop()" );
    	//
		// TODO Auto-generated method stub
		super.onStop();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{	
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onConfigurationChanged(Configuration newConfig)");
		//
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onResume()" );

		// TODO Auto-generated method stub
    	super.onResume();
    	
		//
		getValuesFromPreferences();
		
		// If always record is active => have to record
		// Special case. If user change preferences to always record=> when came back from preferences
		// Activity onStart begins to record
		if ( this._pref_AlwaysRecord && 
				! _recording && 
				!_comeBackFromPreferences && 
				!_comebackFromManageRecordedNotes &&
				!_comebackFromAbout )
		{
			startRecording();
		}
		else if ( _recording )
		{
			this.prepareUIForStarRecording();
		}
		else
		{
        	this.prepareUIForStopRecording();			
		}

		_comeBackFromPreferences = false;
		_comebackFromManageRecordedNotes = false;
		_comebackFromAbout = false;
		
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreateOptionsMenu(Menu menu)");

    	// TODO Auto-generated method stub
		//return super.onCreateOptionsMenu(menu);

    	// Create menu from resources
    	boolean returned = false;
    	try
    	{
    		MenuInflater inflater = getMenuInflater();
    		inflater.inflate(R.menu.autorecnote_menu, menu);
    		returned = true;
    	}
    	catch (InflateException e)
    	{
    		if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "onCreateOptionsMenu()::inflate FAILS: " + e.getMessage());    		
    		e.printStackTrace();
    	}
    	return returned;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onOptionsItemSelected(MenuItem item)");

		// TODO Auto-generated method stub
		//return super.onOptionsItemSelected(item);

		// If recording => don't pass to other activity, specialty to Preferences
		boolean returned = false;
		Intent intent = null;
		
		if ( _recording )
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.RECORDING), Toast.LENGTH_SHORT).show();
		}
		else
		{
			switch (item.getItemId()) 
			{
				case R.id.menu_preferences:
					intent = new Intent(AutoRecNotes.this,Preferences.class);
					startActivityForResult(intent,ConfigAppValues.TO_SHOW_PREFERENCES);
					returned = true;
					break;
				case R.id.menu_about:
					intent = new Intent(AutoRecNotes.this,About.class);
					startActivityForResult(intent,ConfigAppValues.TO_SHOW_ABOUT);
					returned = true;
					break;
			}
		}
		return returned;
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onActivityResult(int requestCode, int resultCode, Intent data)");

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// Not necessary in this case=>  Bundle extras = data.getExtras();
		switch(requestCode)
		{
			case ConfigAppValues.TO_SHOW_PREFERENCES:
				// Umm do not know if preferences have change or not
				// @@ Look in detail to detect
				//
				// Always get preferences we don't know when the user has change settings
			    getValuesFromPreferences();

			    _comeBackFromPreferences = true;
			    _comebackFromManageRecordedNotes = false;
				_comebackFromAbout = false;			    
				break;
			case ConfigAppValues.TO_SHOW_MANAGE_RECORDED_NOTES:
				// Don't care about the result
				_comeBackFromPreferences = false;
				_comebackFromManageRecordedNotes = true;
				_comebackFromAbout = false;				
				break;
			case ConfigAppValues.TO_SHOW_ABOUT:
				// Don't care about the result
				_comeBackFromPreferences = false;
				_comebackFromManageRecordedNotes = false;
				_comebackFromAbout = true;
				break;
		}
		
	}
	
	
	@Override
	public void updateUI(int counter)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "updateUI(" + counter + ")" );
		//

		_progressBar.setProgress(counter);
		if ( _pref_TimeToRecord < counter )
		{
			// Service will stop itself
			// Stop only the part of this activity controls, not the service
			stopRecording(); 
		}
		else if ( ConfigAppValues.SERVICE_RECORDER_STOP_NOMEDIARECORDER == counter )
		{
			// Service stop (problems with MediaRecorder=> StopUIRecording
			stopRecording();
		}
			
	}

	/****************************/
	/* Internal Functionalities */
	/****************************/
	
	private void showManageRecordedActivity()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "showManageRecordedActivity()" );
		//
		Intent intent = new Intent(AutoRecNotes.this,ManageRecordedNotes.class);
		startActivityForResult(intent,ConfigAppValues.TO_SHOW_MANAGE_RECORDED_NOTES);
	}
	private void startRecording()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "startRecording()" );
		//
		if ( Utils.checkMediaStorage() )
		{
			_recording = true;
			saveStateRecordingToPreferences(_recording);
			// UI
			prepareUIForStarRecording();
			//
			Intent svc = new Intent(this, ServiceRecorder.class);
			startService(svc);
		}
		else
		{
       		Toast.makeText(getApplicationContext(), getResources().getString(R.string.NO_ACCESS_TO_SD), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void stopRecordingService()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "stopRecordingService()" );
		//
		_recording = false;
		saveStateRecordingToPreferences(_recording);
		//
		Intent svc = new Intent(this, ServiceRecorder.class);
        stopService(svc);
        //
        prepareUIForStopRecording();
	}

	private void stopRecording()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "stopRecording()" );
		//
		_recording = false;
		saveStateRecordingToPreferences(_recording);
        //
        prepareUIForStopRecording();
	}

	
	private void prepareUIForStarRecording()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "prepareUIForStarRecording()" );
		// Progress Bar
        _progressBar.setMax((int)_pref_TimeToRecord);
		_progressBar.setProgress(0); 
		_progressBar.setVisibility(ProgressBar.VISIBLE);
		// Clickable button
		_btManageRecordedNotes.setEnabled(false);
		_btStartRecording.setEnabled(false);		
	}
	
	private void prepareUIForStopRecording()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "prepareUIForStopRecording()" );
		// Progress Bar
		_progressBar.setProgress(0); // Simulate end => reset progress bar		
		_progressBar.setVisibility(ProgressBar.INVISIBLE);
		// Clickable button
		_btManageRecordedNotes.setEnabled(true);
		_btStartRecording.setEnabled(true);
	}
	
	private void getValuesFromPreferences()
    {
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "getValuesFromPreferences()" );
    	//
    	PreferenceManager.setDefaultValues(getApplication(), R.xml.preferences, false);
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
    	
    	_pref_AlwaysRecord = preferences.getBoolean(ConfigAppValues.PREF_KEY_ALWAYS_RECORD, false);
    	String aux = preferences.getString(ConfigAppValues.PREF_KEY_TIMETORECORD_LIST, ConfigAppValues.PREF_KEY_TIMETORECORD_LIST_DEFAULT_VALUE);
    	try
    	{
    		_pref_TimeToRecord = Long.parseLong(aux);
    	}
    	catch (NumberFormatException e)
    	{
    		if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "Fail to convert String to long (apply default value): " + e.getMessage());
    		// Apply default value
    		_pref_TimeToRecord = ConfigAppValues.DEFAULT_TIMETORECORD;    		
    	}
    	// recording state
    	_recording = preferences.getBoolean(ConfigAppValues.PREF_KEY_RECORDING_STATE_FOR_ACTIVITY, false);
    }
	
	private void saveStateRecordingToPreferences(boolean recording)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "saveStateRecordingToPreferences("+recording+")" );
		//
    	PreferenceManager.setDefaultValues(getApplication(), R.xml.preferences, false);
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
    	Editor editor = preferences.edit();
    	editor.putBoolean(ConfigAppValues.PREF_KEY_RECORDING_STATE_FOR_ACTIVITY, recording);
    	editor.commit();
	}

	
	/**
	 * Check if the directory on the storage card exist. If doesn't exit automatically create it
	 * 
	 * @return true if ok, false if it's not available
	 */
	private boolean checkCreateFolderAppStorage()
	{	
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME,"checkCreateFolderAppStorage()");
		
		boolean returned = true;
		String sd_root ="";
		File f = null;
		
		try
		{
			// If folder doesn't exist automatically will be create
			if ( Utils.checkMediaStorage() )
			{
				sd_root = Environment.getExternalStorageDirectory().getPath();
				f = new File(sd_root + ConfigAppValues.DOUBLE_SLASH + getResources().getString(R.string.sd_storage_folder));
							
				if (null == f || (!f.exists() && !f.mkdir()) )
					returned = false;
			}
        }
		//mkdir throws
		catch(SecurityException e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "checkCreateFolderAppStorage=>SecurityException : " + e.getMessage());
			e.printStackTrace();
			// @@ Tell user doesn't have permission???
			returned = false;
		}
		catch(Exception e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "checkCreateFolderAppStorage=>Exception : " + e.getMessage());
			e.printStackTrace();
			returned = false;
		}
		return returned;
	}
}
