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
package org.artags.android.app.ar.layar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import org.artags.android.app.ar.BrowserHandler;

/**
 *
 * @author Pierre Levy
 */
public class LayarBrowserHandler implements BrowserHandler
{

    private static final String URI_LAYAR = "layar://artags";

    /**
     * {@inheritDoc }
     */
    public String getBrowserKey()
    {
        return "layar";
    }

    /**
     * {@inheritDoc }
     */
    public String getBrowserDescription()
    {
        return "Layar";
    }

    /**
     * {@inheritDoc }
     */
    public void startBrowser(Activity activity)
    {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URI_LAYAR));
            activity.startActivity(intent);
    }

    /**
     * {@inheritDoc }
     */

    public String getPackageName()
    {
        return "com.layar";
    }

}
