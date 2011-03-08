/**
 * 
 */

package com.buddycloud.location.xmpp;

import org.apache.log4j.Logger;
import org.jabberstudio.jso.JID;
import org.jabberstudio.jso.JSOImplementation;
import org.jabberstudio.jso.Packet;
import org.jabberstudio.jso.StreamDataFactory;
import org.jabberstudio.jso.StreamElement;

import com.buddycloud.Constants;
import com.buddycloud.common.Location;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.xmpp.SynchronizedPacketQueue;
import com.buddycloud.common.xmpp.XmppUtils;

/**
 * Adapter for pushing location events to user's personal channels
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

public class ChannelsLocationEventHandler implements LocationEventHandler
{

   private static final JID fromJid =
      new JID( "events@" + Constants.BUTLER_NODE_NAME + "." + Constants.XMPP_HOST_NAME );

   private SynchronizedPacketQueue outQ;

   private StreamDataFactory sdf;

   private Logger logger;


   public ChannelsLocationEventHandler()
   {
      logger = Logger.getLogger( getClass() );
      sdf = JSOImplementation.createInstance( null ).createDataFactory();
   }


   public void init(SynchronizedPacketQueue outQ)
   {
      this.outQ = outQ;
   }


   /*
    * (non-Javadoc)
    * 
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#getAppName()
    */
   public String getAppName()
   {
      return "Buddycloud Channels";
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.buddycloud.thirdparty.apps.ThirdPartyApplicationAdapter#userCurrentLocationChanged
    * (com.buddycloud.common.LocationUser, com.buddycloud.common.Location)
    */
   public void userCurrentLocationChanged(LocationUser user, Location location)
   {
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.buddycloud.thirdparty.apps.ThirdPartyApplicationAdapter#userDefinedNewPlace(
    * com.buddycloud.common.LocationUser, com.buddycloud.common.Place)
    */
   public void userDefinedNewPlace(LocationUser user, Location location)
   {
      pushPlaceEvent( user, "created place ", location, null );
   }


   // /**
   // * Pushes a message to the supplied user's personal channel (sent on his own behalf)
   // *
   // * @param user
   // * The user
   // * @param msg
   // * The message text
   // */
   // private void push(LocationUser user, String msg)
   // {
   // if (outQ == null)
   // {
   // logger.error( user + ": Not initialized. Message '" + msg
   // + "' not sent to personal channel." );
   // return;
   // }
   // logger.info( user + ": sending msg " + msg );
   // JID fromJid =
   // new JID( "events@" + Constants.BUTLER_NODE_NAME + "." + Constants.XMPP_HOST_NAME );
   // JID channelJid =
   // new JID( user.getJid().toBareJID().toString().replace( "@", "%" ) + "@"
   // + Constants.CHANNELS_HOST_NAME );
   // // if (user.toString().equals( "user01" ) || user.toString().equals("user02") ||
   // // user.toString().equals("user03") || user.toString().equals( "user04" ) ||
   // // user.toString().equals( "user05" ))
   // if (user.toString().equals( "user01" ))
   // {
   // logger.info( fromJid + " -> " + channelJid + ": " + msg );
   // Packet p = XmppUtils.createGroupMessage( sdf, fromJid, channelJid, msg );
   // StreamElement eventElement = p.addElement( "event",
   // "http://buddycloud.com/protocol/place#event" );
   // StreamElement idElement = eventElement.addElement( "id" );
   // idElement.addText( "http://buddycloud.com/places/"+l. )
   // outQ.enque( p );
   // }
   //
   // }
   //
   //
   // /**
   // * @param place
   // * @param string
   // * @return
   // */
   // private String createPlaceMessage(LocationUser user, String event, Location
   // location)
   // {
   // String genLocStr = location.getGeneralLocation().toString();
   // if (genLocStr != null && genLocStr.length() > 0)
   // return String.format( "%s %s '%s' (%s)", user.toString(), event, location
   // .getPlaceName(), genLocStr );
   // else
   // return String.format( "%s %s '%s'", user.toString(), event, location
   // .getPlaceName() );
   // }

   /**
    * @param place
    * @param string
    * @return
    */
   private void pushPlaceEvent(LocationUser user, String preText, Location location,
      String postText)
   {
      if (location.getPlaceName() == null || location.getPlaceName().trim().length() == 0)
      {
         logger.info( "No place name, event not pushed" );
         return;
      }

      // create user's personal channel jid
      JID channelJid =
         new JID( user.getJid().toBareJID().toString().replace( "@", "%" ) + "@"
                  + Constants.CHANNELS_HOST_NAME );

      // create event message
      String msg;
      String genLocStr = location.getGeneralLocation().toString();
      if (genLocStr != null && genLocStr.length() > 0)
         msg =
            String.format( "%s %s '%s' (%s)", user.toString(), preText, location
               .getPlaceName(), genLocStr );
      else
         msg =
            String.format( "%s %s '%s'", user.toString(), preText, location
               .getPlaceName() );

      if (postText != null)
         msg += postText;

      // create group message
      Packet p = XmppUtils.createMucMessage( sdf, fromJid, channelJid, msg );

      // add event element
      StreamElement eventElement =
         p.addElement( "event", "http://buddycloud.com/protocol/place#event" );
      StreamElement idElement = eventElement.addElement( "id" );
      idElement.addText( "http://buddycloud.com/places/" + location.getPlaceId() );
      StreamElement nameElement = eventElement.addElement( "id" );
      nameElement.addText( location.getPlaceName() );

      logger.debug( p.toString() );

//      // for now, only push to core members for testing
//      String username = user.getJid().getNode();
//      if (username.equals( "user01" ) || username.equals( "user02" )
//          || username.equals( "user03" ) || username.equals( "user04" )
//          || username.equals( "user05" ) || username.equals( "user06" ))
//      {
//         outQ.enque( p );
//      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.buddycloud.thirdparty.apps.ThirdPartyApplicationAdapter#userEnteredPlace(com
    * .buddycloud.common.LocationUser, com.buddycloud.common.Place)
    */
   public void userEnteredPlace(LocationUser user, Location location)
   {
      pushPlaceEvent( user, "arrived at", location, null );
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.buddycloud.thirdparty.apps.ThirdPartyApplicationAdapter#userLeftPlace(com.buddycloud
    * .common.LocationUser, com.buddycloud.common.Place)
    */
   public void userLeftPlace(LocationUser user, Location location)
   {
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.buddycloud.thirdparty.apps.ThirdPartyApplicationAdapter#userNextLocationChanged
    * (com.buddycloud.common.LocationUser, java.lang.String)
    */
   public void userNextLocationChanged(LocationUser user, Location location)
   {
      pushPlaceEvent( user, "will be at", location, "next" );
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.buddycloud.thirdparty.apps.ThirdPartyApplicationAdapter#userPreviousLocationChanged
    * (com.buddycloud.common.LocationUser, com.buddycloud.common.Location)
    */
   public void userPreviousLocationChanged(LocationUser user, Location location)
   {
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.buddycloud.thirdparty.apps.ThirdPartyApplicationAdapter#usersMet(com.buddycloud
    * .common.LocationUser, com.buddycloud.common.LocationUser,
    * com.buddycloud.common.Place)
    */
   public void usersMet(LocationUser user, LocationUser otherUser, Location location)
   {
      String a = user.getJid().getNode();
      String b = otherUser.getJid().getNode();
      pushPlaceEvent( user, "met " + b + " at", location, null );
      pushPlaceEvent( user, "met " + a + " at", location, null );

   }

}
