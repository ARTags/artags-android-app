/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.artgameweekend.projects.art;

/**
 *
 * @author pierre
 */
public class GenericPOI {
    private String name;
    private String description;
    private String url;
    private double latitude;
    private double longitude;
    private double altitude;
    private String iconUrl;

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
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
     * @return the iconUrl
     */
    public String getIconUrl()
    {
        return iconUrl;
    }

    /**
     * @param iconUrl the iconUrl to set
     */
    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    /**
     * @return the altitude
     */
    public double getAltitude()
    {
        return altitude;
    }

    /**
     * @param altitude the altitude to set
     */
    public void setAltitude(double altitude)
    {
        this.altitude = altitude;
    }


}
