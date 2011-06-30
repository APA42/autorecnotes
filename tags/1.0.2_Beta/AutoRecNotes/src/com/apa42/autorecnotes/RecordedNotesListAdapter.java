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

import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordedNotesListAdapter extends BaseAdapter
{
	// for logs
	private final String CLASS_NAME = getClass().getName();

	private Context _context;
	private List<RecordedNote> _recordedNotesList;
	
	public RecordedNotesListAdapter(List<RecordedNote> listRecordedNotes,Context context)
	{
		_context = context;
		_recordedNotesList = listRecordedNotes;
	}

	public boolean remove(long position)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "remove("+position+")" );
		
		boolean returned = false;
		try
		{
			RecordedNote itemToRemove = _recordedNotesList.get((int)position);
			_recordedNotesList.remove(itemToRemove);
			returned = true;
		}
		catch(UnsupportedOperationException e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME,  "remove("+ position + ") FAILS: " + e.getMessage());
			e.printStackTrace();
		}
		catch(IndexOutOfBoundsException e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME,  "remove("+ position + ") FAILS: " + e.getMessage());
			e.printStackTrace();	
		}
		return returned;
	}
	
	public boolean contains(RecordedNote recordedNote)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "contains(RecordedNote recordedNote)" );
		//
		return _recordedNotesList.contains(recordedNote);
	}
	
	@Override
	public int getCount()
	{
		//if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "getCount()" );
		return _recordedNotesList.size();
	}

	@Override
	public Object getItem(int position)
	{
		//if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "getItem("+position+")" );
		return _recordedNotesList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		//if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "getItemId("+position+")" );
		
		// Use the array index as a unique id
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "getView(int position, View convertView, ViewGroup parent)" );
		
		// convertView The old view to overwrite, if one is passed
		// returns the view that I want to show 
		View view;

		if ( null == convertView )
		{
			LayoutInflater vi = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.recordednote, null);
		}
		else
		{
			// Reuse/Overwrite the View passed
			// We are assuming(!) that it is castable!
			view = convertView;
		}

	      // Get File Name of the RecordedNote
        TextView text= (TextView) view.findViewById(R.id.TextRecordedNote);
        text.setText(_recordedNotesList.get(position).getFileName());
        //
        // Right now is not necessary
        // We apply the same image to every recorded note
        //
        // For the future, We can change the image from resources
        //
        //ImageView img = (ImageView)view.findViewById(R.id.ImageRecordedNote);
        //img.setImageResource(R.drawable.ic_launcher);
        //// Or Better way it that every recorded note has its own image
	    //img.setImageDrawable(_recordedNotesList.get(position).getIcon());
           
		return view;
	}

}
