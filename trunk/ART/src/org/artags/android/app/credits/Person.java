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

package org.artags.android.app.credits;

import android.graphics.Paint;

/**
 *
 * @author Pierre Levy
 */
public class Person extends AbstractTextItem implements CreditsItem
{
    private static Paint mPaint;
    private static int mBeforeSpacing;
    private static int mAfterSpacing;

    /**
     * Constructor
     * @param name The name
     */
    public Person( String name )
    {
        mText = name;
    }

    /**
     * Sets the painter to draw text
     * @param paint The painter
     */
    public static void setPaint( Paint paint )
    {
        mPaint = paint;
    }

    /**
     * Gets the painter
     * @return The painter
     */
    public Paint getPaint()
    {
        return mPaint;
    }

    /**
     * Sets the spacing
     * @param before before spacing
     * @param after after spacing
     */
    public static void setSpacings( int before , int after )
    {
        mBeforeSpacing = before;
        mAfterSpacing = after;
    }

    /**
     * {@inheritDoc }
     */
    public int getBeforeSpacing()
    {
        return mBeforeSpacing;
    }

    /**
     * {@inheritDoc }
     */
    public int getAfterSpacing()
    {
        return mAfterSpacing;
    }
}
