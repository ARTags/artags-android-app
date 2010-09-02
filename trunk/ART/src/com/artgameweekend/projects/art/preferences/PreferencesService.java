/* Copyright (c) 2010 ARt Project owners
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.artgameweekend.projects.art.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *
 * @author pierre
 */
public class PreferencesService
{
    public static final String LAYAR = "Layar";
    public static final String WIKITUDE = "Wikitude";

    private static final String SHARED_PREFS_NAME = "art.preferences";
    private static final String KEY_BROWSER = "ar_browser";

    private static PreferencesService singleton = new PreferencesService();

    private PreferencesService()
    {
    }

    public static PreferencesService instance()
    {
        return singleton;
    }

    public String getAugmentedRealityBrowser( Activity activity )
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getString( KEY_BROWSER , LAYAR );

    }

    public void setAugmentedRealityBrowser( Activity activity , String browser)
    {
        SharedPreferences prefs = activity.getSharedPreferences( SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString( KEY_BROWSER, browser);
        editor.commit();

    }



}
