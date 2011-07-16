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
package org.artags.android.app.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import org.artags.android.app.apilevels.ApiLevel4;
import org.artags.android.app.apilevels.ApiLevels;

/**
 *
 * @author Pierre Levy
 */
public class BitmapUtil
{

    private static final String ROOT_DIRECTORY = "/ARTags";

    /**
     * Save the image as file
     * @param filename The file
     * @param bm The bitmap
     * @return The file path
     */
    public static String saveImage(String filename, Bitmap bm)
    {
        return BitmapUtil.saveImage(filename, bm, false);
    }

    /**
     * Save the image as file
     * @param filename The file
     * @param bm The bitmap
     * @param landscape The orientation
     * @return The file path
     */
    public static String saveImage(String filename, Bitmap bm, boolean landscape)
    {
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite())
        {
            if(landscape)
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            }
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
                Log.e("ARTags:BitmapUtil:saveImage", "Exception while writing the tag", e);
            }
        }
        else
        {
             Log.e("ARTags:BitmapUtil:saveImage", "Can't write on the volume");
        }
        return null;

    }

    /**
     * Load an image
     * @param filename The filename
     * @return The bitmap
     */
    public static Bitmap loadImage(String filename)
    {
        File root = Environment.getExternalStorageDirectory();
        try
        {
            File directory = new File(root.getPath() + ROOT_DIRECTORY);
            String filepath = directory.getPath() + "/" + filename;
            Options option = new Options();
            if(ApiLevels.getApiLevel()>=4)
            {
                //option.inScaled = false;
                option = ApiLevel4.setInScaled(option, false);
            }
            return BitmapFactory.decodeFile(filepath , option );


        } catch (Exception e)
        {
            Log.e("ARTags:BitmapUtil:saveImage", "Exception while loading the tag", e);
        }
        return null;
    }
}
