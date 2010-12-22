/* Copyright (c) 2010 ARTags Project owners (see http://www.artags.org)
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
package org.artags.android.app.ar;

import org.artags.android.app.tag.TagParser;
import android.util.Log;
import java.net.URL;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.artags.android.app.Security;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author Pierre Levy
 */
public class POIService
{

    

    public static List<GenericPOI> getPOIs(double lat, double lon, int maxPOIs )
    {
        List<GenericPOI> list = null;

        try
        {
            /* Create a URL we want to load some xml-data from. */
            URL url = new URL( buildUrl( lat , lon , maxPOIs));

            /* Get a SAXParser from the SAXPArserFactory. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
            XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader*/
            TagParser parser = new TagParser();
            xr.setContentHandler(parser);

            xr.parse(new InputSource(url.openStream()));
            list = parser.getGenericPOIs();

        } catch (Exception e)
        {
            Log.e("ARTags", "POIService", e);
        }

        return list;
    }

    public static String buildUrl(double lat, double lon, int maxPOIs )
    {
        String url = Security.URL_TAGS;

        url += "&lat=" + lat;
        url += "&lon=" + lon;
        url += "&max=" + maxPOIs;

        return url;
    }
}
