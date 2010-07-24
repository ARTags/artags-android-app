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
package com.artgameweekend.projects.art.tag;

import com.artgameweekend.projects.art.util.http.HttpException;
import com.artgameweekend.projects.art.util.http.HttpUtil;
import java.io.File;
import java.util.HashMap;

/**
 *
 * @author pierre
 */
public class TagUploadService
{

    private static final String PARAMETER_TITLE = "name";
    private static final String PARAMETER_LATITUDE = "lat";
    private static final String PARAMETER_LONGITUDE = "lon";
    private static final String PARAMETER_LANDSCAPE = "landscape";
    private static final String URL_UPLOAD_SERVER = "http://art-server.appspot.com/upload";

    public static void upload(Tag tag) throws HttpException
    {
        HashMap map = new HashMap();
        map.put(PARAMETER_TITLE, tag.getTitle());
        map.put(PARAMETER_LATITUDE, tag.getLatitude());
        map.put(PARAMETER_LONGITUDE, tag.getLongitude());
        if (tag.isLandscape())
        {
            map.put(PARAMETER_LANDSCAPE, "on");
        }
        File file = new File(tag.getFilename());
        HttpUtil.post(URL_UPLOAD_SERVER, map, file, "photo");

    }
}
