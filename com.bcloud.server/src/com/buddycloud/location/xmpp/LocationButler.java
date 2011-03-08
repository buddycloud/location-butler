
package com.buddycloud.location.xmpp;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.jabberstudio.jso.StreamException;

import com.buddycloud.common.xmpp.ComponentEventHandlerRegistry;
import com.buddycloud.common.xmpp.PacketHandler;
import com.buddycloud.common.xmpp.XmppComponent;
import com.buddycloud.nearby.xmpp.NearbyQueryHandler;


/**
 * This is the Location Butler. It is responsible for handling location-related
 * requests from the clients and delegating to dedicated handlers:
 * - Beacon logs are added to the database.
 * - Every time a user has logged a beacon, the butler triggers a scan through the logged
 *   history of that user, looking for a stable pattern of beacons, indicating that
 *   the user is stationary. If so, it looks in the table of defined locations to find
 *   a match for the beacon pattern. If this succeeds, the location presence of the 
 *   user is updated. If a location matching the pattern cannot be found, a request is 
 *   sent to the user to name his current whereabouts. If he/she chooses to do so, a new
 *   location is added to the locations table including the user provided name and 
 *   the pattern he just logged. The user may also choose to not name the location.
 * 
 * This class is the central point of the butler, where all Jabber packets are received.
 * These are queued to dedicated handlers which do the actual database interaction.
 * 
 * @author buddycloud
 * 
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class LocationButler extends XmppComponent {
	
	private Vector<PacketHandler> handlers;

	/** 
	 * Creates a new instance. 
	 */
	public LocationButler()
			throws IllegalArgumentException, IOException, UnknownHostException,
			StreamException {
		
		super();
		

	}

	public static void main(String[] args) throws Exception {
		LocationButler comp;
		String conf = "config.xml";
		if (args.length > 1) {
			System.out.println("Usage:  java "+LocationButler.class.getName()+" [config]");
			System.out.println("Arguments: [config]  path to config file");
			return;
		}

		if (args.length == 1)
			conf = args[0];

		// load preferences from configuration file
		try {
			Preferences.importPreferences(new FileInputStream(conf));
		}
		catch (Exception e) {
			System.err.println(e.toString());
         e.printStackTrace();
			return;
		}

		
		// create component
		comp = new LocationButler();

		class ShutdownHandler extends Thread {
			
			LocationButler comp;
			
			protected ShutdownHandler(LocationButler comp) {
				this.comp = comp;
			}
			
			public void run() {
				comp.stop();
			}
		}

		// add shutdown hook
		Runtime vm = Runtime.getRuntime();
		vm.addShutdownHook(new ShutdownHandler(comp));

		try {
			comp.execute();
		}
		catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		System.out.println("System halted.");
	}

	@Override
	protected String getComponentDiscoIdentityType() {
		return "location";
	}

	@Override
	protected String getComponentName() {
		return "Location Butler";
	}

	@Override
	protected String getComponentVersion() {
		return "0.0.1";
	}

	@Override
	protected Collection<PacketHandler> getPacketHandlers() {
		if(handlers == null){
			
			// create handlers
			handlers = new Vector<PacketHandler>();
         handlers.add(new LocationQueryHandler());
         handlers.add(new PlaceQueryHandler());
         handlers.add(new NearbyQueryHandler());
         //handlers.add(new FormHandler());
		}
		return handlers;
	}

   /* (non-Javadoc)
    * @see com.buddycloud.common.xmpp.XmppComponent#getComponentEventHub()
    */
   @Override
   protected ComponentEventHandlerRegistry getEventHandlerRegistry()
   {
      return LocationEventHandlerRegistry.getInstance( );
   }


}
