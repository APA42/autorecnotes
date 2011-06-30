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

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


public class RecordedNotePlayer extends Activity 
{

	// for logs
	private final String CLASS_NAME = getClass().getName();

	// 
	private MediaPlayer _mediaPlayer = null;
	private boolean _playing = false;
	private boolean _pause = false;
	private ProgressBar _progressBarPlayer = null;
	private Thread _thread = null;
	
	final private Runnable _updateProgressBarPlayer = new Runnable()
	{
		@Override
		public void run() 
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "run()" );
			//
			if ( null == _mediaPlayer )
			{
				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "run()=> Fail: This case must not happen" );
				return;
			}
			
			int actualPosition = 0;
			final int totalPosition = _mediaPlayer.getDuration();
			while ( _playing  && actualPosition < totalPosition )
			{
				try
				{
					//Thread.sleep(500);	// wait 0,5 second
					if ( null != _mediaPlayer )
					{
						actualPosition = _mediaPlayer.getCurrentPosition();
						// Update ProgressBar
						_progressBarPlayer.setProgress(actualPosition);
					}
				}				
				catch(Exception e)
				{
					if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "updateProgressBarPlayer FAILS: " + e.getMessage());				
	                e.printStackTrace();
	                return;
				}
			}
		}
	};
	
	final private MediaPlayer.OnCompletionListener _mediaPlayerCompleteListener  = new MediaPlayer.OnCompletionListener() 
	{
		@Override
		public void onCompletion(MediaPlayer mp) 
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCompletion(MediaPlayer mp)" );
			//
			stopPlaying();
			// exit
			finish();
		}
	};


	// Pair onCreate/onDestroy
	// -----------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate(Bundle savedInstanceState)" );
		//
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordednoteplayer);
		// Get uri data
		Uri data = getIntent().getData();
		if ( null == data )
		{
			// No file to play 
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.NO_RECORDEDNOTE_TO_PLAY), Toast.LENGTH_SHORT).show();
			finish();
			return;
		}	
		// Get the name of the RecordedNote
		if ( null != getIntent().getExtras() )
		{
			String aux = getIntent().getExtras().getString(ConfigAppValues.RECORDEDNOTE_NAME);
			setTitle(aux);
			setTitleColor(getResources().getColor(R.color.LimeGreen));
		}
		// Set color for progress bar
		_progressBarPlayer = (ProgressBar) findViewById(R.id.progressBarPlayer);
		_progressBarPlayer.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_greenandroid));
		// Let's play		
		playRecordedNote(data);
	}

	@Override
	protected void onDestroy() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onDestroy()" );
		//
		if (_playing)
		{
			// Activity have to destroy=>if we're playing a recorded note have to stop
			stopPlaying();
		}
		super.onDestroy();
	}

	// Pair onPause/onResume
	// ---------------------
	@Override
	protected void onPause() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onPause()" );
		//
		super.onPause();
	}

	@Override
	protected void onResume() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onResume()" );
		//
		super.onResume();
	}

	// Pair onStart/onStop
	// -------------------
	@Override
	protected void onStart() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onStart()" );
		//
		super.onStart();
	}

	@Override
	protected void onStop() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onStop()" );
		//
		super.onStop();
	}	
	
	@Override
	protected void onRestart() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onRestart()" );
		//
		super.onRestart();
	}
		
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onConfigurationChanged(Configuration newConfig)" );
		//
		super.onConfigurationChanged(newConfig);
	}

	private void playRecordedNote(Uri recordedNoteToPlay)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "playRecordedNote()" );
		//
		if ( _playing )
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "playRecordedNote()=>FLAG: Playing" );
			return;
		}
		
		if ( null != _mediaPlayer && _mediaPlayer.isPlaying() )
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "playRecordedNote()=>Playing..." );
			// Nothing to do, still playing a recordednote
			return;
		}
		//
		_mediaPlayer = MediaPlayer.create(RecordedNotePlayer.this, recordedNoteToPlay);
		if ( null == _mediaPlayer )
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.CANNOT_CREATE_MEDIAPLAYER), Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		//
		_mediaPlayer.setOnCompletionListener(_mediaPlayerCompleteListener);
		try 
		{
			// not necessary => _mediaPlayer.prepare();
			_mediaPlayer.start();
			_playing = true;
			// Configure Progress Bar
			_progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
			_progressBarPlayer.setProgress(0);
			_progressBarPlayer.setMax(_mediaPlayer.getDuration());
			// thread to update progress bar
			_thread = new Thread(null,this._updateProgressBarPlayer,"updateProgressBarPlayer");
			_thread.start();
		} 
		catch (IllegalStateException e) 
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "playRecordedNote() FAILS: " + e.getMessage());				
			e.printStackTrace();
		} 
	}
	
	private void stopPlaying()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "stopPlaying()" );
		//
		_playing = false;
		
		if ( null != _mediaPlayer )
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "stopPlaying()=>Release mediaPlayer" );
			//
			_mediaPlayer.release();
			_mediaPlayer = null;
		}
	}	
	
	public void onClickBtnPauseResume(View arg)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onClickBtnPauseResume(View arg)" );
		//
		if ( _playing )
		{
			if ( null != _mediaPlayer )
			{
				try
				{
					// Change background image
					Button btnAux = (Button) findViewById(R.id.btn_PauseResume);
					if ( _pause )
					{
						_mediaPlayer.start();
						btnAux.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
					}
					else
					{
						_mediaPlayer.pause();
						btnAux.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
					}
					// store option
					_pause = !_pause;
				}
				catch(IllegalStateException	e)
				{
					if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "onClickBtnPauseResume()=>pause/resume FAILS: " + e.getMessage());				
					e.printStackTrace();
				}
			}
		}
		
	}
}