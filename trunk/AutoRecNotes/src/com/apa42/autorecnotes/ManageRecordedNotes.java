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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ManageRecordedNotes extends ListActivity
{
	// for logs
	private final String CLASS_NAME = getClass().getName();
	//
	private List<RecordedNote> _recordedNotesList = null;
	private RecordedNotesListAdapter _recordedNotesListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate(Bundle savedInstanceState)" );
		//
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.managerecordednotes);
		
		// Have to do on Start. We're going to read storage card looking for recorded notes
		if (!Utils.checkMediaStorage())
		{
        	// Can not read => exit
        	Toast.makeText(getApplicationContext(), getResources().getString(R.string.NO_ACCESS_TO_SD), Toast.LENGTH_SHORT).show();
        	finish();
		}		
		
		// Fill all the list and ArrayAdapter with the contents of the Storage 
		fillListWithRecordedNotes();
		
		// Bind to ListView
		this.setListAdapter(_recordedNotesListAdapter);
		// Bind the menu options
		registerForContextMenu(getListView());
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo)");
		//
		super.onCreateContextMenu(menu, v, menuInfo);
		//
		MenuInflater inflater = getMenuInflater();
		menu.setHeaderTitle(getResources().getString(R.string.ct_menu_ManageRecordedNotes_HeaderTitle));
		inflater.inflate(R.menu.ct_menu_recorded_notes, menu);
	}


	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onListItemClick(ListView l, View v, int position, long id)");
				
		// Default option. Not a long press, only one click
		// Play the note
		this.playRecordedNote(position);		
	}

	

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onContextItemSelected(MenuItem item)");
		
		boolean returned = false;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		// Which option user select
		switch ( item.getItemId() )
		{
			case R.id.context_menu_play:
				playRecordedNote(info.id);
				returned = true;
				break;
			case R.id.context_menu_share:
				shareRecordedNote(info.id);
				returned = true;
				break;
			case R.id.context_menu_rename:
				renameRecordedNote(info.id);
				returned = true;
				break;
			case R.id.context_menu_delete:
				askUserDeleteRecordedNote(info.id);
				returned = true;
				break;
			default:
				returned = super.onContextItemSelected(item); 	
		}
		
		return returned;
	}


	/****************************/
	/* Internal Functionalities */
	/****************************/
	
	private String removeFileNameExtension(String fileName)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "removeFileNameExtension("+fileName+")");
		//
		String returned = null;
		try
		{
			if ( fileName.endsWith(ConfigAppValues.FILE_EXTENSION) )
			{
				//returned = fileName.replace(ConfigAppValues.FILE_EXTENSION, "");
				int pos = fileName.indexOf(ConfigAppValues.FILE_EXTENSION);
				if ( -1 != pos )
				{
					returned = fileName.substring(0, pos);
				}
			}
		}
		catch(NullPointerException 	e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "NullPointerException->FAILS: " + e.getMessage() );
			e.printStackTrace();				
		}
		catch (IndexOutOfBoundsException e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "IndexOutOfBoundsException->FAILS: " + e.getMessage() );
			e.printStackTrace();				
		}

		return returned;
	}
	
	private void fillListWithRecordedNotes()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "fillListWithRecordedNotes()");
		
		String auxFileName = null;
		
		// Clear old data
	    if ( null != _recordedNotesList )
	    	_recordedNotesList.clear();
	    else
	    	_recordedNotesList = new ArrayList<RecordedNote>();

	    try
	    {
		    // Read the storage card
			File[] arrayFileRecordedNotes = new File(Utils.giveMePathToStorage(this)).listFiles();
			if (null != arrayFileRecordedNotes )
			{
				for (File file : arrayFileRecordedNotes)
				{
					auxFileName = removeFileNameExtension( file.getName() );
					if (null != auxFileName )
					{
				        // Right now, we apply the same image to every recorded note						
						RecordedNote recordedNote = new RecordedNote(auxFileName,null);
						_recordedNotesList.add(recordedNote);
					}
				}
			}
	    }
	    catch(SecurityException e)
	    {
	    	if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "fillListWithRecordedNotes()::Read from storage card FAILS: " + e.getMessage());
	    	e.printStackTrace();
        	Toast.makeText(getApplicationContext(), getResources().getString(R.string.NO_ACCESS_TO_SD), Toast.LENGTH_SHORT).show();
        	finish();

	    }
	    catch(Exception e)
	    {
	    	if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "fillListWithRecordedNotes()::Read from storage card FAILS: " + e.getMessage());
	    	e.printStackTrace();
	    }
		
		// Fill the ArrayAdapter
	    if ( null != _recordedNotesListAdapter )
	    {
	    	//_listAdapter.clear();
	    	_recordedNotesListAdapter = null;
	    }
		
	    // Sort only first time
	    Collections.sort(_recordedNotesList);
	    
	    _recordedNotesListAdapter = new RecordedNotesListAdapter(_recordedNotesList,this);
	}

	private void playRecordedNote(long indexRecordedNote)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "playRecoredNote(" + indexRecordedNote +")");
		//
		String fileName = giveFileNameAndPath(indexRecordedNote);
		
		if (fileName.length() > 0)
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "playRecordedNote=>fileAbsoluteName: " + fileName);
			
			Uri data = Uri.parse("file:///"+fileName);
			
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "playRecordedNote==>uri data: " + data);
			
			Intent iplayIntent = new Intent(android.content.Intent.ACTION_VIEW);
			iplayIntent.setDataAndType(data,"audio/mp3");
			try
			{
				startActivity(iplayIntent);
			}
			catch (ActivityNotFoundException e) 
			{
				if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "playRecordedNote() FAILS: " + e.getMessage());				
                e.printStackTrace();
			}
         }
		else
		{
        	// Can not read => exit
        	Toast.makeText(getApplicationContext(), getResources().getString(R.string.NO_ACCESS_TO_SD), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void shareRecordedNote(long indexRecordedNote)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "shareRecoredNote(" + indexRecordedNote + ")");

		// Get the file
		String fileName = giveFileNameAndPath(indexRecordedNote);
		if (fileName.length() > 0)
		{
	    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME,"Sharing File: " + fileName);

	    	//File fileToShare = new File(fileName);
	    	//Uri data = Uri.fromFile(fileToShare);
			Uri data = Uri.parse("file://"+fileName);
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
			emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_content));
            emailIntent.putExtra(Intent.EXTRA_STREAM, data);
    		// ------------------------------------------------------
    		// @@ PENDING to change emailIntent.setType("audio/mp3");
    		// ------------------------------------------------------
			emailIntent.setType("audio/mp3");
			try
			{
				startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.ct_menu_ManageRecordedNotes_Share)));
			}
			catch (ActivityNotFoundException e) 
			{
				if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "shareRecordedNote() FAILS: " + e.getMessage());
                e.printStackTrace();
			}
		}
	}
	

	private void renameRecordedNote(long indexRecordedNote)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "renameRecoredNote(long indexRecordedNote)");
		//
		renameDialog(indexRecordedNote);
	}


	private void renameDialog(long indexRecordedNote)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "renameDialog()");
		//
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText inputText = new EditText(this);
		final long positionIndex = indexRecordedNote;
		
    	String fileName = ((RecordedNote)this.getListAdapter().getItem((int)indexRecordedNote)).getFileName();
		inputText.setText(fileName);
		builder.setView(inputText);
		
		OnClickListener yesDialog_listener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "askUserDeleteRecordedNote=>onClick YES");
				//
				String newName = inputText.getText().toString().trim();
				renameRecordedNoteFileName(positionIndex,newName);
			}
		};
		
		OnClickListener noDialog_listener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "askUserDeleteRecordedNote=>onClick NO");
				//
				// TODO Auto-generated method stub
				// Nothing to do
				dialog.cancel();
			}
		};
		builder.setTitle(R.string.RENAME_TITLE_MSG);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes, yesDialog_listener);
		builder.setNegativeButton(R.string.no, noDialog_listener);
		//
		builder.show();
	}
	
	private void renameRecordedNoteFileName(long indexRecordedNote, String newName)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "renameRecordedNoteFileName(" + indexRecordedNote + ","+ newName + ")");
		
		// Check doesn't exist and don't end with file extension
		// File.renameTo doesn't throw exception if name are the same...
		if ( newName.length() > 0 )
		{
			RecordedNote recordedNote_Aux = new RecordedNote(newName,null);
			if ( ((RecordedNotesListAdapter)this.getListAdapter()).contains(recordedNote_Aux) )
			{
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.RENAME_NEWNAME_EXIST), Toast.LENGTH_SHORT).show();
				renameDialog(indexRecordedNote);
				return;
			}
			else if ( newName.endsWith(ConfigAppValues.FILE_EXTENSION) )
			{
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.RENAME_NO_VALID_NAME), Toast.LENGTH_SHORT).show();
				renameDialog(indexRecordedNote);
				return;
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.RENAME_NO_VALID_NAME), Toast.LENGTH_SHORT).show();
			renameDialog(indexRecordedNote);
			return;
		}	
		
		// Try to rename
		if (Utils.checkMediaStorage())
		{
			String fileName = this.giveFileNameAndPath(indexRecordedNote);
	    	
	    	String newFileName = Utils.giveMePathToStorage(this);
	    	newFileName += ConfigAppValues.DOUBLE_SLASH;
	    	newFileName += newName;
	    	newFileName += ConfigAppValues.FILE_EXTENSION;
	
			File file = new File(fileName);
			File newFile = new File(newFileName);
			try
			{
				if ( file.renameTo(newFile) )
				{
					// Rename at ListAdapter
					((RecordedNote)this.getListAdapter().getItem((int)indexRecordedNote)).setFileName(newName);
					// Refresh view
					this.getListView().invalidateViews(); // have to refresh the list view
				}
				else
				{
					Toast.makeText(getApplicationContext(), R.string.RENAME_ERROR, Toast.LENGTH_SHORT).show();
				}
			}
			catch(SecurityException e)
			{
				if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME,"renameRecordedNoteFileName FAILS : " + e.getMessage());
				e.printStackTrace();
			}
		}
		else
		{
        	Toast.makeText(getApplicationContext(), getResources().getString(R.string.NO_ACCESS_TO_SD), Toast.LENGTH_SHORT).show();
		}

	}


	private void askUserDeleteRecordedNote(long indexRecordedNote)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "askUserDeleteRecordedNote(" + indexRecordedNote + ")");
		//
		final long index = indexRecordedNote;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		OnClickListener yesDialog_listener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "askUserDeleteRecordedNote=>onClick YES");
				//
				// TODO Auto-generated method stub
				deleteRecordedNote(index);
			}
		};
		
		OnClickListener noDialog_listener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "askUserDeleteRecordedNote=>onClick NO");
				//
				// TODO Auto-generated method stub
				// Nothing to do
				dialog.cancel();
			}
		};
		
		String title = getResources().getString(R.string.DELETE_TITLE_MSG) + " ";
		String fileName = ((RecordedNote)this.getListAdapter().getItem((int)indexRecordedNote)).getFileName();
		title += fileName;
		
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setMessage(R.string.DELETE_CONFIRMATION);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes, yesDialog_listener);
		builder.setNegativeButton(R.string.no, noDialog_listener);
		//
		builder.show();
	}


	private void deleteRecordedNote(long indexRecordedNote)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "deleteRecoredNote(long indexRecordedNote)");
		//
		String fileName = giveFileNameAndPath(indexRecordedNote);
		// Check if can be delete
		if ((fileName.length() > 0) && Utils.canDeleteFile(fileName))
		{
			File file = new File(fileName);
			if ( file.delete() )
			{
				Toast.makeText(getApplicationContext(), R.string.DELETE, Toast.LENGTH_SHORT).show();
				
				if (ConfigAppValues.DEBUG)
				{
					Log.d(CLASS_NAME, "----------------------");
					Log.d(CLASS_NAME, "Items before delete");
					Log.d(CLASS_NAME, "_RecordedNotesList: " + _recordedNotesList.size());
					Log.d(CLASS_NAME, "_recordedNotesListAdapter: " + _recordedNotesListAdapter.getCount());
				}
				
				try
				{
					// not necessary. We're working with the ListAdapter associate to ListActivity
					//RecordedNote itemToRemove = _RecordedNotesList.get((int)indexRecordedNote);
					//_RecordedNotesList.remove(itemToRemove);
					//_recordedNotesListAdapter.remove(indexRecordedNote);
					
					// IMPORTANT
					// When remove from ListAdapater binded it automatically 
					// removes from _recordedNotesListAdapter and _recordedNotesList
					
					((RecordedNotesListAdapter) getListAdapter()).remove(indexRecordedNote);
					
					// Fails => if there are not RecordedNotes doesn't redraw the view
					//  with TextView when it's empty
					if ( 0 == getListAdapter().getCount() )
					{
						if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "deleteRecordedNote => Re-Bind the ListAdapter");	
						this.setListAdapter(_recordedNotesListAdapter);
					}
					else
					{
						// Update List
						this.getListView().invalidateViews(); // have to refresh the list view
					}
					
				}
				catch(Exception e)
				{
					if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME,"FAILS remove items: " + e.getMessage());
					e.printStackTrace();
				}

				if (ConfigAppValues.DEBUG)
				{
					Log.d(CLASS_NAME, "----------------------");
					Log.d(CLASS_NAME, "Items AFTER DELETE");
					Log.d(CLASS_NAME, "_RecordedNotesList: " + _recordedNotesList.size());
					Log.d(CLASS_NAME, "_recordedNotesListAdapter: " + _recordedNotesListAdapter.getCount());
				}

			}
			else
			{
				Toast.makeText(getApplicationContext(), R.string.DELETE_ERROR, Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), R.string.DELETE_ERROR, Toast.LENGTH_SHORT).show();
		}
	}


	private String giveFileNameAndPath(long indexRecordedNote)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "giveFileNameAndPath(" + indexRecordedNote +")");
		//
		String fileName = Utils.giveMePathToStorage(this);
		if (fileName.length() > 0 )
		{
			fileName += ConfigAppValues.DOUBLE_SLASH;
			fileName += ((RecordedNote)this.getListAdapter().getItem((int)indexRecordedNote)).getFileName();
			fileName += ConfigAppValues.FILE_EXTENSION;
		}
		return fileName;
	}
}
