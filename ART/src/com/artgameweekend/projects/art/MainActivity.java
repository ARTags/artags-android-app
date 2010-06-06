/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.artgameweekend.projects.art;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import com.zmosoft.flickrfree.APICalls;
import com.zmosoft.flickrfree.JSONParser;
import com.zmosoft.flickrfree.JavaMD5Sum;
import com.zmosoft.flickrfree.RestClient;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.List;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.openintents.intents.WikitudeARIntent;
import org.openintents.intents.WikitudePOI;

/**
 *
 * @author pierre
 */
public class MainActivity extends Activity implements OnClickListener {

    ImageButton mButtonDraw;
    ImageButton mButtonDisplay;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ;

        setContentView(R.layout.main);
        mButtonDraw = (ImageButton) findViewById(R.id.button_draw);
        mButtonDisplay = (ImageButton) findViewById(R.id.button_display);

        mButtonDraw.setOnClickListener(this);
        mButtonDisplay.setOnClickListener(this);

    }

    public void onClick(View view) {
        if (view == mButtonDraw) {
            Log.d("MainActivity", "onClick:DRAW");
            Intent intent = new Intent();
            intent.setClassName("com.artgameweekend.projects.art", "com.artgameweekend.projects.art.FingerPaint");
            startActivity(intent);
        } else if (view == mButtonDisplay) {
            if (RestClient.m_frob.equals("")) {
                Boolean bo = APICalls.setFrob();
                Intent intent = new Intent();
                intent.setClassName("com.artgameweekend.projects.art", "com.artgameweekend.projects.art.WebViewActivity");
                startActivity(intent);
            } else {


                JSONObject js = APICalls.getToken();
                Log.i("OMHWTFBBQ", js.toString());
                String token = JSONParser.getString(js, "auth/token/_content");

                String signature = "";
                signature = RestClient.m_secret;
//auth_token=9765984

                String description = "coucou description";
                String title = "Super Titre";

                signature += "api_key" + RestClient.m_apikey + "auth_token" + token + "description"+description+"title"+title;

                try {
                    signature = JavaMD5Sum.computeSum(signature).toLowerCase();
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                }
                File file = new File(Environment.getExternalStorageDirectory(), "art.jpg");
                
                
                Hashtable<String, String> params = new Hashtable<String, String>();
                params.put("api_key", RestClient.m_apikey);
                params.put("auth_token", token);
                params.put("description", description);
                params.put("title", title);
                params.put("api_sig", signature);

                
                Log.i("OMGWTFBBQPath", file.getPath());
                Log.i("OMGWTFBBQAbsPath", file.getAbsolutePath());
                Log.i("OMGWTFBBQSize", Long.toString(file.length()));

                MyHttpRequest req = new MyHttpRequest();

                req.post("http://api.flickr.com/services/upload/", params, file);
//HttpData data = req.post("http://lutece-cloud.appspot.com/art.jsp", params, file);

                //Log.i("OMGWTFBBQDATA", (data.content!= null?data.content:"NULLCONTENT"));


                //RestClient.UploadPicture(file, title, description, "");
            }

            if (true) {
                return;
            }
            WikitudeARIntent intent = new WikitudeARIntent(this.getApplication(), null, "507419D8685F116E0AB61704F21734D0", "art");
            //intent.addTitleText("titleText");
            //intent.addTitleImageUri("http://www.clubtone.net/avatar/58/191139.png");

            intent.setPrintMarkerSubText(false);


            List<WikitudePOI> list = MyPOIs.getPOIs();
            intent.addPOIs(list);
            //WikitudePOI poi = new WikitudePOI();
            try {
                intent.startIntent(this);
            } catch (ActivityNotFoundException e) {
                WikitudeARIntent.handleWikitudeNotFound(this);
            }
        }
    }
}
