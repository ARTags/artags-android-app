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
package com.artgameweekend.projects.art.tag;

import com.artgameweekend.projects.art.ar.GenericPOI;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author pierre@androidsoft.org
 */
public class TagParser extends DefaultHandler
{
    private static final String TAG = "tag";
    private static final String URL = "http://art-server.appspot.com/display?id=";
    private static final String ID = "id";
    private static final String TITLE = "name";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    private List<GenericPOI> GenericPOIs;
    private GenericPOI currentGenericPOI;
    private StringBuilder builder;

    public List<GenericPOI> getGenericPOIs(){
        return this.GenericPOIs;
    }
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri, localName, name);
        if (this.currentGenericPOI != null){
            if (localName.equalsIgnoreCase(ID)){
                currentGenericPOI.setUrl( URL + builder.toString());
                currentGenericPOI.setIconUrl( URL + builder.toString());
            } else if (localName.equalsIgnoreCase(TITLE)){
                currentGenericPOI.setName(builder.toString());
            } else if (localName.equalsIgnoreCase(LATITUDE)){
                currentGenericPOI.setLatitude( Double.parseDouble(builder.toString()));
            } else if (localName.equalsIgnoreCase(LONGITUDE)){
                currentGenericPOI.setLongitude(Double.parseDouble(builder.toString()));
            } else if (localName.equalsIgnoreCase(TAG)){
                GenericPOIs.add(currentGenericPOI);
            }
            builder.setLength(0);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        GenericPOIs = new ArrayList<GenericPOI>();
        builder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        if (localName.equalsIgnoreCase(TAG)){
            this.currentGenericPOI = new GenericPOI();
        }
    }
}


