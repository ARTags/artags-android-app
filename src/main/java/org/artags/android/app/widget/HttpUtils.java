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
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Pierre Levy
 */
public class HttpUtils
{

    private static final String TAG = "ARTags HttpUtils";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
    private static final int TIMEOUT_IN_MILLIS = 20000;
    
    /**
     * Gets an url content
     * @param url The url
     * @return The content
     */
    public static String getUrl(String url)
    {
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();

        HttpGet httpget = new HttpGet(url);
        HttpResponse response;

        try
        {
            response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
            }
        } catch (IOException ex)
        {
            Log.e(HttpUtils.class.getName(), ex.getMessage());
        }
        return result;
    }

    private static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
        } catch (IOException e)
        {
            Log.e(HttpUtils.class.getName(), e.getMessage());
        } finally
        {
            try
            {
                is.close();
            } catch (IOException e)
            {
                Log.e(HttpUtils.class.getName(), e.getMessage());
            }
        }
        return sb.toString();
    }


    /**
     * Load a bitmap
     * @param url The URL
     * @return The bitmap
     */
    public static Bitmap loadBitmap(String url)
    {
        try
        {
            final HttpClient httpClient = getHttpClient();
            final HttpResponse resp = httpClient.execute(new HttpGet(url));
            final HttpEntity entity = resp.getEntity();

            final int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK || entity == null)
            {
                return null;
            }

            final byte[] respBytes = EntityUtils.toByteArray(entity);
            // Decode the bytes and return the bitmap.
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inSampleSize = 1;
            return BitmapFactory.decodeByteArray(respBytes, 0, respBytes.length, decodeOptions);
        } catch (Exception e)
        {
            Log.w(TAG, "Problem while loading image: " + e.toString(), e);
        }
        return null;

    }

    /**
     * Generate and return a {@link HttpClient} configured for general use,
     * including setting an application-specific user-agent string.
     * @return 
     */
    public static HttpClient getHttpClient()
    {
        final HttpParams params = new BasicHttpParams();

        // Use generous timeouts for slow mobile networks
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_IN_MILLIS);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT_IN_MILLIS);

        HttpConnectionParams.setSocketBufferSize(params, 8192);

        final DefaultHttpClient client = new DefaultHttpClient(params);

        client.addRequestInterceptor(new HttpRequestInterceptor()
        {

            public void process(HttpRequest request, HttpContext context)
            {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING))
                {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor()
        {

            public void process(HttpResponse response, HttpContext context)
            {
                // Inflate any responses compressed with gzip
                final HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null)
                {
                    for (HeaderElement element : encoding.getElements())
                    {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP))
                        {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        return client;
    }
    

    /**
     * Simple {@link HttpEntityWrapper} that inflates the wrapped
     * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
     */
    private static class InflatingEntity extends HttpEntityWrapper
    {

        public InflatingEntity(HttpEntity wrapped)
        {
            super(wrapped);
        }
    }
}
