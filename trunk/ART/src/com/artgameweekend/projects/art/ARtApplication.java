/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.artgameweekend.projects.art;

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
public class ARtApplication extends Application {

	/** the POIs */
	private List<WikitudePOI> pois;

	public List<WikitudePOI> getPois() {
		return pois;
	}

	public void setPois(List<WikitudePOI> pois) {
		this.pois = pois;
	}

}
