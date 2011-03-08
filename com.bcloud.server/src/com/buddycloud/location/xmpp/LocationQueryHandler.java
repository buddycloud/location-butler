
package com.buddycloud.location.xmpp;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.jabberstudio.jso.InfoQuery;
import org.jabberstudio.jso.JID;
import org.jabberstudio.jso.Packet;
import org.jabberstudio.jso.StreamElement;
import org.jabberstudio.jso.format.DateTimeProfileFormat;

import com.buddycloud.Constants;
import com.buddycloud.common.Location;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.xmpp.PacketFilter;
import com.buddycloud.common.xmpp.PacketHandler;
import com.buddycloud.common.xmpp.XmppUtils;
import com.buddycloud.location.Beacon;
import com.buddycloud.location.CountryCode;
import com.buddycloud.location.LocationEngine;
import com.buddycloud.location.LocationQuery;
import com.buddycloud.location.sql.LocationDbAccess;

/**
 * Handler for the location query queue. TODO write a up-to date class description
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

public class LocationQueryHandler extends PacketHandler
{

   private String firstTimeOnTheRoadHelpMessage;

   private String firstTimeUnnamedPlaceHelpMessage;

   private String firstQueryHelpMessage;

   private String secondQueryHelpMessage;

   private String thirdQueryHelpMessage;

   private LocationDbAccess lDB;

   private LocationEngine locationEngine;

   private LocationEventHandlerRegistry thirdPartyApps;

   private String logPrefix;

   private String welcomeMessageFormat;

   private JID welcomeMessageToJid;

   private JID welcomeMessageFromJid;
   
   private String welcomeMessageFromName;


   public LocationQueryHandler()
   {

      super();
      this.lDB = new LocationDbAccess();
      thirdPartyApps = LocationEventHandlerRegistry.getInstance();

      Preferences prefs = Preferences.userNodeForPackage( getClass() );
      this.welcomeMessageFormat = prefs.get( "welcome_message_format", "" );
      String welcomeMessageTo = prefs.get( "welcome_message_to_jid", "" );
      String welcomeMessageFrom = prefs.get( "welcome_message_from_jid", "" );
      try
      {
         welcomeMessageToJid = new JID( welcomeMessageTo );
         welcomeMessageFromJid = new JID( welcomeMessageFrom );
         welcomeMessageFromName = "Buddycloud";
      }
      catch (Exception e)
      {
         logger.error( "HELP: Invalid welcome message JIDs: " + welcomeMessageTo + " "
                       + welcomeMessageFrom + ". Welcome messages will not be sent." );
      }
      logger.info( "HELP: Welcome msg format: " + welcomeMessageFormat );
      logger.info( "HELP: From JID: " + welcomeMessageFromJid );
      logger.info( "HELP:   To JID: " + welcomeMessageToJid );
      this.firstQueryHelpMessage = prefs.get( "first_query_help_message", "" );
      this.secondQueryHelpMessage = prefs.get( "second_query_help_message", "" );
      this.thirdQueryHelpMessage = prefs.get( "third_query_help_message", "" );
      this.firstTimeOnTheRoadHelpMessage =
         prefs.get( "first_time_on_the_road_help_message", "" );
      this.firstTimeUnnamedPlaceHelpMessage =
         prefs.get( "first_time_unnamed_place_help_message", "" );

      if (firstQueryHelpMessage == null || firstQueryHelpMessage.length() == 0)
         logger.error( "first_query_help_message is null or zero length!" );
      if (secondQueryHelpMessage == null || secondQueryHelpMessage.length() == 0)
         logger.error( "second_query_help_message is null or zero length!" );
      if (thirdQueryHelpMessage == null || thirdQueryHelpMessage.length() == 0)
         logger.error( "third_query_help_message is null or zero length!" );
      if (firstTimeUnnamedPlaceHelpMessage == null
          || firstTimeUnnamedPlaceHelpMessage.length() == 0)
         logger.error( "first_time_unnamed_place_help_message is null or zero length!" );
      if (firstTimeOnTheRoadHelpMessage == null
          || firstTimeOnTheRoadHelpMessage.length() == 0)
         logger.error( "first_time_on_the_road_help_message is null or zero length!" );

      locationEngine = new LocationEngine( lDB );

   }


   /**
    * @deprecated
    */
   private LocationQuery extractBuddycloudLocationQuery(StreamElement se)
   {
      LocationQuery lq = new LocationQuery();
      String timeString = se.getAttributeValue( "time" );
      String latitudeString = se.getAttributeValue( "latitude" );
      String longitudeString = se.getAttributeValue( "longitude" );
      String accuracyString = se.getAttributeValue( "accuracy" );
      String formatString = se.getAttributeValue( "format" );
      String parseLogPrefix = logPrefix + ": PARSE: ";

      logger.info( parseLogPrefix + "time      : " + timeString );
      logger.info( parseLogPrefix + "latitude  : " + latitudeString );
      logger.info( parseLogPrefix + "longitude : " + longitudeString );
      logger.info( parseLogPrefix + "accuracy  : " + accuracyString );
      logger.info( parseLogPrefix + "format    : " + formatString );

      if (latitudeString != null && latitudeString.contains( "," ))
      {
         latitudeString = latitudeString.replace( ",", "." );
         logger.info( parseLogPrefix + "latitude  : replaced comma with dot" );
      }

      if (longitudeString != null && longitudeString.contains( "," ))
      {
         longitudeString = longitudeString.replace( ",", "." );
         logger.info( parseLogPrefix + "longtitude : replaced comma with dot" );
      }

      if (accuracyString != null && accuracyString.contains( "," ))
      {
         accuracyString = accuracyString.replace( ",", "." );
         logger.info( parseLogPrefix + "accuracy  : replaced comma with dot" );
      }

      try
      {
         lq.setTime( Long.parseLong( timeString ) );
         long latency = System.currentTimeMillis() - lq.getTime();
         logger.info( parseLogPrefix + "time diff to client time: "
                      + ( latency / 1000 / 60 ) + " min " + ( latency / 1000 % 60 )
                      + " sec " + ( latency % 1000 ) + " msec" );
      }
      catch (Exception e)
      {
         logger.error( parseLogPrefix + "Malformed time value: " + timeString );
      }

      try
      {
         if (latitudeString != null)
            lq.setLatitude( Double.parseDouble( latitudeString ) );
      }
      catch (Exception e)
      {
         logger.info( parseLogPrefix + "Malformed latitude value: " + latitudeString
                      + ", ignored." );
      }

      try
      {
         if (longitudeString != null)
            lq.setLongitude( Double.parseDouble( longitudeString ) );
      }
      catch (Exception e)
      {
         logger.info( parseLogPrefix + "Malformed longitude value: " + longitudeString
                      + ", ignored." );
      }

      try
      {
         if (accuracyString != null)
            lq.setAccuracy( Double.parseDouble( accuracyString ) );
      }
      catch (Exception e)
      {
         logger.info( parseLogPrefix + "Malformed accuracy value: " + accuracyString
                      + ", ignored." );
      }

      lq.setPublish( true );

      // try
      // {
      // lq.setFormat( LocationQuery.Format.valueOf( formatString.toUpperCase() ) );
      // }
      // catch (Exception e)
      // {
      // }
      //
      StreamElement beaconsElement = se.getFirstElement( "beacons" );
      Vector<Beacon> beacons = new Vector<Beacon>();
      Vector<Integer> signalStrengths = new Vector<Integer>();
      if (beaconsElement != null)
      {
         Iterator<?> iter = beaconsElement.listElements().iterator();
         while (iter.hasNext())
         {
            StreamElement be = (StreamElement) iter.next();
            String macString = be.getAttributeValue( "id" );
            String typeString = be.getAttributeValue( "type" );
            String signalStrengthString = be.getAttributeValue( "signalstrength" );

            if (macString != null && typeString != null && macString.length() >= 7
                && typeString.length() >= 2)
            {
               Beacon beacon = new Beacon();
               beacon.setMac( macString );

               try
               {
                  beacon.setType( Beacon.Type.valueOf( typeString.toUpperCase() ) );
               }
               catch (Exception e)
               {
               }

               if (beacon.getType() == Beacon.Type.CELL)
               {

                  try
                  {
                     beacon.setCountryCode( CountryCode.getInstanceFromMCC( beacon
                        .getMcc() ) );
                  }
                  catch (Exception e)
                  {
                     logger.error( "Failed to get country code for beacon " + macString );
                  }
               }

               beacons.add( beacon );

               Integer signalStrength = 0;
               // note, ignore sign for historic reasons (blah)
               try
               {
                  signalStrength = Math.abs( Integer.parseInt( signalStrengthString ) );
               }
               catch (Exception e)
               {
                  if (beacon.getType() != Beacon.Type.BLUETOOTH)
                     logger.error( parseLogPrefix + ": Invalid signal strength: "
                                   + signalStrengthString, e );
               }
               signalStrengths.add( signalStrength );
               logger.info( parseLogPrefix + "" + beacon + " @ " + signalStrengthString
                            + " dBM" );
            }
         }
      }
      if (beacons.size() != signalStrengths.size())
      {
         throw new RuntimeException( "beacons.size() [" + beacons.size()
                                     + "] != signalStrengths.size() ["
                                     + signalStrengths.size() + "]" );
      }

      lq.setBeacons( beacons );
      lq.setSignalStrengths( signalStrengths );

      return lq;
   }


   private LocationQuery extractStandardLocationQuery(StreamElement locationquery)
   {
      LocationQuery lq = new LocationQuery();
      String queryTimeString = getChildElementText( locationquery, "timestamp" );
      String latitudeString = getChildElementText( locationquery, "lat" );
      String longitudeString = getChildElementText( locationquery, "lon" );
      String accuracyString = getChildElementText( locationquery, "accuracy" );
      String altitudeString = getChildElementText( locationquery, "alt" );
      String bearingString = getChildElementText( locationquery, "bearing" );
      String datumString = getChildElementText( locationquery, "datum" );
      String speedString = getChildElementText( locationquery, "speed" );
      String publishString = getChildElementText( locationquery, "publish" );
      String parseLogPrefix = logPrefix + ": PARSE: ";

      logger.info( parseLogPrefix + "timestamp : " + queryTimeString );
      logger.info( parseLogPrefix + "lat       : " + latitudeString );
      logger.info( parseLogPrefix + "lon       : " + longitudeString );
      logger.info( parseLogPrefix + "accuracy  : " + accuracyString );
      logger.info( parseLogPrefix + "alt       : " + altitudeString );
      logger.info( parseLogPrefix + "bearing   : " + bearingString );
      logger.info( parseLogPrefix + "speed     : " + speedString );
      logger.info( parseLogPrefix + "datum     : " + datumString );
      logger.info( parseLogPrefix + "publish   : " + publishString );

      // make sure english decimal signs are used
      if (latitudeString != null && latitudeString.contains( "," ))
      {
         latitudeString = latitudeString.replace( ",", "." );
         logger.info( parseLogPrefix + "lat       : replaced comma with dot" );
      }

      if (longitudeString != null && longitudeString.contains( "," ))
      {
         longitudeString = longitudeString.replace( ",", "." );
         logger.info( parseLogPrefix + "lon       : replaced comma with dot" );
      }

      if (accuracyString != null && accuracyString.contains( "," ))
      {
         accuracyString = accuracyString.replace( ",", "." );
         logger.info( parseLogPrefix + "accuracy  : replaced comma with dot" );
      }

      lq.setLocale( locationquery.getDeclaredLocale() );

      try
      {
         lq.setTime( DateTimeProfileFormat.getInstance( DateTimeProfileFormat.DATETIME )
            .parse( queryTimeString ).getTime() );
         long latency = System.currentTimeMillis() - lq.getTime();
         logger.info( parseLogPrefix + "time diff to client time: "
                      + ( latency / 1000 / 60 ) + " min " + ( latency / 1000 % 60 )
                      + " sec " + ( latency % 1000 ) + " msec" );
      }
      catch (Exception e)
      {
         lq.setTime( System.currentTimeMillis() );
      }

      try
      {
         lq.setLatitude( Double.parseDouble( latitudeString ) );
      }
      catch (Exception e)
      {
      }

      try
      {
         lq.setLongitude( Double.parseDouble( longitudeString ) );
      }
      catch (Exception e)
      {
      }

      try
      {
         lq.setAccuracy( Double.parseDouble( accuracyString ) );
      }
      catch (Exception e)
      {
      }

      try
      {
         lq.setAltitude( Double.parseDouble( altitudeString ) );
      }
      catch (Exception e)
      {
      }

      try
      {
         lq.setBearing( Double.parseDouble( bearingString ) );
      }
      catch (Exception e)
      {
      }

      try
      {
         lq.setSpeed( Double.parseDouble( speedString ) );
      }
      catch (Exception e)
      {
      }

      try
      {
         lq.setDatum( datumString );
      }
      catch (Exception e)
      {
      }

      try
      {
         lq.setPublish( publishString.toLowerCase().equals( "true" )
                        || publishString.equals( "1" ) );
      }
      catch (Exception e)
      {
      }

      Iterator<?> iter = locationquery.listElements( "reference" ).iterator();
      Vector<Beacon> beacons = new Vector<Beacon>();
      Vector<Integer> signalStrengths = new Vector<Integer>();
      Vector<Long> timestamps = new Vector<Long>();
      while (iter.hasNext())
      {
         StreamElement reference = (StreamElement) iter.next();
         String macString = getChildElementText( reference, "id" );
         String typeString = getChildElementText( reference, "type" );
         String signalStrengthString = getChildElementText( reference, "signalstrength" );
         String timestampString = getChildElementText( reference, "timestamp" );

         if (macString != null && typeString != null && macString.length() >= 7
             && typeString.length() >= 2)
         {
            Beacon beacon = new Beacon();
            beacon.setMac( macString );

            try
            {
               beacon.setType( Beacon.Type.valueOf( typeString.toUpperCase() ) );
            }
            catch (Exception e)
            {
               logger.error( logPrefix + "Unexpected beacon type: " + typeString );
            }

            if (beacon.getType() == Beacon.Type.CELL)
            {

               try
               {
                  beacon
                     .setCountryCode( CountryCode.getInstanceFromMCC( beacon.getMcc() ) );
               }
               catch (Exception e)
               {
                  logger.error( "Failed to get country code for beacon " + macString );
               }
            }

            try
            {
               long t =
                  DateTimeProfileFormat.getInstance( DateTimeProfileFormat.DATETIME )
                     .parse( timestampString ).getTime();
               timestamps.add( t );
            }
            catch (Exception e)
            {
               timestamps.add( lq.getTime() );
            }

            beacons.add( beacon );

            Integer signalStrength = 0;
            // note, ignore sign for historic reasons (blah)
            try
            {
               signalStrength = Math.abs( Integer.parseInt( signalStrengthString ) );
            }
            catch (Exception e)
            {
            }
            signalStrengths.add( signalStrength );
            logger.info( parseLogPrefix + "" + beacon + " @ " + signalStrengthString
                         + " dBM " + timestampString );
         }
      }
      if (beacons.size() != signalStrengths.size())
      {
         throw new RuntimeException( "beacons.size() [" + beacons.size()
                                     + "] != signalStrengths.size() ["
                                     + signalStrengths.size() + "]" );
      }

      lq.setBeacons( beacons );
      lq.setSignalStrengths( signalStrengths );

      return lq;
   }


   @Override
   public Collection<PacketFilter> getHandledPacketFilters()
   {
      Vector<PacketFilter> filters = new Vector<PacketFilter>();

      PacketFilter standardLocationQueryFilter = new PacketFilter();
      standardLocationQueryFilter.addNamespace( "locationquery",
         "urn:xmpp:locationquery:0" );
      standardLocationQueryFilter
         .setXPath( "jabber:iq[@type='get']/locationquery:locationquery" );

      PacketFilter locationClearFilter = new PacketFilter();
      locationClearFilter.addNamespace( "location_clear",
         "http://buddycloud.com/protocol/location#clear" );
      locationClearFilter.setXPath( "jabber:iq[@type='set']/location_clear:query" );

      PacketFilter locationDeleteFilter = new PacketFilter();
      locationDeleteFilter.addNamespace( "location_delete",
         "http://buddycloud.com/protocol/location#delete_all" );
      locationDeleteFilter.setXPath( "jabber:iq[@type='set']/location_delete:query" );

      PacketFilter buddycloudLocationQueryFilter = new PacketFilter();
      buddycloudLocationQueryFilter.addNamespace( "location",
         "http://buddycloud.com/protocol/location" );
      buddycloudLocationQueryFilter.setXPath( "jabber:iq[@type='set']/location:location" );

      filters.add( standardLocationQueryFilter );
      filters.add( locationClearFilter );
      filters.add( locationDeleteFilter );
      filters.add( buddycloudLocationQueryFilter );
      return filters;
   }


   private Packet handleLocationQuery(InfoQuery iq)
   {
      Packet result = null;

      try
      {

         // get the user id corresponding to the submitter's JID
         // Note: will create a new DB entry if submitter is federated user not already
         // known
         LocationUser user = getOrAddUser( iq.getFrom(), true );

         if (user == null)
         {
            logger.error( "Unknown user: " + iq.getFrom() + ". Form ignored" );
            return XmppUtils.createNotAuthorizedError( iq, "Unknown JID " + iq.getFrom() );
         }

         logPrefix = user + " :";

         logger.info( "-----------------------------------------------------" );
         logger.info( user + "(UID " + user.getId() + "): Processing location query..." );

         // get the location element of IQ stanza
         StreamElement buddycloudLocationQueryElement = iq.getFirstElement( "location" );
         StreamElement standardLocationQueryElement =
            iq.getFirstElement( "locationquery" );
         LocationQuery query = null;
         String clientVersion = null;

         // parse location element to LocationQuery object
         if (standardLocationQueryElement != null)
         {
            query = extractStandardLocationQuery( standardLocationQueryElement );
            clientVersion = standardLocationQueryElement.getAttributeValue( "clientver" );
            logger.info( logPrefix + "Client version : " + clientVersion );
         }
         else if (buddycloudLocationQueryElement != null)
         {
            query = extractBuddycloudLocationQuery( buddycloudLocationQueryElement );
         }

         // if there are no useful data in query, stop here and return an error
         if (query == null || !query.isValid())
         {
            logger.error( user + ": No beacons or coordinates in query, ignored" );

            return XmppUtils.createBadRequestError( iq,
               "No beacons or coordinates in query." );
         }

         // add query to data base. This will also add all
         // new beacons to beacon table and update the beacon objects
         // with their assigned database ID
         lDB.addLocationQuery( user.getId(), query );

         // get the most recently set location of the querying user
         Location oldCurrentLocation = lDB.getCurrentLocation( user.getId() );
         if (oldCurrentLocation == null)
         {
            oldCurrentLocation = new Location();
            oldCurrentLocation.setPlaceId( -1 );
         }

         Location newCurrentLocation = locationEngine.getLocation( user );

         if (newCurrentLocation == null)
         {
            logger.info( user + " : Returning bad request error." );
            return XmppUtils.createBadRequestError( iq,
               "Sorry, could not find any location for you this time." );
         }

         logger.info( user + ": Location: " + newCurrentLocation.getLabel() );
         // }

         logger.info( user + ": Old current location : " + oldCurrentLocation );
         logger.info( user + ": New current location : " + newCurrentLocation );

         // determine whether or not an update of the user's location PEP nodes is needed
         if (query.isPublish() && locationEngine.hasLocationChangedSinceLastQuery())
         {

            logger.info( user + ": Current location update needed : true" );

            // get the previous location resulting from last location query of this user
            Location oldPreviousLocation =
               lDB.getPreviousLocation( user.getId(), oldCurrentLocation.getPlaceId() );

            // get the new previous location resulting from this location query
            Location newPreviousLocation =
               lDB.getPreviousLocation( user.getId(), newCurrentLocation.getPlaceId() );

            // get location with the most recent place entry
            Location mostRecentPlaceEntryLocation =
               lDB.getPreviousLocation( user.getId(), -1 );

            logger.debug( logPrefix + "Old previous location " + oldPreviousLocation );
            logger.debug( logPrefix + "New previous location " + newPreviousLocation );

            // push previous location update if changed
            if (newPreviousLocation != null
                && ( oldPreviousLocation == null || !newPreviousLocation.getLabel()
                   .equals( oldPreviousLocation.getLabel() ) ))
            {

               // send XMPP previous place pubsub update packet
               Packet prevLocationPubsubPacket =
                  XmppExtensionsFactory.createPubsubPreviousLocationPacket( stream
                     .getDataFactory(), iq.getFrom(), query, newPreviousLocation );

               outQ.enque( prevLocationPubsubPacket );
               logger.debug( user + ": Updated previous location to pubsub : "
                             + newPreviousLocation.getLabel() );

               logger.info( user + ": Updated previous location : "
                            + newPreviousLocation.getLabel() );

            }

            // send XMPP current place pubsub update packet
            Packet currLocationPubsubPacket =
               XmppExtensionsFactory.createPubsubCurrentLocationPacket( stream
                  .getDataFactory(), iq.getFrom(), query, newCurrentLocation );

            outQ.enque( currLocationPubsubPacket );
            logger.debug( user + ": Updated current location to pubsub : "
                          + newCurrentLocation.toString() );

            // add the new current location to DB history
            lDB.addLocationHistory( user.getId(), newCurrentLocation );

            // if the user has left a place...
            if (isPlaceExit( oldCurrentLocation, newCurrentLocation ))
            {
               logger.info( user + ": Left place " + oldCurrentLocation.getPlaceName() );

               // update place history with exit time
               lDB.updateLastPlaceHistoryEntry( user.getId(), oldCurrentLocation
                  .getPlaceId(), System.currentTimeMillis() );

               // notify 3rd party apps that user left
               thirdPartyApps.userLeftPlace( user, oldCurrentLocation );

               // get all users at place
               Collection<Integer> usersAtPlace =
                  lDB.getUsersAtPlace( newCurrentLocation.getPlaceId() );
               int population = usersAtPlace.size();

               // push population of old current place
               pushPopulationUpdate( oldCurrentLocation.getPlaceId(), population );
            }

            // if the user has entered a place...
            if (isPlaceEntry( mostRecentPlaceEntryLocation, newCurrentLocation ))
            {
               logger.info( user + ": Entered place " + newCurrentLocation );

               // add to place history
               lDB.addPlaceHistory( user.getId(), newCurrentLocation.getPlaceId(), System
                  .currentTimeMillis() );

               // notify 3rd party apps that user entered
               thirdPartyApps.userEnteredPlace( user, newCurrentLocation );

               // get all users at place
               Collection<Integer> usersAtPlace =
                  lDB.getUsersAtPlace( newCurrentLocation.getPlaceId() );
               int population = usersAtPlace.size();

               // notify 3rd party apps that $a met $b @ $place
               for (int otherUserId : usersAtPlace)
               {
                  if (otherUserId != user.getId())
                  {
                     LocationUser otherUser = lDB.getLocationUser( otherUserId );
                     LocationQuery q = lDB.getMostRecentLocationQuery( otherUser.getId() );
                     long otherUserLastSeenMillis = 0;
                     if (q != null)
                     {
                        otherUserLastSeenMillis = q.getTime();
                     }
                     int otherUserLastSeenMinAgo =
                        (int) ( System.currentTimeMillis() - otherUserLastSeenMillis ) / 60000;

                     if (otherUser != null && q != null && otherUserLastSeenMinAgo < 15)
                     {

                        thirdPartyApps.usersMet( user, otherUser, newCurrentLocation );

                        thirdPartyApps.usersMet( otherUser, user, newCurrentLocation );
                     }
                     else
                     {
                        logger.error( user + ":  met non-existing user " + otherUserId );
                     }
                  }

               }

               // push PEP population of new current place to all subscribers
               pushPopulationUpdate( newCurrentLocation.getPlaceId(), population );

            }

            // if this is the first time this user have been through here, send
            // him/her a welcome message as well as a short introduction to
            // the open-to-all group
            if (!lDB.isAngelMessageSent( user.getId(), "WELCOME" ))
            {

               logger.info( user + ": HELP: Sending welcome messages:" );

               if (firstQueryHelpMessage != null
                   && firstQueryHelpMessage.trim().length() > 0)
               {
                  sendUserHelpMessage( user, firstQueryHelpMessage );
                  logger.info( user + ": HELP: " + firstQueryHelpMessage );
               }

               if (secondQueryHelpMessage != null
                   && secondQueryHelpMessage.trim().length() > 0)
               {
                  sendUserHelpMessage( user, secondQueryHelpMessage );
                  logger.info( user + ": HELP: " + secondQueryHelpMessage );
               }

               if (thirdQueryHelpMessage != null
                   && thirdQueryHelpMessage.trim().length() > 0)
               {
                  sendUserHelpMessage( user, thirdQueryHelpMessage );
                  logger.info( user + ": HELP: " + thirdQueryHelpMessage );
               }

               sendFirstTimeUserWelcomeMessage( user, newCurrentLocation.toString( 3,
                  Location.Layer.CITY ) );

               lDB.setAngelMessageSent( user.getId(), "WELCOME" );

            }

            // if this is the first time the user is at an unnamed place, remind him/her
            // how to name it
            if (newCurrentLocation.isStationary() && newCurrentLocation.getPlaceId() < 1
                && firstTimeUnnamedPlaceHelpMessage != null
                && firstTimeUnnamedPlaceHelpMessage.trim().length() > 0
                && !lDB.isAngelMessageSent( user.getId(), "PLACE_NAMING" ))
            {

               logger
                  .info( user + ": HELP: Sending first time at unnamed place message:" );

               sendUserHelpMessage( user, firstTimeUnnamedPlaceHelpMessage );

               logger.info( user + ": HELP: " + firstTimeUnnamedPlaceHelpMessage );

               lDB.setAngelMessageSent( user.getId(), "PLACE_NAMING" );

            }

            // if this is the first time the user is moving, send a message explaining the
            // "On the road" place label
            if (newCurrentLocation.isMoving() && firstTimeOnTheRoadHelpMessage != null
                && firstTimeOnTheRoadHelpMessage.trim().length() > 0
                && !lDB.isAngelMessageSent( user.getId(), "NEXT_PLACE" ))
            {

               logger.info( user + ": HELP: Sending first-time-on-the-road message:" );

               sendUserHelpMessage( user, firstTimeOnTheRoadHelpMessage );

               logger.info( user + ": HELP: " + firstTimeOnTheRoadHelpMessage );

               lDB.setAngelMessageSent( user.getId(), "NEXT_PLACE" );

            }

            // update third party apps
            if (!oldPreviousLocation.getLabel().equals( newPreviousLocation.getLabel() ))
            {
               thirdPartyApps.userPreviousLocationChanged( user, newPreviousLocation );
            }

            thirdPartyApps.userCurrentLocationChanged( user, newCurrentLocation );

         }

         if (newCurrentLocation == null)
         {
            logger
               .error( "newCurrentLocation is null. How the hell did that happen? To be investigated further...." );
         }

         // post processing
         // TODO do this in separate thread, to ensure quick server response
         // if query contains gps coordinates, add a "beacon observation" entry in DB
         // for each beacon with the query coordinates and trigger a new estimation of
         // each beacon's position
         // if (query.getGpsLatitude() != 0 && query.getGpsLongitude() != 0)
         // {
         // updateBeaconPositions( query.getBeacons(), query.getGpsLatitude(), query
         // .getGpsLongitude() );
         // }
         // not yet sure this is a good idea...
         // else
         // {
         // updateBeaconLocations( query.getBeacons(), newCurrentLocation );
         // }

         result =
            XmppExtensionsFactory.createLocationResultPacket( iq, query,
               newCurrentLocation );
      }
      catch (Exception e)
      {
         logger.error( "Unhandled exception", e );
         result =
            XmppUtils.createUnhandledExceptionError( iq,
               "Something wrong is not right. Please try again." );
      }

      return result;
   }


   @Override
   protected Packet handlePacket(Packet p)
   {

      if (!( p instanceof InfoQuery ))
      {
         throw new IllegalArgumentException( "Unsupported package class: "
                                             + p.getClass().getName() );
      }

      InfoQuery iq = (InfoQuery) p;
      Packet result = null;

      // look for data clearance and deletion request
      if (iq.getFirstElement( "query" ) != null)
      {
         if (iq.getType().equals( InfoQuery.SET ))
         {
            result = handleLocationClearance( iq );
         }
         else
         {
            return XmppUtils.createBadRequestError( iq, "No can do" );
         }
      }
      // look for a old-style buddycloud location query
      else if (iq.getFirstElement( "location" ) != null)
      {
         // process the query and get result
         result = handleLocationQuery( iq );
      }

      // look for a XEP-0255 location query
      else if (iq.getFirstElement( "locationquery" ) != null)
      {
         // process the query and get result
         result = handleLocationQuery( iq );
      }

      // if not found, something is messed up
      else
      {
         logger.error( "Invalid request: " + iq );
         result =
            XmppUtils.createBadRequestError( iq,
               "Expected location query, got something else." );
      }
      if (result == null)
      {
         logger.error( "No result stanza generated" );
         result = XmppUtils.createItemNotFoundError( iq, "Something wrong is not right." );
      }

      // return result to caller
      return result;
   }


   /**
    * @param iq
    * @return
    */
   private Packet handleLocationClearance(InfoQuery iq)
   {
      logger.info( "Received clear location data request." );
      try
      {

         // get the user id corresponding to the submitter's JID
         LocationUser user = getOrAddUser( iq.getFrom(), false );

         if (user == null)
         {
            logger.error( "Unknown user: " + iq.getFrom() + ". Form ignored" );
            return XmppUtils.createNotAuthorizedError( iq, "Unknown JID " + iq.getFrom() );
         }

         logPrefix = user + " :";
         StreamElement q = iq.getFirstElement( "query" );
         String ns = q.getNamespaceURI();
         logger.debug( "ns URI: " + ns );
         int uid = user.getId();

         if (ns.endsWith( "#clear" ) || ns.endsWith( "#delete_all" ))
         {
            logger.info( logPrefix + "(UID " + uid + ") Deleting current location..." );
            lDB.deleteCurrentLocation( user.getId() );

            logger.info( logPrefix + "(UID " + uid + ") Deleting location history..." );
            lDB.deleteUserRefsFromLocationHistory( user.getId() );

            logger.info( logPrefix + "(UID " + uid + ") Deleting place history..." );
            lDB.deleteUserRefsFromPlaceHistory( user.getId() );

            logger.info( logPrefix + "(UID " + uid + ") Deleting location queries..." );
            lDB.deleteAllQueries( user.getId(), 0 );
         }

         if (ns.endsWith( "#delete_all" ))
         {
            logger.info( logPrefix + "(UID " + uid + ") Deleting location user..." );
            lDB.deleteLocationUser( user.getId() );
         }

         return XmppUtils.createCopyResult( iq );
      }
      catch (Exception e)
      {
         logger.error( iq.getFrom().toBareJID() + ": Failed to clear all location data: "
                       + e.getMessage() );
         return XmppUtils.createBadRequestError( iq, "Unknown JID " + iq.getFrom() );
      }
   }


   @Override
   protected void handleStop()
   {

      // gracefully shutdown DB connection
      try
      {
         logger.debug( "Closing DB conncetion..." );
         lDB.disconnect();
         logger.debug( "DB conncetion closed." );
      }
      catch (SQLException sqle)
      {
         logger.error( "Failed to close DB conncetion.", sqle );
      }

   }


   private boolean isPlaceEntry(Location lastPlaceEntryLocation,
      Location newCurrentLocation)
   {
      if (newCurrentLocation == null)
         return false;
      if (lastPlaceEntryLocation == null)
         return newCurrentLocation.isPlaceFix();

      boolean b =
         newCurrentLocation.isPlaceFix()
            && newCurrentLocation.getPlaceId() != lastPlaceEntryLocation.getPlaceId();

      logger.debug( logPrefix + "isPlaceEntry(" + lastPlaceEntryLocation.getLabel()
                    + ", " + newCurrentLocation.getLabel() + ") = " + b );

      return b;
   }


   private boolean isPlaceExit(Location oldCurrentLocation, Location newCurrentLocation)
   {
      if (oldCurrentLocation == null || newCurrentLocation == null)
         return false;

      return oldCurrentLocation.isPlaceFix() && !newCurrentLocation.isPlaceFix()
             && newCurrentLocation.getPlaceId() != oldCurrentLocation.getPlaceId();
   }


   // private boolean isUpdateNeeded(Location oldCurrentLocation, Location
   // newCurrentLocation)
   // {
   //
   // boolean update = false;
   //
   // if (oldCurrentLocation == null && newCurrentLocation == null)
   // return false;
   //
   // if (oldCurrentLocation == null && newCurrentLocation != null)
   // return true;
   //
   // if (newCurrentLocation.getLabel().equals( oldCurrentLocation.getLabel() ))
   // {
   // if (newCurrentLocation.getPlaceId() > 0 && oldCurrentLocation.getPlaceId() > 0
   // && newCurrentLocation.getPlaceId() != oldCurrentLocation.getPlaceId())
   // {
   // logger
   // .error( "Two places appliccable to same user share names, consider merging: '"
   // + newCurrentLocation.getPlaceName()
   // + "' (pid "
   // + newCurrentLocation.getPlaceId()
   // + " & pid "
   // + oldCurrentLocation.getPlaceId() + ")" );
   // logger.info( "update needed pid " + newCurrentLocation.getPlaceId() );
   // update = true;
   //
   // }
   // }
   // else
   // {
   // int newPid = newCurrentLocation.getPlaceId();
   // int oldPid = oldCurrentLocation.getPlaceId();
   // int newErr = newCurrentLocation.getMostAccurateLayer().getDefaultError();
   // int oldErr = oldCurrentLocation.getMostAccurateLayer().getDefaultError();
   //
   // // in general, when label has changed, do update
   // update = true;
   // logger.info( "label different, update needed..." );
   //
   // // one exception is if place stays the same but the motion state changes from
   // // stationary to restless or moving. in this case we'd rather wait until another
   // // place is found.
   // // this reduces "near" toggling
   // if (newPid == oldPid && oldPid > 0 && oldCurrentLocation.isPlaceFix())
   // {
   // logger
   // .info( "old loc was fix, new is not, don't think I want to update after all..." );
   // update = false;
   // }
   //
   // // if no place is found and the most accurate information degrades from, say
   // // "area" to "city" or even "country", don't bother to update
   // // this reduces "near" toggling
   // if (newPid < 0 && oldErr < newErr)
   // {
   // logger
   // .debug( "old loc was more accurate, don't think I want to update after all..." );
   // update = false;
   // }
   //
   // }
   //
   // return update;
   // }
   //

   private void pushPopulationUpdate(int placeId, int population) throws SQLException
   {
      if (placeId > 0)
      {
         for (int userId : lDB.getPlaceSubscribers( placeId ))
         {
            LocationUser user = lDB.getLocationUser( userId );
            if (user != null)
            {
               logger.info( user + ": Pushing population update of place " + placeId );

               InfoQuery iq =
                  XmppExtensionsFactory.createPlacePopulationPacket( stream
                     .getDataFactory(), user.getJid(), placeId, population );

               outQ.enque( iq );
            }
            else
            {
               logger.error( "Failed to push population update of place " + placeId
                             + " to user " + userId + ": User does not exist!" );
            }

         }
      }
   }


   // @SuppressWarnings("unused")
   // private void sendGroupHelpMessage(String groupName, JID sender, String message)
   // {
   //
   // // String sender = "help@" + Constants.xmppHostName + "/help";
   // JID recipient = new JID( groupName + "@" + Constants.xmppConferenceHostName );
   // String userName = "buddycloud";
   //
   // final Packet presencePacket =
   // XmppUtils.createMucPresence( stream.getDataFactory(), sender, recipient,
   // userName );
   //
   // final Packet messagePacket =
   // XmppUtils.createGroupMessage( stream.getDataFactory(), sender, recipient,
   // message );
   //
   // final Packet absencePacket =
   // XmppUtils
   // .createMucAbsence( stream.getDataFactory(), sender, recipient, userName );
   //
   // Thread delegate = new Thread()
   // {
   //
   // public void run()
   // {
   // // send packets with a bit of delay, otherwise message
   // // might be rejected as spam
   // try
   // {
   // logger.info( "HELP: MUC message: Sending presence packet..." );
   // outQ.enque( presencePacket );
   // Thread.sleep( 10000 );
   // logger.info( "HELP: MUC message: Sending message packet..." );
   // outQ.enque( messagePacket );
   // Thread.sleep( 10000 );
   // logger.info( "HELP: MUC message: Sending absence packet..." );
   // outQ.enque( absencePacket );
   // logger.info( "HELP: MUC message sent" );
   // }
   // catch (Exception e)
   // {
   // logger.error(
   // "HELP: Failed to send group help message: " + e.getMessage(), e );
   // }
   // }
   // };
   // delegate.start();
   //
   // }

   private void sendFirstTimeUserWelcomeMessage(LocationUser user, String location)
   {
      if (welcomeMessageFormat == null || welcomeMessageFormat.length() == 0)
      {
         logger
            .error( user
                    + " : HELP: No welcome message format defined in prefs, skipping." );
         return;
      }
      if (welcomeMessageToJid == null)
      {
         logger
            .error( user
                    + " : HELP: No welcome message to JID defined in prefs, skipping." );
         return;
      }
      if (welcomeMessageFromJid == null)
      {
         logger
            .error( user
                    + " : HELP: No welcome message from JID defined in prefs, skipping." );
         return;
      }

      String welcomingJid = user.getJid().getNode() + "@" + user.getJid().getDomain();
      String welcomeMessage =
         String.format( welcomeMessageFormat, welcomingJid, location.toString() );

      logger.info( user + " : HELP: Sending welcome message from "
                   + welcomeMessageFromJid + " to " + welcomeMessageToJid );
      logger.info( user + " : HELP: " + welcomeMessage );

      // post to NG welcome channel
      Packet pubsubMessagePacket =
         XmppUtils.createPubsubMessage( stream.getDataFactory(), "/channel/welcome",
            welcomeMessageFromJid, welcomeMessageFromName, welcomeMessage );

      logger.debug( "Sending welcome message to pubsub: " + pubsubMessagePacket );
      outQ.enque( pubsubMessagePacket );
   }


   private void sendUserHelpMessage(LocationUser user, String message)
   {

      JID sender = new JID( "help@" + Constants.XMPP_HOST_NAME + "/help" );
      JID recipient = user.getJid();

      Packet messagePacket =
         XmppUtils.createMessage( stream.getDataFactory(), sender, recipient, message );

      outQ.enque( messagePacket );

   }


   private LocationUser getOrAddUser(JID jid, boolean addIfNotFound) throws SQLException
   {
      if (isConference( jid ))
      {
         logger.error( "JID " + jid + " represents a conference, not a user" );
         return null;
      }
      else
      {
         LocationUser u = lDB.getLocationUser( jid.toBareJID() );
         if (u == null && addIfNotFound)
         {
            u = lDB.addLocationUser( jid );
            logger.info( u + ": New location user! Added to DB" );
         }
         return u;
      }
   }
}
