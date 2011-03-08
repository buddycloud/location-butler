/**
 * 
 */

package com.buddycloud.location.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jabberstudio.jso.JID;

import com.buddycloud.common.GeneralLocation;
import com.buddycloud.common.Location;
import com.buddycloud.common.LocationConstants;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.Place;
import com.buddycloud.common.sql.CommonDbAccess;
import com.buddycloud.geoid.Point;
import com.buddycloud.geoid.Position;
import com.buddycloud.location.Beacon;
import com.buddycloud.location.BeaconPattern;
import com.buddycloud.location.CountryCode;
import com.buddycloud.location.LocationQuery;

/**
 * Object interface to location database
 * 
 * TODO Generate this from a schema or something. It's too much work to maintain this
 * monster
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

public class LocationDbAccess extends CommonDbAccess
{

   /**
    * The fields to be returned when selecting a beacon object
    */
   protected static final String DEFAULT_BEACON_FIELDS =
      "beacon_id, mac, latitude, longitude, range, is_fixed, beacon_type, pos_source, country";

   private PreparedStatement deleteAllQueriesStatement;

   private PreparedStatement deleteAllQueryBeaconReferencesStatement;

   private PreparedStatement deleteBeaconPatternBeaconReferencesStatement;

   private PreparedStatement deleteBeaconPatternStatement;

   private PreparedStatement deleteCurrentLocationStatement;

   private PreparedStatement deleteLocationUserStatement;

   private PreparedStatement deletePlaceOverrideStatement;

   private PreparedStatement deletePlaceRefsFromLocationHistoryStatement;

   private PreparedStatement deletePlaceRefsFromPlaceHistoryStatement;

   private PreparedStatement deletePlaceRefsFromPlaceSubscriptionsStatement;

   private PreparedStatement deletePlaceStatement;

   private PreparedStatement deleteUserPlaceRefsFromLocationHistoryStatement;

   private PreparedStatement deleteUserPlaceRefsFromPlaceHistoryStatement;

   private PreparedStatement deleteUserPlaceRefsFromPlaceSubscriptionsStatement;

   private PreparedStatement deleteUserRefsFromLocationHistoryStatement;

   private PreparedStatement deleteUserRefsFromPlaceHistoryStatement;

   private PreparedStatement getManualOverrideCountStatement;

   private PreparedStatement getNextPlaceStatement;

   private PreparedStatement insertBeacon3rdPartyInfoStatement;

   private PreparedStatement insertBeaconStatement;

   private PreparedStatement insertGeneralLocationCacheStatement;

   private PreparedStatement insertLocationHistoryStatement;

   private PreparedStatement insertLocationQueryStatement;

   private PreparedStatement insertLocationQueryToBeaconReferenceStatement;

   private PreparedStatement insertLocationUserStatement;

   private PreparedStatement insertNextPlaceLogStatement;

   private PreparedStatement insertPatternStatement;

   private PreparedStatement insertPatternToBeaconReferenceStatement;

   private PreparedStatement insertPlaceHistoryStatement;

   private PreparedStatement insertPlaceOverrideStatement;

   private PreparedStatement insertPlaceStatement;

   private PreparedStatement insertPlaceSubscriptionStatement;

   private PreparedStatement selectAngelMessageStatement;

   private PreparedStatement selectBeacon3rdPartyInfoStatement;

   private PreparedStatement selectBeaconFromIdStatement;

   private PreparedStatement selectBeaconFromMacSatement;

   private PreparedStatement selectBeaconPatternsForPlaceStatement;

   private PreparedStatement selectCachedGeneralLocationStatement;

   private PreparedStatement selectLastLocationHistoryEntryStatement;

   private PreparedStatement selectLastPlaceHistoryEntryStatement;

   private PreparedStatement selectLocationQuerysFromTimeWindowStatement;

   private PreparedStatement selectLocationQueryToBeaconReferenceFromLocationQueryIdStatement;

   private PreparedStatement selectLogReferenceStatement;

   private PreparedStatement selectLogStatement;

   private PreparedStatement selectManualOverrideStatement;

   private PreparedStatement selectMostRecentLocationQueryStatement;

   private PreparedStatement selectOwnedPlacesStatement;

   private PreparedStatement selectPatternFromPatternIdStatement;

   private PreparedStatement selectPatternToBeaconReferencesFromBeaconIdStatement;

   private PreparedStatement selectPatternToBeaconReferencesFromPatternIdStatement;

   private PreparedStatement selectPlaceFromCoordsStatement;

   private PreparedStatement selectPlaceFromNameStatement;

   private PreparedStatement selectPlaceFromPlaceIdStatement;

   private PreparedStatement selectPlaceHistoryStatement;

   private PreparedStatement selectPlaceSubscribersFromPlaceIdStatement;

   private PreparedStatement selectPlaceSubscriptionFromUserIdStatement;

   private PreparedStatement selectPlaceSubscriptionStatement;

   private PreparedStatement selectPreviousPlaceLocationStatement;

   private PreparedStatement selectUsersAtPlaceStatement;

   private PreparedStatement setAngelMessageStatement;

   private PreparedStatement setCurrentLocationStatement;

   private PreparedStatement updateBeaconStatement;

   private PreparedStatement updateCurrentLocationStatement;

   private PreparedStatement updateLocationHistoryStatement;

   private PreparedStatement updatePlaceHistoryStatement;

   private PreparedStatement updatePlaceStatement;


   /**
    * Adds beacon information obtained from a 3rd party source to the database table
    * <i>beacon_info_3rdparty</i>
    * 
    * @param beaconId
    *           The ID of the beacon
    * @param source
    *           The source of the information
    * @param latitude
    *           The beacon position latitude in degrees range -90 to 90
    * @param longitude
    *           The beacon position longitude in degrees range -180 to 180
    * @param range
    *           The beacon position horizontal accuracy in meters
    * @throws SQLException
    *            if something wrong is not right
    */
   public void addBeacon3drPartyInfo(int beaconId, Beacon.PositionSource source,
      double latitude, double longitude, int range) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertBeacon3rdPartyInfoStatement == null)
      {
         insertBeacon3rdPartyInfoStatement =
            connection
               .prepareStatement( "INSERT INTO beacon_info_3rdparty (beacon_id, lookup_time, latitude, longitude, source, success, range) VALUES (?, ?, ?, ?, ?, ?, ?)" );
      }

      // set variables
      insertBeacon3rdPartyInfoStatement.setInt( 1, beaconId );
      insertBeacon3rdPartyInfoStatement.setTimestamp( 2, new Timestamp( System
         .currentTimeMillis() ) );
      insertBeacon3rdPartyInfoStatement.setDouble( 3, latitude );
      insertBeacon3rdPartyInfoStatement.setDouble( 4, longitude );
      insertBeacon3rdPartyInfoStatement.setString( 5, source.toString() );
      insertBeacon3rdPartyInfoStatement.setBoolean( 6, latitude != 0 && longitude != 0 );
      insertBeacon3rdPartyInfoStatement.setDouble( 7, range );

      // execute query
      insertBeacon3rdPartyInfoStatement.execute();

   }


   /**
    * Adds a beacon pattern to the database table <i>beacon_patterns</i>. Beacon
    * references (pattern_id to beacon_id) will be inserted into table
    * <i>beacon_pattern_beacons</i>.
    * 
    * @param pattern
    *           The beacon pattern
    * @param placeId
    *           The ID of the place it references
    * @return The ID (primary key) of the beacon pattern as it persists in the database
    * @throws SQLException
    *            if something wrong is not right
    */
   public int addBeaconPattern(BeaconPattern pattern, int placeId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertPatternStatement == null)
      {
         insertPatternStatement =
            connection
               .prepareStatement( "INSERT INTO beacon_patterns (place_id) VALUES (?) RETURNING pattern_id" );
      }

      // set variables
      insertPatternStatement.setInt( 1, placeId );

      // execute query and get generated key (pattern id)
      ResultSet rs = insertPatternStatement.executeQuery();
      if (!rs.next())
         throw new RuntimeException(
            "Failed to get id of newly created place pattern. Something wrong is not right." );
      int patternId = rs.getInt( 1 );

      // close
      rs.close();

      // insert beacon references
      Iterator<BeaconPattern.BeaconPatternElement> elements =
         pattern.getElements().iterator();
      while (elements.hasNext())
      {
         BeaconPattern.BeaconPatternElement e = elements.next();

         // set up statement if needed
         if (insertPatternToBeaconReferenceStatement == null)
         {
            insertPatternToBeaconReferenceStatement =
               connection
                  .prepareStatement( "INSERT INTO beacon_pattern_beacons (pattern_id, beacon_id, time_fraction, avg_signal_strength) VALUES (?, ?, ?, ?);" );
         }

         // set variables
         insertPatternToBeaconReferenceStatement.setInt( 1, patternId );
         insertPatternToBeaconReferenceStatement.setInt( 2, e.beacon.getId() );
         insertPatternToBeaconReferenceStatement.setDouble( 3, e.timeFraction );
         insertPatternToBeaconReferenceStatement.setDouble( 4, e.avgSignalStrength );

         // execute query
         insertPatternToBeaconReferenceStatement.execute();
      }

      // update object id
      pattern.setId( patternId );

      return patternId;

   }


   public void addGeneralLocationCache(Point p, GeneralLocation gl) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertGeneralLocationCacheStatement == null)
      {
         insertGeneralLocationCacheStatement =
            connection
               .prepareStatement( "INSERT INTO general_location_cache (timestamp, latitude, longitude, country_code, region, city, area) VALUES (?, ?, ?, ?, ?, ?, ?)" );
      }

      // set variables
      insertGeneralLocationCacheStatement.setTimestamp( 1, new Timestamp( System
         .currentTimeMillis() ) );
      insertGeneralLocationCacheStatement.setDouble( 2, p.getLatitude() );
      insertGeneralLocationCacheStatement.setDouble( 3, p.getLongitude() );
      insertGeneralLocationCacheStatement.setString( 4, gl.getCountryCode().toString() );
      insertGeneralLocationCacheStatement.setString( 5, gl.getRegion() );
      insertGeneralLocationCacheStatement.setString( 6, gl.getCity() );
      insertGeneralLocationCacheStatement.setString( 7, gl.getArea() );

      // execute query
      insertGeneralLocationCacheStatement.execute();
   }


   /**
    * Adds a location history entry for the specified user to the database table
    * <i>location_history</i>
    * 
    * @param userId
    *           The user ID
    * @param location
    *           The location
    * @throws SQLException
    *            if something wrong is not right
    */
   public void addLocationHistory(int userId, Location location) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertLocationHistoryStatement == null)
      {
         insertLocationHistoryStatement =
            connection
               .prepareStatement( "INSERT INTO location_history (user_id, label, latitude, longitude, place, street, area, city, postal_code, region, country, motion_state, error, place_id, pattern_id, pattern_match) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
      }

      // set variables
      insertLocationHistoryStatement.setInt( 1, userId );
      insertLocationHistoryStatement.setString( 2, location.getLabel() );
      insertLocationHistoryStatement.setDouble( 3, location.getLatitude() );
      insertLocationHistoryStatement.setDouble( 4, location.getLongitude() );
      insertLocationHistoryStatement.setString( 5, location.getPlaceName() );
      insertLocationHistoryStatement.setString( 6, location.getStreet() );
      insertLocationHistoryStatement.setString( 7, location.getArea() );
      insertLocationHistoryStatement.setString( 8, location.getCity() );
      insertLocationHistoryStatement.setString( 9, location.getPostalCode() );
      insertLocationHistoryStatement.setString( 10, location.getRegion() );
      insertLocationHistoryStatement.setString( 11, location.getCountryCode()
         .getEnglishCountryName() ); // TODO store only the 2-letter code itself
      insertLocationHistoryStatement.setString( 12, location.getMotionState().toString() );
      insertLocationHistoryStatement.setInt( 13, (int) location.getAccuracy() );
      insertLocationHistoryStatement.setInt( 14, location.getPlaceId() );
      insertLocationHistoryStatement.setInt( 15, location.getPatternId() );
      insertLocationHistoryStatement.setInt( 16, location.getPatternMatch() );

      // execute query
      insertLocationHistoryStatement.execute();

   }


   /**
    * Adds a location query for the specified user to the queries table. The beacons
    * referenced by the location query will be inserted into the beacons table IF not
    * already present there. The references (query ID to beacon ID) will be inserted in
    * query_beacons table
    * 
    * @param userId
    *           The ID of the user
    * @param query
    *           The location query
    * @return The ID (primary key) of the location query as it presists in the database
    * @throws SQLException
    *            if something wrong is not right
    */
   public int addLocationQuery(int userId, LocationQuery query) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertLocationQueryStatement == null)
      {
         insertLocationQueryStatement =
            connection
               .prepareStatement( "INSERT INTO queries (user_id, client_time, latitude, longitude, error) VALUES (?, ?, ?, ?, ?) RETURNING log_id" );
      }

      // set variables
      insertLocationQueryStatement.setInt( 1, userId );
      insertLocationQueryStatement.setTimestamp( 2, new Timestamp( query.getTime() ) );
      if (query.getLatitude() == 0 && query.getLongitude() == 0)
      {
         insertLocationQueryStatement.setNull( 3, java.sql.Types.DOUBLE );
         insertLocationQueryStatement.setNull( 4, java.sql.Types.DOUBLE );
      }
      else
      {
         insertLocationQueryStatement.setDouble( 3, query.getLatitude() );
         insertLocationQueryStatement.setDouble( 4, query.getLongitude() );
      }
      insertLocationQueryStatement.setInt( 5, (int) query.getAccuracy() );

      // execute query and get the generated key (log id)
      ResultSet rs = insertLocationQueryStatement.executeQuery();

      int logId;
      if (rs.next())
      {
         logId = rs.getInt( 1 );
      }
      else
      {
         throw new RuntimeException( "No keys generated" );
      }
      if (logId < 1)
      {
         throw new RuntimeException( "Insert statement did not return a proper log id: "
                                     + logId );
      }

      // insert beacons and references
      Iterator<Beacon> beacons = query.getBeacons().iterator();
      Iterator<Integer> signalStrengths = query.getSignalStrengths().iterator();
      ArrayList<Integer> processedBeaconIds = new ArrayList<Integer>();
      while (beacons.hasNext())
      {
         Beacon beacon = beacons.next();

         // get beacon id from stored beacon
         int beaconId = -1;
         Beacon storedBeacon = getBeacon( beacon.getMac() );
         if (storedBeacon != null)
         {
            beaconId = storedBeacon.getId();
         }
         // add beacon (if not already exists)
         else
         {
            // TODO Optimize by using dedicated addBeacon(Beacon) method
            beaconId = addOrUpdateBeacon( beacon );
         }

         if (!processedBeaconIds.contains( beaconId ))
         {
            int signalStrength = signalStrengths.next();

            // set up statement if needed
            if (insertLocationQueryToBeaconReferenceStatement == null)
            {
               insertLocationQueryToBeaconReferenceStatement =
                  connection
                     .prepareStatement( "INSERT INTO query_beacons (log_id, beacon_id, signal_strength) VALUES (?, ?, ?)" );
            }

            // add variables
            insertLocationQueryToBeaconReferenceStatement.setInt( 1, logId );
            insertLocationQueryToBeaconReferenceStatement.setInt( 2, beaconId );
            insertLocationQueryToBeaconReferenceStatement.setInt( 3, signalStrength );

            // execute query
            insertLocationQueryToBeaconReferenceStatement.execute();

            processedBeaconIds.add( beaconId );
         }
         else
         {
            logger.info( "Ignored beacon reference already added: log ID = " + logId
                         + ", beacon ID = " + beacon.getId() + " (" + beacon + ")" );
         }

      }

      // close and return
      rs.close();
      return logId;
   }


   /**
    * Adds a location user to the database table <i>location_user</i>
    * 
    * @param jid
    *           The Jabber ID of the user
    * @return A location user object containing the supplied JID and the corresponding
    *         database ID (primary key)
    * @throws SQLException
    *            if something wrong is not right
    */
   public LocationUser addLocationUser(JID jid) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertLocationUserStatement == null)
      {
         insertLocationUserStatement =
            connection
               .prepareStatement( "INSERT INTO location_users (jid) VALUES (?) RETURNING user_id" );
      }

      // set variables
      insertLocationUserStatement.setString( 1, jid.toBareJID().toString() );

      // execute query and get generated key (pattern id)
      ResultSet rs = insertLocationUserStatement.executeQuery();
      if (!rs.next())
         throw new SQLException(
            "Failed to get id of newly created external user. Something wrong is not right." );
      int userId = rs.getInt( 1 );

      rs.close();

      LocationUser u = new LocationUser();
      u.setId( userId );
      u.setJid( jid );

      return u;
   }


   /**
    * Adds a entry to the database table <i>place_overrides</i> indicating that a user has
    * manually set his location to a place
    * 
    * @param userId
    *           The ID of the user
    * @param overriddenPatternId
    *           The ID of the beacon pattern resulting in the place that was overridden
    * @param overridingPatternId
    *           The ID of the user's beacon pattern generated by the override
    * @throws SQLException
    *            if something wrong is not right
    */
   public void addManualPlaceOverride(int userId, int overriddenPatternId,
      int overridingPatternId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertPlaceOverrideStatement == null)
      {

         insertPlaceOverrideStatement =
            connection
               .prepareStatement( "INSERT INTO place_overrides (user_id, overridden_pattern_id, overriding_pattern_id) VALUES (?, ?, ?)" );
      }
      // set variables
      insertPlaceOverrideStatement.setInt( 1, userId );
      insertPlaceOverrideStatement.setInt( 2, overriddenPatternId );
      insertPlaceOverrideStatement.setInt( 3, overridingPatternId );

      // execute query
      insertPlaceOverrideStatement.execute();
   }


   /**
    * Adds a beacon to the database table <i>beacons</i> if a beacon with the same MAC
    * does not already exist in the table. If so, the supplied beacon data is compared
    * with the stored data and the stored beacon data is updated with the supplied beacon
    * data IF:
    * 
    * - supplied beacon has "higher quality" position info OR
    * 
    * - position has changed by more than a meter OR
    * 
    * - the isFixed flag has changed
    * 
    * NOTE : This method is called for EACH beacon in EACH location query by EACH
    * connected user. Very high optimization potential
    * 
    * TODO : Split this into a dedicated addBeacon(Beacon) method and a
    * updateBeacon(Beacon) and move the checks to app layer
    * 
    * @param beacon
    *           The beacon to be added or updated
    * @return The ID (primary key) of the beacon as it persists in the database
    * @throws SQLException
    *            if something wrong is not right
    */
   public int addOrUpdateBeacon(Beacon beacon) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // see if it already exist
      Beacon b = getBeacon( beacon.getMac() );
      if (b != null)
      {
         // set the id, as this may have not been done by app (for instance if beacon was
         // parsed from location query)
         beacon.setId( b.getId() );

         // only update if needed...
         if (isUpdateNeeded( b, beacon ))
         {
            logger.debug( "DB update: " + b.toLongString() + " -> "
                          + beacon.toLongString() );
            // setup statement if needed
            if (updateBeaconStatement == null)
            {
               updateBeaconStatement =
                  connection
                     .prepareStatement( "UPDATE beacons SET country=?, latitude=?, longitude=?, range=?, pos_source=?, is_fixed=? WHERE mac= ?;" );
            }

            updateBeaconStatement.setString( 1, beacon.getCountryCode() != null ? beacon
               .getCountryCode().getEnglishCountryName() : null );
            updateBeaconStatement.setDouble( 2, beacon.getLatitude() );
            updateBeaconStatement.setDouble( 3, beacon.getLongitude() );
            updateBeaconStatement.setDouble( 4, beacon.getRange() );
            if (beacon.getPositionSource() != null)
            {
               updateBeaconStatement.setString( 5, beacon.getPositionSource().toString() );
            }
            else
            {
               updateBeaconStatement.setString( 5, null );
            }
            updateBeaconStatement.setBoolean( 6, beacon.isFixed() );
            updateBeaconStatement.setString( 7, beacon.getMac() );

            // execute
            updateBeaconStatement.execute();
         }

         return b.getId();
      }
      else
      {
         // setup statement if needed
         if (insertBeaconStatement == null)
         {
            insertBeaconStatement =
               connection
                  .prepareStatement( "INSERT INTO beacons (beacon_type, mac, latitude, longitude, range, country, is_fixed, pos_source) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING beacon_id;" );

         }

         // add variables
         insertBeaconStatement.setString( 1, beacon.getType().toString() );
         insertBeaconStatement.setString( 2, beacon.getMac() );
         insertBeaconStatement.setDouble( 3, beacon.getLatitude() );
         insertBeaconStatement.setDouble( 4, beacon.getLongitude() );
         insertBeaconStatement.setDouble( 5, beacon.getRange() );
         // TODO review if country code is really needed. Is primarily used to detect
         // false position data, but for cells this is redundant with the MCC and for wifi
         // beacons the position is currently only derived from co-observed cells, so
         // probably not needed anymore
         insertBeaconStatement.setString( 6, beacon.getCountryCode() == null ? null
            : beacon.getCountryCode().getEnglishCountryName() ); // TODO store only the
         // 2-letter code itself
         insertBeaconStatement.setBoolean( 7, beacon.isFixed() );
         if (beacon.getPositionSource() != null)
         {
            insertBeaconStatement.setString( 8, beacon.getPositionSource().toString() );
         }
         else
         {
            insertBeaconStatement.setString( 8, null );
         }

         // execute query and get the generated key (beacon id)
         ResultSet rs = insertBeaconStatement.executeQuery();
         if (!rs.next())
            throw new RuntimeException(
               "Failed to get last inserted id. Something wrong is not right." );
         int beaconId = rs.getInt( 1 );

         // update object id
         beacon.setId( beaconId );

         // close and return
         rs.close();
         return beaconId;

      }

   }


   /**
    * Adds a place to the database <i>places</i> table.
    * 
    * @param p
    *           The place
    * @return The place ID (primary key) of the place as it persists in the database
    * @throws SQLException
    */
   public int addPlace(Place p) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertPlaceStatement == null)
      {
         insertPlaceStatement =
            connection
               .prepareStatement( "INSERT INTO places ("
                                  + "defined_by, "
                                  + "is_public, "
                                  + "name, "
                                  + "description, "
                                  + "street, "
                                  + "area, "
                                  + "city, "
                                  + "postal_code, "
                                  + "region, "
                                  + "country, "
                                  + "wiki_url, "
                                  + "site_url, "
                                  + "latitude, "
                                  + "longitude, "
                                  + "accuracy "
                                  + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) RETURNING place_id" );

      }
      // set variables
      insertPlaceStatement.setInt( 1, p.getOwnerId() );
      insertPlaceStatement.setBoolean( 2, p.isPublic() );
      insertPlaceStatement.setString( 3, p.getName() );
      insertPlaceStatement.setString( 4, p.getDescription() );
      insertPlaceStatement.setString( 5, p.getStreet() );
      insertPlaceStatement.setString( 6, p.getArea() );
      insertPlaceStatement.setString( 7, p.getCity() );
      insertPlaceStatement.setString( 8, p.getPostalCode() );
      insertPlaceStatement.setString( 9, p.getRegion() );
      // TODO store only the 2-letter code itself
      insertPlaceStatement.setString( 10, p.getCountryCode().getEnglishCountryName() );
      insertPlaceStatement.setString( 11, p.getWikiUrl() );
      insertPlaceStatement.setString( 12, p.getSiteUrl() );
      insertPlaceStatement.setDouble( 13, p.getLatitude() );
      insertPlaceStatement.setDouble( 14, p.getLongitude() );
      insertPlaceStatement.setDouble( 15, p.getAccuracy() );

      // execute query
      ResultSet rs = insertPlaceStatement.executeQuery();

      // get generated key
      if (!rs.next())
         throw new RuntimeException(
            "Failed to get id of newly created place. Something wrong is not right." );
      int placeId = rs.getInt( 1 );

      // update object id
      p.setId( placeId );

      // close and return
      rs.close();

      updateGeometry( "places", "geom", "place_id", "" + p.getId(), p.getLatitude(), p
         .getLongitude() );

      return placeId;

   }


   /**
    * Adds a place history entry for the specified user to the database table
    * <i>place_history</i>
    * 
    * @param userId
    *           The user ID
    * @param placeId
    *           The id of the place
    * @throws SQLException
    *            if something wrong is not right
    */
   public void addPlaceHistory(int userId, int placeId, long entryTime)
      throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertPlaceHistoryStatement == null)
      {
         insertPlaceHistoryStatement =
            connection
               .prepareStatement( "INSERT INTO place_history (user_id, place_id, entry_time) VALUES (?, ?, ?)" );
      }

      // set variables
      insertPlaceHistoryStatement.setInt( 1, userId );
      insertPlaceHistoryStatement.setInt( 2, placeId );
      insertPlaceHistoryStatement.setTimestamp( 3, new Timestamp( entryTime ) );

      // execute query
      insertPlaceHistoryStatement.execute();

   }


   /**
    * Adds a place subscription (my places) to the database table
    * <i>place_subscriptions</i>
    * 
    * @param userId
    *           The ID if the subscribing user
    * @param placeId
    *           The ID of the subscribed place
    * @throws SQLException
    */
   public void addPlaceSubscription(int userId, int placeId) throws SQLException
   {

      // check if this job is necessary
      if (isPlaceSubscriber( userId, placeId ))
         return;

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertPlaceSubscriptionStatement == null)
      {

         insertPlaceSubscriptionStatement =
            connection
               .prepareStatement( "INSERT INTO place_subscriptions (user_id, place_id) VALUES (?, ?)" );
      }
      // set variables
      insertPlaceSubscriptionStatement.setInt( 1, userId );
      insertPlaceSubscriptionStatement.setInt( 2, placeId );

      // execute query
      insertPlaceSubscriptionStatement.execute();
   }


   /**
    * Deletes all queries stored for the specified user
    * 
    * @param userId
    *           the userId whose queries shall be deleted
    * @param minAge
    *           the the minimum age of the queries that shall be deleted in milliseconds
    * @throws SQLException
    */
   public void deleteAllQueries(int userId, long minAge) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // --- 1) delete all beacon references from users queries

      // set up statement if needed
      if (deleteAllQueryBeaconReferencesStatement == null)
      {
         deleteAllQueryBeaconReferencesStatement =
            connection
               .prepareStatement( "DELETE FROM query_beacons WHERE log_id in (SELECT log_id FROM queries WHERE user_id = ? and timestamp <= ?);" );
      }

      // set variables
      deleteAllQueryBeaconReferencesStatement.setInt( 1, userId );
      deleteAllQueryBeaconReferencesStatement.setTimestamp( 2, new Timestamp( System
         .currentTimeMillis()
                                                                              - minAge ) );

      // execute query
      deleteAllQueryBeaconReferencesStatement.execute();

      // --- 2) delete all queries from user

      // set up statement if needed
      if (deleteAllQueriesStatement == null)
      {
         deleteAllQueriesStatement =
            connection
               .prepareStatement( "DELETE FROM queries WHERE user_id = ? and timestamp <= ?;" );
      }

      // set variables
      deleteAllQueriesStatement.setInt( 1, userId );
      deleteAllQueriesStatement.setTimestamp( 2, new Timestamp( System
         .currentTimeMillis()
                                                                - minAge ) );

      // execute query
      deleteAllQueriesStatement.execute();

   }


   /**
    * Deletes the beacon pattern with the supplied ID from the database table
    * <i>beacon_patterns</i> and all references to it from table
    * <i>beacon_pattern_beacons</i> and <i>place_overrides</i>
    * 
    * @param patternId
    *           The pattern ID
    * @throws SQLException
    */
   public void deleteBeaconPattern(int patternId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deleteBeaconPatternStatement == null)
      {
         deleteBeaconPatternStatement =
            connection
               .prepareStatement( "DELETE FROM beacon_patterns WHERE pattern_id = ?;" );
      }

      // set variables
      deleteBeaconPatternStatement.setInt( 1, patternId );

      // execute query
      deleteBeaconPatternStatement.execute();

      // --- 2) delete all references of this pattern ---

      // set up statement if needed
      if (deleteBeaconPatternBeaconReferencesStatement == null)
      {
         deleteBeaconPatternBeaconReferencesStatement =
            connection
               .prepareStatement( "DELETE FROM beacon_pattern_beacons WHERE pattern_id = ?;" );
      }

      // set variables
      deleteBeaconPatternBeaconReferencesStatement.setInt( 1, patternId );

      // execute query
      deleteBeaconPatternBeaconReferencesStatement.execute();

      // 1) delete references to this pattern in the manual override table
      // set up statement if needed
      if (deletePlaceOverrideStatement == null)
      {

         deletePlaceOverrideStatement =
            connection.prepareStatement( "DELETE FROM place_overrides WHERE "
                                         + "overridden_pattern_id = ? OR "
                                         + "overriding_pattern_id = ?;" );
      }
      // set variables
      deletePlaceOverrideStatement.setInt( 1, patternId );
      deletePlaceOverrideStatement.setInt( 2, patternId );

      // execute query
      deletePlaceOverrideStatement.execute();

      // place deleted
      logger.info( "Place pattern " + patternId + " deleted." );

   }


   /**
    * Deletes the current location for the specified user from the database table
    * <i>current_locations</i>
    * 
    * @param userId
    *           The user ID
    * @throws SQLException
    */
   public void deleteCurrentLocation(int userId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deleteCurrentLocationStatement == null)
      {
         deleteCurrentLocationStatement =
            connection
               .prepareStatement( "DELETE FROM current_locations WHERE user_id = ?;" );
      }

      // set variables
      deleteCurrentLocationStatement.setInt( 1, userId );

      // execute query
      deleteCurrentLocationStatement.execute();

   }


   /**
    * Deletes the specified location user from database table <i>location_users</i>
    * 
    * @param userId
    *           The user ID
    * @throws SQLException
    */
   public void deleteLocationUser(int userId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deleteLocationUserStatement == null)
      {
         deleteLocationUserStatement =
            connection.prepareStatement( "DELETE FROM location_users WHERE user_id = ?;" );
      }

      // set variables
      deleteLocationUserStatement.setInt( 1, userId );

      // execute query
      deleteLocationUserStatement.execute();

   }


   /**
    * Deletes a place from database table <i>places</i>. Also deletes references to this
    * in tables <i>beacon_patterns</i> and their beacon references from table
    * <i>beacon_pattern_beacons</i>. Also references in <i>locatiom_history</i> and
    * <i>place_subscriptions</i> are deleted.
    * 
    * @param placeId
    *           The place ID (primary key)
    * @throws SQLException
    */
   public void deletePlace(int placeId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deletePlaceStatement == null)
      {
         deletePlaceStatement =
            connection.prepareStatement( "DELETE FROM places WHERE place_id = ?;" );
      }

      // set variables
      deletePlaceStatement.setInt( 1, placeId );

      // execute query
      deletePlaceStatement.execute();

      // place deleted
      logger.info( "Place " + placeId + " deleted." );

   }


   /**
    * Deletes all references to the specified place from database table
    * <i>location_history</i>
    * 
    * @param placeId
    *           The place ID
    * @throws SQLException
    */
   public void deletePlaceRefsFromLocationHistory(int placeId) throws SQLException
   {
      if (placeId < 1)
         throw new SQLException( "Place ID must be non-zero positive" );

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deletePlaceRefsFromLocationHistoryStatement == null)
      {
         deletePlaceRefsFromLocationHistoryStatement =
            connection
               .prepareStatement( "DELETE FROM location_history WHERE place_id = ?;" );
      }

      // set variables
      deletePlaceRefsFromLocationHistoryStatement.setInt( 1, placeId );

      // execute query
      deletePlaceRefsFromLocationHistoryStatement.execute();

   }


   /**
    * Deletes all place history entries for the specified place from database table
    * <i>place_history</i>
    * 
    * @param placeId
    *           The place ID
    * @throws SQLException
    */
   public void deletePlaceRefsFromPlaceHistory(int placeId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deletePlaceRefsFromPlaceHistoryStatement == null)
      {
         deletePlaceRefsFromPlaceHistoryStatement =
            connection.prepareStatement( "DELETE FROM place_history WHERE place_id = ?;" );
      }

      // set variables
      deletePlaceRefsFromPlaceHistoryStatement.setInt( 1, placeId );

      // execute query
      deletePlaceRefsFromPlaceHistoryStatement.execute();

   }


   /**
    * Deletes all subscriptions to the specified place from database table
    * <i>place_subscriptions</i>
    * 
    * @param placeId
    *           ID of place
    * @throws SQLException
    */
   public void deletePlaceRefsFromPlaceSubscriptions(int placeId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deletePlaceRefsFromPlaceSubscriptionsStatement == null)
      {

         deletePlaceRefsFromPlaceSubscriptionsStatement =
            connection
               .prepareStatement( "DELETE FROM place_subscriptions WHERE place_id = ?;" );
      }
      // set variables
      deletePlaceRefsFromPlaceSubscriptionsStatement.setInt( 1, placeId );

      // execute query
      deletePlaceRefsFromPlaceSubscriptionsStatement.execute();
   }


   /**
    * Deletes all references to the specified place, made by the specified user, from
    * database table <i>location_history</i>
    * 
    * @param userId
    *           The user ID
    * @param placeId
    *           The place ID
    * @throws SQLException
    */
   public void deleteUserPlaceRefsFromLocationHistory(int userId, int placeId)
      throws SQLException
   {
      if (userId < 1)
         throw new SQLException( "User ID must be non-zero positive" );
      if (placeId < 1)
         throw new SQLException( "Place ID must be non-zero positive" );

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deleteUserPlaceRefsFromLocationHistoryStatement == null)
      {
         deleteUserPlaceRefsFromLocationHistoryStatement =
            connection
               .prepareStatement( "DELETE FROM location_history WHERE user_id = ? AND place_id = ?;" );
      }

      // set variables
      deleteUserPlaceRefsFromLocationHistoryStatement.setInt( 1, userId );
      deleteUserPlaceRefsFromLocationHistoryStatement.setInt( 2, placeId );

      // execute query
      deleteUserPlaceRefsFromLocationHistoryStatement.execute();

   }


   /**
    * Deletes all place history entries for the specified place made by the specified user
    * from database table <i>place_history</i>
    * 
    * @param userIdId
    *           The user ID
    * @param placeId
    *           The place ID
    * @throws SQLException
    */
   public void deleteUserPlaceRefsFromPlaceHistory(int userId, int placeId)
      throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deleteUserPlaceRefsFromPlaceHistoryStatement == null)
      {
         deleteUserPlaceRefsFromPlaceHistoryStatement =
            connection
               .prepareStatement( "DELETE FROM place_history WHERE user_id = ? AND place_id = ?;" );
      }

      // set variables
      deleteUserPlaceRefsFromPlaceHistoryStatement.setInt( 1, userId );
      deleteUserPlaceRefsFromPlaceHistoryStatement.setInt( 2, placeId );

      // execute query
      deleteUserPlaceRefsFromPlaceHistoryStatement.execute();

   }


   /**
    * Deletes the specified user's subscription to the specified place from database table
    * <i>place_subscriptions</i>
    * 
    * @param userId
    *           ID of the user
    * @param placeId
    *           ID of place
    * @throws SQLException
    */
   public void deleteUserPlaceRefsFromPlaceSubscriptions(int userId, int placeId)
      throws SQLException
   {

      // check if this job is necessary
      if (!isPlaceSubscriber( userId, placeId ))
         return;

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deleteUserPlaceRefsFromPlaceSubscriptionsStatement == null)
      {

         deleteUserPlaceRefsFromPlaceSubscriptionsStatement =
            connection.prepareStatement( "DELETE FROM place_subscriptions WHERE "
                                         + "user_id = ? AND " + "place_id = ?;" );
      }
      // set variables
      deleteUserPlaceRefsFromPlaceSubscriptionsStatement.setInt( 1, userId );
      deleteUserPlaceRefsFromPlaceSubscriptionsStatement.setInt( 2, placeId );

      // execute query
      deleteUserPlaceRefsFromPlaceSubscriptionsStatement.execute();
   }


   /**
    * Deletes the entire location history for the specified user from database table
    * <i>location_history</i>
    * 
    * @param userId
    *           The user ID
    * @throws SQLException
    */
   public void deleteUserRefsFromLocationHistory(int userId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deleteUserRefsFromLocationHistoryStatement == null)
      {
         deleteUserRefsFromLocationHistoryStatement =
            connection
               .prepareStatement( "DELETE FROM location_history WHERE user_id = ?;" );
      }

      // set variables
      deleteUserRefsFromLocationHistoryStatement.setInt( 1, userId );

      // execute query
      deleteUserRefsFromLocationHistoryStatement.execute();

   }


   /**
    * Deletes the entire place history for the specified user from database table
    * <i>place_history</i>
    * 
    * @param userId
    *           The user ID
    * @throws SQLException
    */
   public void deleteUserRefsFromPlaceHistory(int userId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (deleteUserRefsFromPlaceHistoryStatement == null)
      {
         deleteUserRefsFromPlaceHistoryStatement =
            connection.prepareStatement( "DELETE FROM place_history WHERE user_id = ?;" );
      }

      // set variables
      deleteUserRefsFromPlaceHistoryStatement.setInt( 1, userId );

      // execute query
      deleteUserRefsFromPlaceHistoryStatement.execute();

   }


   /**
    * Returns the beacon with the specified ID (primary key) from the database table
    * <i>beacons</i>
    * 
    * @param beaconId
    *           The beacon ID
    * @return The beacon object
    * @throws SQLException
    */
   public Beacon getBeacon(int beaconId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectBeaconFromIdStatement == null)
      {
         selectBeaconFromIdStatement =
            connection.prepareStatement( "SELECT " + DEFAULT_BEACON_FIELDS
                                         + " FROM beacons WHERE beacon_id = ?;" );
      }

      // set variables
      selectBeaconFromIdStatement.setInt( 1, beaconId );

      // execute query
      ResultSet rs = selectBeaconFromIdStatement.executeQuery();

      // create object
      Beacon beacon = null;
      if (rs.next())
      {
         beacon = parseBeacon( rs );
      }

      // close and return
      rs.close();
      return beacon;
   }


   /**
    * Returns the beacon with the specified MAC from the database table <i>beacons</i>
    * 
    * @param mac
    *           The beacon MAC
    * @return The beacon object
    * @throws SQLException
    */
   public Beacon getBeacon(String mac) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectBeaconFromMacSatement == null)
      {
         selectBeaconFromMacSatement =
            connection.prepareStatement( "SELECT " + DEFAULT_BEACON_FIELDS
                                         + " FROM beacons WHERE mac = ?;" );
      }

      // set variables
      selectBeaconFromMacSatement.setString( 1, mac );

      // execute query
      ResultSet rs = selectBeaconFromMacSatement.executeQuery();

      // create object
      Beacon beacon = null;
      if (rs.next())
      {
         beacon = parseBeacon( rs );
      }

      // close and return
      rs.close();
      return beacon;

   }


   /**
    * Returns the beacon pattern with the specified ID (primary key) from the database
    * table <i>beacon_patterns</i>
    * 
    * @param patternId
    *           The pattern ID
    * @return The beacon pattern object
    * @throws SQLException
    */
   public BeaconPattern getBeaconPattern(int patternId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // create beacon object
      BeaconPattern beaconPattern = new BeaconPattern();
      beaconPattern.setId( patternId );

      // get beacon references

      // set up statement if needed
      if (selectPatternToBeaconReferencesFromPatternIdStatement == null)
      {
         selectPatternToBeaconReferencesFromPatternIdStatement =
            connection
               .prepareStatement( "SELECT beacon_id, time_fraction, avg_signal_strength FROM beacon_pattern_beacons WHERE pattern_id = ?;" );
      }

      // set variables
      selectPatternToBeaconReferencesFromPatternIdStatement.setInt( 1, beaconPattern
         .getId() );

      // execute query
      ResultSet rs3 =
         selectPatternToBeaconReferencesFromPatternIdStatement.executeQuery();

      // create objects
      while (rs3.next())
      {
         int bid = rs3.getInt( "beacon_id" );
         double timeFraction = rs3.getDouble( "time_fraction" );
         double signalStrength = rs3.getDouble( "avg_signal_strength" );

         // --- Step 4 Start ----

         Beacon beacon = getBeacon( bid );

         // --- Step 4 end ---

         beaconPattern.addElement( beacon, timeFraction, signalStrength );

      }

      // close
      rs3.close();

      return beaconPattern;

   }


   /**
    * Returns all beacon patterns referencing the supplied beacon from the database table
    * <i>beacon_patterns</i>
    * 
    * @param beaconId
    *           The beacon ID
    * @return The beacon pattern objects
    * @throws SQLException
    */
   public Collection<BeaconPattern> getBeaconPatternsForBeacon(int beaconId)
      throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPatternToBeaconReferencesFromBeaconIdStatement == null)
      {
         selectPatternToBeaconReferencesFromBeaconIdStatement =
            connection
               .prepareStatement( "SELECT pattern_id FROM beacon_pattern_beacons WHERE beacon_id = ?;" );
      }

      // set variables
      selectPatternToBeaconReferencesFromBeaconIdStatement.setInt( 1, beaconId );

      // execute query
      ResultSet rs = selectPatternToBeaconReferencesFromBeaconIdStatement.executeQuery();

      // create objects
      ArrayList<BeaconPattern> beaconPatterns = new ArrayList<BeaconPattern>();
      while (rs.next())
      {

         int pid = rs.getInt( "pattern_id" );
         BeaconPattern beaconPattern = getBeaconPattern( pid );

         beaconPatterns.add( beaconPattern );

      }

      // close and return
      rs.close();
      return beaconPatterns;

   }


   /**
    * Returns all beacon patterns referencing the supplied place from the database table
    * <i>beacon_patterns</i>
    * 
    * @param placeId
    *           The place ID
    * @return The beacon pattern objects
    * @throws SQLException
    */
   public Collection<BeaconPattern> getBeaconPatternsForPlace(int placeId)
      throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectBeaconPatternsForPlaceStatement == null)
      {
         selectBeaconPatternsForPlaceStatement =
            connection
               .prepareStatement( "SELECT pattern_id FROM beacon_patterns WHERE place_id = ?" );
      }

      selectBeaconPatternsForPlaceStatement.setInt( 1, placeId );

      ResultSet rs = selectBeaconPatternsForPlaceStatement.executeQuery();

      ArrayList<BeaconPattern> patterns = new ArrayList<BeaconPattern>();
      while (rs.next())
      {
         int patternId = rs.getInt( "pattern_id" );
         patterns.add( getBeaconPattern( patternId ) );
      }

      return patterns;
   }


   /**
    * Utility method to fetch and add all beacon objects referenced by the given location
    * query
    * 
    * @param query
    *           The location query (ID must be set)
    * @throws SQLException
    */
   private void getBeaconsForQuery(LocationQuery query) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectLocationQueryToBeaconReferenceFromLocationQueryIdStatement == null)
      {
         selectLocationQueryToBeaconReferenceFromLocationQueryIdStatement =
            connection
               .prepareStatement( "SELECT beacon_id, signal_strength FROM query_beacons WHERE log_id = ?;" );
      }

      // set variables
      selectLocationQueryToBeaconReferenceFromLocationQueryIdStatement.setInt( 1, query
         .getId() );

      // execute query
      ResultSet rsInner =
         selectLocationQueryToBeaconReferenceFromLocationQueryIdStatement.executeQuery();

      // create objects
      ArrayList<Beacon> beacons = new ArrayList<Beacon>();
      ArrayList<Integer> signalStrengths = new ArrayList<Integer>();
      while (rsInner.next())
      {
         int beaconId = rsInner.getInt( "beacon_id" );
         Beacon b = getBeacon( beaconId );
         beacons.add( b );
         signalStrengths.add( rsInner.getInt( "signal_strength" ) );
      }
      query.setBeacons( beacons );
      query.setSignalStrengths( signalStrengths );

      // close
      rsInner.close();

   }


   /**
    * Returns the position of a (cell) beacon as stored in the database table
    * <i>beacon_info_3rdparty</i>
    * 
    * @param beaconId
    * @return The beacon position or null if not found or if the "success" attribute is
    *         set to false (indicating that the 3rd party source did not know any position
    *         for this beacon)
    * @throws SQLException
    */
   public Position getCachedBeaconPosition(int beaconId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectBeacon3rdPartyInfoStatement == null)
      {
         selectBeacon3rdPartyInfoStatement =
            connection
               .prepareStatement( "SELECT success, latitude, longitude, range FROM beacon_info_3rdparty WHERE beacon_id = ?" );
      }

      // set variables
      selectBeacon3rdPartyInfoStatement.setInt( 1, beaconId );

      // execute query
      ResultSet rs = selectBeacon3rdPartyInfoStatement.executeQuery();

      Position p = null;
      if (rs.next())
      {
         p = new Position();
         if (rs.getBoolean( "success" ))
         {
            p.setLatitude( rs.getDouble( "latitude" ) );
            p.setLongitude( rs.getDouble( "longitude" ) );
            p.setAccuracy( rs.getDouble( "range" ) );
         }
      }
      rs.close();

      return p;

   }


   /**
    * Returns the general location corresponding to the specified position or null if not
    * cached in table <i>general_location_cache</i>
    * 
    * 
    * @param p
    *           The position
    * @param accuracy
    *           How accuratly the posirion must match the cached posirion, in delta
    *           degrees lat/lon
    * @return The cached general location or null
    * @deprecated
    * @throws SQLException
    */
   public GeneralLocation getCachedGeneralLocation(Point p, double accuracy, long maxAge)
      throws SQLException
   {
      // double latMin = Math.floor( p.getLatitude()/accuracy )*accuracy;
      // double latMax = Math.ceil( p.getLatitude()/accuracy )*accuracy;
      // double lonMin = Math.floor( p.getLongitude()/accuracy )*accuracy;
      // double lonMax = Math.ceil( p.getLongitude()/accuracy )*accuracy;
      // calculate min and max lat/lon
      double latMin = p.getLatitude() - accuracy / 2;
      double latMax = p.getLatitude() + accuracy / 2;
      double lonMin = p.getLongitude() - accuracy / 2;
      double lonMax = p.getLongitude() + accuracy / 2;
      long minTime = System.currentTimeMillis() - maxAge;

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectCachedGeneralLocationStatement == null)
      {
         selectCachedGeneralLocationStatement =
            connection
               .prepareStatement( "SELECT country_code, region, city, area FROM general_location_cache WHERE latitude >= ? AND latitude <= ? and longitude >= ? AND longitude <= AND timestamp >= ? ORDER BY timestamp DESC LIMIT 1" );
      }

      // set variables
      selectCachedGeneralLocationStatement.setDouble( 1, latMin );
      selectCachedGeneralLocationStatement.setDouble( 2, latMax );
      selectCachedGeneralLocationStatement.setDouble( 3, lonMin );
      selectCachedGeneralLocationStatement.setDouble( 4, lonMax );
      selectCachedGeneralLocationStatement.setTimestamp( 5, new Timestamp( minTime ) );

      // execute query
      ResultSet rs = selectCachedGeneralLocationStatement.executeQuery();

      GeneralLocation gl = null;
      if (rs.next())
      {
         gl = new GeneralLocation();
         gl.setCountryCode( CountryCode.getInstance( rs.getString( "country_code" ) ) );
         gl.setRegion( rs.getString( "region" ) );
         gl.setCity( rs.getString( "city" ) );
         gl.setArea( rs.getString( "area" ) );
      }
      rs.close();

      return gl;

   }


   /**
    * Get all recent location queries logged by the specified user from the tables
    * <i>queries</i>, <i>query_beacons</i> and <i>beacons</i>
    * 
    * @param userId
    *           The ID of the user
    * @param maxAge
    *           The maximum age of the location queries in milliseconds from now()
    * @return The recent queries in a list TODO consider using a Set rather than a List
    * @throws SQLException
    */
   public ArrayList<LocationQuery> getLocationQueries(int userId, long maxAge)
      throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectLocationQuerysFromTimeWindowStatement == null)
      {
         selectLocationQuerysFromTimeWindowStatement =
            connection
               .prepareStatement( "SELECT log_id, timestamp, latitude, longitude, error FROM queries WHERE user_id = ? and timestamp >= ? ORDER BY timestamp DESC LIMIT 20;" );
      }

      // set variables
      selectLocationQuerysFromTimeWindowStatement.setInt( 1, userId );
      selectLocationQuerysFromTimeWindowStatement.setTimestamp( 2, new Timestamp(
         System.currentTimeMillis() - maxAge ) );

      // execute query
      ResultSet rs = selectLocationQuerysFromTimeWindowStatement.executeQuery();

      // create objects
      ArrayList<LocationQuery> queries = new ArrayList<LocationQuery>();
      while (rs.next())
      {

         // Get the relevant data from the beacon log
         LocationQuery query = new LocationQuery();
         query.setId( rs.getInt( "log_id" ) );
         query.setTime( rs.getTimestamp( "timestamp" ).getTime() );
         query.setLatitude( rs.getDouble( "latitude" ) );
         query.setLongitude( rs.getDouble( "longitude" ) );
         query.setAccuracy( rs.getDouble( "error" ) );

         getBeaconsForQuery( query );

         queries.add( query );

      }

      // close and return
      rs.close();

      return queries;

   }


   /**
    * Returns the number of times the specified beacon pattern has been overriden by the
    * user manually setting his current location by counting the number of entries in the
    * database table <i>place_overrides</i> for this pattern ID
    * 
    * @param patternId
    *           The beacon pattern id
    * @return The number of manually overrides
    * @throws SQLException
    */
   public int getManuallyOverriddenCount(int patternId) throws SQLException
   {

      if (patternId < 0)
         return 0;

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (getManualOverrideCountStatement == null)
      {
         getManualOverrideCountStatement =
            connection
               .prepareStatement( "SELECT count(override_id) FROM place_overrides "
                                  + "WHERE overridden_pattern_id = ?;" );
      }

      // set variables
      getManualOverrideCountStatement.setInt( 1, patternId );

      // execute query
      ResultSet rs = getManualOverrideCountStatement.executeQuery();

      int count = 0;
      if (rs.next())
      {
         count = rs.getInt( "count" );
      }

      return count;

   }


   /**
    * Returns the most recently logged location query for the specified user from the
    * tables <i>queries</i>, <i>query_beacons</i> and <i>beacons</i>
    * 
    * @param userId
    *           The user ID
    * @return The most recent query or null if none
    * @throws SQLException
    */
   public LocationQuery getMostRecentLocationQuery(int userId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectMostRecentLocationQueryStatement == null)
      {
         selectMostRecentLocationQueryStatement =
            connection
               .prepareStatement( "SELECT log_id, timestamp, latitude, longitude, error FROM queries WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1;" );
      }

      // set variables
      selectMostRecentLocationQueryStatement.setInt( 1, userId );

      // execute query
      ResultSet rs = selectMostRecentLocationQueryStatement.executeQuery();

      // create objects
      LocationQuery query = null;
      if (rs.next())
      {

         // Get the relevant data from the beacon log
         query = new LocationQuery();
         query.setId( rs.getInt( "log_id" ) );
         query.setTime( rs.getTimestamp( "timestamp" ).getTime() );
         query.setLatitude( rs.getDouble( "latitude" ) );
         query.setLongitude( rs.getDouble( "longitude" ) );
         query.setAccuracy( rs.getDouble( "error" ) );

         getBeaconsForQuery( query );
      }

      // close and return
      rs.close();
      return query;

   }


   /**
    * Returns the ID of the place most recently manually set as current location by the
    * specified user, within the specified time window
    * 
    * @param userId
    *           The ID of the user
    * @param maxAgeMilliseconds
    *           the start of the time window, relative to now
    */
   public int getMostRecentManualPlaceOverride(int userId, long maxAgeMilliseconds)
      throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectManualOverrideStatement == null)
      {
         selectManualOverrideStatement =
            connection
               .prepareStatement( "SELECT overriding_pattern_id FROM place_overrides "
                                  + "WHERE user_id = ? " + "AND timestamp > ? "
                                  + "ORDER BY timestamp DESC " + "LIMIT 1;" );
      }

      Timestamp timestamp =
         new Timestamp( System.currentTimeMillis() - maxAgeMilliseconds );

      // set variables
      selectManualOverrideStatement.setInt( 1, userId );
      selectManualOverrideStatement.setTimestamp( 2, timestamp );

      // execute query
      ResultSet rs = selectManualOverrideStatement.executeQuery();

      // get overriding pattern id
      int patternId = -1;
      int placeId = -1;
      if (rs.next())
      {
         patternId = rs.getInt( "overriding_pattern_id" );
      }

      // if an override exists, get the corresponding place
      if (patternId > 0)
      {
         BeaconPattern pattern = getBeaconPattern( patternId );
         if (pattern != null)
         {
            placeId = getPlaceId( pattern.getId() );
         }
      }

      return placeId;
   }


   /**
    * Returns the label of the currently set next location of the specified user from
    * table <i>next_locations</i>
    * 
    * @param userId
    *           The user ID
    * @return The next location label or null if none
    * @throws SQLException
    */
   public String getNextLocation(int userId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (getNextPlaceStatement == null)
      {
         getNextPlaceStatement =
            connection.prepareStatement( "SELECT label FROM next_locations "
                                         + "WHERE user_id = ? "
                                         + "ORDER BY timestamp DESC " + "LIMIT 1;" );
      }

      // set variables
      getNextPlaceStatement.setInt( 1, userId );

      // execute query
      ResultSet rs = getNextPlaceStatement.executeQuery();

      String nextPlace = null;
      if (rs.next())
      {
         nextPlace = rs.getString( "label" );
      }

      return nextPlace;

   }


   /**
    * Returns all places owned (defined by) the specified user
    * 
    * @param userId
    *           the user ID
    * @return The owned places
    */
   public Collection<Place> getOwnedPlaces(int userId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectOwnedPlacesStatement == null)
      {
         selectOwnedPlacesStatement =
            connection.prepareStatement( "SELECT " + DEFAULT_PLACE_FIELDS
                                         + " FROM places WHERE defined_by=?" );
      }

      // set variables
      selectOwnedPlacesStatement.setInt( 1, userId );

      // execute query
      ResultSet rs = selectOwnedPlacesStatement.executeQuery();

      // create object
      ArrayList<Place> places = new ArrayList<Place>();
      while (rs.next())
      {
         Place place = extractPlace( rs );
         if (place != null)
         {
            places.add( place );
         }
      }

      // close and return
      rs.close();
      return places;
   }


   /**
    * Returns the place history of the specified user extracted from the database table
    * <i>location_history</i>. TODO use dedicated table for place_history
    * 
    * @param userId
    *           The user ID
    * @param maxResults
    *           The maximum number of results to return
    * @return The place history
    * @throws SQLException
    */
   public ArrayList<Place> getPlaceHistory(int userId, int maxResults)
      throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPlaceHistoryStatement == null)
      {
         selectPlaceHistoryStatement =
            connection.prepareStatement( "SELECT place_id FROM location_history WHERE "
                                         + "place_id > 0 AND " + "pattern_match >= "
                                         + LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH
                                         + " AND " + "motion_state = '"
                                         + Location.MotionState.STATIONARY.name()
                                         + "' AND " + "user_id = ? "
                                         + "ORDER BY timestamp DESC LIMIT ?;" );
      }

      // set variables
      selectPlaceHistoryStatement.setInt( 1, userId );
      selectPlaceHistoryStatement.setInt( 2, maxResults );

      // execute query
      ResultSet rs = selectPlaceHistoryStatement.executeQuery();

      // create objects
      ArrayList<Place> places = new ArrayList<Place>();
      while (rs.next())
      {
         int pid = rs.getInt( "place_id" );
         Place p = getPlace( pid );
         if (p != null)
            places.add( p );
      }

      // close and return
      rs.close();
      return places;

   }


   /**
    * Returns the ID of place referenced by the beacon pattern with the specified ID from
    * the <i>beacon_patterns</i> table
    * 
    * @param beaconPatternId
    *           The beacon pattern ID
    * @return The place ID
    * @throws SQLException
    *            If no pattern with the specified ID exists
    */
   public int getPlaceId(int beaconPatternId) throws SQLException
   {

      // set up statement if needed
      if (selectPatternFromPatternIdStatement == null)
      {
         selectPatternFromPatternIdStatement =
            connection
               .prepareStatement( "SELECT place_id FROM beacon_patterns WHERE pattern_id = ?;" );
      }

      // set variables
      selectPatternFromPatternIdStatement.setInt( 1, beaconPatternId );

      // execute query
      ResultSet rs2 = selectPatternFromPatternIdStatement.executeQuery();

      // assert result
      if (!rs2.next())
         throw new SQLException( "No pattern with id " + beaconPatternId
                                 + " in database." );

      // get id of place for which this pattern is defined
      int pid = rs2.getInt( "place_id" );

      // close
      rs2.close();

      return pid;
   }


   /**
    * Returns all places located within a "rectangle" defined by two lines of constant
    * latitude and two lines of constant longitude
    * 
    * @param latmin
    *           The latitude which the place position must be larger than
    * @param lonmin
    *           The longitude which the place position must be larger than
    * @param latmax
    *           The latitude which the place position must be smaller than
    * @param lonmax
    *           The longitude which the place position must be smaller than
    * @return
    * @throws SQLException
    */
   public Collection<Place> getPlaces(double latmin, double lonmin, double latmax,
      double lonmax) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // safety check
      if (latmin < -90 || latmin > 90 || latmax < -90 || latmax > 90)
         throw new IllegalArgumentException( "Latitude must be within -90 and 90 degrees" );
      if (lonmin < -180 || lonmin > 180 || lonmax < -180 || lonmax > 180)
         throw new IllegalArgumentException( "Latitude must be within -90 and 90 degrees" );
      if (latmin > latmax)
      {
         double temp = latmax;
         latmax = latmin;
         latmin = temp;
      }
      if (lonmin > lonmax)
      {
         double temp = lonmax;
         lonmax = lonmin;
         lonmin = temp;
      }

      // set up statement if needed
      if (selectPlaceFromCoordsStatement == null)
      {
         selectPlaceFromCoordsStatement =
            connection
               .prepareStatement( "SELECT " + DEFAULT_PLACE_FIELDS + " FROM places "
                                  + "WHERE "
                                  + "latitude IS NOT NULL AND latitude != 0.0 AND "
                                  + "longitude IS NOT NULL AND longitude != 0.0 AND "
                                  + "latitude > ? AND " + "latitude < ? AND "
                                  + "longitude > ? AND " + "longitude < ? LIMIT 50" );
      }

      // set variables
      selectPlaceFromCoordsStatement.setDouble( 1, latmin );
      selectPlaceFromCoordsStatement.setDouble( 2, latmax );
      selectPlaceFromCoordsStatement.setDouble( 3, lonmin );
      selectPlaceFromCoordsStatement.setDouble( 4, lonmax );

      // execute query
      ResultSet rs = selectPlaceFromCoordsStatement.executeQuery();

      // create objects
      ArrayList<Place> places = new ArrayList<Place>();
      while (rs.next())
      {
         Place place = extractPlace( rs );

         if (place != null)
            places.add( place );
      }

      // close and return
      rs.close();
      return places;

   }


   /**
    * Returns all places with the specified name from the database table <i>places</i>
    * 
    * @param name
    *           The place name
    * @return The places
    * @throws SQLException
    */
   public Collection<Place> getPlaces(String name) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPlaceFromNameStatement == null)
      {
         selectPlaceFromNameStatement =
            connection.prepareStatement( "SELECT " + DEFAULT_PLACE_FIELDS
                                         + " FROM places WHERE name = ?;" );
      }

      // set variables
      selectPlaceFromNameStatement.setString( 1, name );

      // execute query
      ResultSet rs = selectPlaceFromNameStatement.executeQuery();

      // create objects
      ArrayList<Place> places = new ArrayList<Place>();
      while (rs.next())
      {
         Place place = extractPlace( rs );
         if (place != null)
            places.add( place );
      }

      // close and return
      rs.close();
      return places;

   }


   /**
    * Returns the ID of all users subscribing to the specified place from the database
    * table <i>place_subscriptions</i>
    * 
    * @param placeId
    *           The ID of the place
    * @return The subscriber IDs
    * @throws SQLException
    */
   public Collection<Integer> getPlaceSubscribers(int placeId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPlaceSubscribersFromPlaceIdStatement == null)
      {
         selectPlaceSubscribersFromPlaceIdStatement =
            connection
               .prepareStatement( "SELECT user_id FROM place_subscriptions WHERE place_id = ?;" );
      }

      // set variables
      selectPlaceSubscribersFromPlaceIdStatement.setInt( 1, placeId );

      // execute query
      ResultSet rs = selectPlaceSubscribersFromPlaceIdStatement.executeQuery();

      // create object
      ArrayList<Integer> userIds = new ArrayList<Integer>();
      while (rs.next())
      {
         userIds.add( rs.getInt( "user_id" ) );
      }

      // close and return
      rs.close();
      return userIds;

   }


   /**
    * Returns all the places to which the user subscribes from the tables
    * <i>place_subscriptions<i> and <i>places</i>
    * 
    * @param userId
    *           The user ID
    * @return The subscribed places
    * @throws SQLException
    */
   public Collection<Place> getPlaceSubscriptions(int userId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPlaceSubscriptionFromUserIdStatement == null)
      {
         selectPlaceSubscriptionFromUserIdStatement =
            connection
               .prepareStatement( "SELECT place_id FROM place_subscriptions WHERE user_id = ?;" );
      }

      // set variables
      selectPlaceSubscriptionFromUserIdStatement.setInt( 1, userId );

      // execute query
      ResultSet rs = selectPlaceSubscriptionFromUserIdStatement.executeQuery();

      // create object
      ArrayList<Place> places = new ArrayList<Place>();
      while (rs.next())
      {
         int lid = rs.getInt( "place_id" );
         Place l = getPlace( lid );
         if (l == null)
         {
            logger.error( "user " + userId + " subscribes to non-existing place with id "
                          + lid + "!" );
         }
         else
         {
            places.add( l );
         }
      }

      // close and return
      rs.close();
      return places;

   }


   /**
    * Returns the previous location of the user with the specified ID. The previous
    * location is defined as the most recent location where a place ID is set which is not
    * equal to the current place at which the user is at. Furthermore, the motion state
    * must be stationary and the pattern match above a certain threshold.
    * 
    * @param userId
    *           The user ID
    * @param currentPlaceId
    *           The ID of the current place the user is at, or -1 if none
    * @param skipPlaceId
    *           The ID of another place, besides the current, that is not allowed as the
    *           previous place. This can be used when getting the location that was
    *           previous before the current previous place (getting dizzy?)
    * @return The previous location
    * @throws SQLException
    */
   public Location getPreviousLocation(int userId, int currentPlaceId)
      throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPreviousPlaceLocationStatement == null)
      {

         selectPreviousPlaceLocationStatement =
            connection
               .prepareStatement( "SELECT "
                                  + DEFAULT_LOCATION_FIELDS
                                  + " FROM location_history WHERE user_id = ? AND motion_state = '"
                                  + Location.MotionState.STATIONARY.name()
                                  + "' AND place_id != ? AND place_id >= 1 AND pattern_match >= "
                                  + LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH + " "
                                  + "ORDER BY timestamp DESC LIMIT 1;" );
      }
      // set variables
      selectPreviousPlaceLocationStatement.setInt( 1, userId );
      selectPreviousPlaceLocationStatement.setInt( 2, currentPlaceId );

      // execute query
      ResultSet rs = selectPreviousPlaceLocationStatement.executeQuery();

      Location previousPlaceLocation = null;

      // check that there is any result at all
      if (rs.next())
      {
         // extract the previous place details
         previousPlaceLocation = this.extractLocation( rs );
      }

      if (previousPlaceLocation == null)
      {
         previousPlaceLocation = new Location();
         // logger.error( "No previous place found for user " + userId );
      }

      // close and return
      rs.close();
      return previousPlaceLocation;
   }


   /**
    * Returns the IDs of all users currently at the specified place from the database
    * table <i>current_location</i>
    * 
    * @param placeId
    *           The place ID
    * @return The user IDs
    * @throws SQLException
    */
   public Collection<Integer> getUsersAtPlace(int placeId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectUsersAtPlaceStatement == null)
      {
         selectUsersAtPlaceStatement =
            connection
               .prepareStatement( "SELECT user_id, "
                                  + DEFAULT_LOCATION_FIELDS
                                  + " FROM current_locations WHERE place_id =? AND timestamp > ?;" );
      }

      long now = System.currentTimeMillis();
      long fadeoutTime = now - LocationConstants.NEARBY_FADEOUT_TIME_DAYS * 24 * 3600000;

      // get all users who have been at this place lately
      selectUsersAtPlaceStatement.setInt( 1, placeId );
      selectUsersAtPlaceStatement.setTimestamp( 2, new Timestamp( fadeoutTime ) );

      ResultSet rs = selectUsersAtPlaceStatement.executeQuery();
      ArrayList<Integer> userIds = new ArrayList<Integer>();
      while (rs.next())
      {
         int uid = rs.getInt( "user_id" );
         Location l = this.extractLocation( rs );
         if (l.isPlaceFix())
         {
            userIds.add( uid );
         }

      }

      return userIds;
   }


   /**
    * Returns all users that has logged a location query referencing the specified beacon
    * in the specified time interval from the database table s <i>queries</i> and
    * <i>query_beacons</i>
    * 
    * @param beaconId
    *           The beacon ID
    * @param t0
    *           The start of the time interval in milliseconds since Unix epoch
    * @param t1
    *           The end of the time interval in milliseconds since the Unix epoch
    * @return The user IDs
    * @throws SQLException
    */
   public Collection<Integer> getUsersNearBeacon(int beaconId, long t0, long t1)
      throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectLogReferenceStatement == null)
      {
         selectLogReferenceStatement =
            connection.prepareStatement( "SELECT log_id FROM query_beacons WHERE "
                                         + "beacon_id =?;" );
      }

      if (selectLogStatement == null)
      {
         selectLogStatement =
            connection.prepareStatement( "SELECT user_id, timestamp FROM queries WHERE "
                                         + "log_id =?;" );
      }

      selectLogReferenceStatement.setInt( 1, beaconId );

      ResultSet beaconRefs = selectLogReferenceStatement.executeQuery();
      ArrayList<Integer> userIds = new ArrayList<Integer>();
      while (beaconRefs.next())
      {
         int logId = beaconRefs.getInt( "log_id" );
         if (logId > 0)
         {
            selectLogStatement.setInt( 1, logId );
            ResultSet logs = selectLogStatement.executeQuery();
            if (logs.next())
            {
               int uid = logs.getInt( "user_id" );
               Timestamp ts = logs.getTimestamp( "timestamp" );
               long t = ts.getTime();
               if (t >= t0 && t <= t1)
               {
                  if (uid > 0)
                     userIds.add( uid );
               }
            }
         }
      }

      return userIds;
   }


   /**
    * Returns true if an angle (help) message with the specified message ID (type rather)
    * has been sent to the specified user
    * 
    * @param userId
    *           The user ID
    * @param messageId
    *           The message ID (type)
    * @return true if sent, otherwise false
    * @throws SQLException
    */
   public boolean isAngelMessageSent(int userId, String messageId) throws SQLException
   {
      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectAngelMessageStatement == null)
      {
         selectAngelMessageStatement =
            connection
               .prepareStatement( "SELECT id FROM help_messages_sent WHERE user_id = ? AND message_id = ?;" );
      }

      // set variables
      selectAngelMessageStatement.setInt( 1, userId );
      selectAngelMessageStatement.setString( 2, messageId );

      // execute query
      ResultSet rs = selectAngelMessageStatement.executeQuery();

      boolean firstExists = rs.next();

      // close and return
      rs.close();
      return firstExists;
   }


   /**
    * Returns whether or not the specified user subscribes to the specified place
    * 
    * @param userId
    *           The user ID
    * @param placeId
    *           The place ID
    * @return true if subscriber, otherwise false
    * @throws SQLException
    */
   public boolean isPlaceSubscriber(int userId, int placeId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPlaceSubscriptionStatement == null)
      {
         selectPlaceSubscriptionStatement =
            connection
               .prepareStatement( "SELECT user_id, place_id FROM place_subscriptions WHERE user_id = ? AND place_id = ?;" );
      }

      // set variables
      selectPlaceSubscriptionStatement.setInt( 1, userId );
      selectPlaceSubscriptionStatement.setInt( 2, placeId );

      // execute query
      ResultSet rs = selectPlaceSubscriptionStatement.executeQuery();

      boolean isSubscriber = rs.next();

      // close and return
      rs.close();
      return isSubscriber;
   }


   /**
    * Returns whether or not the specified place is private
    * 
    * @param placeId
    *           The place ID
    * @return true if private, otherwise false
    * @throws SQLException
    */
   public boolean isPrivatePlace(int placeId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (selectPlaceFromPlaceIdStatement == null)
      {
         selectPlaceFromPlaceIdStatement =
            connection
               .prepareStatement( "SELECT is_public FROM places WHERE place_id = ?;" );
      }

      // set variables
      selectPlaceFromPlaceIdStatement.setInt( 1, placeId );

      // execute query
      ResultSet rs = selectPlaceFromPlaceIdStatement.executeQuery();

      // TODO change defined_by to owner_id in the table
      boolean isPrivate = false;
      if (rs.next())
      {
         isPrivate = rs.getString( "is_public" ).toLowerCase().equals( "n" );
      }
      else
      {
         logger.error( "isPrivatePlace(Place): Place with ID " + placeId + " not found" );
      }

      // close and return
      rs.close();
      return isPrivate;
   }


   /**
    * Utility method to check if a beacon already inserted into the <i>beacons</i> table
    * needs to be updated with the supplied information. TODO move this to the app layer
    * 
    * @param oldBeacon
    * @param newBeacon
    * @return
    */
   private boolean isUpdateNeeded(Beacon oldBeacon, Beacon newBeacon)
   {
      // in general, update is needed when beacons are not equal
      boolean update = !oldBeacon.toLongString().equals( newBeacon.toLongString() );

      // but if old beacon has more info than the new, don't update (cell only)
      if (update && oldBeacon.getType() == Beacon.Type.CELL)
      {
         // don't overwrite poor quality position info with higher quality info
         if (newBeacon.getPositionSource() != null
             && oldBeacon.getPositionSource() != null)
         {
            if (oldBeacon.getPositionSource() == Beacon.PositionSource.GOOGLE
                && newBeacon.getPositionSource() != Beacon.PositionSource.GOOGLE)
               update = false;
            else if (oldBeacon.getPositionSource() == Beacon.PositionSource.GPS
                     && newBeacon.getPositionSource() != Beacon.PositionSource.GPS
                     && newBeacon.getPositionSource() != Beacon.PositionSource.GOOGLE)
               update = false;
            else if (newBeacon.getPositionSource() == Beacon.PositionSource.GENERAL)
               update = false;
         }

         if (newBeacon.getPositionSource() == null
             && oldBeacon.getPositionSource() != null)
            update = false;
         if (newBeacon.getLatitude() == 0 && oldBeacon.getLatitude() != 0)
            update = false;
         if (newBeacon.getLongitude() == 0 && oldBeacon.getLongitude() != 0)
            update = false;
         if (newBeacon.getCountryCode() == null && oldBeacon.getCountryCode() != null)
            update = false;

         // check distance, no need to update if less than 1 meter different
         Point gNew = new Point( newBeacon.getLatitude(), newBeacon.getLongitude() );
         Point gOld = new Point( oldBeacon.getLatitude(), oldBeacon.getLongitude() );
         if (!gNew.isDefault() && !gOld.isDefault() && gNew.getDistanceTo( gOld ) < 1)
            update = false;

      }

      // if the isFixed flag has changed, update
      if (newBeacon.isFixed() != oldBeacon.isFixed())
      {
         update = true;
      }
      return update;
   }


   /**
    * Utility method for extracting a beacon object from a result set returned from a
    * query on the <i>beacons</i> table.
    * 
    * @param rs
    *           The result set
    * @return The beacon object
    * @throws SQLException
    */
   private Beacon parseBeacon(ResultSet rs) throws SQLException
   {
      Beacon beacon = new Beacon();
      beacon.setId( rs.getInt( "beacon_id" ) );
      beacon.setMac( rs.getString( "mac" ) );
      beacon.setType( Beacon.Type.valueOf( rs.getString( "beacon_type" ) ) );
      beacon.setCountryCode( CountryCode.getInstance( rs.getString( "country" ) ) );
      beacon.setLatitude( rs.getDouble( "latitude" ) );
      beacon.setLongitude( rs.getDouble( "longitude" ) );
      beacon.setRange( rs.getDouble( "range" ) );
      beacon.setFixed( rs.getBoolean( "is_fixed" ) );
      String posSourceString = rs.getString( "pos_source" );
      if (posSourceString != null && !posSourceString.equals( "" ))
      {
         try
         {
            beacon.setPositionSource( Beacon.PositionSource.valueOf( posSourceString ) );
         }
         catch (Exception e)
         {
            logger.error( "Unexpected position source '" + posSourceString
                          + "' in beacon " + beacon.getId() );
         }
      }
      return beacon;
   }


   /**
    * Adds an entry to the <i>help_messages_sent</i> table specifying that a help message
    * with the specified ID (type) has been sent to the specified user
    * 
    * @param userId
    *           The user ID
    * @param messageId
    *           The message ID
    * @throws SQLException
    */
   public void setAngelMessageSent(int userId, String messageId) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (setAngelMessageStatement == null)
      {
         setAngelMessageStatement =
            connection
               .prepareStatement( "INSERT INTO help_messages_sent (user_id, message_id) VALUES (?, ?);" );
      }
      // set variables
      setAngelMessageStatement.setInt( 1, userId );
      setAngelMessageStatement.setString( 2, messageId );
      setAngelMessageStatement.execute();

   }


   /**
    * Update the specified user's entry to the database <i>current_locations</i> with the
    * supplied location. If the user has no previous entries it will be added
    * 
    * @param userId
    *           The user DI
    * @param location
    *           The current location of the user
    * @throws SQLException
    */
   public void setCurrentLocation(int userId, Location location) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // if there is no stored location for this user, add it
      if (getCurrentLocation( userId ) == null)
      {

         // set up statement if needed (note: timestamp set implicitly by db)
         if (setCurrentLocationStatement == null)
         {
            setCurrentLocationStatement =
               connection
                  .prepareStatement( "INSERT INTO current_locations ("
                                     + "user_id, "
                                     + "label, "
                                     + "latitude, "
                                     + "longitude, "
                                     + "place, "
                                     + "street, "
                                     + "area, "
                                     + "city, "
                                     + "postal_code, "
                                     + "region, "
                                     + "country, "
                                     + "motion_state, "
                                     + "error, "
                                     + "place_id, "
                                     + "pattern_id, "
                                     + "pattern_match"
                                     + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
         }

         // set variables
         setCurrentLocationStatement.setInt( 1, userId );
         setCurrentLocationStatement.setString( 2, location.getLabel() );
         setCurrentLocationStatement.setDouble( 3, location.getLatitude() );
         setCurrentLocationStatement.setDouble( 4, location.getLongitude() );
         setCurrentLocationStatement.setString( 5, location.getPlaceName() );
         setCurrentLocationStatement.setString( 6, location.getStreet() );
         setCurrentLocationStatement.setString( 7, location.getArea() );
         setCurrentLocationStatement.setString( 8, location.getCity() );
         setCurrentLocationStatement.setString( 9, location.getPostalCode() );
         setCurrentLocationStatement.setString( 10, location.getRegion() );
         setCurrentLocationStatement.setString( 11, location.getCountryCode()
            .getEnglishCountryName() ); // TODO store only the 2-letter code itself
         setCurrentLocationStatement.setString( 12, location.getMotionState().toString() );
         setCurrentLocationStatement.setInt( 13, (int) location.getAccuracy() );
         setCurrentLocationStatement.setInt( 14, location.getPlaceId() );
         setCurrentLocationStatement.setInt( 15, location.getPatternId() );
         setCurrentLocationStatement.setInt( 16, location.getPatternMatch() );

         // execute query
         setCurrentLocationStatement.execute();

      }

      // if there is, update current
      else
      {
         // set up statement if needed (note: timeset set explicitly by app)
         if (updateCurrentLocationStatement == null)
         {
            updateCurrentLocationStatement =
               connection.prepareStatement( "UPDATE current_locations SET "
                                            + "timestamp = ?, " + "label = ?, "
                                            + "latitude = ?, " + "longitude = ?, "
                                            + "place = ?, " + "street = ?, "
                                            + "area = ?, " + "city = ?, "
                                            + "postal_code = ?, " + "region = ?, "
                                            + "country = ?, " + "motion_state = ?, "
                                            + "error = ?, " + "place_id = ?, "
                                            + "pattern_id = ?, " + "pattern_match = ? "
                                            + "WHERE user_id = ?;" );
         }

         // set variables
         updateCurrentLocationStatement.setTimestamp( 1, new Timestamp( System
            .currentTimeMillis() ) );
         updateCurrentLocationStatement.setString( 2, location.getLabel() );
         updateCurrentLocationStatement.setDouble( 3, location.getLatitude() );
         updateCurrentLocationStatement.setDouble( 4, location.getLongitude() );
         updateCurrentLocationStatement.setString( 5, location.getPlaceName() );
         updateCurrentLocationStatement.setString( 6, location.getStreet() );
         updateCurrentLocationStatement.setString( 7, location.getArea() );
         updateCurrentLocationStatement.setString( 8, location.getCity() );
         updateCurrentLocationStatement.setString( 9, location.getPostalCode() );
         updateCurrentLocationStatement.setString( 10, location.getRegion() );
         updateCurrentLocationStatement.setString( 11, location.getCountryCode()
            .getEnglishCountryName() ); // TODO store only the 2-letter code itself
         updateCurrentLocationStatement.setString( 12, location.getMotionState()
            .toString() );
         updateCurrentLocationStatement.setInt( 13, (int) location.getAccuracy() );
         updateCurrentLocationStatement.setInt( 14, location.getPlaceId() );
         updateCurrentLocationStatement.setInt( 15, location.getPatternId() );
         updateCurrentLocationStatement.setInt( 16, location.getPatternMatch() );
         updateCurrentLocationStatement.setInt( 17, userId );

         // execute query
         updateCurrentLocationStatement.execute();

      }

      // update the geom field
      updateGeometry( "current_locations", "geom", "user_id", "" + userId, location
         .getLatitude(), location.getLongitude() );
   }


   /**
    * Adds an entry to the <i>next_locations</i> table for the specified user and next
    * location label
    * 
    * @param userId
    *           The user ID
    * @param label
    *           The location label
    * @throws SQLException
    */
   public void setNextLocation(int userId, String label) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (insertNextPlaceLogStatement == null)
      {

         insertNextPlaceLogStatement =
            connection
               .prepareStatement( "INSERT INTO next_locations (user_id, label) VALUES (?, ?)" );
      }
      // set variables
      insertNextPlaceLogStatement.setInt( 1, userId );
      insertNextPlaceLogStatement.setString( 2, label );

      // execute query
      insertNextPlaceLogStatement.execute();
   }


   /**
    * Updates the PostGIS geometry of a particular entry in a table. If lat and lon are
    * both zero, the geometry will be set to NULL
    * 
    * @param table
    *           The table name
    * @param column
    *           The name of geometry column
    * @param keyName
    *           The name of the key column
    * @param keyValue
    *           The value of the key for the item to be updated
    * @param lat
    *           The latitude of the new geometry
    * @param lon
    *           The longitude of the new geometry
    * @throws SQLException
    */
   private void updateGeometry(String table, String column, String keyName,
      String keyValue, double lat, double lon) throws SQLException
   {
      String query = null;
      if (lat != 0 || lon != 0)
      {
         query =
            String
               .format(
                  "UPDATE %s SET %s = ST_PointFromText('POINT(%f %f)', 4326)  WHERE %s = %s;",
                  table, column, lon, lat, keyName, keyValue );
         logger.debug( "GEOM, query = " + query );
      }
      else
      {
         query =
            String.format( "UPDATE %s SET %s = NULL WHERE %s = %s;", table, column,
               keyName, keyValue );
      }
      Statement statement = connection.createStatement();
      statement.execute( query );
   }


   /**
    * Updates the last entry of the specified user in the table <i>location_history</i>,
    * if the specified user has an entries in this table.
    * 
    * @param userId
    *           The user ID
    * @param location
    *           The location to overwrite the last entry
    * @throws SQLException
    */
   public void updateLastLocationHistoryEntry(Integer userId, Location location)
      throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // PART 1: get the row where the current place of the user is stored

      // set up statement if needed
      if (selectLastLocationHistoryEntryStatement == null)
      {

         selectLastLocationHistoryEntryStatement =
            connection.prepareStatement( "SELECT history_id FROM location_history WHERE "
                                         + "user_id = ? "
                                         + "ORDER BY timestamp DESC LIMIT 1;" );
      }
      // set variables
      selectLastLocationHistoryEntryStatement.setInt( 1, userId );

      // execute query
      ResultSet rs = selectLastLocationHistoryEntryStatement.executeQuery();
      if (rs.next())
      {
         int id = rs.getInt( "history_id" );

         // PART 2: Replace data

         // set up statement if needed
         if (updateLocationHistoryStatement == null)
         {
            updateLocationHistoryStatement =
               connection.prepareStatement( "UPDATE location_history SET "
                                            + "user_id = ?, " + "label = ?, "
                                            + "latitude = ?, " + "longitude = ?, "
                                            + "place = ?, " + "street = ?, "
                                            + "area = ?, " + "city = ?, "
                                            + "postal_code = ?, " + "region = ?, "
                                            + "country = ?, " + "motion_state = ?, "
                                            + "error = ?, " + "place_id = ?, "
                                            + "pattern_id = ?, " + "pattern_match = ? "
                                            + "WHERE history_id = ?;" );
         }

         // set variables
         updateLocationHistoryStatement.setInt( 1, userId );
         updateLocationHistoryStatement.setString( 2, location.getLabel() );
         updateLocationHistoryStatement.setDouble( 3, location.getLatitude() );
         updateLocationHistoryStatement.setDouble( 4, location.getLongitude() );
         updateLocationHistoryStatement.setString( 5, location.getPlaceName() );
         updateLocationHistoryStatement.setString( 6, location.getStreet() );
         updateLocationHistoryStatement.setString( 7, location.getArea() );
         updateLocationHistoryStatement.setString( 8, location.getCity() );
         updateLocationHistoryStatement.setString( 9, location.getPostalCode() );
         updateLocationHistoryStatement.setString( 10, location.getRegion() );
         updateLocationHistoryStatement.setString( 11, location.getCountryCode()
            .getEnglishCountryName() ); // TODO store only the 2-letter code itself
         updateLocationHistoryStatement.setString( 12, location.getMotionState()
            .toString() );
         updateLocationHistoryStatement.setInt( 13, (int) location.getAccuracy() );
         updateLocationHistoryStatement.setInt( 14, location.getPlaceId() );
         updateLocationHistoryStatement.setInt( 15, location.getPatternId() );
         updateLocationHistoryStatement.setInt( 16, location.getPatternMatch() );
         updateLocationHistoryStatement.setInt( 17, id );

         // execute query
         updateLocationHistoryStatement.execute();
      }
      rs.close();
   }


   /**
    * Updates the place history entry for the specified user and entry time with the
    * specified place ID and exit time in table <i>place_history</i>
    * 
    * @param userId
    *           The user ID
    * @param newPlaceId
    *           The id of the corrected place (may be same as old entry)
    * @param exitTime
    *           The time the user exited the place
    * @throws SQLException
    *            if something wrong is not right
    */
   public void updateLastPlaceHistoryEntry(int userId, int newPlaceId, long exitTime)
      throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // PART 1: get the row where the current place of the user is stored

      // set up statement if needed
      if (selectLastPlaceHistoryEntryStatement == null)
      {

         selectLastPlaceHistoryEntryStatement =
            connection.prepareStatement( "SELECT entry_time FROM place_history WHERE "
                                         + "user_id = ? "
                                         + "ORDER BY entry_time DESC LIMIT 1;" );
      }
      // set variables
      selectLastPlaceHistoryEntryStatement.setInt( 1, userId );

      // execute query
      ResultSet rs = selectLastPlaceHistoryEntryStatement.executeQuery();
      if (rs.next())
      {
         Timestamp entryTime = rs.getTimestamp( "entry_time" );

         // PART 2: Replace data

         // set up statement if needed
         if (updatePlaceHistoryStatement == null)
         {
            updatePlaceHistoryStatement =
               connection
                  .prepareStatement( "UPDATE place_history SET place_id=?, exit_time=? WHERE user_id = ? AND entry_time = ?" );
         }

         // set variables
         updatePlaceHistoryStatement.setInt( 1, newPlaceId );
         updatePlaceHistoryStatement.setTimestamp( 2, exitTime == 0 ? null
            : new Timestamp( exitTime ) );
         updatePlaceHistoryStatement.setInt( 3, userId );
         updatePlaceHistoryStatement.setTimestamp( 4, entryTime );

         // execute query
         updatePlaceHistoryStatement.execute();
      }
   }


   /**
    * Updates the details in the table <i>places</i> with thos of the supplied place (ID
    * must be set)
    * 
    * @param p
    *           The place
    * @param revision
    *           The new revision number
    * @throws SQLException
    */
   public void updatePlace(Place p, int revision) throws SQLException
   {

      // make sure we're connected
      validateConnected();

      // set up statement if needed
      if (updatePlaceStatement == null)
      {
         updatePlaceStatement =
            connection.prepareStatement( "UPDATE places SET " + "is_public = ?, "
                                         + "name = ?, " + "description = ?, "
                                         + "street = ?, " + "area = ?, " + "city = ?, "
                                         + "postal_code = ?, " + "region = ?, "
                                         + "country = ?, " + "wiki_url = ?, "
                                         + "site_url = ?, " + "latitude = ?, "
                                         + "longitude = ?, " + "accuracy = ?, "
                                         + "revision = ? " + "WHERE place_id = ?;" );
      }
      // set variables
      updatePlaceStatement.setBoolean( 1, p.isPublic() );
      updatePlaceStatement.setString( 2, p.getName() );
      updatePlaceStatement.setString( 3, p.getDescription() );
      updatePlaceStatement.setString( 4, p.getStreet() );
      updatePlaceStatement.setString( 5, p.getArea() );
      updatePlaceStatement.setString( 6, p.getCity() );
      updatePlaceStatement.setString( 7, p.getPostalCode() );
      updatePlaceStatement.setString( 8, p.getRegion() );
      updatePlaceStatement.setString( 9, p.getCountryCode().getEnglishCountryName() ); // TODO
      // store
      // only
      // the
      // 2-letter
      // code
      // itself
      updatePlaceStatement.setString( 10, p.getWikiUrl() );
      updatePlaceStatement.setString( 11, p.getSiteUrl() );
      updatePlaceStatement.setDouble( 12, p.getLatitude() );
      updatePlaceStatement.setDouble( 13, p.getLongitude() );
      updatePlaceStatement.setDouble( 14, p.getAccuracy() );
      updatePlaceStatement.setDouble( 15, revision );
      updatePlaceStatement.setInt( 16, p.getId() );

      // execute query
      updatePlaceStatement.execute();

      // update the geom column
      updateGeometry( "places", "geom", "place_id", "" + p.getId(), p.getLatitude(), p
         .getLongitude() );

   }

}
