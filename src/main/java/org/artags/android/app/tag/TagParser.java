/* Copyright (c) 2010-2011 ARTags Project owners (see http://www.artags.org)
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
package org.artags.android.app.tag;

import org.artags.android.app.ar.GenericPOI;
import java.util.ArrayList;
import java.util.List;
import org.artags.android.app.Security;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Pierre Levy
 */
public class TagParser extends DefaultHandler
{

    private static final String TAG = "tag";
    private static final String ID = "id";
    private static final String TITLE = "name";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
    private static final String THUMBNAIL = "thumbnail-id";
    private static final String DATE = "date";
    private static final String RATING = "rating";
    private List<GenericPOI> GenericPOIs;
    private GenericPOI currentGenericPOI;
    private StringBuilder builder;

    /**
     * Gest the list of POI
     * @return The list
     */
    public List<GenericPOI> getGenericPOIs()
    {
        return this.GenericPOIs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException
    {
        super.endElement(uri, localName, name);
        if (this.currentGenericPOI != null)
        {
            if (localName.equalsIgnoreCase(ID))
            {
                String id = builder.toString();
                currentGenericPOI.setUrl(Security.URL_TAG + id);
                currentGenericPOI.setImageUrl(Security.URL_IMAGE + id);
            } else if (localName.equalsIgnoreCase(TITLE))
            {
                currentGenericPOI.setName(builder.toString());
            } else if (localName.equalsIgnoreCase(DATE))
            {
                currentGenericPOI.setDate(builder.toString());
            } else if (localName.equalsIgnoreCase(RATING))
            {
                currentGenericPOI.setRating(builder.toString());
            } else if (localName.equalsIgnoreCase(LATITUDE))
            {
                currentGenericPOI.setLatitude(Double.parseDouble(builder.toString()));
            } else if (localName.equalsIgnoreCase(LONGITUDE))
            {
                currentGenericPOI.setLongitude(Double.parseDouble(builder.toString()));
            } else if (localName.equalsIgnoreCase(THUMBNAIL))
            {
                currentGenericPOI.setIconUrl(Security.URL_ICON + builder.toString());
            } else if (localName.equalsIgnoreCase(TAG))
            {
                GenericPOIs.add(currentGenericPOI);
            }
            builder.setLength(0);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        GenericPOIs = new ArrayList<GenericPOI>();
        builder = new StringBuilder();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, name, attributes);
        if (localName.equalsIgnoreCase(TAG))
        {
            this.currentGenericPOI = new GenericPOI();
        }
    }
}
