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
package org.artags.android.app.util.http;

import android.util.Log;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Pierre Levy
 */
public class HttpUtil
{

    /**
     * Post data and attachements
     * @param url The POST url
     * @param params Parameters
     * @param files Files
     * @return The return value
     * @throws HttpException If an error occurs
     */
    public static String post(String url, HashMap<String, String> params, HashMap<String, File> files) throws HttpException
    {
        String ret = "";
        try
        {
            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(url);


            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE );

            for (String key : files.keySet())
            {
                FileBody bin = new FileBody(files.get(key));
                reqEntity.addPart(key, bin);
            }

            for (String key : params.keySet())
            {
                String val = params.get(key);

                reqEntity.addPart(key, new StringBody(val , Charset.forName("UTF-8")));
            }
            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);

            HttpEntity resEntity = response.getEntity();
            if (resEntity != null)
            {
                ret = EntityUtils.toString(resEntity);
                Log.i("ARTags:HttpUtil:Post:Response", ret);
            }

            //return response;
        } catch (Exception e)
        {
            Log.e("ARTags:HttpUtil", "Error : " + e.getMessage());
            throw new HttpException(e.getMessage());
        }
        return ret;
    }
}
