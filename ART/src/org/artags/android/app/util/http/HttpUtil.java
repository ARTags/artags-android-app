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
package org.artags.android.app.util.http;

import android.util.Log;
import java.io.File;
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
 * @author pierre
 */
public class HttpUtil
{

    public static void post(String sUrl, HashMap<String, String> params, File file, String fileParam ) throws HttpException
    {

        try
        {
            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(sUrl);
            FileBody bin = new FileBody(file);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart( fileParam, bin);

            for (String key : params.keySet())
            {
                String val = params.get(key);

                reqEntity.addPart(key, new StringBody(val));
            }
            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);

            HttpEntity resEntity = response.getEntity();
            if (resEntity != null)
            {
                Log.i("RESPONSE", EntityUtils.toString(resEntity));
            }

            //return response;
        } catch (Exception e)
        {
            Log.e("ART:HttpUtil", "Error : " + e.getMessage());
            throw new HttpException( e.getMessage() );
        }
    }
}
