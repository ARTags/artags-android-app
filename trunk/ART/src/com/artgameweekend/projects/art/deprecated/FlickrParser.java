/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.artgameweekend.projects.art.deprecated;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.openintents.intents.WikitudePOI;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author Android
 */
public class FlickrParser extends org.xml.sax.helpers.DefaultHandler{
    List<WikitudePOI> list = null;
    WikitudePOI tmpPOI = null;
    Boolean fail = false;
    
    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException {
        /*
        <rsp stat="fail">
            <err code="2" msg="Unknown user"/>
        </rsp>
         */
        /*
        <rsp stat="ok">
            <photos page="1" pages="1" perpage="100" total="1">
                <photo
                    id="4671924715"
                    owner="50633954@N02"
                    secret="1b440bff2e"
                    server="4058"
                    farm="5"
                    title="2010-06-05 20.26.32"
                    ispublic="1"
                    isfriend="0"
                    isfamily="0"
                    latitude="48.870216"
                    longitude="2.342091"
                    accuracy="16"
                    place_id="G1oWa7ycBJSxFnGPsw"
                    woeid="55843756"
                    geo_is_family="0"
                    geo_is_friend="0"
                    geo_is_contact="0"
                    geo_is_public="1"
                    url_sq="http://farm5.static.flickr.com/4058/4671924715_1b440bff2e_s.jpg"
                    height_sq="75"
                    width_sq="75"/>
            </photos>
        </rsp>
         *
         * http://www.flickr.com/photos/50633954@N02/4671924715
         * http://farm5.static.flickr.com/4058/4671924715_1b440bff2e_b_d.jpg
         */
        if(fail)
            return;
        if(localName.equalsIgnoreCase("rsp")) {
            if(atts.getValue("stat").equalsIgnoreCase("fail"))
                fail = true;
        }
        else if (localName.equalsIgnoreCase("photo"))
        {
            float lon = new Float(0);
            float lat = new Float(0);
            float alt = new Float(0);
            String name = "";
            String desc = "";
            String url = "http://www.flickr.com/photos/50633954@N02/4671924715";
            String iconUrl = "http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg";

            Log.i("OMGWTFBBQ", atts.getValue("longitude"));
            Log.i("OMGWTFBBQ", atts.getValue("latitude"));
            lon = Float.parseFloat(atts.getValue("longitude"));
            lat = Float.parseFloat(atts.getValue("latitude"));

            name = atts.getValue("title");
            iconUrl = atts.getValue("url_sq");
            url = "http://www.flickr.com/photos/"+atts.getValue("owner")+"/"+atts.getValue("id");

            WikitudePOI poi = new WikitudePOI(lat, lon, alt, name, desc, url, null, iconUrl, "wikitudeapi.SHOWIMAGE");
            poi.setIconuri(iconUrl);
            poi.setDetailAction("wikitudeapi.SHOWIMAGE");
            list.add(poi);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException {
    }

    /** Gets be called on the following structure:
    * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) {
        //String textBetween = new String(ch, start, length);

    }

    @Override
    public void startDocument() throws SAXException {
                // Do some startup if needed
        list = new ArrayList<WikitudePOI>();
    }

    public List<WikitudePOI> getParsedData()
    {
        return this.list;
    }
}
