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
package com.artgameweekend.projects.art.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @author pierre
 */
public class BitmapUtil
{

    private static final String ROOT_DIRECTORY = "/ARt";

    public static String saveImage(String filename, Bitmap bm)
    {
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite())
        {
            try
            {
                File directory = new File(root.getPath() + ROOT_DIRECTORY);
                if (!directory.exists())
                {
                    directory.mkdir();
                }
                String filepath = directory.getPath() + "/" + filename;
                File file = new File(filepath);
                FileOutputStream fos = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 0, fos);
                fos.close();

                return filepath;


            } catch (Exception e)
            {
                Log.e("ARt:BitmapUtil:saveImage", "Exception while writing the tag", e);
            }
        }
        return null;

    }

    public static Bitmap loadImage(String filename)
    {
        File root = Environment.getExternalStorageDirectory();
        try
        {
            File directory = new File(root.getPath() + ROOT_DIRECTORY);
            String filepath = directory.getPath() + "/" + filename;
            return BitmapFactory.decodeFile(filepath );


        } catch (Exception e)
        {
            Log.e("ARt:BitmapUtil:saveImage", "Exception while loading the tag", e);
        }
        return null;
    }
}
