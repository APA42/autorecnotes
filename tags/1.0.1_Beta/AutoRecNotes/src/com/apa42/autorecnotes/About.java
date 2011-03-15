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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class About extends Activity
{
	// for logs
	private final String CLASS_NAME = getClass().getName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate(Bundle savedInstanceState)" );

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		//int versionCode=1;
		String versionName="";
		try
		{
			PackageManager pm = this.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
			//versionCode = pi.versionCode;
			versionName = pi.versionName;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "onCreate::getPackageInfo() FAILS: " + e.getMessage());
			e.printStackTrace();
		}
		
		TextView txVersionContent = (TextView) findViewById(R.id.About_Version_Content);
		txVersionContent.setText(versionName);
	}

}
