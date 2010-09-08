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
package org.artags.android.app;

import java.util.List;

import org.openintents.intents.WikitudePOI;

import android.app.Application;

/**
 * The application object which holds information about all objects needed to be
 * exchanged in the application
 *
 * @author Martin Lechner, Mobilizy GmbH., martin.lechner@mobilizy.com
 *
 */
public class ARtApplication extends Application
{

    /** the POIs */
    private List<WikitudePOI> pois;

    public List<WikitudePOI> getPois()
    {
        return pois;
    }

    public void setPois(List<WikitudePOI> pois)
    {
        this.pois = pois;
    }
}
