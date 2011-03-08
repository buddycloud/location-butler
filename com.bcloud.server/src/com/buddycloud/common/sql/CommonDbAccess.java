/**
 * 
 */

package com.buddycloud.common.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jabberstudio.jso.JID;

import com.buddycloud.common.Location;
import com.buddycloud.common.Place;
import com.buddycloud.common.LocationUser;
import com.buddycloud.location.CountryCode;

/**
 * Database layer providing read-only access to the most common data used by several components
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
public class CommonDbAccess extends GenericDbAccess
{
   protected static final String DEFAULT_PLACE_FIELDS = "place_id, name, is_public, latitude, longitude, accuracy, defined_by, wiki_url, site_url, description, street, area, city, postal_code, region, revision, country";
   
   protected static final String DEFAULT_LOCATION_FIELDS = "pattern_id, place_id, error, timestamp, area, city, country, region, latitude, longitude, pattern_match, place, postal_code, motion_state";
   
   private PreparedStatement selectCurrentLocationStatement;

   private PreparedStatement selectPlaceStatement;

   private PreparedStatement selectLocationUserFromIdStatement;

   private PreparedStatement selectLocationUserFromJidStatement;


//   /**
//    * Extracts a Channel object from a result set. Make sure rs.next() is called before
//    * using this method.
//    * 
//    * @param rs
//    *           The ResultSet
//    * @return The Channel object
//    * @throws SQLException
//    */
//   protected Channel extractChannel(ResultSet rs) throws JIDFormatException, SQLException
//   {
//      String name = rs.getString( "name" );
//      String host = rs.getString( "hostname" );
//      Channel c = new Channel();
//      c.setId( rs.getInt( "id" ) );
//      c.setJid( new JID( name + "@" + host ) );
//      c.setTitle( rs.getString( "roomname" ) );
//      c.setSubject( rs.getString( "subject" ) );
//      c.setDescription( rs.getString( "description" ) );
//      c.setRank( rs.getInt( "rank" ) );
//      c.setPersonal( rs.getBoolean( "personal" ));
//
//      return c;
//   }
//
//
   /**
    * Extracts a Location object from a result set. Make sure rs.next() is called before
    * using this method
    * 
    * @param rs
    *           The ResultSet
    * @return The Location object
    * @throws SQLException
    */
   protected Location extractLocation(ResultSet rs) throws SQLException
   {
      Location l = new Location();
      l.setPatternId( rs.getInt( "pattern_id" ) );
      l.setPlaceId( rs.getInt( "place_id" ) );
      // Note: Label is derived info, not settable
      l.setAccuracy( rs.getInt( "error" ) );
      l.setEntryTime( rs.getTimestamp( "timestamp" ).getTime() );
      l.setArea( rs.getString( "area" ) );
      l.setCity( rs.getString( "city" ) );
      l.setCountryCode( CountryCode.getInstance( rs.getString( "country" ) ) );
      l.setRegion( rs.getString( "region" ) );
      l.setLatitude( rs.getDouble( "latitude" ) );
      l.setLongitude( rs.getDouble( "longitude" ) );
      l.setPatternMatch( rs.getInt( "pattern_match" ) );
      l.setPlaceName( rs.getString( "place" ) );
      l.setPostalCode( rs.getString( "postal_code" ) );
      l.setMotionState( Location.MotionState.valueOf( rs.getString( "motion_state" ) ) );

      if (l.getEntryTime() > System.currentTimeMillis())
      {
         logger.warn( "Location timestamp is in the future: "
                      + rs.getString( "timestamp" ) );
      }
      return l;
   }


   /**
    * Extracts a Place object from a result set. Make sure rs.next() is called before
    * using this method
    * 
    * @param rs
    *           The ResultSet
    * @return The Place object
    * @throws SQLException
    */
   protected Place extractPlace(ResultSet rs) throws SQLException
   {
      Place place = null;
      place = new Place();
      place.setId( rs.getInt( "place_id" ) );
      place.setName( rs.getString( "name" ) );
      place.setPublic( rs.getBoolean( "is_public" ) );
      place.setLatitude( rs.getDouble( "latitude" ) );
      place.setLongitude( rs.getDouble( "longitude" ) );
      place.setAccuracy( rs.getDouble( "accuracy" ) );
      place.setOwnerId( rs.getInt( "defined_by" ) );
      place.setWikiUrl( rs.getString( "wiki_url" ) );
      place.setSiteUrl( rs.getString( "site_url" ) );
      place.setDescription( rs.getString( "description" ) );
      place.setStreet( rs.getString( "street" ) );
      place.setArea( rs.getString( "area" ) );
      place.setCity( rs.getString( "city" ) );
      place.setPostalCode( rs.getString( "postal_code" ) );
      place.setRegion( rs.getString( "region" ) );
      place.setRevision( rs.getInt( "revision" ) );
      place.setCountryCode( CountryCode.getInstance( rs.getString( "country" ) ) );
      return place;
   }

   /**
    * Extracts a LocationUser object from a result set. Make sure rs.next() is called before
    * using this method
    * 
    * @param rs
    *           The ResultSet
    * @return The User object
    * @throws SQLException
    */
   protected LocationUser extractLocationUser(ResultSet rs) throws SQLException
   {
      int id = rs.getInt( "user_id" );
      String jid = rs.getString( "jid" );
      LocationUser user = new LocationUser();
      user.setId( id );
      user.setJid( new JID( jid ) );
      return user;
   }

//   /**
//    * Extracts a LocationUser object from a result set. Make sure rs.next() is called before
//    * using this method
//    * 
//    * @param rs
//    *           The ResultSet
//    * @return The User object
//    * @throws SQLException
//    */
//   protected ChannelUser extractChannelUser(ResultSet rs) throws SQLException
//   {
//      int id = rs.getInt( "id" );
//      String jid = rs.getString( "username" );
//      ChannelUser user = new ChannelUser();
//      user.setId( id );
//      user.setJid( new JID( jid ) );
//      return user;
//   }
//

//   /**
//    * Returns the Channel object for the specified channel ID or null if not found
//    * 
//    * @param channelId
//    *           The channel ID (DB primary key)
//    * @return The channel object
//    * @throws SQLException
//    *            If something wrong is not right
//    */
//   public Channel getChannel(int channelId) throws SQLException
//   {
//
//      // make sure we're connected
//      validateConnected();
//
//      // set up statement if needed
//      if (selectChannelStatement == null)
//      {
//         selectChannelStatement =
//            connection.prepareStatement( "SELECT * FROM palaver.muc_rooms WHERE id = ?;" );
//      }
//
//      // set variables
//      selectChannelStatement.setInt( 1, channelId );
//
//      // execute query
//      ResultSet rs = selectChannelStatement.executeQuery();
//
//      Channel c = null;
//
//      if (rs.next())
//      {
//         c = extractChannel( rs );
//      }
//      if(c==null){
//         logger.info( "Channel with ID "+channelId+" not found" );
//      }
//
//      // close and return
//      rs.close();
//
//      return c;
//
//   }
//

   /**
    * Returns the current location of the specified user
    * 
    * @param userId
    *           The user ID (DB primary key)
    * @return The current location, or null if none set
    * @throws SQLException
    *            if something woring is not right
    */
   public Location getCurrentLocation(int userId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectCurrentLocationStatement == null)
      {

         selectCurrentLocationStatement =
            connection
               .prepareStatement( "SELECT " + DEFAULT_LOCATION_FIELDS + " FROM current_locations WHERE user_id = ? ;" );
      }
      // set variables
      selectCurrentLocationStatement.setInt( 1, userId );

      // execute query
      ResultSet rs = selectCurrentLocationStatement.executeQuery();

      // init object
      Location l = null;
      if (rs.next())
      {
         l = extractLocation( rs );
      }

      // close and return
      rs.close();
      return l;
   }


   /**
    * Returns the Place object for the specified place ID or null if not found
    * 
    * @param placeId
    *           The place ID (DB primary key)
    * @return The place
    * @throws SQLException
    *            If something wrong is not right
    */
   public Place getPlace(int placeId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPlaceStatement == null)
      {
         selectPlaceStatement =
            connection.prepareStatement( "SELECT " + DEFAULT_PLACE_FIELDS + " FROM places WHERE place_id = ?;" );
      }

      // set variables
      selectPlaceStatement.setInt( 1, placeId );

      // execute query
      ResultSet rs = selectPlaceStatement.executeQuery();

      // create object
      Place place = null;
      if (rs.next())
      {
         place = extractPlace( rs );
      }

      // close and return
      rs.close();
      return place;
   }


   /**
    * Returns the User object for the specified user ID (DB primary key) or null if not
    * found. The user may be a buddycloud.com user or an external user (other host)
    * 
    * @param userId
    *           The user ID
    * @return The corresponding user object or null if not found
    * @throws SQLException
    *            If something wrong is not right
    */
   public LocationUser getLocationUser(int userId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectLocationUserFromIdStatement == null)
      {
         // NOTE: Use location_users rather than users, as this contains also external
         // users
         selectLocationUserFromIdStatement =
            connection
               .prepareStatement( "SELECT user_id, jid FROM location_users WHERE user_id = ?;" );
      }

      // set variables
      selectLocationUserFromIdStatement.setInt( 1, userId );

      // execute query
      ResultSet rs = selectLocationUserFromIdStatement.executeQuery();

      // create object
      LocationUser user = null;
      if (rs.next())
      {
         user = extractLocationUser( rs );
      }

      // close and return
      rs.close();
      return user;
   }

   /**
    * Returns the LocationUser object for the specified user JID (JabberID) or null if not found.
    * The user may be a buddycloud.com user or an external user (other host)
    * 
    * @param jid
    *           The the JID
    * @return The corresponding user object or null if not found
    * @throws SQLException
    *            If something wrong is not right
    */
   public LocationUser getLocationUser(JID jid) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectLocationUserFromJidStatement == null)
      {
         selectLocationUserFromJidStatement =
            connection.prepareStatement( "SELECT * FROM location_users WHERE jid = ?;" );
      }

      // set variables
      selectLocationUserFromJidStatement.setString( 1, jid.toBareJID().toString() );

      // execute query
      ResultSet rs = selectLocationUserFromJidStatement.executeQuery();

      // create object
      LocationUser user = null;
      if (rs.next())
      {
         user = extractLocationUser( rs );
         user.setJid( jid );
      }
      else
      {
         logger.error( "User \"" + jid + "\" not found in database." );
      }

      // close and return
      rs.close();
      return user;
   }

//   /**
//    * Returns the ChannelUser object for the specified user JID (JabberID) or null if not found.
//    * The user may be a buddycloud.com user or an external user (other host)
//    * 
//    * @param jid
//    *           The the JID
//    * @return The corresponding user object or null if not found
//    * @throws SQLException
//    *            If something wrong is not right
//    */
//   public ChannelUser getChannelUser(JID jid) throws SQLException
//   {
//      // make sure we're connected
//      validateConnected();
//
//      // set up statement if needed
//      if (selectChannelUserFromJidStatement == null)
//      {
//         selectChannelUserFromJidStatement =
//            connection.prepareStatement( "SELECT * FROM palaver.muc_users WHERE username = ?;" );
//      }
//
//      // set variables
//      selectChannelUserFromJidStatement.setString( 1, jid.toBareJID().toString() );
//
//      // execute query
//      ResultSet rs = selectChannelUserFromJidStatement.executeQuery();
//
//      // create object
//      ChannelUser user = null;
//      if (rs.next())
//      {
//         user = extractChannelUser( rs );
//      }
//      else
//      {
//         logger.error( "User \"" + jid + "\" not found in database." );
//      }
//
//      // close and return
//      rs.close();
//      return user;
//   }
//
}
