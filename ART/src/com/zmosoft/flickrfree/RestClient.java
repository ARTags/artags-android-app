package com.zmosoft.flickrfree;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import java.io.DataOutputStream;
import java.io.FileInputStream;

public class RestClient {

    public static String m_apikey = "1e3327bffcb66bce29221be81bfdb559";
    public static String m_secret = "ac1cdb9c5f261593";
    public static String m_fulltoken = "";
    public static String m_frob = "";
    private static String m_UPLOADURL = "http://api.flickr.com/services/upload/";


    public static void setAuth(Activity activity) {
        m_apikey = "1e3327bffcb66bce29221be81bfdb559";
        m_secret = "ac1cdb9c5f261593";
        m_fulltoken = activity.getSharedPreferences("Auth",0).getString("full_token", "");
    }
    
    private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static JSONObject UploadPicture(File file, String title, String description, String tags){//,
									 //boolean is_public, boolean is_friend, boolean is_family,
									 //int safety_level){
		/*String safety = "1";
		if (safety_level > 0 && safety_level < 4) {
			safety = Integer.toString(safety_level);
		}*/
		Vector<String> pNames = new Vector<String>();
		Vector<String> pVals = new Vector<String>();

		pNames.add("photo");
		pVals.add("");
		if (!title.equals("")) {
			pNames.add("title");
			pVals.add(title);
		}
		if (!description.equals("")) {
			pNames.add("description");
			pVals.add(description);
		}
		if (!tags.equals("")) {
			pNames.add("tags");
			pVals.add(tags);
		}
		/*pNames.add("is_public");
		pVals.add(is_public ? "1" : "0");
		pNames.add("is_friend");
		pVals.add(is_friend ? "1" : "0");
		pNames.add("is_family");
		pVals.add(is_family ? "1" : "0");
		pNames.add("safety_level");
		pVals.add(safety);
		pNames.add("content_type");
		pVals.add("1");
		pNames.add("hidden");
		pVals.add("1");*/

		String [] paramNames, paramVals;
		paramNames = paramVals = new String[]{};
		paramNames = pNames.toArray(paramNames);
		paramVals = pVals.toArray(paramVals);
		
		return CallFunction("", paramNames, paramVals, true, true, file);
	}
	
	public static JSONObject CallFunction(String methodName, String[] paramNames, String[] paramVals)
	{
		return CallFunction(methodName, paramNames, paramVals, true, false, null);
	}
	
	public static JSONObject CallFunction(String methodName, String[] paramNames, String[] paramVals, boolean authenticated)
	{
		return CallFunction(methodName, paramNames, paramVals, authenticated, false, null);
	}
	
	public static JSONObject CallFunction(String methodName, String[] paramNames, String[] paramVals,
                        boolean authenticated, boolean ispost, File file)
	{
		JSONObject json = new JSONObject();
		HttpClient httpclient = new DefaultHttpClient();
	    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

	    /*File file = null;
    	if (ispost && !filename.equals("")) {
    		file = new File(filename);
    	}*/
	    
		if (paramNames == null) {
			paramNames = new String[0];
		}
		if (paramVals == null) {
			paramVals = new String[0];
		}
		
		if (paramNames.length != paramVals.length) {
			return json;
		}

		String url;
		if (ispost) {
			url = m_UPLOADURL;
		}
		else {
			url = m_RESTURL + "?method=" + methodName;
		}
		
		url += "&api_key=" + m_apikey;
		for (int i = 0; i < paramNames.length; i++) {
			if (ispost && paramNames[i].equals("photo") && file != null) {
				//TODO: Replace the final "" in this line with the binary photo
				//      data that will be uploaded.
                            try {
                                url += "&" + paramNames[i] + "=";
                        int bytesAvailable;
                        int bufferSize;
                        int maxBufferSize = 4096;
                        int bytesRead;
                            FileInputStream fis = new FileInputStream(file);
                            bytesAvailable = fis.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                byte[] buffer = new byte[bufferSize];
                                bytesRead = fis.read(buffer, 0, bufferSize);
                                //DataOutputStream dos;
                                Log.i("OMGWTFBBQ", "On commence la boucle");
                                while (bytesRead > 0) {
                                    url += buffer;
                                        //dos.write(buffer, 0, bufferSize);
                                        bytesAvailable = fis.available();
                                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                        bytesRead = fis.read(buffer, 0, bufferSize);
                                }
                                Log.i("OMGWTFBBQ", "Sortie de la boucle");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
				//url += "&" + paramNames[i] + "=" + "";
			}
			else {
				url += "&" + paramNames[i] + "=" + paramVals[i];
			}
		}
		
		authenticated = authenticated && !m_fulltoken.equals("");
		if (authenticated) {
			url += "&auth_token=" + m_fulltoken;
		}
		
		// Generate the signature
		String signature = "";
		SortedMap<String,String> sig_params = new TreeMap<String,String>();
		sig_params.put("api_key", m_apikey);
		if (!ispost) {
			sig_params.put("method", methodName);
		}
		sig_params.put("format", "json");
		for (int i = 0; i < paramNames.length; i++) {
			if (!ispost || !paramNames[i].equals("photo")) {
				sig_params.put(paramNames[i],paramVals[i]);
			}
		}
		if (authenticated) {
			sig_params.put("auth_token",m_fulltoken);
		}
		signature = m_secret;
		for (Map.Entry<String,String> entry : sig_params.entrySet()) {
			signature += entry.getKey() + entry.getValue();
		}		
		try {
			signature = JavaMD5Sum.computeSum(signature).toLowerCase();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		url += "&api_sig=" + signature + "&format=json";

		// Replace any spaces in the URL with "+".
		url = url.replace(" ", "+");
	Log.i("OMGWTFBBQ", url);
		HttpResponse response = null;

		try {
			// Prepare a request object
			if (ispost) {
			    HttpPost httppost = new HttpPost(m_UPLOADURL);
			    FileEntity entity = new FileEntity(file, "binary/octet-stream");
			    httppost.setEntity(entity);
			    entity.setContentType("binary/octet-stream");
				response = httpclient.execute(httppost);
			}
			else {
				HttpGet httpget = new HttpGet(url);
				response = httpclient.execute(httpget);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			// Get hold of the response entity
			HttpEntity entity = null;
			if (response != null) {
				entity = response.getEntity();
			}

			// If the response does not enclose an entity, there is no need
			// to worry about connection release
			if (entity != null) {
				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				result = result.substring(result.indexOf("{"),result.lastIndexOf("}") + 1);
				// A Simple JSONObject Creation
				json = new JSONObject(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
	
    private static String m_RESTURL = "http://api.flickr.com/services/rest/";
}
