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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity
{
	/** IMPORTANT **/
	/** PreferenceActivity change for HONEYCOMB **/
	/** --------------------------------------- **/

	// for logs
	private final String CLASS_NAME = getClass().getName();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate(Bundle savedInstanceState)" );
		//
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
	}
}
