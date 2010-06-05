/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.artgameweekend.projects.art;

import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import org.openintents.intents.WikitudePOI;

/**
 *
 * @author Android
 */
public class MyPOIs {
    public static List<WikitudePOI> getPOIs(){
        List<WikitudePOI> list = new ArrayList<WikitudePOI>();
        WikitudePOI poi = new WikitudePOI(48.844779, 2.326398, 0, null, "Description de Test", "http://google.fr", null, "http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg", ".MainActivity");
        poi.setIconuri("http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg");
        
        
        Intent intent = new Intent();
        intent.setClassName("com.artgameweekend.projects.art", "com.artgameweekend.projects.art.FingerPaint");
        
        poi.setDetailAction(intent.getAction());

        list.add(poi);

        poi = new WikitudePOI(48.864579, 2.326298, 0, "Icon de Test 2", "Description de Test 2", "http://google.fr", null, "http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg", ".MainActivity");
        poi.setIconuri("http://pics.homere.jmsp.net/t_15/64x64/040119_tag41.jpg");
        poi.setDetailAction(intent.getAction());
        //48.844779,2.326398
        list.add(poi);
        return list;
    }
}
