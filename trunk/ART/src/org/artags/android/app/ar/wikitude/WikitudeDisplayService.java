/* Copyright (c) 2010 ARTags Project owners (see http://artags.org)
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
package org.artags.android.app.ar.wikitude;

import org.artags.android.app.ar.GenericPOI;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.util.Log;
import org.artags.android.app.R;
import java.util.ArrayList;
import java.util.List;
import org.artags.android.app.ARTagsApplication;
import org.artags.android.app.Security;
import org.openintents.intents.WikitudeARIntent;
import org.openintents.intents.WikitudePOI;

/**
 *
 * @author pierre@artags.org
 */
public class WikitudeDisplayService
{

    private static Activity mActivity;
    public static final String CALLBACK_INTENT = "wikitudeapi.SHOWIMAGE";

    public static void display(List<GenericPOI> listGenericPOIs, Activity activity)
    {
        mActivity = activity;
        List<WikitudePOI> list = buildWikitudePOIs(listGenericPOIs);

        WikitudeARIntent intent = new WikitudeARIntent(activity.getApplication(), null, Security.KEY_WIKITUDE, "artags");

        intent.setPrintMarkerSubText(false);

        intent.addPOIs(list);
        (( ARTagsApplication ) activity.getApplication()).setPOIs(list);

        try
        {
            intent.startIntent(activity);
        } catch (ActivityNotFoundException e)
        {
            WikitudeARIntent.handleWikitudeNotFound(activity);
        }
    }

    private static List<WikitudePOI> buildWikitudePOIs(List<GenericPOI> listGenericPOIs)
    {
        List<WikitudePOI> list = new ArrayList<WikitudePOI>();
        for (GenericPOI poi : listGenericPOIs)
        {
            String name = poi.getName();
            String desc = "Date : " + poi.getDate() + " -Rating : " + poi.getRating();
            double lat = poi.getLatitude();
            double lon = poi.getLongitude();
            double alt = poi.getAltitude();
            String url = poi.getUrl();
            String iconUrl = poi.getIconUrl();
            Log.d("Wikitude ", "Adding POI : " + lat + ", " + lon + ", 10, \"" + name + "\", \"" + desc + "\"");
            WikitudePOI wpoi = new WikitudePOI(lat, lon, 10, name, desc);
            wpoi.setLink(poi.getUrl());
//            wpoi.setIconuri(iconUrl);
            wpoi.setIconresource(mActivity.getResources().getResourceName(R.drawable.marker));
            wpoi.setDetailAction( WikitudeDisplayService.CALLBACK_INTENT);
            list.add(wpoi);
        }
        return list;

    }
}
