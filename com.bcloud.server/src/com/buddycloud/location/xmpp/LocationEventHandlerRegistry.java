/**
 * 
 */

package com.buddycloud.location.xmpp;

import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.buddycloud.common.Location;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.xmpp.ComponentEventHandlerRegistry;
import com.buddycloud.common.xmpp.SynchronizedPacketQueue;

/**
 * A central place to call third party application adapters
 * 
 * TODO: re-brand adapter interface as listener and add config strings with qualified
 * class names of all listeners to be added by the butler on startup
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

public class LocationEventHandlerRegistry implements ComponentEventHandlerRegistry, LocationEventHandler
{

   private static LocationEventHandlerRegistry instance = null;


   public static LocationEventHandlerRegistry getInstance()
   {
      if (instance == null)
      {
         instance = new LocationEventHandlerRegistry( );
      }
      return instance;
   }

   private Collection<LocationEventHandler> handlers;

   private Logger logger;


   private LocationEventHandlerRegistry()
   {
      try
      {
         logger = Logger.getLogger( getClass() );
         handlers = new Vector<LocationEventHandler>();

         // Register all adapters here
         // TODO do that reflectively based on class names set in config
         handlers.add( new FacebookLocationEventHandler() );
         handlers.add( new ChannelsLocationEventHandler( ) );

         logger.debug( "Installed adapters:" );
         for (LocationEventHandler a : handlers)
         {
            logger.debug( "   " + a.getAppName() );
         }
      }
      catch (Exception e)
      {
         logger.error( "Failed to initialize 3rd party app hub", e );
      }

   }


   /*
    * (non-Javadoc)
    * 
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#getAppName()
    */
   public String getAppName()
   {
      return "Hub";
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userCurrentLocationChanged
    * (com.bcloud.server.community.User, java.lang.String)
    */
   public void userCurrentLocationChanged(LocationUser user, Location location)
   {
      logger.info( user + ": userCurrentLocationChanged(" + location.getLabel() + ")" );
      for (LocationEventHandler a : handlers)
      {
         try
         {
            a.userCurrentLocationChanged( user, location );
         }
         catch (Exception e)
         {
            logger.error( user + ": Third party application " + a.getAppName()
                          + " current location update failed(" + location.getLabel() + "):"
                          + e.getMessage() );
         }
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userNextLocationChanged(
    * com.bcloud.server.community.User, java.lang.String)
    */
   public void userNextLocationChanged(LocationUser user, Location location)
   {
      logger.info( user + ": userNextLocationChanged(" + location.getLabel() + ")" );
      for (LocationEventHandler a : handlers)
      {
         try
         {
            a.userNextLocationChanged( user, location );
         }
         catch (Exception e)
         {
            logger.error( user + ": Third party application " + a.getAppName()
                          + " next location update failed (" + location.getLabel() + ")", e );
         }
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userDefinedNewPlace(com.
    * bcloud.server.community.User, java.lang.String)
    */
   public void userDefinedNewPlace(LocationUser user, Location location)

   {
      logger.info( user + ": userDefinedPlace(" + location.getPlaceName() + ")" );
      for (LocationEventHandler a : handlers)
      {
         try
         {
            a.userDefinedNewPlace( user, location );
         }
         catch (Exception e)
         {
            logger.error( user + ": Third party application " + a.getAppName()
                          + " new place defined notification failed (" + location.getPlaceName()
                          + ")", e );
         }
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userEnteredPlace(com.bcloud
    * .server.community.User, java.lang.String)
    */
   public void userEnteredPlace(LocationUser user, Location location)
   {
      logger.info( user + ": userEnteredPlace(" + location.getPlaceName() + ")" );
      for (LocationEventHandler a : handlers)
      {
         try
         {
            a.userEnteredPlace( user, location );
         }
         catch (Exception e)
         {
            logger.error( user + ": Third party application " + a.getAppName()
                          + " place enetered notification failed(" + location.getPlaceName()
                          + "):" + e.getMessage() );
         }
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userLeftPlace(com.bcloud
    * .server.community.User, java.lang.String)
    */
   public void userLeftPlace(LocationUser user, Location location)
   {
      logger.info( user + ": userLeftPlace(" + location.getPlaceName() + ")" );
      for (LocationEventHandler a : handlers)
      {
         try
         {
            a.userLeftPlace( user, location );
         }
         catch (Exception e)
         {
            logger.error( user + ": Third party application " + a.getAppName()
                          + " place left notification failed(" + location.getPlaceName() + "):"
                          + e.getMessage() );
         }
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userPreviousLocationChanged
    * (com.bcloud.server.community.User, java.lang.String)
    */
   public void userPreviousLocationChanged(LocationUser user, Location location)

   {
      logger.info( user + ": userPreviousLocationChanged(" + location.getLabel() + ")" );
      for (LocationEventHandler a : handlers)
      {
         try
         {
            a.userPreviousLocationChanged( user, location );
         }
         catch (Exception e)
         {
            logger.error( user + ": Third party application " + a.getAppName()
                          + " previous location update failed (" + location.getLabel() + ")", e );
         }
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#usersMet(com.bcloud.server
    * .community.User, int, java.lang.String, java.lang.String)
    */
   public void usersMet(LocationUser user, LocationUser otherUser, Location location)
   {
      logger.info( user + ": usersMet(" + otherUser + ", " + location.getPlaceName() + ")" );
      for (LocationEventHandler a : handlers)
      {
         try
         {
            if (a == null)
               logger.debug( "Adapter is null..." );
            a.usersMet( user, otherUser, location );
         }
         catch (Exception e)
         {
            logger.error( user + ": Third party application " + a.getAppName()
                          + " rendezvous notification failed (" + otherUser + " @ "
                          + location.getPlaceName() + ")", e );
         }
      }
   }


//   /*
//    * (non-Javadoc)
//    * 
//    * @see
//    * com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userStatusChanged(com.bcloud
//    * .server.community.User, java.lang.String)
//    */
//   public void userStatusChanged(LocationUser user, String status)
//
//   {
//      logger.info( user + ": userStatusChanged(" + status + ")" );
//      for (LocationEventHandler a : handlers)
//      {
//         try
//         {
//            if (a == null)
//               logger.debug( "Adapter is null..." );
//            a.userStatusChanged( user, status );
//         }
//         catch (Exception e)
//         {
//            logger.error( user + ": Third party application " + a.getAppName()
//                          + " status update failed (" + status + ")", e );
//         }
//      }
//   }


   /* (non-Javadoc)
    * @see com.buddycloud.thirdparty.apps.LocationEventHandler#init(com.buddycloud.common.xmpp.SynchronizedPacketQueue)
    */
   public void init(SynchronizedPacketQueue outQ)
   {
      for(LocationEventHandler h : handlers){
         h.init( outQ );
      }
      
   }
}
