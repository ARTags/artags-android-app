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

package org.artags.android.app.draw;

/**
 *
 * @author Pierre Levy
 */
public class SendInfos 
{
    private String title;
    private boolean landscape;
    private double latitude;
    private double longitude;
    private long tagId;

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
     * @return the landscape
     */
    public boolean isLandscape()
    {
        return landscape;
    }

    /**
     * @param landscape the landscape to set
     */
    public void setLandscape(boolean landscape)
    {
        this.landscape = landscape;
    }

    /**
     * @return the latitude
     */
    public double getLatitude()
    {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude()
    {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    /**
     * @return the tagId
     */
    public long getTagId()
    {
        return tagId;
    }

    /**
     * @param tagId the tagId to set
     */
    public void setTagId(long tagId)
    {
        this.tagId = tagId;
    }
}
