/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art.deprecated;

import com.artgameweekend.projects.art.deprecated.FlickrParser;
import android.util.Log;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.openintents.intents.WikitudePOI;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author Android
 */
public class MyPOIs {

    private static final String api_key = "1e3327bffcb66bce29221be81bfdb559";

    public static List<WikitudePOI> getPOIs() {
        List<WikitudePOI> list = new ArrayList<WikitudePOI>();
        /*
         WikitudePOI poi = new WikitudePOI(48.844779, 2.326398, 0, null, "Description de Test", "http://google.fr", null, "http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg", ".MainActivity");
        poi.setIconuri("http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg");



        poi.setDetailAction("wikitudeapi.SHOWIMAGE");

        list.add(poi);

        poi = new WikitudePOI(48.864579, 2.326298, 0, "Icon de Test 2", "Description de Test 2", "http://google.fr", null, "http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg", ".MainActivity");
        poi.setIconuri("http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg");
        poi.setDetailAction("wikitudeapi.SHOWIMAGE");
        //48.844779,2.326398
        list.add(poi);
*/


        try {
            /* Create a URL we want to load some xml-data from. */
            URL url = new URL(MyPOIs.forgeUrl(48.870216, 2.342091));

            /* Get a SAXParser from the SAXPArserFactory. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
            XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader*/
            FlickrParser myExampleHandler = new FlickrParser();
            xr.setContentHandler(myExampleHandler);

            /* Parse the xml-data from our URL. */
            xr.parse(new InputSource(url.openStream()));
            /* Parsing has finished. */

            /* Our ExampleHandler now provides the parsed data to us. */

            list = myExampleHandler.getParsedData();

            /* Set the result to be displayed in our GUI. */
            //tv.setText(parsedExampleDataSet.toString());

        } catch (Exception e) {
            /* Display any Error to the GUI. */
            //tv.setText("Error: " + e.getMessage());
            Log.e("OMGWTFBBQ", "WeatherQueryError", e);
        }




        return list;
    }

    public static String forgeUrl(Double lat, Double lon) {
        /*
        http://api.flickr.com/services/rest/?
         method=flickr.photos.search
         &api_key=78d13dc34f7a5ce0e63a7bf4d87033b8
         &user_id=50633954%40N02
         &media=photo
         &lat=48.864375
         &lon=2.326869
         &radius=10
         &radius_units=km
         &extras=geo%2C+url_sq
        */
        String url = "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + MyPOIs.api_key;

        url += "&user_id=50633954%40N02";
        url += "&media=photo";
        url += "&lat=" + lat;
        url += "&lon=" + lon;
        url += "&radius=1";
        url += "&radius_units=km";
        url += "&extras=geo%2C+url_sq";
        
        return url;
    }
}
