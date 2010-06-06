/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.artgameweekend.projects.art;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author Android
 */
public class FlickrUploader {

    public void uploadFile(String path)
    {
        boolean mExternalStorageAvailable = false;
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
    }

    public void testAPI()
    {

        

    }
}
