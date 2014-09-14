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
package org.artags.android.app.widget;

import android.graphics.Bitmap;

/**
 *
 * @author Pierre Levy
 */
public class Tag
{
    private String id;
    private int thumbnailId;
    private String thumbnailUrl;
    private String text;
    private Bitmap bitmap;
    private String rating;

    /**
     * Constructor
     */
    public Tag()
    {
        
    }
    
    /**
     * Constructor
     * @param id The ID
     * @param text The text
     */
    public Tag( int id , String text)
    {
        this.thumbnailId = id;
        this.text = text;
    }
    
    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * @return the bitmap
     */
    public Bitmap getBitmap()
    {
        return bitmap;
    }

    /**
     * @param bitmap the bitmap to set
     */
    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    /**
     * @return the thumbnailId
     */
    public int getThumbnailId()
    {
        return thumbnailId;
    }

    /**
     * @param thumbnailId the thumbnailId to set
     */
    public void setThumbnailId(int thumbnailId)
    {
        this.thumbnailId = thumbnailId;
    }

    /**
     * @return the thumbnailUrl
     */
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    /**
     * @param thumbnailUrl the thumbnailUrl to set
     */
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the rating
     */
    public String getRating()
    {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(String rating)
    {
        this.rating = rating;
    }
    
    
}
