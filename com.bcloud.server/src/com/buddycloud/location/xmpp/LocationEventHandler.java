
package com.buddycloud.location.xmpp;

import com.buddycloud.common.Location;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.xmpp.SynchronizedPacketQueue;

/**
 * A third party application is an application hosted by some one else, capable of
 * displaying information about Buddycloud users. To add support for a third party
 * application, implement this interface and add it to the adapter list in
 * <code>ThirdPartyApplicationHub<code>
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
 * limitations under the License.
 *
 *
 */ 

public interface LocationEventHandler
{

   /**
    * Notifies the event handler that the location component has successfully connected or
    * reconnected to the XMPP stream. Implementers can use the provided message queue to
    * send XMPP stanzas as response to location events. NOTE: This will be called more
    * every time the component reconnects, and the message queue instance may refer to a
    * different object.
    * 
    * @param outQ The message queue where location event handlers can post outgoing messages
    */
   public void init(SynchronizedPacketQueue outQ);


//   /**
//    * Notify the third party application that a user has changed his status
//    * 
//    * @param user
//    *           The user
//    * @param status
//    *           The new status
//    */
//   public void userStatusChanged(LocationUser user, String status);


   /**
    * Notify the third party application that a user's previous place has changed
    * 
    * @param user
    *           The user
    * @param location
    *           The user's new previous location
    */
   public void userPreviousLocationChanged(LocationUser user, Location location);


   /**
    * Notify the third party application that a user's current location has changed
    * 
    * @param user
    *           The user
    * @param location
    *           The user's current location
    */
   public void userCurrentLocationChanged(LocationUser user, Location location);


   /**
    * Notify the third party application that a user's next location has changed
    * 
    * @param user
    *           The user
    * @param location
    *           The user's next location
    */
   public void userNextLocationChanged(LocationUser user, Location location);


   /**
    * Notify the third party application that a user has defined a new place
    * 
    * @param user
    *           The user
    * @param location
    *           The user's current location (with reference to place)
    */
   public void userDefinedNewPlace(LocationUser user, Location location);


   /**
    * Notify the third party application that a user has entered a place
    * 
    * @param user
    *           The user
    * @param location
    *           The user's current location (with reference to place)
    */
   public void userEnteredPlace(LocationUser user, Location location);


   /**
    * Notify the third party application that a user has left a place
    * 
    * @param user
    *           The user
    * @param location
    *           The user's current location (with reference to place)
    */
   public void userLeftPlace(LocationUser user, Location location);


   /**
    * Notify the third party application that a has met up with another at a place
    * 
    * @param user
    *           The user
    * @param otherUser
    *           The other user
    * @param location
    *           The users' current location (with reference to place)
    */
   public void usersMet(LocationUser user, LocationUser otherUser, Location location);


   /**
    * @return the name of the application being adapted (for logging purposes)
    */
   public String getAppName();

}
