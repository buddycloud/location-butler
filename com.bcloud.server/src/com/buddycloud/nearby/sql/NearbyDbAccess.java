/**
 * 
 */

package com.buddycloud.nearby.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.jabberstudio.jso.JID;

import com.buddycloud.channels.Channel;
import com.buddycloud.common.GeneralLocation;
import com.buddycloud.common.Place;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.sql.CommonDbAccess;
import com.buddycloud.geoid.Point;
import com.buddycloud.location.CountryCode;
import com.buddycloud.nearby.NearbyObject;

/**
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


public class NearbyDbAccess extends CommonDbAccess
{

   private PreparedStatement selectChannelsNearPointStatementOG;

   private PreparedStatement selectChannelsNearUserStatement;

   private PreparedStatement selectPlacesNearPointStatement;

   private PreparedStatement selectPlacesNearUserStatement;

   private PreparedStatement selectUsersNearPointStatement;

   private PreparedStatement selectUsersNearUserStatement;

   private PreparedStatement selectChannelsNearPointStatement;


   /**
    * @deprecated
    * @param rs
    * @param maxRange
    * @return
    * @throws SQLException
    */
   private Collection<NearbyObject<Channel>> extractNearbyChannelsOG(ResultSet rs,
      int maxRange) throws SQLException
   {
      ArrayList<NearbyObject<Channel>> nearbyChannels =
         new ArrayList<NearbyObject<Channel>>();

      while (rs.next())
      {
         // int cid = rs.getInt( "nn_id" );
         int d = rs.getInt( "distance" );
         if (d <= maxRange)
         {
            String country = rs.getString( "country" );
            String region = rs.getString( "region" );
            String city = rs.getString( "city" );
            String area = rs.getString( "area" );
            // Double lat = rs.getDouble( "latitude" );
            // Double lon = rs.getDouble( "longitude" );

            String id = rs.getString( "id" );
            String name = rs.getString( "name" );
            String host = rs.getString( "host" );
            String description = rs.getString( "description" );
            String subject = rs.getString( "subject" );

            int rank = rs.getInt( "rank" );
            logger.info( "Nearby Channel: " + name + "( " + id + "@" + host + ", rank "
                         + rank + ")" );

            try
            {
               NearbyObject<Channel> o = new NearbyObject<Channel>();
               Channel c = new Channel();
               c.setJid( new JID( id + "@" + host ) );
               c.setName( name );
               c.setPersonal( false );
               c.setDescription( description );
               c.setSubject( subject );
               c.setRank( rank );
               GeneralLocation l = new GeneralLocation();
               l.setArea( area );
               l.setCity( city );
               l.setRegion( region );
               l.setCountryCode( CountryCode.getInstance( country ) );
               o.setObject( c );
               o.setLocation( l );
               o.setDistance( d );

               nearbyChannels.add( o );
            }
            catch (Exception e)
            {
               logger.error( "Exception occured while extracting channel '" + name
                             + " from db: " + e.getMessage() );
            }
         }

      }

      return nearbyChannels;
   }


   private Collection<NearbyObject<Channel>> extractNearbyChannels(ResultSet rs,
      int maxRange) throws SQLException
   {
      ArrayList<NearbyObject<Channel>> nearbyChannels =
         new ArrayList<NearbyObject<Channel>>();

      while (rs.next())
      {
         // int cid = rs.getInt( "nn_id" );
         int d = rs.getInt( "distance" );
         if (d <= maxRange)
         {
//            String country = rs.getString( "country" );
//            String region = rs.getString( "region" );
//            String city = rs.getString( "city" );
//            String area = rs.getString( "area" );

            String id = rs.getString( "id" ); // note: = nodename
            String title = rs.getString( "title" );
            String description = rs.getString( "description" );
            int rank = rs.getInt( "rank" );
            logger.info( "Nearby Channel: " + title + "( " + id + ", rank "
                         + rank + ")" );

            try
            {
               NearbyObject<Channel> o = new NearbyObject<Channel>();
               Channel c = new Channel();
               c.setNode( id );
               c.setTitle( title );
               c.setDescription( description );
               c.setRank( rank );
//               GeneralLocation l = new GeneralLocation();
//               l.setArea( area );
//               l.setCity( city );
//               l.setRegion( region );
//               l.setCountryCode( CountryCode.getInstance( country ) );
               o.setObject( c );
//               o.setLocation( l );
               o.setDistance( d );

               nearbyChannels.add( o );
            }
            catch (Exception e)
            {
               logger.error( "Exception occured while extracting channel '" + name
                             + " from db: " + e.getMessage() );
            }
         }

      }

      logger.debug("Nearby channels: "+nearbyChannels.size());
      return nearbyChannels;
   }


   private Collection<NearbyObject<Place>> extractNearbyPlaces(ResultSet rs, int maxRange)
      throws SQLException
   {
      ArrayList<NearbyObject<Place>> nearbyPlaces = new ArrayList<NearbyObject<Place>>();

      while (rs.next())
      {
         int d = rs.getInt( "distance" );
         if (d <= maxRange)
         {
            int pid = rs.getInt( "id" );
            String name = rs.getString( "name" );
            String street = rs.getString( "street" );
            String area = rs.getString( "area" );
            String city = rs.getString( "city" );
            String region = rs.getString( "region" );
            String country = rs.getString( "country" );

            Place p = new Place();
            p.setId( pid );
            p.setName( name );
            p.setPublic( true );
            p.setStreet( street );
            p.setArea( area );
            p.setCity( city );
            p.setRegion( region );
            p.setCountryCode( CountryCode.getInstance( country ) );

            GeneralLocation l = new GeneralLocation();
            l.setArea( area );
            l.setCity( city );
            l.setRegion( region );
            l.setCountryCode( CountryCode.getInstance( country ) );

            NearbyObject<Place> o = new NearbyObject<Place>();
            o.setDistance( d );
            o.setLocation( l );
            o.setObject( p );

            nearbyPlaces.add( o );
         }

      }

      return nearbyPlaces;

   }


   private Collection<NearbyObject<LocationUser>> extractNearbyUsers(ResultSet rs,
      int maxRange) throws SQLException
   {

      ArrayList<NearbyObject<LocationUser>> nearbyUsers =
         new ArrayList<NearbyObject<LocationUser>>();

      while (rs.next())
      {
         int uid = rs.getInt( "nn_id" );
         int d = rs.getInt( "distance" );
         if (d <= maxRange)
         {
            String country = rs.getString( "country" );
            String region = rs.getString( "region" );
            String city = rs.getString( "city" );
            String area = rs.getString( "area" );

            NearbyObject<LocationUser> o = new NearbyObject<LocationUser>();
            LocationUser u = getLocationUser( uid );
            if (u != null)
            {
               GeneralLocation l = new GeneralLocation();
               l.setArea( area );
               l.setCity( city );
               l.setRegion( region );
               l.setCountryCode( CountryCode.getInstance( country ) );
               o.setObject( u );
               o.setLocation( l );
               o.setDistance( d );

               nearbyUsers.add( o );
            }
            else
            {
               logger.error( "Location user with id " + uid + " not found." );
            }
         }

      }

      return nearbyUsers;
   }


   /**
    * @deprecated
    * @param point
    * @param limit
    * @param range
    * @param since
    * @return
    * @throws SQLException
    */
   public Collection<NearbyObject<Channel>> getNearestChannelsOG(Point point, int limit,
      int range, long since) throws SQLException
   {

      validateConnected();

      if (selectChannelsNearPointStatementOG == null)
      {
         selectChannelsNearPointStatementOG =
            connection.prepareStatement( "SELECT * from bc_nearest_channel(?, ?, ?, ?)" );
      }

      selectChannelsNearPointStatementOG.setDouble( 1, point.getLatitude() );
      selectChannelsNearPointStatementOG.setDouble( 2, point.getLongitude() );
      selectChannelsNearPointStatementOG.setTimestamp( 3, new Timestamp( since ) );
      selectChannelsNearPointStatementOG.setInt( 4, limit );

      ResultSet rs = selectChannelsNearPointStatementOG.executeQuery();

      Collection<NearbyObject<Channel>> nearbyChannels =
         extractNearbyChannelsOG( rs, range );
      rs.close();

      return nearbyChannels;
   }


   /**
    * @param point
    * @param limit
    * @param range
    * @param since
    * @return
    * @throws SQLException
    */
   public Collection<NearbyObject<Channel>> getNearestChannels(Point point, int limit,
      int range, long since) throws SQLException
   {

      validateConnected();

      if (selectChannelsNearPointStatement == null)
      {
         selectChannelsNearPointStatement =
            connection
               .prepareStatement( "SELECT bn.nodename AS id, bn.title, bn.description, nc.distance, mm.rank FROM maitred.bc_nearest_channel_ng(?, ?, ?) nc, broadcaster.leafnode bn, maitred.channel_metadata mm WHERE bn.leafnode_id = nc.leafnode_id AND mm.leafnode_id = nc.leafnode_id" );
      }

      selectChannelsNearPointStatement.setDouble( 1, point.getLatitude() );
      selectChannelsNearPointStatement.setDouble( 2, point.getLongitude() );
      selectChannelsNearPointStatement.setInt( 3, limit );

      ResultSet rs = selectChannelsNearPointStatement.executeQuery();

      Collection<NearbyObject<Channel>> nearbyChannels =
         extractNearbyChannels( rs, range );
      rs.close();

      return nearbyChannels;
   }


   /**
    * @deprecated
    * @param userRef
    * @param limit
    * @param range
    * @param since
    * @return
    * @throws SQLException
    */
   public Collection<NearbyObject<Channel>> getNearestChannels(LocationUser userRef,
      int limit, int range, long since) throws SQLException
   {
      validateConnected();

      if (selectChannelsNearUserStatement == null)
      {
         selectChannelsNearUserStatement =
            connection.prepareStatement( "SELECT * from bc_nearest_channel(?, ?, ?)" );
      }

      selectChannelsNearUserStatement.setString( 1, userRef.getJid().toString() );
      selectChannelsNearUserStatement.setTimestamp( 2, new Timestamp( since ) );
      selectChannelsNearUserStatement.setInt( 3, limit );

      ResultSet rs = selectChannelsNearUserStatement.executeQuery();

      Collection<NearbyObject<Channel>> nearbyChannels =
         extractNearbyChannelsOG( rs, range );
      rs.close();

      return nearbyChannels;
   }


   public Collection<NearbyObject<Place>> getNearestPlaces(Point point, int limit,
      int range) throws SQLException
   {
      validateConnected();

      // set up statement if needed
      if (selectPlacesNearPointStatement == null)
      {
         selectPlacesNearPointStatement =
            connection.prepareStatement( "SELECT * FROM bc_nearest_place(?, ?, ?);" );
      }

      selectPlacesNearPointStatement.setDouble( 1, point.getLatitude() );
      selectPlacesNearPointStatement.setDouble( 2, point.getLongitude() );
      selectPlacesNearPointStatement.setInt( 3, limit );

      ResultSet rs = selectPlacesNearPointStatement.executeQuery();

      Collection<NearbyObject<Place>> nearbyPlaces = extractNearbyPlaces( rs, range );

      rs.close();
      return nearbyPlaces;
   }


   /**
    * @param userRef
    * @param limit
    * @param range
    * @return
    * @throws SQLException
    */
   public Collection<NearbyObject<Place>> getNearestPlaces(LocationUser userRef,
      int limit, int range) throws SQLException
   {
      validateConnected();

      // set up statement if needed
      if (selectPlacesNearUserStatement == null)
      {
         selectPlacesNearUserStatement =
            connection.prepareStatement( "SELECT * FROM bc_nearest_place(?, ?);" );
      }

      selectPlacesNearUserStatement.setInt( 1, userRef.getId() );
      selectPlacesNearUserStatement.setInt( 2, limit );

      ResultSet rs = selectPlacesNearUserStatement.executeQuery();

      Collection<NearbyObject<Place>> nearbyPlaces = extractNearbyPlaces( rs, range );

      rs.close();
      return nearbyPlaces;

   }


   public Collection<NearbyObject<LocationUser>> getNearestUsers(Point point, int limit,
      int range, long since) throws SQLException
   {

      validateConnected();

      if (selectUsersNearPointStatement == null)
      {
         selectUsersNearPointStatement =
            connection.prepareStatement( "SELECT * from bc_nearest_neighbor(?, ?, ?, ?)" );
      }

      selectUsersNearPointStatement.setDouble( 1, point.getLatitude() );
      selectUsersNearPointStatement.setDouble( 2, point.getLongitude() );
      selectUsersNearPointStatement.setTimestamp( 3, new Timestamp( since ) );
      selectUsersNearPointStatement.setInt( 4, limit );

      ResultSet rs = selectUsersNearPointStatement.executeQuery();

      Collection<NearbyObject<LocationUser>> nearbyUsers = extractNearbyUsers( rs, range );
      rs.close();

      return nearbyUsers;
   }


   /**
    * @param userRef
    * @param limit
    * @param range
    * @param since
    * @return
    * @throws SQLException
    */
   public Collection<NearbyObject<LocationUser>> getNearestUsers(LocationUser userRef,
      int limit, int range, long since) throws SQLException
   {
      validateConnected();

      if (selectUsersNearUserStatement == null)
      {
         selectUsersNearUserStatement =
            connection.prepareStatement( "SELECT * from bc_nearest_neighbor(?, ?, ?)" );
      }

      selectUsersNearUserStatement.setInt( 1, userRef.getId() );
      selectUsersNearUserStatement.setTimestamp( 2, new Timestamp( since ) );
      selectUsersNearUserStatement.setInt( 3, limit );

      ResultSet rs = selectUsersNearUserStatement.executeQuery();

      Collection<NearbyObject<LocationUser>> nearbyUsers = extractNearbyUsers( rs, range );
      rs.close();

      return nearbyUsers;
   }
}
