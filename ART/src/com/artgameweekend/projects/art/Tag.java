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

/**
 *
 * @author pierre
 */
public class Tag {

    private String title;
    private String filename;
    private String latitude;
    private String longitude;

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the filename
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * @return the latitude
     */
    public String getLatitude()
    {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public String getLongitude()
    {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }
    

}
