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

import android.graphics.drawable.Drawable;

public class RecordedNote implements Comparable<RecordedNote>
{
	private String _fileName =  "";
	private Drawable _icon = null;
	
	public RecordedNote(String fileName, Drawable image) 
	{
		setFileName(fileName);
		setIcon(image);
    }

	public void setIcon(Drawable icon)
	{
		this._icon = icon;
	}

	public Drawable getIcon()
	{
		return _icon;
	}

	public void setFileName(String fileName)
	{
		this._fileName = fileName;
	}

	public String getFileName()
	{
		return _fileName;
	}

	@Override
	public int compareTo(RecordedNote another)
	{
		//if (ConfigAppValues.DEBUG) Log.d("RecordedNote", "compareTo(RecordedNote another)" );
		
		// Useful for sorting the list
		// comparable by its name
		if ( _fileName.length() > 0 && another.getFileName().length()>0 )
			return _fileName.compareTo(another.getFileName());
		else
			throw new IllegalArgumentException();
	}
	
	@Override
	public boolean equals(Object o)
	{
		// This is necessary for contains
		//if (ConfigAppValues.DEBUG) Log.d("RecordedNote", "equals(Object o)" );
		
		// Equals by its name
		RecordedNote aux = (RecordedNote) o;
		return this._fileName.equals(aux.getFileName());
		
		// TODO Auto-generated method stub
		//return super.equals(o);
	}

}
