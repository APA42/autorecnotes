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

public class ConfigAppValues
{
	public static final boolean DEBUG = false;
	// Preferences
	public static final String PREF_KEY_ALWAYS_RECORD = "Pref_AlwaysRecord";	
	public static final String PREF_KEY_TIMETORECORD_LIST = "Pref_TimeToRecord_List";
	public static final String PREF_KEY_TIMETORECORD_LIST_DEFAULT_VALUE = "15000";
	public static final String PREF_KEY_RECORDING_STATE_FOR_ACTIVITY = "Pref_Activity_Recording";
	public static final String PREF_KEY_RECORDING_STATE_FOR_SERVICE = "Pref_Service_Recording";
	// ActiviyForResult
	public static final int TO_SHOW_PREFERENCES = 1;
	public static final int TO_SHOW_MANAGE_RECORDED_NOTES = 2;
	public static final int TO_SHOW_ABOUT = 3;
	//
	public static final long DEFAULT_TIMETORECORD = 15000;
	// Others
	public static final String DOUBLE_SLASH = "//";
	public static final String FILE_EXTENSION = ".3GP";
	// Service Stop Message
	public static final int SERVICE_RECORDER_STOP_NOMEDIARECORDER = -42;

}
