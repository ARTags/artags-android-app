/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art.deprecated;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.artgameweekend.projects.art.MyHttpRequest;
import com.zmosoft.flickrfree.APICalls;
import com.zmosoft.flickrfree.JSONParser;
import com.zmosoft.flickrfree.JavaMD5Sum;
import com.zmosoft.flickrfree.RestClient;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import org.json.JSONObject;

/**
 *
 * @author Android
 */
public class FlickrUploader {

    public static void authentication(Context con) {
        Boolean bo = APICalls.setFrob();
        Intent intent = new Intent();
        intent.setClassName("com.artgameweekend.projects.art", "com.artgameweekend.projects.art.WebViewActivity");
        con.startActivity(intent);
    }

    public static void uploadFile() {
        JSONObject js = APICalls.getToken();
        Log.i("OMHWTFBBQ", js.toString());
        String token = JSONParser.getString(js, "auth/token/_content");

        String signature = "";
        signature = RestClient.m_secret;
//auth_token=9765984

        String description = "";
        String title = "";

        signature += "api_key" + RestClient.m_apikey + "auth_token" + token + "description" + description + "title" + title;

        try {
            signature = JavaMD5Sum.computeSum(signature).toLowerCase();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        File file = new File(Environment.getExternalStorageDirectory(), "/ARt/ARt.jpeg");


        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("api_key", RestClient.m_apikey);
        params.put("auth_token", token);
        params.put("description", description);
        params.put("title", title);
        params.put("api_sig", signature);


        Log.i("OMGWTFBBQPath", file.getPath());
        Log.i("OMGWTFBBQAbsPath", file.getAbsolutePath());
        Log.i("OMGWTFBBQSize", Long.toString(file.length()));

        //MyHttpRequest req = new MyHttpRequest();

        MyHttpRequest.post("http://api.flickr.com/services/upload/", params, file);
//HttpData data = req.post("http://lutece-cloud.appspot.com/art.jsp", params, file);

        //Log.i("OMGWTFBBQDATA", (data.content!= null?data.content:"NULLCONTENT"));


        //RestClient.UploadPicture(file, title, description, "");
        RestClient.m_frob = "";
    }



public void testAPI()
    {

        /*boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        ArrayList<File> files = new ArrayList<File>();
        files.add(new File(Environment.getExternalStorageDirectory(), "art.jpg"));

        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("api_key", "1e3327bffcb66bce29221be81bfdb559");
        params.put("perms", "write");
        //http://flickr.com/services/auth/?api_key=1e3327bffcb66bce29221be81bfdb559&perms=write&frob=72157624211614412-9c23800732265494-8733&api_sig=a586c119a00e03acda7f6d5f5d1ca725

        MyHttpRequest req = new MyHttpRequest();

        //HttpData data = req.post("http://api.flickr.com/services/upload/", null, null);
       */

    }
}
