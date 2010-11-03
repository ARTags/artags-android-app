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
package org.artags.android.app.menu;

import android.graphics.drawable.Drawable;
import android.view.View.OnClickListener;

/**
 * Action item, displayed as menu with icon and text.
 *
 * @author Lorensius. W. L. T
 *
 */
public class ActionItem
{

    private Drawable icon;
    private String title;
    private OnClickListener listener;

    /**
     * Constructor
     */
    public ActionItem()
    {
    }

    /**
     * Constructor
     *
     * @param icon {@link Drawable} action icon
     */
    public ActionItem(Drawable icon)
    {
        this.icon = icon;
    }

    public ActionItem(String title, Drawable icon, OnClickListener listener)
    {
        this.icon = icon;
        this.title = title;
        this.listener = listener;
    }

    /**
     * Set action title
     *
     * @param title action title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Get action title
     *
     * @return action title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Set action icon
     *
     * @param icon {@link Drawable} action icon
     */
    public void setIcon(Drawable icon)
    {
        this.icon = icon;
    }

    /**
     * Get action icon
     * @return  {@link Drawable} action icon
     */
    public Drawable getIcon()
    {
        return this.icon;
    }

    /**
     * Set on click listener
     *
     * @param listener on click listener {@link View.OnClickListener}
     */
    public void setOnClickListener(OnClickListener listener)
    {
        this.listener = listener;
    }

    /**
     * Get on click listener
     *
     * @return on click listener {@link View.OnClickListener}
     */
    public OnClickListener getListener()
    {
        return this.listener;
    }
}
