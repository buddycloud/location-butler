
package com.buddycloud.nearby.xmpp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jabberstudio.jso.InfoQuery;
import org.jabberstudio.jso.JID;
import org.jabberstudio.jso.NSI;
import org.jabberstudio.jso.Packet;
import org.jabberstudio.jso.PacketError;
import org.jabberstudio.jso.StreamDataFactory;
import org.jabberstudio.jso.StreamElement;
import org.jabberstudio.jso.format.DateTimeProfileFormat;

import com.buddycloud.channels.Channel;
import com.buddycloud.common.Place;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.xmpp.PacketFilter;
import com.buddycloud.common.xmpp.PacketHandler;
import com.buddycloud.common.xmpp.XmppUtils;
import com.buddycloud.geoid.Point;
import com.buddycloud.location.sql.LocationDbAccess;
import com.buddycloud.nearby.NearbyObject;
import com.buddycloud.nearby.sql.NearbyDbAccess;

/**
 * Handler for nearby (location discovery) queries
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

public class NearbyQueryHandler extends PacketHandler
{

   private static final String NEARBY_NAME_SPACE = "urn:oslo:nearbyobjects";

   private enum NearbyObjectType {
      PERSON, PLACE, CHANNEL
   };

   private NearbyDbAccess nDB;
   private LocationDbAccess lDB;

   private String logPrefix;


   public NearbyQueryHandler()
   {

      super();

      this.nDB = new NearbyDbAccess();
      this.lDB = new LocationDbAccess();

   }


   @Override
   public Collection<PacketFilter> getHandledPacketFilters()
   {

      ArrayList<PacketFilter> filters = new ArrayList<PacketFilter>();
      PacketFilter nearbyFilter = new PacketFilter();
      nearbyFilter.addNamespace( "nearby", NEARBY_NAME_SPACE );
      nearbyFilter.setXPath( "jabber:iq[@type='get']/nearby:query" );

      filters.add( nearbyFilter );
      return filters;
   }


   @SuppressWarnings("unchecked")
   @Override
   protected Packet handlePacket(Packet p)
   {

      // make sure that the packet was not sent from a conference
      if (!isUser( p.getFrom() ))
      {
         logger.error( "Packet received from non-user: " + p.getFrom() + ". Ignored" );
      }

      if (!( p instanceof InfoQuery ))
      {
         throw new IllegalArgumentException( "Unsupported package class: "
                                             + p.getClass().getName() );
      }

      InfoQuery iq = (InfoQuery) p;

      // Prepare default reply IQ
      InfoQuery response = (InfoQuery) iq.copy();
      response.setID( iq.getID() );
      response.setTo( iq.getFrom() );
      response.setFrom( iq.getTo() );
      response.setType( InfoQuery.RESULT );

      try
      {
      	logPrefix = iq.getFrom().getNode() + "@" + iq.getFrom().getDomain() + ": ";

         StreamElement query = iq.getFirstElement( "query" );

         // parse point
         Point geoRef = null;
         LocationUser userRef = null;
         StreamElement pointElement = query.getFirstElement( "point" );
         if (pointElement != null)
         {
            String lat = pointElement.getAttributeValue( "lat" );
            String lon = pointElement.getAttributeValue( "lon" );
            try
            {
               geoRef = new Point( Double.parseDouble( lat ), Double.parseDouble( lon ) );
               logger.info( logPrefix + "Geo reference from supplied lat/lon " + geoRef );
            }
            catch (Exception e)
            {
               String errmsg = "Invalid latitude/longitude: " + lat + ", " + lon;
               logger.error( logPrefix + errmsg );
               return XmppUtils.createBadRequestError( iq, errmsg );
            }

         }

         // parse reference
         StreamElement referenceElement = query.getFirstElement( "reference" );
         if (referenceElement != null)
         {
            String typeStr = referenceElement.getAttributeValue( "type" );
            String idStr = referenceElement.getAttributeValue( "id" );
            if (typeStr == null || typeStr.length() == 0)
               typeStr = "user";
            else if (typeStr.equalsIgnoreCase( "person" ))
               typeStr = "user";

            try
            {
               if (typeStr.equalsIgnoreCase( "user" ))
               {
                  JID jid = new JID( idStr );
                  userRef = nDB.getLocationUser( jid );
                  geoRef = lDB.getCurrentLocation( userRef.getId() ).getPoint();
                  logger.info( logPrefix + "User as reference: " + userRef + ", curr loc: "+geoRef );
               }
               else if (typeStr.equalsIgnoreCase( "place" ))
               {
                  String pidStr = idStr.substring( idStr.lastIndexOf( "/" ) );
                  int pid = Integer.parseInt( pidStr );
                  Place place = nDB.getPlace( pid );
                  geoRef = new Point( place.getLatitude(), place.getLongitude() );
                  logger.info( logPrefix + "Geo reference from supplied place (" + place + "):" + geoRef );
               }
               else if (typeStr.equalsIgnoreCase( "channel" ))
               {
                  String errmsg = "Channel as reference not yet supported";
                  logger.error( logPrefix + errmsg );
                  return XmppUtils.createBadRequestError( p, errmsg );
               }
               else
               {
                  String errmsg = "Unsupported reference type '" + typeStr + "'";
                  logger.error( logPrefix + errmsg );
                  return XmppUtils.createBadRequestError( p, errmsg );
               }
            }
            catch (Exception e)
            {
               String errmsg =
                  "Invalid reference type/id '" + typeStr + "'/'" + idStr + "'";
               logger.error( logPrefix + errmsg );
               return XmppUtils.createBadRequestError( p, errmsg );
            }
         }

         if (geoRef.isDefault())
         {
            String errmsg = "No reference supplied";
            logger.error( logPrefix + errmsg );
            return XmppUtils.createBadRequestError( p, errmsg );
         }

         // parse requests
         List<StreamElement> requestElementList = query.listElements( "request" );
         ArrayList<NearbyObjectType> requests = new ArrayList<NearbyObjectType>();
         for (StreamElement requestElement : requestElementList)
         {
            String request = requestElement.getAttributeValue( "var" );
            request = request.trim().toUpperCase();
            try
            {
               requests.add( NearbyObjectType.valueOf( request ) );
            }
            catch (Exception e)
            {
               logger
                  .error( logPrefix + "Invalid request type: '" + request + "'. Ignored." );
            }
         }

         if (requests.size() == 0)
         {
            requests.add( NearbyObjectType.PERSON );
            requests.add( NearbyObjectType.PLACE );
            requests.add( NearbyObjectType.CHANNEL );
         }

         logger.info( logPrefix + "Requested objects: " );
         for (NearbyObjectType n : requests)
         {
            logger.info( logPrefix + "   " + n );
         }

         StreamElement optionsElement = query.getFirstElement( "options" );
         int limit = 100;
         int range = 6000000;
         long since = System.currentTimeMillis() - 7 * 84600000; // 7 days
         if (optionsElement != null)
         {
            String limitString = optionsElement.getAttributeValue( "limit" );
            String rangeString = optionsElement.getAttributeValue( "range" );
            String sinceString = optionsElement.getAttributeValue( "since" );
            try
            {
               limit = Integer.parseInt( limitString );
            }
            catch (Exception e)
            {
            }
            try
            {
               range = Integer.parseInt( rangeString );
            }
            catch (Exception e)
            {
            }
            try
            {
               since =
                  DateTimeProfileFormat.getInstance( DateTimeProfileFormat.DATETIME )
                     .parse( sinceString ).getTime();
            }
            catch (Exception e)
            {
            }

         }

         logger.info( logPrefix + "Limit: " + limit );
         logger.info( logPrefix + "Range: " + range );
         logger.info( logPrefix + "Since: " + ( System.currentTimeMillis() - since )
                      / 3600000 + " hrs ago" );

         Collection<NearbyObject<LocationUser>> people =
            new ArrayList<NearbyObject<LocationUser>>();
         Collection<NearbyObject<Place>> places = new ArrayList<NearbyObject<Place>>();
         Collection<NearbyObject<Channel>> channels =
            new ArrayList<NearbyObject<Channel>>();

         if (requests.contains( NearbyObjectType.PERSON ))
         {
               people = nDB.getNearestUsers( geoRef, limit, range, since );
         }

         if (requests.contains( NearbyObjectType.PLACE ))
         {
               places = nDB.getNearestPlaces( geoRef, limit, range );
         }

         if (requests.contains( NearbyObjectType.CHANNEL ))
         {
               channels = nDB.getNearestChannels( geoRef, limit, range, since );
         }

         return buildReturnStanza( iq, people, places, channels );

      }
      catch (Exception e)
      {
         logger.error( logPrefix + "Failed to handle nearby query:" + e.getMessage() );
         response.setError( iq.getDataFactory().createPacketError( PacketError.WAIT,
            PacketError.INTERNAL_SERVER_ERROR_CONDITION ) );
      }

      return response;
   }


   /**
    * @param iq
    * @param people
    * @param places
    * @param channels
    * @return
    */
   private Packet buildReturnStanza(InfoQuery iq,
      Collection<NearbyObject<LocationUser>> people,
      Collection<NearbyObject<Place>> places, Collection<NearbyObject<Channel>> channels)
   {
      StreamDataFactory sdf = iq.getDataFactory();
      InfoQuery result =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );

      result.setType( InfoQuery.RESULT );
      result.setFrom( iq.getTo() );
      result.setTo( iq.getFrom() );
      result.setID( iq.getID() );

      StreamElement query = result.addElement( "query", NEARBY_NAME_SPACE );
      StreamElement items = query.addElement( "items" );
      for (NearbyObject<LocationUser> u : people)
      {
         logger.info( logPrefix + "NERABY PEOPLE: " + u );
         StreamElement item =
            buildNerarbyItemElement( items, u.getObject().getJid().getNode(), null, u );
         item.setAttributeValue( "id", u.getObject().getJid().toString() );
         item.setAttributeValue( "var", "person" );
      }
      for (NearbyObject<Place> p : places)
      {
         logger.info( logPrefix + "NEARBY PLACES: " + p );
         StreamElement item =
            buildNerarbyItemElement( items, p.getObject().getName(), p.getObject()
               .getDescription(), p );
         item.setAttributeValue( "id", "http://buddycloud.com/places/"
                                       + p.getObject().getId() );
         item.setAttributeValue( "var", "place" );
      }
      for (NearbyObject<Channel> c : channels)
      {
         logger.info( logPrefix + "NEARBY CHANNELS: " + c );
         StreamElement item =
            buildNerarbyItemElement( items, c.getObject().getTitle().toString(), c.getObject()
               .getDescription(), c );
         item.setAttributeValue( "id", c.getObject().getNode().toString() );
         item.setAttributeValue( "var", "channel" );
      }

      return result;
   }


   private StreamElement buildNerarbyItemElement(StreamElement items, String name,
      String description, NearbyObject<?> o)
   {
      StreamElement item = items.addElement( "item" );
      addChildElementTextIfNotNull( item, "name", name );
      addChildElementTextIfNotNull( item, "description", description );
      addChildElementTextIfNotNull( item, "distance", o.getDistance() );

      if (o.getLocation() != null)
      {
         // create geoloc sub element
         StreamElement geoloc =
            item.addElement( new NSI( "geoloc", "http://jabber.org/protocol/geoloc" ) );
         geoloc.setAttributeValue( "xml:lang", "en" );

         addChildElementTextIfNotNull( geoloc, "text", o.getLocation().toString() );
         addChildElementTextIfNotNull( geoloc, "area", o.getLocation().getArea() );
         addChildElementTextIfNotNull( geoloc, "locality", o.getLocation().getCity() );
         addChildElementTextIfNotNull( geoloc, "region", o.getLocation().getRegion() );
         if (o.getLocation().getCountryCode() != null)
         {
            addChildElementTextIfNotNull( geoloc, "country", o.getLocation()
               .getCountryCode().getEnglishCountryName() );
         }
      }
      return item;

   }


   @Override
   protected void handleStop()
   {

      // gracefully shutdown DB connection
      try
      {
         logger.debug( "Closing DB conncetion..." );
         nDB.disconnect();
         logger.debug( "DB conncetion closed." );
      }
      catch (SQLException sqle)
      {
         logger.error( "Failed to close DB conncetion.", sqle );
      }
   }

}
