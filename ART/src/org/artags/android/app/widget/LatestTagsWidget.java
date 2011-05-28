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

/**
 *
 * @author Pierre Levy
 */
public class LatestTagsWidget extends AbstractWidgetProvider
{
    private static Tag mTag;
    @Override
    String getTagListUrl()
    {
        return Constants.URL_LATEST_TAGS;
    }

    @Override
    void setCurrentTag(Tag tag)
    {
        mTag = tag;
    }

    @Override
    Tag getCurrentTag()
    {
        return mTag;
    }
 
}
