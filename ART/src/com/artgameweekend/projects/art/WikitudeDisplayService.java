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
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.openintents.intents.WikitudeARIntent;
import org.openintents.intents.WikitudePOI;

/**
 *
 * @author pierre
 */
public class WikitudeDisplayService {
    
    static void display( List<GenericPOI> listGenericPOIs , Activity activity)
    {
        List<WikitudePOI> list = buildWikitudePOIs( listGenericPOIs );

                WikitudeARIntent intent = new WikitudeARIntent(activity.getApplication(), null, "507419D8685F116E0AB61704F21734D0", "art");

            intent.setPrintMarkerSubText(false);

            intent.addPOIs(list);
            try
            {
                intent.startIntent(activity);
            } catch (ActivityNotFoundException e)
            {
                WikitudeARIntent.handleWikitudeNotFound(activity);
            }
    }

    private static List<WikitudePOI> buildWikitudePOIs( List<GenericPOI> listGenericPOIs )
    {
        List<WikitudePOI> list = new ArrayList<WikitudePOI>();
        for( GenericPOI poi : listGenericPOIs )
        {
            String name = poi.getName();
            String desc = poi.getDescription();
            double lat = poi.getLatitude();
            double lon = poi.getLongitude();
            double alt = poi.getAltitude();
            String url = poi.getUrl();
            String iconUrl = poi.getIconUrl();
            WikitudePOI wpoi = new WikitudePOI( lat, lon, alt, name, desc, url, null, iconUrl, "wikitudeapi.SHOWIMAGE");
            wpoi.setIconuri(iconUrl);
            wpoi.setDetailAction("wikitudeapi.SHOWIMAGE");
            list.add(wpoi);
        }
        return list;

    }

}
