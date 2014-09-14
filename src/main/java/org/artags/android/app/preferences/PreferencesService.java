/* Copyright (c) 2010-2014 ARTags Project owners (see http://www.artags.org)
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
package org.artags.android.app.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import org.artags.android.app.draw.BrushParameters;

/**
 *
 * @author Pierre Levy
 */
public class PreferencesService
{

    public static final String KEY_VERSION = "version";
    public static final String LAYAR = "Layar";
    public static final String WIKITUDE = "Wikitude";
    public static final String JUNAIO = "Junaio";
    public static final String MIXARE = "Mixare";

    private static final String SHARED_PREFS_NAME = "art.preferences";
    private static final String KEY_BROWSER = "ar_browser";
    private static final String KEY_MYLOCATION = "mylocation";
    private static final String KEY_BRUSH_SIZE = "brush.size";
    private static final String KEY_BRUSH_COLOR = "brush.color";
    private static final String KEY_BRUSH_COLOR_BASE = "brush.color.base";
    private static final String KEY_BRUSH_COLOR_INTENSITY = "brush.color.intensity";
    private static final String KEY_OPACITY = "brush.opacity";
    private static final String KEY_BLUR_EFFECT = "brush.blur";
    private static final String KEY_EMBOSS_EFFECT = "brush.emboss";
    private static final String KEY_EULA_ACCEPTED = "eula.accepted";
    private static final String KEY_DISPLAY_DRAW_README = "readme.draw";
    private static final int DEFAULT_BRUSH_SIZE = 12;
    private static final int DEFAULT_COLOR = 0xFFA5C739;
    private static final int DEFAULT_INTENSITY = 50;
    private static final int DEFAULT_OPACITY = 0xFF;
    private static final boolean DEFAULT_MYLOCATION = true;
    //change this if readme have to be displayed again on app update.
    //Check android:versionCode in AndroidManifest.xml
    private int LAST_VERSION_WHERE_DRAW_README_CHANGED = 5;
    private static PreferencesService singleton = new PreferencesService();

    private PreferencesService()
    {
    }

    /**
     * Returns the unique instance
     * @return The instance
     */
    public static PreferencesService instance()
    {
        return singleton;
    }

    /**
     * Gets the current browser
     * @param activity The Activity
     * @return The browser key
     */
    public String getAugmentedRealityBrowser(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getString(KEY_BROWSER, LAYAR);

    }

    /**
     * Store the current browser 
     * @param activity The activity
     * @param browser The browser key
     */
    public void setAugmentedRealityBrowser(Activity activity, String browser)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(KEY_BROWSER, browser);
        editor.apply();

    }

    /**
     * Save brush parameters
     * @param activity The activity
     * @param bp Brush parameters
     */
    public void saveBrushParameters(Activity activity, BrushParameters bp)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(KEY_BRUSH_SIZE, bp.getBrushSize());
        editor.putInt(KEY_BRUSH_COLOR, bp.getColor());
        editor.putInt(KEY_BRUSH_COLOR_BASE, bp.getColorBase());
        editor.putInt(KEY_BRUSH_COLOR_INTENSITY, bp.getColorIntensity());
        editor.putInt(KEY_OPACITY, bp.getOpacity());
        editor.putBoolean(KEY_BLUR_EFFECT, bp.isBlur());
        editor.putBoolean(KEY_EMBOSS_EFFECT, bp.isEmboss());
        editor.apply();
    }

    /**
     * Restore brush parameters
     * @param activity The activity
     * @param bp Brush Parameters
     */
    public void restoreBrushParameters(Activity activity, BrushParameters bp)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        bp.setBrushSize(prefs.getInt(KEY_BRUSH_SIZE, DEFAULT_BRUSH_SIZE));
        bp.setColor(prefs.getInt(KEY_BRUSH_COLOR, DEFAULT_COLOR));
        bp.setColorBase(prefs.getInt(KEY_BRUSH_COLOR_BASE, DEFAULT_COLOR));
        bp.setColorIntensity(prefs.getInt(KEY_BRUSH_COLOR_INTENSITY, DEFAULT_INTENSITY));
        bp.setOpacity(prefs.getInt(KEY_OPACITY, DEFAULT_OPACITY));
        bp.setBrushSize(prefs.getInt(KEY_BRUSH_SIZE, DEFAULT_BRUSH_SIZE));
        bp.setBlur(prefs.getBoolean(KEY_BLUR_EFFECT, false));
        bp.setEmboss(prefs.getBoolean(KEY_EMBOSS_EFFECT, false));
    }

    /**
     * Gets the Application's version
     * @param activity The activity
     * @return The version
     */
    public int getVersion(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getInt(KEY_VERSION, 0);
    }

    /**
     * Save the application's version
     * @param activity The activity
     * @param version The version
     */
    public void saveVersion(Activity activity, int version)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(KEY_VERSION, version);
        editor.apply();
    }

    /**
     * Save the MyLocation option
     * @param activity The activity
     * @param enabled On or Off
     */
    public void setMyLocation(Activity activity, boolean enabled)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(KEY_MYLOCATION, enabled);
        editor.apply();
    }

    /**
     * Gets the mylocation option
     * @param activity The activity
     * @return The option value : On or Off
     */
    public boolean getMyLocation(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getBoolean(KEY_MYLOCATION, DEFAULT_MYLOCATION);
    }

    /**
     * Sets the EULA flag
     * @param activity The activity
     */
    public void setEulaAccepted(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(KEY_EULA_ACCEPTED, true);
        editor.apply();
    }

    /**
     * Gets the EULA flag
     * @param activity The activity
     * @return true if accepted otherwise false
     */
    public boolean isEulaAccepted(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getBoolean(KEY_EULA_ACCEPTED, false);
    }

    /**
     * 
     * @return
     */
    public int getLastReadMeChangeVersion()
    {
        return LAST_VERSION_WHERE_DRAW_README_CHANGED;
    }

    /**
     * Sets the reame flag for Draw
     * @param activity The activity
     * @param display The flag value : true if read
     */
    public void setDrawReadme(Activity activity, boolean display)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(KEY_DISPLAY_DRAW_README, display);
        editor.apply();
    }

    /**
     * Gets the readme flag for Draw
     * @param activity The activity
     * @return true if read
     */
    public boolean isDrawReadme(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DISPLAY_DRAW_README, true);
    }

    /**
     * Gets the version number
     * @param context The context
     * @return The version number
     */
    public int getVersionNumber(Context context)
    {
        int versionNo = 0;
        PackageInfo pInfo = null;
        try
        {
            pInfo = context.getPackageManager().getPackageInfo("org.artags.android.app", PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e)
        {
            pInfo = null;
        }
        if (pInfo != null)
        {
            versionNo = pInfo.versionCode;
        }

        return versionNo;
    }
}
