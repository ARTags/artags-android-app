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
package org.artags.android.app.draw;

import android.graphics.MaskFilter;

/**
 *
 * @author Pierre Levy
 */
public class BrushParameters
{

    private int brushSize;
    private int color;
    private int opacity;
    private boolean blur;
    private boolean emboss;
    private int colorBase;
    private int colorIntensity;
    private MaskFilter embossFilter;
    private MaskFilter blurFilter;

    /**
     * @return the brushSize
     */
    public int getBrushSize()
    {
        return brushSize;
    }

    /**
     * @param brushSize the brushSize to set
     */
    public void setBrushSize(int brushSize)
    {
        this.brushSize = brushSize;
    }

    /**
     * @return the color
     */
    public int getColor()
    {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(int color)
    {
        this.color = color;
    }

    /**
     * @return the blur
     */
    public boolean isBlur()
    {
        return blur;
    }

    /**
     * @param blur the blur to set
     */
    public void setBlur(boolean blur)
    {
        this.blur = blur;
    }

    /**
     * @return the emboss
     */
    public boolean isEmboss()
    {
        return emboss;
    }

    /**
     * @param emboss the emboss to set
     */
    public void setEmboss(boolean emboss)
    {
        this.emboss = emboss;
    }

    /**
     * @return the colorBase
     */
    public int getColorBase()
    {
        return colorBase;
    }

    /**
     * @param colorBase the colorBase to set
     */
    public void setColorBase(int colorBase)
    {
        this.colorBase = colorBase;
    }

    /**
     * @return the colorIntensity
     */
    public int getColorIntensity()
    {
        return colorIntensity;
    }

    /**
     * @param colorIntensity the colorIntensity to set
     */
    public void setColorIntensity(int colorIntensity)
    {
        this.colorIntensity = colorIntensity;
    }

    /**
     * @return the embossFilter
     */
    public MaskFilter getEmbossFilter()
    {
        return embossFilter;
    }

    /**
     * @param embossFilter the embossFilter to set
     */
    public void setEmbossFilter(MaskFilter embossFilter)
    {
        this.embossFilter = embossFilter;
    }

    /**
     * @return the blurFilter
     */
    public MaskFilter getBlurFilter()
    {
        return blurFilter;
    }

    /**
     * @param blurFilter the blurFilter to set
     */
    public void setBlurFilter(MaskFilter blurFilter)
    {
        this.blurFilter = blurFilter;
    }

    /**
     * @return the opacity
     */
    public int getOpacity()
    {
        return opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void setOpacity(int opacity)
    {
        this.opacity = opacity;
    }
}
