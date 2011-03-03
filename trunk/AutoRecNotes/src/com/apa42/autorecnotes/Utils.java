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

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * @author Alberto Perez Alonso
 *
 */
public class Utils
{
	/**
	 * Check Media Storage Available and Writeable
	 * @return true if ok, false if it's not available
	 */
	public static boolean checkMediaStorage()
	{
		if (ConfigAppValues.DEBUG) Log.d("Utils", "checkMediaStorage()" );
		//
		boolean returned = false;

		// Note, example on how to monitor state of external storage
		// http://developer.android.com/reference/android/os/Environment.html#getExternalStorageDirectory%28%29
		try
		{
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) 
				returned = true;
		}
		catch(Exception e)
		{
			if (ConfigAppValues.DEBUG) Log.d("Utils", "FAIL=>checkMediaStorage(): " + e.getMessage());
			e.printStackTrace();			
		}
		return returned;
	}

	
	/**
	 * @param context To get resources
	 * @return Path to the directory where storage the recorded notes
	 */
	public static String givePathToStorage(Context context)
	{
		if (ConfigAppValues.DEBUG) Log.d("Utils", "givePathToStorage(Context context)" );
		//
		String returned = "";
		if ( checkMediaStorage() )
		{
			String sd_root = Environment.getExternalStorageDirectory().getPath();
			returned =  sd_root + ConfigAppValues.DOUBLE_SLASH + context.getResources().getString(R.string.sd_storage_folder);
		}
		return returned;
	}
}
