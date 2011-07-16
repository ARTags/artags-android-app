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
package org.artags.android.app.ar;

import android.app.Activity;

/**
 *
 * @author Pierre Levy
 */

public interface BrowserHandler {

    /**
     * Should return the handler key
     * @return The key
     */
    String getBrowserKey();


    /**
     * Should return the handler description
     * @return The description
     */
    String getBrowserDescription();

    /**
     * Start the application
     * @param activity The calling activity
     */
    void startBrowser( Activity activity );

    /**
     * Should return the package name to find the application on the market
     * @return The package name
     */
    String getPackageName();

}
