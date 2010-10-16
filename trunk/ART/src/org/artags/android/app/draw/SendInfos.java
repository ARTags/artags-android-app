/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
}
