
package com.buddycloud.location.xmpp;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jabberstudio.jso.InfoQuery;
import org.jabberstudio.jso.JID;
import org.jabberstudio.jso.NSI;
import org.jabberstudio.jso.StreamDataFactory;
import org.jabberstudio.jso.StreamElement;

import com.buddycloud.Constants;
import com.buddycloud.common.Location;
import com.buddycloud.common.LocationConstants;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.Place;
import com.buddycloud.location.LocationQuery;

/**
 * Factory for Buddycloud Jabber extension specific stream elements.
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

public class XmppExtensionsFactory
{

   private static final JID PUBSUB_SERVER_JID =
      new JID( "broadcaster." + Constants.XMPP_HOST_NAME );

   private static final JID BUTLER_JID =
      new JID( Constants.BUTLER_NODE_NAME + "." + Constants.XMPP_HOST_NAME );

   private static int PACKET_COUNT;

   private static Logger logger = Logger.getLogger( XmppExtensionsFactory.class );


   private static void addTextElementIfNotNull(StreamElement parent, String elementName,
      Object value)
   {
      if (value != null && value.toString().length() > 0)
      {
         StreamElement e = parent.addElement( elementName );
         e.addText( value.toString() );
      }
   }


   private static void addTextElementIfNotZero(StreamElement parent, String elementName,
      double value)
   {
      if (value != 0.0)
      {
         StreamElement e = parent.addElement( elementName );
         e.addText( "" + value );
      }
   }


   private static void createField(StreamElement parent, String name, String type)
   {
      StreamElement f = parent.addElement( "field" );
      f.setAttributeValue( "type", type );
      f.setAttributeValue( "var", name );
   }


   private static void createFieldValue(StreamElement parent, String name, String value)
   {
      if (value != null && value.length() > 0)
      {
         StreamElement f = parent.addElement( "field" );
         f.setAttributeValue( "type", "text-single" );
         f.setAttributeValue( "var", name );
         StreamElement v = f.addElement( "value" );
         v.addText( value );
      }
   }


   private static void createGeolocElement(StreamElement parent, LocationQuery query,
      Location l)
   {

      StreamElement geoloc =
         parent.addElement( new NSI( "geoloc", "http://jabber.org/protocol/geoloc" ) );
      geoloc.setAttributeValue( "xml:lang", "en" );

      Locale locale = Locale.US;
      if (query != null && query.getLocale() != null)
      {
         locale = query.getLocale();
      }

      // set fields
      if (l.getPlaceId() > 0)
      {
         StreamElement e = geoloc.addElement( "uri" );
         e.addText( "http://buddycloud.com/places/" + l.getPlaceId() );
      }

      addTextElementIfNotNull( geoloc, "text", l.getLabel() );
      addTextElementIfNotNull( geoloc, "street", l.getStreet() );
      addTextElementIfNotNull( geoloc, "area", l.getArea() );
      addTextElementIfNotNull( geoloc, "locality", l.getCity() );
      addTextElementIfNotNull( geoloc, "postalcode", l.getPostalCode() );
      addTextElementIfNotNull( geoloc, "region", l.getRegion() );
      if (l.getCountryCode() != null)
      {
         addTextElementIfNotNull( geoloc, "country", l.getCountryCode().getCountryName(
            locale ) );
      }
      addTextElementIfNotZero( geoloc, "lat", l.getLatitude() );
      addTextElementIfNotZero( geoloc, "lon", l.getLongitude() );
      addTextElementIfNotZero( geoloc, "accuracy", l.getAccuracy() );

      // add extra info piped through from a location query
      if (query != null)
      {
         addTextElementIfNotZero( geoloc, "speed", query.getSpeed() );
         addTextElementIfNotZero( geoloc, "bearing", query.getBearing() );
         addTextElementIfNotZero( geoloc, "altitude", query.getAltitude() );
         addTextElementIfNotNull( geoloc, "datum", query.getDatum() );
      }
   }


   public static InfoQuery createLocationResultPacket(InfoQuery in, LocationQuery query,
      Location res)
   {
      if (res == null)
         throw new IllegalArgumentException(
            "Can not create location result packet for a location that is null" );

      StreamDataFactory sdf = in.getDataFactory();
      InfoQuery infoQuery =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      infoQuery.setFrom( new JID( Constants.BUTLER_NODE_NAME + "."
                                  + Constants.XMPP_HOST_NAME ) );
      infoQuery.setType( InfoQuery.RESULT );
      infoQuery.setTo( in.getFrom() );
      infoQuery.setID( in.getID() );

      if (query.isPublish())
      {

         StreamElement locationElement =
            infoQuery.addElement( new NSI( "location",
               "http://buddycloud.com/protocol/location" ) );

         locationElement.setAttributeValue( "state", ""
                                                     + res.getMotionState().toString()
                                                        .toLowerCase() );
         int scaledPQ = 0;
         if (res.getCellPatternQuality() >= LocationConstants.MOTION_STATE_STATIONARY_CONFIDENCE_LIMIT)
         {
            scaledPQ = 100;
         }
         else if (res.getCellPatternQuality() > LocationConstants.MOTION_STATE_MOVING_CONFIDENCE_LIMIT)
         {
            double range =
               LocationConstants.MOTION_STATE_STATIONARY_CONFIDENCE_LIMIT
                  - LocationConstants.MOTION_STATE_MOVING_CONFIDENCE_LIMIT;
            double q1 = LocationConstants.MOTION_STATE_MOVING_CONFIDENCE_LIMIT;
            scaledPQ = (int) ( 100 * ( res.getCellPatternQuality() - q1 ) / range );
         }
         setAttributeIfNotNull( locationElement, "cellpatternquality", "" + scaledPQ );
         setAttributeIfNotNull( locationElement, "placeid", "" + res.getPlaceId() );
         setAttributeIfNotNull( locationElement, "label", "" + res.getLabel() );
      }
      else
      {
         logger.debug( infoQuery.getTo()
                       + ": publish = false: Creating full result packet" );
         logger.debug( infoQuery.getTo() + ": Location: " + res );
         for (String s : res.toStrings())
         {
            logger.debug( infoQuery.getTo() + ": " + s );
         }
         createGeolocElement( infoQuery, query, res );
         logger.debug( infoQuery );

      }
      return infoQuery;
   }


   /**
    * @deprecated Use new nearby formats (NearbyQueryHandler)
    * @param request
    * @param nearbyUsers
    * @param nearbyUserLocations
    * @return
    */
   public static InfoQuery createNearbyUserListResultForm(InfoQuery request,
      Collection<LocationUser> nearbyUsers, Collection<String> nearbyUserLocations)
   {
      StreamDataFactory sdf = request.getDataFactory();

      InfoQuery results =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      results.setFrom( new JID( Constants.BUTLER_NODE_NAME + "."
                                + Constants.XMPP_HOST_NAME ) );
      results.setTo( request.getFrom() );
      results.setType( InfoQuery.RESULT );
      results.setID( request.getID() );

      StreamElement command =
         results.addElement( new NSI( "command", "http://jabber.org/protocol/commands" ) );
      command.setAttributeValue( "node", "location" );
      StreamElement x = command.addElement( new NSI( "x", "jabber:x:data" ) );
      x.setAttributeValue( "type", "form" );

      // header
      StreamElement reported = x.addElement( "reported" );
      createField( reported, "jid", "text-single" );
      createField( reported, "locationid", "text-single" );
      createField( reported, "locationname", "text-single" );

      Iterator<LocationUser> userIter = nearbyUsers.iterator();
      Iterator<String> locationIter = nearbyUserLocations.iterator();
      while (userIter.hasNext())
      {
         LocationUser u = userIter.next();
         if (u != null)
         {
            String l = locationIter.next();
            StreamElement item = x.addElement( "item" );
            createFieldValue( item, "jid", u.getJid().toString() );
            createFieldValue( item, "locationname", l );
         }

      }
      return results;
   }


   /**
    * @deprecated
    */
   public static InfoQuery createPepCurrentLocationPacket(StreamDataFactory sdf, JID jid,
      Location l)
   {
      return createPepCurrentLocationPacket( sdf, jid, null, l );
   }


   /**
    * @deprecated use createPubsubCurrentLocationPacket
    */
   public static InfoQuery createPepCurrentLocationPacket(StreamDataFactory sdf, JID jid,
      LocationQuery q, Location l)
   {
      return createPubsubLocationPacket( sdf, "http://jabber.org/protocol/geoloc", jid,
         jid, q, l );
   }


   /**
    * @deprecated use createPubsubNextLocationPacket
    */
   public static InfoQuery createPepNextLocationPacket(StreamDataFactory sdf, JID jid,
      Location l)
   {
      return createPubsubLocationPacket( sdf, "http://jabber.org/protocol/geoloc-next",
         jid, jid, null, l );
   }


   /**
    * @deprecated use createPubsubPreviousLocationPacket
    */
   public static InfoQuery createPepPreviousLocationPacket(StreamDataFactory sdf,
      JID jid, Location l)
   {
      return createPubsubLocationPacket( sdf, "http://jabber.org/protocol/geoloc-prev",
         jid, jid, null, l );
   }


   public static InfoQuery createPlaceDetailsStanza(InfoQuery request, Place p)
   {
      StreamDataFactory sdf = request.getDataFactory();

      InfoQuery results =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      results.setFrom( new JID( Constants.BUTLER_NODE_NAME + "."
                                + Constants.XMPP_HOST_NAME ) );
      results.setTo( request.getFrom() );
      results.setType( InfoQuery.RESULT );
      results.setID( request.getID() );

      StreamElement command =
         results.addElement( new NSI( "command", "http://jabber.org/protocol/commands" ) );
      command.setAttributeValue( "node", "location" );
      StreamElement x = command.addElement( new NSI( "x", "jabber:x:data" ) );
      x.setAttributeValue( "type", "form" );
      StreamElement item = x.addElement( "item" );

      createFieldValue( item, "id", "" + p.getId() );
      createFieldValue( item, "visibility", p.isPublic() ? "public" : "private" );
      createFieldValue( item, "name", p.getName() );
      createFieldValue( item, "description", p.getDescription() );
      createFieldValue( item, "street", p.getStreet() );
      // TODO: stop using deprecated field "district"
      createFieldValue( item, "district", p.getCity() + " " + p.getPostalCode() );
      createFieldValue( item, "area", p.getArea() );
      createFieldValue( item, "city", p.getCity() );
      createFieldValue( item, "postalcode", p.getPostalCode() );
      createFieldValue( item, "region", p.getRegion() );
      createFieldValue( item, "country", p.getCountryCode().getCountryName(
         request.getLocale() ) );
      if (p.getLatitude() != 0.0)
         createFieldValue( item, "latitude", "" + p.getLatitude() );
      if (p.getLongitude() != 0.0)
         createFieldValue( item, "longitude", "" + p.getLongitude() );
      createFieldValue( item, "wikiurl", p.getWikiUrl() );
      createFieldValue( item, "siteurl", p.getSiteUrl() );
      return results;
   }


   public static InfoQuery createPlacePopulationPacket(StreamDataFactory sdf, JID jid,
      int locationId, int population)
   {

      InfoQuery infoQuery =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      infoQuery.setFrom( new JID( Constants.BUTLER_NODE_NAME + "."
                                  + Constants.XMPP_HOST_NAME ) );
      infoQuery.setTo( jid );
      infoQuery.setType( InfoQuery.SET );
      infoQuery.setID( "pub" + getPacketCount() );

      StreamElement location =
         infoQuery.addElement( new NSI( "place",
            "http://buddycloud.com/protocol/location" ) );
      StreamElement id = location.addElement( "id" );
      id.addText( "" + locationId );

      StreamElement pop = location.addElement( "population" );
      pop.addText( "" + population );

      return infoQuery;
   }


   public static InfoQuery createPlaceResultForm(InfoQuery request,
      Collection<Integer> placeIds, Collection<String> placeNames,
      Collection<String> placeLocations, Collection<Integer> populations)
   {
      StreamDataFactory sdf = request.getDataFactory();

      InfoQuery results =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      results.setFrom( new JID( Constants.BUTLER_NODE_NAME + "."
                                + Constants.XMPP_HOST_NAME ) );
      results.setTo( request.getFrom() );
      results.setType( InfoQuery.RESULT );
      results.setID( request.getID() );

      StreamElement command =
         results.addElement( new NSI( "command", "http://jabber.org/protocol/commands" ) );
      command.setAttributeValue( "node", "location" );
      StreamElement x = command.addElement( new NSI( "x", "jabber:x:data" ) );
      x.setAttributeValue( "type", "form" );

      // header
      StreamElement reported = x.addElement( "reported" );
      createField( reported, "id", "text-single" );
      createField( reported, "name", "text-single" );
      createField( reported, "location", "text-single" );
      createField( reported, "visibility", "text-single" );
      createField( reported, "population", "text-single" );

      Iterator<Integer> ids = placeIds.iterator();
      Iterator<String> names = placeNames.iterator();
      Iterator<String> locs = placeLocations.iterator();
      Iterator<Integer> pops = populations.iterator();
      while (ids.hasNext())
      {
         int id = ids.next();
         String name = names.next();
         String adr = locs.next();
         int pop = pops.next();

         StreamElement item = x.addElement( "item" );
         createFieldValue( item, "id", "" + id );
         createFieldValue( item, "name", name );
         createFieldValue( item, "location", adr );
         createFieldValue( item, "visibility", "public" );
         createFieldValue( item, "population", "" + pop );

      }
      return results;
   }


   public static InfoQuery createPlaceResultForm(InfoQuery request,
      Collection<Place> locations, Collection<Integer> populations)
   {
      StreamDataFactory sdf = request.getDataFactory();

      InfoQuery results =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      results.setFrom( new JID( Constants.BUTLER_NODE_NAME + "."
                                + Constants.XMPP_HOST_NAME ) );
      results.setTo( request.getFrom() );
      results.setType( InfoQuery.RESULT );
      results.setID( request.getID() );

      StreamElement command =
         results.addElement( new NSI( "command", "http://jabber.org/protocol/commands" ) );
      command.setAttributeValue( "node", "location" );
      StreamElement x = command.addElement( new NSI( "x", "jabber:x:data" ) );
      x.setAttributeValue( "type", "form" );

      // header
      StreamElement reported = x.addElement( "reported" );
      createField( reported, "name", "text-single" );
      createField( reported, "id", "text-single" );
      createField( reported, "visibility", "text-single" );
      createField( reported, "population", "text-single" );

      Iterator<Place> lociter = locations.iterator();
      Iterator<Integer> popiter = populations.iterator();
      while (lociter.hasNext())
      {
         Place l = lociter.next();
         int population = popiter.next();
         StreamElement item = x.addElement( "item" );
         createFieldValue( item, "name", l.getName() );
         createFieldValue( item, "id", "" + l.getId() );
         createFieldValue( item, "visibility", l.isPublic() ? "public" : "private" );
         createFieldValue( item, "population", "" + population );

      }
      return results;
   }


   public static InfoQuery createPlaceSearchResultForm(InfoQuery request,
      Collection<Place> searchResults)
   {

      if (searchResults == null)
         throw new IllegalArgumentException( "Search results may not be null" );
      StreamDataFactory sdf = request.getDataFactory();

      InfoQuery results =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      results.setFrom( new JID( Constants.BUTLER_NODE_NAME + "."
                                + Constants.XMPP_HOST_NAME ) );
      results.setTo( request.getFrom() );
      results.setType( InfoQuery.RESULT );
      results.setID( request.getID() );

      StreamElement command =
         results.addElement( new NSI( "command", "http://jabber.org/protocol/commands" ) );
      command.setAttributeValue( "node", "location" );
      StreamElement x = command.addElement( new NSI( "x", "jabber:x:data" ) );
      x.setAttributeValue( "type", "form" );

      // header
      StreamElement reported = x.addElement( "reported" );
      createField( reported, "name", "text-single" );
      createField( reported, "street", "text-single" );
      createField( reported, "area", "text-single" );
      createField( reported, "district", "text-single" );
      createField( reported, "country", "text-single" );
      createField( reported, "region", "text-single" );
      createField( reported, "city", "text-single" );
      createField( reported, "postalcode", "text-single" );
      createField( reported, "latitude", "text-single" );
      createField( reported, "longitude", "text-single" );

      Iterator<Place> i = searchResults.iterator();
      while (i.hasNext())
      {
         Place res = i.next();
         StreamElement item = x.addElement( "item" );
         createFieldValue( item, "name", res.getName() );
         createFieldValue( item, "street", res.getStreet() );
         createFieldValue( item, "area", res.getArea() );
         createFieldValue( item, "city", res.getCity() );
         createFieldValue( item, "postalcode", res.getPostalCode() );
         // TODO: stop using deprecated field "district"
         createFieldValue( item, "district", res.getCity() + " " + res.getPostalCode() );
         createFieldValue( item, "region", res.getRegion() );
         createFieldValue( item, "country", res.getCountryCode().getCountryName(
            request.getLocale() ) );
         createFieldValue( item, "latitude", "" + res.getLatitude() );
         createFieldValue( item, "longitude", "" + res.getLongitude() );

      }
      return results;

   }


   public static InfoQuery createPubsubCurrentLocationPacket(StreamDataFactory sdf,
      JID jid, Location l)
   {
      return createPubsubCurrentLocationPacket( sdf, jid, null, l );
   }


   public static InfoQuery createPubsubCurrentLocationPacket(StreamDataFactory sdf,
      JID jid, LocationQuery q, Location l)
   {
      return createPubsubLocationPacket( sdf,
         "/user/" + jid.toBareJID() + "/geo/current", BUTLER_JID, PUBSUB_SERVER_JID, q, l );
   }


   private static InfoQuery createPubsubLocationPacket(StreamDataFactory sdf,
      String nodeNameSpace, JID from, JID to, LocationQuery q, Location l)
   {

      InfoQuery infoQuery =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      infoQuery.setFrom( from.toBareJID() );
      infoQuery.setTo( to.toBareJID() ); // NOTE: no resource
      infoQuery.setType( InfoQuery.SET );
      infoQuery.setID( "pub" + getPacketCount() );

      StreamElement pubsub =
         infoQuery.addElement( new NSI( "pubsub", "http://jabber.org/protocol/pubsub" ) );
      StreamElement publish = pubsub.addElement( "publish" );
      publish.setAttributeValue( "node", nodeNameSpace );

      StreamElement item = publish.addElement( "item" );
      createGeolocElement( item, q, l );

      logger.debug( infoQuery.toString() );
      return infoQuery;
   }


   public static InfoQuery createPubsubNextLocationPacket(StreamDataFactory sdf, JID jid,
      Location l)
   {
      return createPubsubLocationPacket( sdf, "/user/" + jid.toBareJID() + "/geo/future",
         BUTLER_JID, PUBSUB_SERVER_JID, null, l );
   }


   public static InfoQuery createPubsubNextLocationPacket(StreamDataFactory sdf,
      JID jid, LocationQuery q, Location l)
   {
      return createPubsubLocationPacket( sdf,
         "/user/" + jid.toBareJID() + "/geo/future", BUTLER_JID, PUBSUB_SERVER_JID, q, l );
   }


   public static InfoQuery createPubsubPreviousLocationPacket(StreamDataFactory sdf,
      JID jid, Location l)
   {
      return createPubsubLocationPacket( sdf, "/user/" + jid.toBareJID()
                                              + "/geo/previous", BUTLER_JID,
         PUBSUB_SERVER_JID, null, l );
   }


   public static InfoQuery createPubsubPreviousLocationPacket(StreamDataFactory sdf,
      JID jid, LocationQuery q, Location l)
   {
      return createPubsubLocationPacket( sdf,
         "/user/" + jid.toBareJID() + "/geo/previous", BUTLER_JID, PUBSUB_SERVER_JID, q, l );
   }


   public static InfoQuery createSetCurrentLocationResult(InfoQuery setQuery,
      int locationId)
   {

      StreamDataFactory sdf = setQuery.getDataFactory();

      InfoQuery result =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      result.setFrom( new JID( Constants.BUTLER_NODE_NAME + "."
                               + Constants.XMPP_HOST_NAME ) );
      result.setTo( setQuery.getFrom() );
      result.setType( InfoQuery.RESULT );
      result.setID( setQuery.getID() );

      StreamElement command =
         result.addElement( new NSI( "command", "http://jabber.org/protocol/commands" ) );
      command.setAttributeValue( "node", "location" );

      StreamElement x = command.addElement( new NSI( "x", "jabber:x:data" ) );
      x.setAttributeValue( "type", "form" );

      StreamElement field = command.addElement( "field" );
      field.setAttributeValue( "type", "text-single" );
      field.setAttributeValue( "var", "id" );

      StreamElement value = field.addElement( "value" );
      value.addText( "" + locationId );

      return result;
   }


   private static int getPacketCount()
   {
      PACKET_COUNT++;
      return PACKET_COUNT;
   }


   private static void setAttributeIfNotNull(StreamElement e, String atr, String val)
   {
      if (val != null && !val.equals( "null" ))
      {
         e.setAttributeValue( atr, val );
      }
   }

}
