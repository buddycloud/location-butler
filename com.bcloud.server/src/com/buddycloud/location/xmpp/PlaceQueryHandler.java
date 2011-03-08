
package com.buddycloud.location.xmpp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;

import org.jabberstudio.jso.InfoQuery;
import org.jabberstudio.jso.Packet;
import org.jabberstudio.jso.PacketError;
import org.jabberstudio.jso.StreamElement;

import com.buddycloud.common.Location;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.Place;
import com.buddycloud.common.xmpp.PacketFilter;
import com.buddycloud.common.xmpp.PacketHandler;
import com.buddycloud.common.xmpp.XmppUtils;
import com.buddycloud.geoid.Point;
import com.buddycloud.location.BeaconPattern;
import com.buddycloud.location.CountryCode;
import com.buddycloud.location.LocationEngine;
import com.buddycloud.location.sql.LocationDbAccess;
import com.buddycloud.thirdparty.services.GoogleMapsWebService;

/**
 * Handler for place management stanzas
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

public class PlaceQueryHandler extends PacketHandler
{

   private static final String ID_PREFIX = "http://buddycloud.com/places/";

   private static final String QUERY_NAME_SPACE = "http://buddycloud.com/protocol/place";

   private LocationDbAccess lDB;

   private LocationEngine locationEngine;

   private LocationEventHandlerRegistry locationEventHub;

   private String logPrefix;

   /** Names of places that can only be personal/private */
   private Collection<String> nonSharablePlaceNames;


   public PlaceQueryHandler()
   {

      super();

      this.lDB = new LocationDbAccess();

      locationEngine = new LocationEngine( lDB );

      locationEventHub = LocationEventHandlerRegistry.getInstance();

      nonSharablePlaceNames = new ArrayList<String>();
      try
      {
         Preferences prefs = Preferences.userNodeForPackage( getClass() );
         String placeNames = prefs.get( "non_sharable_place_names", "" );
         if (placeNames != null && placeNames.length() > 0)
         {
            // remove extra spaces
            while (placeNames.contains( ", " ))
            {
               placeNames = placeNames.replace( ", ", "," );
            }

            // split to array
            String[] placeNameArray = placeNames.split( "," );

            // add to collection
            for (String s : placeNameArray)
            {
               nonSharablePlaceNames.add( s.toLowerCase() );
               logger.info( "Added non-sharable place name '" + s + "'" );
            }
         }
      }
      catch (Exception e)
      {
         logger.error( "Failed to load non-sharable place names from config.xml" );
      }
   }


   /**
    * Checks if it is ok to create a new place or edit an existing place with the supplied
    * details. There will be a conflict if:
    * 
    * there are any places with same name already defined, suppled place has no street
    * address or same street address as existing place
    * 
    * and
    * 
    * both places are public
    * 
    * or
    * 
    * existing place was defined by same user
    * 
    * @param place
    *           The place to be created or edited
    * @return null if ok, otherwise an error message
    */
   private String checkForConflict(LocationUser user, Place place)
   {

      try
      {

         // check for existing places with same name
         Collection<Place> existingPlaces = lDB.getPlaces( place.getName() );
         String errMsg = null;

         for (Place p : existingPlaces)
         {

            if (p.getId() != place.getId())
            {

               // if an identically named place exists, and submitted place has no street
               // info or same street info as existing, there may be a conflict
               if (place.getStreet() == null || place.getStreet().length() == 0
                   || place.getStreet().equals( p.getStreet() ))
               {
                  String adr = p.getGeneralLocation().toString();
                  if (p.getStreet() != null && p.getStreet().length() > 0)
                  {
                     adr = p.getStreet() + ", " + adr;
                  }

                  // if both places are public: conflict
                  if (place.isPublic() && p.isPublic())
                  {
                     errMsg =
                        "A place named '"
                           + place.getName()
                           + "' ("
                           + adr
                           + ") already exists with id "
                           + ID_PREFIX
                           + p.getId()
                           + ": Please use this, make place private or specify a different street address";
                  }

                  // if private, and same owner as existing, reject
                  else if (p.getOwnerId() == user.getId())
                  {
                     errMsg =
                        "You already have a personal place named '" + place.getName()
                           + "' (" + adr + ") with id " + ID_PREFIX + p.getId()
                           + ": Please use this or specify a different street address";
                  }
               }
            }
         }
         return errMsg;
      }
      catch (Exception e)
      {
         return e.getMessage() + " @ " + e.getStackTrace()[0];
      }

   }


   /**
    * @param iq
    * @param locations
    * @param populations
    * @return
    */
   private InfoQuery createPlaceListResult(InfoQuery iq, Collection<Place> places,
      Collection<String> features)
   {
      InfoQuery res = (InfoQuery) iq.copy();
      res.setTo( iq.getFrom() );
      res.setFrom( iq.getTo() );
      res.setType( InfoQuery.RESULT );
      StreamElement queryElement = res.getFirstElement( "query" );
      if (queryElement == null)
      {
         queryElement = res.addElement( "query", QUERY_NAME_SPACE );
      }
      queryElement.clearElements();

      for (Place place : places)
      {
         StreamElement placeElement = queryElement.addElement( "place" );
         createPlaceStanza( placeElement, place, features );
      }
      return res;
   }


   /**
    * Creates a place query result for the supplied place, population and feature list
    * 
    * @param iq
    * @param place
    * @param features
    * @return
    */
   private InfoQuery createPlaceResult(InfoQuery iq, Place place,
      Collection<String> features)
   {
      InfoQuery res = (InfoQuery) iq.copy();
      res.clearElements();
      res.setTo( iq.getFrom() );
      res.setFrom( iq.getTo() );
      res.setType( InfoQuery.RESULT );
      StreamElement queryElement = res.addElement( "query", QUERY_NAME_SPACE );
      StreamElement placeElement = queryElement.addElement( "place" );
      createPlaceStanza( placeElement, place, features );
      return res;
   }


   /**
    * Adds sub-element to the supplied placeElement as given by supplied place and
    * features list
    * 
    * @param placeElement
    * @param place
    * @param features
    */
   private void createPlaceStanza(StreamElement placeElement, Place place,
      Collection<String> features)
   {
      if (features == null || features.contains( "id" ) && place.getId() > 0) // place
      // search
      // results
      // may have
      // no ID
      {
         addChildElementTextIfNotNull( placeElement, "id", ID_PREFIX + place.getId() );
      }
      if (features == null || features.contains( "name" ))
      {
         addChildElementTextIfNotNull( placeElement, "name", place.getName() );
      }
      if (features == null || features.contains( "description" ))
      {
         addChildElementTextIfNotNull( placeElement, "description", place
            .getDescription() );
      }
      if (features == null || features.contains( "shared" ))
      {
         addChildElementTextIfNotNull( placeElement, "shared", place.isPublic() );
      }
      if (( features == null || features.contains( "lat" ) ) && place.getLatitude() > 0)
      {
         addChildElementTextIfNotNull( placeElement, "lat", place.getLatitude() );
      }
      if (( features == null || features.contains( "lon" ) ) && place.getLatitude() > 0)
      {
         addChildElementTextIfNotNull( placeElement, "lon", place.getLongitude() );
      }
      if (features == null || features.contains( "street" ))
      {
         addChildElementTextIfNotNull( placeElement, "street", place.getStreet() );
      }
      if (features == null || features.contains( "area" ))
      {
         addChildElementTextIfNotNull( placeElement, "area", place.getArea() );
      }
      if (features == null || features.contains( "city" ))
      {
         addChildElementTextIfNotNull( placeElement, "city", place.getCity() );
      }
      if (features == null || features.contains( "region" ))
      {
         addChildElementTextIfNotNull( placeElement, "region", place.getRegion() );
      }
      if (features == null || features.contains( "country" )
          && place.getCountryCode() != null)
      {
         addChildElementTextIfNotNull( placeElement, "country", place.getCountryCode()
            .getEnglishCountryName() );
      }
      if (features == null || features.contains( "postalcode" ))
      {
         addChildElementTextIfNotNull( placeElement, "postalcode", place.getPostalCode() );
      }
      if (features == null || features.contains( "site" ))
      {
         addChildElementTextIfNotNull( placeElement, "site", place.getSiteUrl() );
      }
      if (features == null || features.contains( "wiki" ))
      {
         addChildElementTextIfNotNull( placeElement, "wiki", place.getWikiUrl() );
      }
      if (features == null || features.contains( "population" ))
      {
         try
         {
            int population = lDB.getUsersAtPlace( place.getId() ).size();
            addChildElementTextIfNotNull( placeElement, "population", population );
         }
         catch (Exception e)
         {
            logger.error( logPrefix + "Failed to get population of place " + place );
         }
      }
      if (features == null || features.contains( "revision" ))
      {
         addChildElementTextIfNotNull( placeElement, "revision", place.getRevision() );
      }

   }


   /**
    * Common method for deleting place and pushing updates to affected users. Used by
    * handleDeletePlace and handleDeleteAllPlaces
    * 
    * @param iq
    *           The original info query (needed for stream data factory and from-JID)
    * @param userId
    *           The user ID
    * @param placeId
    *           The ID of the place to be removed or deleted
    * @param delete
    *           If true, place will be deleted, if false, it will merely be removed from
    *           user#s "my places" list
    * @throws SQLException
    */
   private void deleteOrRemovePlace(InfoQuery iq, int userId, int placeId, boolean delete)
      throws SQLException
   {

      Location currentLocation = lDB.getCurrentLocation( userId );
      Location previousLocation = null;
      if (currentLocation != null)
      {
         previousLocation =
            lDB.getPreviousLocation( userId, currentLocation.getPlaceId() );
      }

      if (delete)
      {

         lDB.deletePlaceRefsFromPlaceSubscriptions( placeId );
         lDB.deletePlaceRefsFromPlaceHistory( placeId );
         lDB.deletePlaceRefsFromLocationHistory( placeId );
         for (BeaconPattern pattern : lDB.getBeaconPatternsForPlace( placeId ))
         {
            lDB.deleteBeaconPattern( pattern.getId() );
         }
         lDB.deletePlace( placeId );
         logger.info( logPrefix + "Deleted place with id " + placeId );

      }
      else
      {

         lDB.deleteUserPlaceRefsFromPlaceHistory( userId, placeId );
         lDB.deleteUserPlaceRefsFromLocationHistory( userId, placeId );
         lDB.deleteUserPlaceRefsFromPlaceSubscriptions( userId, placeId );
         logger.info( logPrefix + "Removed place with id " + placeId + " for user "
                      + userId );
      }

      // if place was referenced by current location, push update
      if (currentLocation != null && currentLocation.getPlaceId() == placeId)
      {
         // new current location is old one minus the place reference
         logger.info( logPrefix
                      + "Place referenced by current location was deleted or removed" );
         logger.info( logPrefix + "Old current location: " + currentLocation.getLabel() );
         currentLocation.setPlaceId( -1 );
         currentLocation.setPlaceName( null );
         logger.info( logPrefix + "New current location: " + currentLocation.getLabel() );

         Packet currLocPubsubUpdate =
            XmppExtensionsFactory.createPubsubCurrentLocationPacket( iq.getDataFactory(),
               iq.getFrom(), currentLocation );
         outQ.enque( currLocPubsubUpdate );
         logger.info( logPrefix + "Pushed current location update to pubsub" );

         lDB.setCurrentLocation( userId, currentLocation );
         logger.info( logPrefix + "Uppdated current location in DB: " + currentLocation );
      }

      // if place was referenced by previous location, get the new previous location and
      // push update
      if (previousLocation != null && previousLocation.getPlaceId() == placeId)
      {
         // get new previous location
         logger.info( logPrefix
                      + "Place referenced by previous location was deleted or removed" );
         logger
            .info( logPrefix + "Old previous location: " + previousLocation.getLabel() );
         previousLocation =
            lDB.getPreviousLocation( userId, currentLocation.getPlaceId() );
         logger
            .info( logPrefix + "New previous location: " + previousLocation.getLabel() );

         Packet prevLocPubsubUpdate =
            XmppExtensionsFactory.createPubsubPreviousLocationPacket(
               iq.getDataFactory(), iq.getFrom(), previousLocation );
         outQ.enque( prevLocPubsubUpdate );
         logger.info( logPrefix + "Pushed previous location update to pubsub" );

      }

      // TODO: Notify all subscribers and update current / previous location of other
      // people at place too?

   }


   @Override
   public Collection<PacketFilter> getHandledPacketFilters()
   {
      ArrayList<PacketFilter> filters = new ArrayList<PacketFilter>();
      PacketFilter placeFilter = new PacketFilter();
      placeFilter.addNamespace( "place", QUERY_NAME_SPACE );
      placeFilter.setXPath( "jabber:iq/place:query" );
      filters.add( placeFilter );

      String[] actions =
         new String[]
         {
            "create", "edit", "delete", "delete_all", "search", "current", "next", "add",
            "remove", "myplaces", "history"
         };

      for (String action : actions)
      {
         PacketFilter actionFilter = new PacketFilter();
         actionFilter.addNamespace( "place" + action, QUERY_NAME_SPACE + "#" + action );
         actionFilter.setXPath( "jabber:iq/place" + action + ":query" );

         filters.add( actionFilter );
      }
      return filters;
   }


   private int getMax(StreamElement options)
   {
      int max = 30;
      if (options != null)
      {
         try
         {
            max = Integer.parseInt( options.getAttributeValue( "max" ) );
            logger.info( logPrefix + "Max: " + max );
         }
         catch (Exception e)
         {
            logger.error( logPrefix + "Invalid max result option "
                          + options.getAttributeValue( "max" ) + " ignored" );
         }
      }
      return max;
   }


   /**
    * Extract requested features from <options> element.
    * 
    * @param options
    *           The options element
    * @return List of the requested feature names, or null if none
    */
   private Collection<String> getRequestedFeatures(StreamElement options)
   {
      ArrayList<String> features = null;
      if (options != null)
      {
         for (Object o : options.listElements( "feature" ))
         {
            StreamElement se = (StreamElement) o;
            String var = se.getAttributeValue( "var" );
            if (var != null)
            {
               logger.info( logPrefix + "Requested feature: " + var );
               if (features == null)
                  features = new ArrayList<String>();
               features.add( var );
            }
         }
      }
      return features;
   }


   /**
    * Adds the supplied place to the db with the supplied user as owner, and adds it to
    * his list of subscribed places
    * 
    * @param iq
    *           The triggering IQ
    * @param user
    *           The user
    * @param place
    *           The place
    * @return Result or error packet
    */
   private Packet handleCreatePlace(InfoQuery iq, LocationUser user, Place place)
   {
      if (place == null)
      {
         logger.error( logPrefix + "No place submitted. Can't create." );
         return (InfoQuery) XmppUtils.createBadRequestError( iq,
            "No place submitted. Can't create." );
      }

      // check that the updated place has a name
      if (place.getName() == null || place.getName().length() == 0)
      {
         logger.error( logPrefix + "A place needs a name. Set current canceled." );
         return (InfoQuery) XmppUtils.createBadRequestError( iq, "A place needs a name" );
      }

      // if place is set as shared and on the list of non-sharable place names, set as
      // non-shared
      if (place.isPublic()
          && nonSharablePlaceNames.contains( place.getName().toLowerCase() ))
      {
         place.setPublic( false );
         logger.warn( logPrefix + "Place specified as shared, but name '"
                      + place.getName()
                      + "' is on the list of non-sharable names. Forced to personal." );
      }

      try
      {

         // check for potential conflicts
         String errMsg = checkForConflict( user, place );

         if (errMsg != null)
         {
            logger.info( logPrefix + errMsg );
            return XmppUtils.createBadRequestError( iq, errMsg );
         }

         logger.info( logPrefix + "No name conflict with existing places found." );
         place.setOwnerId( user.getId() );

         // add country code (can't be null in db)
         if (place.getCountryCode() == null)
         {
            Location l = lDB.getCurrentLocation( user.getId() );
            if (l == null)
            {
               errMsg = "No country specified, and current location is unknown";
               logger.info( logPrefix + errMsg );
               return XmppUtils.createBadRequestError( iq, errMsg );
            }
            CountryCode cc = l.getCountryCode();
            place.setCountryCode( cc );
            logger.info( logPrefix + "Added country code " + cc );
         }

         int placeId = lDB.addPlace( place );
         place.setId( placeId );
         logger.info( logPrefix + "Place '" + place.getName() + "' (ID " + place.getId()
                      + ") added to database." );

         // add user as subscriber
         lDB.addPlaceSubscription( user.getId(), place.getId() );
         logger.info( logPrefix + "Added location subscription to location "
                      + place.getName() );

         // wrap place in location to trigger location event
         Location location = new Location();
         location.setPlace( place );
         locationEventHub.userDefinedNewPlace( user, location );

         Packet result = XmppUtils.createCopyResult( iq );
         StreamElement queryElement = result.getFirstElement( "query" );
         StreamElement placeElement = queryElement.getFirstElement( "place" );
         addChildElementTextIfNotNull( placeElement, "id", ID_PREFIX + place.getId() );

         return result;

      }
      catch (Exception e)
      {
         logger.error( logPrefix + "Failed to create new place " + place, e );
         return XmppUtils.createUnhandledExceptionError( iq, e.getClass().getName()
                                                             + ": " + e.getMessage()
                                                             + " @ "
                                                             + e.getStackTrace()[0] );
      }
   }


   private Packet handleDeleteAllPlaces(InfoQuery iq, LocationUser user)
      throws SQLException
   {

      logger.info( logPrefix + "Deleting all places owned by " + user + "." );

      Collection<Place> ownedPlaces;
      try
      {
         ownedPlaces = lDB.getOwnedPlaces( user.getId() );
      }
      catch (Exception e)
      {
         logger.error( logPrefix + "Deleting all places failed: " + e.getMessage(), e );
         return XmppUtils.createUnhandledExceptionError( iq, e.getMessage() );
      }

      String notDeleted = "";
      for (Place p : ownedPlaces)
      {
         try
         {
            int placeId = p.getId();

            logger.info( logPrefix + "Deleting '" + p.getName() + "' (" + placeId + ")" );

            // delete from referencing tables
            lDB.deletePlaceRefsFromPlaceSubscriptions( placeId );
            lDB.deletePlaceRefsFromPlaceHistory( placeId );
            lDB.deletePlaceRefsFromLocationHistory( placeId );
            for (BeaconPattern pattern : lDB.getBeaconPatternsForPlace( placeId ))
            {
               lDB.deleteBeaconPattern( pattern.getId() );
            }
            lDB.deletePlace( placeId );

            logger.info( logPrefix + "Deleted place with ID " + placeId );

         }
         catch (Exception e)
         {
            logger.error( logPrefix + "Failed to delete place '" + p.getName() + "': "
                          + e.getMessage() );
            notDeleted += p.getName() + " ";
         }
      }

      if (notDeleted.length() == 0)
      {
         // reset PEP nodes
         logger.info( logPrefix
                      + "Resetting current and previous location pubsub nodes" );
         Packet currLocPubsubReset =
            XmppExtensionsFactory.createPubsubCurrentLocationPacket( iq.getDataFactory(),
               user.getJid(), new Location() );
         Packet prevLocPubsubReset =
            XmppExtensionsFactory.createPubsubPreviousLocationPacket(
               iq.getDataFactory(), user.getJid(), new Location() );
         outQ.enque( currLocPubsubReset );
         outQ.enque( prevLocPubsubReset );
         // create response iq
         return XmppUtils.createSimpleResult( iq );
      }
      else
      {
         return XmppUtils.createBadRequestError( iq, "Could not delete " + notDeleted );
      }
   }


   private Packet handleDeletePlace(InfoQuery iq, LocationUser user, int placeId)
      throws SQLException
   {

      if (placeId <= 0)
      {
         logger.error( logPrefix + "Missing place ID, place can not be deleted" );
         return XmppUtils.createBadRequestError( iq, "Place ID not positive integer" );
      }

      // check that location exists
      Place p = lDB.getPlace( placeId );
      if (p == null)
      {
         logger.error( logPrefix + "Place with ID " + placeId + " not found in db." );
         return XmppUtils.createItemNotFoundError( iq, "Place does not exist" );
      }

      // check that submitter = owner
      if (p.getOwnerId() != user.getId())
      {
         logger.error( logPrefix + "Could not delete location with ID " + p.getId()
                       + " (" + p.getName() + "). " + iq.getFrom() + " not owner." );
         return XmppUtils.createNotAuthorizedError( iq, "Not place owner" );
      }

      try
      {

         deleteOrRemovePlace( iq, user.getId(), placeId, true );

         // create response iq
         return XmppUtils.createSimpleResult( iq );

      }
      catch (Exception e)
      {
         logger.error( e.getMessage(), e );

         // create response iq
         return XmppUtils.createUnhandledExceptionError( iq, e.getMessage() );
      }

   }


   private Packet handleEditPlace(InfoQuery iq, LocationUser user, Place newPlace)
      throws SQLException
   {

      // get the existing location of this ID
      Place oldPlace = lDB.getPlace( newPlace.getId() );

      // check that it exists
      if (oldPlace == null)
      {
         logger.error( user + " Place with ID " + newPlace.getId() + ". " + iq.getFrom()
                       + " not found in db." );
         return (InfoQuery) XmppUtils
            .createItemNotFoundError( iq, "Place does not exist" );
      }

      // check that the submitting user can modify this location
      if (oldPlace.getOwnerId() != user.getId())
      {
         logger.error( logPrefix + "Could not edit place with ID " + newPlace.getId()
                       + ". " + iq.getFrom() + " not owner. Place edit request denied." );
         return (InfoQuery) XmppUtils.createNotAuthorizedError( iq, "Not place owner" );
      }

      // check that the updated place has a name
      if (newPlace.getName() == null || newPlace.getName().length() == 0)
      {
         logger.error( logPrefix + "A place needs a name. Old name was '"
                       + oldPlace.getName() + "'. Place edit request denied." );
         return (InfoQuery) XmppUtils.createBadRequestError( iq, "A place needs a name" );
      }

      // if place is set as shared and on the list of non-sharable place names, return
      // error
      if (newPlace.isPublic()
          && nonSharablePlaceNames.contains( newPlace.getName().toLowerCase() ))
      {
         logger.warn( logPrefix + "Place edited to shared, but name '"
                      + newPlace.getName()
                      + "' is on the list of non-sharable names. Place edit request denied." );
         return (InfoQuery) XmppUtils.createBadRequestError( iq,
            "A named '" + newPlace.getName()
               + "' cannot be shared. Please rename or set as personal" );
      }

      // if user attempts to change the country of place, deny request
      if (newPlace.getCountryCode() != null && oldPlace.getCountryCode() != null
          && newPlace.getCountryCode() != oldPlace.getCountryCode())
      {
         logger.error( logPrefix + "Existing place is in "
                       + oldPlace.getCountryCode().getEnglishCountryName()
                       + ", attempted edited to "
                       + newPlace.getCountryCode().getEnglishCountryName() + ". Place edit request denied." );
         return XmppUtils.createBadRequestError( iq, "Country can not be changed to "
                                                     + newPlace.getCountryCode()
                                                        .getEnglishCountryName() );
      }

      try
      {
         Location l = lDB.getCurrentLocation( user.getId() );

         if (oldPlace.getCountryCode() != null
             && oldPlace.getCountryCode() != l.getCountryCode())
         {
            logger.error( logPrefix + "User is in "
                          + l.getCountryCode().getEnglishCountryName() + ", place is in "
                          + oldPlace.getCountryCode() + ". Place edit request denied." );
            return XmppUtils.createBadRequestError( iq, "Place not editable outside of "
                                                        + oldPlace.getCountryCode()
                                                           .getEnglishCountryName() );
         }
         Point p = new Point( newPlace.getLatitude(), newPlace.getLongitude() );
         if (!p.isDefault())
         {
            double dist = p.getDistanceTo( l.getPoint() );
            logger.info( logPrefix + "User's current distance from place: " + dist + "m" );
            if (dist > 100000)
            {
               logger.error( logPrefix + "User is too far from place to be from place: "
                             + dist + "m. Place edit request denied." );
               return XmppUtils.createBadRequestError( iq,
                  "Place not editable outside of "
                     + newPlace.getCountryCode().getEnglishCountryName() );
            }
         }
      }
      catch (Exception e)
      {
         logger.warn( logPrefix + "Failed to check distance from place to user:"
                      + e.getMessage() );
      }

      // check for potential conflicts
      String errMsg = checkForConflict( user, newPlace );

      // if conflict, deny request
      if (errMsg != null)
      {
         logger.info( logPrefix + errMsg+". Place edit request denied." );
         return XmppUtils.createBadRequestError( iq, errMsg );
      }

      // update place in DB
      lDB.updatePlace( newPlace, oldPlace.getRevision() + 1 );

      if (!oldPlace.getName().equals( newPlace.getName() ))
      {
         logger.info( logPrefix + "Place " + newPlace.getId() + " renamed: "
                      + oldPlace.getName() + " -> " + newPlace.getName() + " (Revision "
                      + ( oldPlace.getRevision() + 1 ) + ")" );
      }
      else
      {
         logger.info( logPrefix + "Place " + newPlace.getName() + "(ID "
                      + newPlace.getId() + ") edited (Revision "
                      + ( oldPlace.getRevision() + 1 ) + ")" );
      }

      // send PEP update to all users currently at this place
      Collection<Integer> userIds = lDB.getUsersAtPlace( newPlace.getId() );
      for (Integer uId : userIds)
      {
         if (uId != null && uId > 0)
         {
            LocationUser u = lDB.getLocationUser( uId );
            Location newCurrLoc = lDB.getCurrentLocation( user.getId() );
            newCurrLoc.setPlace( newPlace );

            InfoQuery locationPubsubUpdate =
               XmppExtensionsFactory.createPubsubCurrentLocationPacket( iq
                  .getDataFactory(), u.getJid(), newCurrLoc );
            
            outQ.enque( locationPubsubUpdate );

            logger.info( logPrefix + "Pushing current location pubsub update to "
                         + u + ": " + newCurrLoc.getLabel() );

            // update 3rd party apps
            locationEventHub.userPreviousLocationChanged( u, newCurrLoc );
         }
      }

      // if this was the previous place of the user submitting the update, update his prev
      // location node
      Location submittingUserPrevLoc =
         lDB.getPreviousLocation( user.getId(), lDB.getCurrentLocation( user.getId() )
            .getPlaceId() );
      if (submittingUserPrevLoc != null
          && submittingUserPrevLoc.getPlaceId() == newPlace.getId())
      {
         submittingUserPrevLoc.setPlace( newPlace );

         InfoQuery locationPubsubUpdate =
            XmppExtensionsFactory.createPubsubPreviousLocationPacket(
               iq.getDataFactory(), user.getJid(), submittingUserPrevLoc );

         outQ.enque( locationPubsubUpdate );

         logger.info( logPrefix + "Pushing previous location pubsub update : "
                      + submittingUserPrevLoc.getLabel() );

         // update 3rd party apps
         locationEventHub.userPreviousLocationChanged( user, submittingUserPrevLoc );
      }

      // create response iq
      return XmppUtils.createSimpleResult( iq );
   }


   private InfoQuery handleGetPlaceDetails(InfoQuery iq, LocationUser user, int placeId,
      StreamElement options) throws SQLException
   {

      if (placeId >= 0)
      {
         Place p = lDB.getPlace( placeId );
         if (p == null)
         {
            String msg = "Place with ID " + placeId + " does not exist.";
            logger.error( logPrefix + msg );
            return (InfoQuery) XmppUtils.createBadRequestError( iq, msg );
         }
         if (!p.isPublic() && p.getOwnerId() != user.getId())
         {
            String msg = "Place with ID " + placeId + " is private.";
            logger.error( logPrefix + msg );
            return (InfoQuery) XmppUtils.createNotAuthorizedError( iq, msg );
         }

         logger.info( logPrefix + "Getting place details for place with ID " + placeId );

         Collection<String> features = getRequestedFeatures( options );

         return createPlaceResult( iq, p, features );
      }
      else
      {
         return (InfoQuery) XmppUtils.createBadRequestError( iq,
            "Place ID must be a positive integer." );
      }

   }


   private InfoQuery handleGetPlaceHistory(InfoQuery iq, LocationUser user,
      StreamElement options) throws SQLException
   {
      logger.info( logPrefix + "Getting place history:" );
      int max = getMax( options );
      Collection<String> features = getRequestedFeatures( options );

      ArrayList<Place> places = lDB.getPlaceHistory( user.getId(), max );

      InfoQuery response = createPlaceListResult( iq, places, features );

      return response;

   }


   private InfoQuery handleGetPlaceSubscriptions(InfoQuery iq, LocationUser user,
      StreamElement options) throws SQLException
   {

      logger.info( logPrefix + "Getting subscribed places:" );
      Collection<String> features = getRequestedFeatures( options );
      Collection<Place> places = lDB.getPlaceSubscriptions( user.getId() );
      InfoQuery response = createPlaceListResult( iq, places, features );

      return response;

   }


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
      Packet response = null;

      try
      {
         // get the user id corresponding to the submitter's JID
         // Note: will NOT create a new DB entry if submitter is federated user not
         // already
         // known
         LocationUser user = lDB.getLocationUser( iq.getFrom() );

         if (user == null)
         {
            logger.error( "Unknown user: " + iq.getFrom() + ". Form ignored" );
            return XmppUtils.createNotAuthorizedError( iq, "Unknown JID " + iq.getFrom() );
         }

         logPrefix = user + ": ";

         StreamElement queryElement = iq.getFirstElement( "query" );
         String ns = queryElement.getNamespaceURI();
         String action = null;
         if (ns.contains( "#" ))
         {
            action = ns.substring( ns.lastIndexOf( "#" ) + 1, ns.length() );
            logger.info( logPrefix + "Sub name space: " + action );
         }
         if (action != null)
            action = action.trim().toLowerCase();

         StreamElement optionsElement = queryElement.getFirstElement( "options" );

         StreamElement placeElement = queryElement.getFirstElement( "place" );
         Place place = parsePlace( user, placeElement );

         InfoQuery.Type type = (InfoQuery.Type) iq.getType();

         if (type == InfoQuery.GET)
         {
            if (action == null || action.length() == 0)
            {
               response = handleGetPlaceDetails( iq, user, place.getId(), optionsElement );
            }
            else if (action.equals( "search" ))
            {
               response = handlePlaceSearch( iq, user, place, optionsElement );
            }
            else if (action.equals( "myplaces" ))
            {
               response = handleGetPlaceSubscriptions( iq, user, optionsElement );
            }
            else if (action.equals( "history" ))
            {
               response = handleGetPlaceHistory( iq, user, optionsElement );
            }
            else
            {
               response =
                  XmppUtils.createBadRequestError( iq, "Unknown action '" + action + "'" );
            }
         }
         else if (type == InfoQuery.SET)
         {
            if (action == null)
            {
               response =
                  XmppUtils.createBadRequestError( iq,
                     "No action specified. I'm confused." );
            }
            else if (action.equals( "current" ))
            {
               response = handleSetCurrentPlace( iq, user, place );
            }
            else if (action.equals( "create" ))
            {
               response = handleCreatePlace( iq, user, place );
            }
            else if (action.equals( "next" ))
            {
               response = handleSetNextPlace( iq, user, place );
            }
            else if (action.equals( "edit" ))
            {
               response = handleEditPlace( iq, user, place );
            }
            else if (action.equals( "delete" ))
            {
               response = handleDeletePlace( iq, user, place.getId() );
            }
            else if (action.equals( "delete_all" ))
            {
               response = handleDeleteAllPlaces( iq, user );
            }
            else if (action.equals( "add" ))
            {
               response = handlePlaceSubscribe( iq, user, place.getId() );
            }
            else if (action.equals( "remove" ))
            {
               response = handlePlaceUnsubscribe( iq, user, place.getId() );
            }
            else
            {
               response = (InfoQuery) iq.copy();
               response.setID( iq.getID() );
               response.setTo( iq.getFrom() );
               response.setFrom( iq.getTo() );
               response.setType( InfoQuery.RESULT );
               PacketError error =
                  iq.getDataFactory().createPacketError( PacketError.WAIT,
                     PacketError.INTERNAL_SERVER_ERROR_CONDITION );
               error.setText( "Unsupported action: " + action );
               response.setError( error );
            }
         }

      }
      catch (Exception e)
      {
         logger.error( logPrefix + "Failed to handle place management query:", e );
         response = (InfoQuery) iq.copy();
         response.setID( iq.getID() );
         response.setTo( iq.getFrom() );
         response.setFrom( iq.getTo() );
         response.setType( InfoQuery.RESULT );
         response.setError( iq.getDataFactory().createPacketError( PacketError.WAIT,
            PacketError.INTERNAL_SERVER_ERROR_CONDITION ) );
      }

      return response;
   }


   private Packet handlePlaceSearch(InfoQuery iq, LocationUser user, Place place,
      StreamElement options) throws SQLException
   {

      // look for existing places in bc db
      Collection<Place> bcResults = lDB.getPlaces( place.getName() );
      for (Place p : bcResults)
      {
         logger.info( logPrefix + "bc result: '" + p + "' (ID " + p.getId() + ") "
                      + p.getGeneralLocation() );
      }

      // extract place details
      String name = place.getName();
      String country =
         place.getCountryCode() == null ? "" : place.getCountryCode()
            .getEnglishCountryName();
      String region = place.getRegion();
      String city = place.getCity();
      String area = place.getArea();
      String street = place.getStreet();
      String postalCode = place.getPostalCode();

      // get requested features
      Collection<String> features = getRequestedFeatures( options );

      if (features == null || features.size() == 0)
      {
         features = new ArrayList<String>();
         features.add( "id" );
         features.add( "name" );
         features.add( "shared" );
         features.add( "lat" );
         features.add( "lon" );
         features.add( "street" );
         features.add( "city" );
         features.add( "region" );
         features.add( "country" );
         features.add( "postalcode" );
      }

      logger.debug( logPrefix + "Place Search: given: " + name + " | " + street + " | "
                    + area + " | " + city + " | " + region + " | " + postalCode + " | "
                    + country );

      // // augment missing fields with what we may know
      // Location l = lDB.getCurrentLocation( user.getId() );
      //
      // if (l != null)
      // {
      // if (area == null || area.trim().length() == 0)
      // area = l.getArea();
      //
      // if (city == null || city.trim().length() == 0)
      // city = l.getCity();
      //
      // if (region == null || region.trim().length() == 0)
      // region = l.getRegion();
      //
      // if (l.getCountryCode() != null)
      // country = l.getCountryCode().getEnglishCountryName();
      // }
      //
      // logger.debug( logPrefix + "Place Search: augmented : " + name + " | " + street
      // + " | " + area + " | " + city + " | " + region + " | " + postalCode
      // + " | " + country );

      String localHints = "";

      if (street != null && street.trim().length() > 0)
         localHints += street.trim() + " ";

      if (area != null && area.trim().length() > 0)
         localHints += area.trim() + " ";

      if (city != null && city.trim().length() > 0)
         localHints += city.trim() + " ";

      if (postalCode != null && postalCode.trim().length() > 0)
         localHints += postalCode.trim() + " ";

      if (region != null && region.trim().length() > 0)
         localHints += region.trim() + " ";

      localHints = localHints.trim();

      Collection<Place> googleResults = new ArrayList<Place>();
      if (localHints.length() > 0)
      {

         logger.info( logPrefix + "Triggering google search for '" + name
                      + "' with local hints '" + localHints + "' in '" + country + "'" );

         GoogleMapsWebService google = new GoogleMapsWebService();
         try
         {

            googleResults = google.findPlaces( name, localHints, country );

         }
         catch (IOException e)
         {
            logger.error( logPrefix + "Place search failed", e );
         }

         // insert area and region if missing
         for (Place p : googleResults)
         {
            p.setPublic( true );
            if (( p.getArea() == null || p.getArea().trim().length() == 0 )
                && area != null)
            {
               p.setArea( area );
               logger.debug( user + ": Added area/neighbourhood '" + area
                             + "' to result '" + p.getName() + "'" );
            }

            if (( p.getRegion() == null || p.getRegion().trim().length() == 0 )
                && region != null)
            {
               p.setRegion( region );
               logger.debug( user + ": Added region/province '" + region
                             + "' to result '" + p.getName() + "'" );
            }
         }
      }

      ArrayList<Place> results = new ArrayList<Place>();

      // first add buddycloud places in same city
      for (Place p : bcResults)
      {
         if (results.size() < 10 && p.getArea() != null && p.getArea().equals( area ))
         {
            results.add( p );
         }
      }

      // then add buddycloud places in same city
      for (Place p : bcResults)
      {
         if (results.size() < 10 && p.getCity() != null && p.getCity().equals( city ))
         {
            results.add( p );
         }
      }

      // then add google results
      results.addAll( googleResults );

      // then add remaining buddycloud places
      for (Place p : bcResults)
      {
         if (results.size() < 10 && !results.contains( p ))
         {
            results.add( p );
         }
      }

      Packet response = createPlaceListResult( iq, results, features );

      return response;
   }


   private Packet handlePlaceSubscribe(InfoQuery iq, LocationUser user, int placeId)
      throws SQLException
   {

      Place p = lDB.getPlace( placeId );
      if (p == null)
      {
         String msg = "Place with ID " + placeId + " does not exist.";
         logger.error( logPrefix + msg );
         return (InfoQuery) XmppUtils.createBadRequestError( iq, msg );
      }
      if (!p.isPublic() && p.getOwnerId() != user.getId())
      {
         String msg = "Place with ID " + placeId + " is private.";
         logger.error( logPrefix + msg );
         return (InfoQuery) XmppUtils.createNotAuthorizedError( iq, msg );
      }

      Packet response;
      boolean isSubscriber = lDB.isPlaceSubscriber( user.getId(), placeId );

      if (!isSubscriber)
      {
         lDB.addPlaceSubscription( user.getId(), placeId );
         logger.info( logPrefix + "Added place with id " + placeId
                      + " to subscribed places" );

         response = XmppUtils.createCopyResult( iq );
      }
      else
      {
         logger.error( logPrefix + "User already subscribes to place with ID " + placeId );
         response =
            XmppUtils.createBadRequestError( iq, "Place is already on your place list" );
      }
      return response;

   }


   private Packet handlePlaceUnsubscribe(InfoQuery iq, LocationUser user, int placeId)
      throws SQLException
   {

      Packet response;
      boolean isSubscriber = lDB.isPlaceSubscriber( user.getId(), placeId );

      if (isSubscriber)
      {
         deleteOrRemovePlace( iq, user.getId(), placeId, false );

         response = XmppUtils.createCopyResult( iq );
      }
      else
      {
         logger.error( logPrefix + "User does not subscribe to place with ID " + placeId );
         response =
            XmppUtils.createBadRequestError( iq, "Place was not on your place list" );
      }
      return response;

   }


   private Packet handleSetCurrentPlace(InfoQuery iq, LocationUser user, Place place)
      throws SQLException
   {

      if (place == null)
      {
         logger.error( logPrefix + "No place submitted. Can't set current." );
         return (InfoQuery) XmppUtils.createBadRequestError( iq,
            "No place submitted. Can't set current." );
      }

      if (place.getId() <= 0)
      {
         logger.error( logPrefix + "No place ID submitted. Can't set current." );
         return (InfoQuery) XmppUtils.createBadRequestError( iq,
            "No place ID submitted. Can't set current." );
      }

      // generate current beacon pattern
      Location oldCurrentLocation = lDB.getCurrentLocation( user.getId() );
      Location currentLocation = locationEngine.getLocation( user );
      BeaconPattern cellPattern = locationEngine.getCellPattern();
      BeaconPattern wifiPattern = locationEngine.getWifiPattern();

      // get the existing place from the database
      int pid = place.getId();
      place = lDB.getPlace( pid );

      if (place == null)
      {
         logger.error( logPrefix + "Place with ID " + pid + " not found in db." );
         return (InfoQuery) XmppUtils
            .createItemNotFoundError( iq, "Place does not exist" );
      }

      // if place is not in same country as user, return error
      if (place.getCountryCode() != null)
      {
         if (currentLocation != null && currentLocation.getCountryCode() != null)
         {
            if (currentLocation.getCountryCode() != place.getCountryCode())
            {
               logger.error( logPrefix + "ERROR: User is in "
                             + currentLocation.getCountryCode() + ", place is in "
                             + place.getCountryCode() + " Can't set current place to "
                             + place.getName() );
               return (InfoQuery) XmppUtils.createItemNotFoundError( iq,
                  "You are currently in another country." );
            }
         }
         else if (oldCurrentLocation != null
                  && oldCurrentLocation.getCountryCode() != null)
         {
            if (oldCurrentLocation.getCountryCode() != place.getCountryCode())
            {
               logger.error( logPrefix + "ERROR: User is in "
                             + currentLocation.getCountryCode() + ", place is in "
                             + place.getCountryCode() + " Can't set current place to "
                             + place.getName() );
               return (InfoQuery) XmppUtils.createItemNotFoundError( iq,
                  "You are currently in another country." );
            }
         }
      }

      // if location is private, check that it is owned by the submitting
      // user
      if (!place.isPublic() && place.getOwnerId() != user.getId())
      {
         logger.error( logPrefix + "Place with ID " + place.getId() + " is private. "
                       + iq.getFrom() + " not owner." );
         return (InfoQuery) XmppUtils.createNotAuthorizedError( iq, "Place is private" );
      }

      // augment place details from current location

      if (place.getCountryCode() == null)
      {
         logger.info( logPrefix + "Adding place country code'"
                      + currentLocation.getCountryCode()
                      + "' from user's current location" );
         place.setCountryCode( currentLocation.getCountryCode() );
      }
      if (place.getRegion() == null)
      {
         logger.info( logPrefix + "Adding place region '" + currentLocation.getRegion()
                      + "' from user's current location" );
         place.setRegion( currentLocation.getRegion() );
      }
      if (place.getCity() == null)
      {
         logger.info( logPrefix + "Adding place city '" + currentLocation.getCity()
                      + "' from user's current location" );
         place.setCity( currentLocation.getCity() );
      }

      if (place.getLatitude() == 0 && place.getLongitude() == 0
          && !currentLocation.getPoint().isDefault())
      {
         logger.info( logPrefix + "Adding place coordinates ("
                      + currentLocation.getLatitude() + ", "
                      + currentLocation.getLongitude() + " @ "
                      + currentLocation.getAccuracy() + " m)"
                      + " from user's current location" );
         place.setLatitude( currentLocation.getLatitude() );
         place.setLongitude( currentLocation.getLongitude() );
         place.setAccuracy( currentLocation.getAccuracy() );

         lDB.updatePlace( place, place.getRevision() + 1 );
         logger.info( logPrefix
                      + "Updated place in db with position from current location: "
                      + place.getName() + " " + currentLocation.getPosition() );
      }

      // get the currently set location
      Location overridden = lDB.getCurrentLocation( user.getId() );

      Location overriding = new Location();
      overriding.setPlace( place );
      overriding.setArea( overridden.getArea() );
      overriding.setCity( overridden.getCity() );
      overriding.setRegion( overridden.getRegion() );
      overriding.setCountryCode( overridden.getCountryCode() );
      overriding.setMotionState( Location.MotionState.STATIONARY );
      overriding.setAccuracy( Location.Layer.PLACE.getDefaultError() );
      overriding.setPatternMatch( 100 );

      if (overridden != null)
      {
         logger.debug( user + ": Overriding current place " + overridden.getLabel()
                       + " (ID " + overridden.getPlaceId() + " ) to '"
                       + overriding.getLabel() + "' (ID " + overriding.getPlaceId()
                       + ")." );
      }
      else
      {
         logger
            .debug( user
                    + ": Current location is null... no patterns need to be overridden" );
      }

      int overridingPatternId = 0;
      if (cellPattern != null && cellPattern.getElements().size() > 0)
      {
         overridingPatternId = lDB.addBeaconPattern( cellPattern, place.getId() );
         logger.info( logPrefix + "Current cell pattern assigned to location "
                      + place.getId() );
      }
      if (wifiPattern != null && wifiPattern.getElements().size() > 0)
      {
         overridingPatternId = lDB.addBeaconPattern( wifiPattern, place.getId() );
         logger.info( logPrefix + "Current wifi assigned to location " + place.getId() );
      }
      overriding.setPatternId( overridingPatternId );

      // if user was marked (falsely) at some place, override this entry in
      // history
      if (overridden != null && overridden.getPlaceId() > 0 && overridden.isStationary()
          && !overridden.isPlaceNearby()
          && overridden.getPlaceId() != overriding.getPlaceId())
      {

         lDB.updateLastLocationHistoryEntry( user.getId(), overriding );

         logger.info( logPrefix + "Overriding last place history entry from "
                      + overridden.getLabel() + " to " + overriding.getLabel() );
         lDB.updateLastPlaceHistoryEntry( user.getId(), overriding.getPlaceId(), 0 );

         // add override entry (manually set place will be given
         // selection bonus when matching patterns and frequently overridden patterns
         // will be deleted)
         lDB.addManualPlaceOverride( user.getId(), overridden.getPatternId(), overriding
            .getPatternId() );
      }

      // otherwise, add to database
      else
      {
         lDB.addLocationHistory( user.getId(), overriding );
         lDB.addPlaceHistory( user.getId(), overriding.getPlaceId(), System
            .currentTimeMillis() );

         // add override entry even if no pattern has been overridden, this will give
         // the manually set place an advantage during place selection and thus reduce
         // toggling when patterns are not very good
         lDB.addManualPlaceOverride( user.getId(), -1, overriding.getPatternId() );
      }

      // add to current location
      lDB.setCurrentLocation( user.getId(), overriding );

      // if the old pattern has caused trouble before, delete it
      if (overridden != null
          && lDB.getManuallyOverriddenCount( overridden.getPatternId() ) > 1)
      {
         lDB.deleteBeaconPattern( overridden.getPatternId() );
         logger.info( logPrefix + "Deleting pattern with ID " + overridden.getPatternId()
                      + " that has repeatedly resulted in a false fix" );
      }

      // Create a location presence info query packet for the new current
      // location
      InfoQuery currLocPubsubPacket =
         XmppExtensionsFactory.createPubsubCurrentLocationPacket(
            stream.getDataFactory(), iq.getFrom(), overriding );

      // post it
      outQ.enque( currLocPubsubPacket );

      // notify location event handlers
      locationEventHub.userCurrentLocationChanged( user, overriding );
      locationEventHub.userEnteredPlace( user, overriding );

      Location newPreviousLocation =
         lDB.getPreviousLocation( user.getId(), overriding.getPlaceId() );

      // Create a location presence info query packet for the new current
      // location
      InfoQuery prevLocPubsubPacket =
         XmppExtensionsFactory.createPubsubPreviousLocationPacket( stream
            .getDataFactory(), iq.getFrom(), newPreviousLocation );

      outQ.enque( prevLocPubsubPacket );

      logger.info( logPrefix + "Pushed current and previous location to pubsub" );

      // notify location event handlers
      locationEventHub.userPreviousLocationChanged( user, newPreviousLocation );

      // create response iq
      return XmppUtils.createCopyResult( iq );

   }


   private Packet handleSetNextPlace(InfoQuery iq, LocationUser user, Place place)
      throws SQLException
   {

      // String placeName = "";
      // if (place != null && place.getName() != null)
      // {
      // placeName = place.getName().trim();
      // }

      Location nextLocation = new Location();
      nextLocation.setPatternMatch( 100 );
      nextLocation.setMotionState( Location.MotionState.STATIONARY );
      if (place != null && place.getId() > 0)
      {
         Place p = lDB.getPlace( place.getId() );
         nextLocation.setPlace( p );

         // use provided place name to preserve additional info like time micro formats
         // etc...
         if (place.getName() != null && place.getName().length() > 0)
         {
            nextLocation.setPlaceName( place.getName() );
         }
      }
      else
      {
         // TODO: consider parsing for time micro formats (e.g. "Home @ 19:30" or
         // "Barcelona @ Tuesday")
         nextLocation.setPlace( place );
      }

      // can#t have a null here, empty string will do
      if (nextLocation.getPlaceName() == null)
      {
         nextLocation.setPlaceName( "" );
      }

      // add label to db
      lDB.setNextLocation( user.getId(), nextLocation.getPlaceName() );

      Collection<String> nextLocInfo = nextLocation.toStrings();
      for (String s : nextLocInfo)
      {
         logger.info( logPrefix + "NEXT: '" + s + "'" );
      }
      if (nextLocInfo.size() == 0)
      {
         logger.info( logPrefix + "NEXT: ''" );
      }
      logger.info( logPrefix + "NEXT: (stored to db) '" + nextLocation.getPlaceName()
                   + "'" );

      // update third party apps
      locationEventHub.userNextLocationChanged( user, nextLocation );

      // Create a location presence info query packet for the new current
      // location
      InfoQuery nextLocPubsubPacket =
         XmppExtensionsFactory.createPubsubNextLocationPacket( stream.getDataFactory(),
            iq.getFrom(), nextLocation );

      // post it
      outQ.enque( nextLocPubsubPacket );
      
      logger.info( logPrefix + "NEXT: Pushed update to pubsub" );

      // create response iq
      return XmppUtils.createCopyResult( iq );

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


   private Place parsePlace(LocationUser user, StreamElement placeElement)
      throws IllegalArgumentException
   {
      if (placeElement == null)
      {
         logger.info( logPrefix + "No place info supplied." );
         return null;
      }

      // verbose
      String elementString = placeElement.toString();
      elementString.replace( "><", ">#§$<" );
      String[] elementStrings = elementString.split( "#§$" );
      for (String s : elementStrings)
      {
         logger.info( logPrefix + "" + s.trim() );
      }
      String pid = getChildElementText( placeElement, "id" );

      Place place = new Place();
      if (pid != null)
      {
         try
         {
            if (pid.contains( "/" ))
            {
               logger.info( logPrefix + "Place id (url): " + pid );
               pid = pid.substring( pid.lastIndexOf( "/" ) + 1, pid.length() );
               logger.info( logPrefix + "Place id (int): " + pid );
            }
            place.setId( Integer.parseInt( pid ) );
         }
         catch (Exception e)
         {
            logger.info( logPrefix + "Invalid place id ignored: " + pid );
         }
      }
      String shared = getChildElementText( placeElement, "shared" );
      logger.info( logPrefix + "Shared: " + shared );
      if (shared != null)
      {
         shared = shared.toLowerCase();
         if (shared.equals( "true" ))
         {
            place.setPublic( true );
            logger.info( logPrefix + "Place object set to public" );
         }
         if (shared.equals( "1" ))
         {
            place.setPublic( true );
            logger.info( logPrefix + "Place object set to public" );
         }
      }
      place.setName( getChildElementText( placeElement, "name" ) );
      place.setDescription( getChildElementText( placeElement, "description" ) );
      place.setStreet( getChildElementText( placeElement, "street" ) );
      place.setArea( getChildElementText( placeElement, "area" ) );
      place.setCity( getChildElementText( placeElement, "city" ) );
      place.setPostalCode( getChildElementText( placeElement, "postalcode" ) );
      place.setRegion( getChildElementText( placeElement, "region" ) );
      place.setCountryCode( CountryCode.getInstance( getChildElementText( placeElement,
         "country" ) ) );

      String latString = getChildElementText( placeElement, "lat" );
      String lonString = getChildElementText( placeElement, "lon" );
      if (latString != null && lonString != null)
      {
         try
         {
            latString.replace( ",", "." );
            lonString.replace( ",", "." );
            double lat = Double.parseDouble( latString );
            double lon = Double.parseDouble( lonString );
            place.setLatitude( lat );
            place.setLongitude( lon );
            place.setAccuracy( 50.0 ); // default
         }
         catch (Exception e)
         {
            logger.error( logPrefix + "Malformated lat/lon: (" + latString + ", "
                          + lonString + "), data ignored" );
         }
      }

      place.setWikiUrl( getChildElementText( placeElement, "wikiurl" ) );
      place.setSiteUrl( getChildElementText( placeElement, "siteurl" ) );

      // if address set, but not position, try to obtain one
      if (place.getLatitude() == 0.0 && place.getLongitude() == 0.0
          && place.getStreet() != null && place.getStreet().length() > 0)
      {
         GoogleMapsWebService gs = new GoogleMapsWebService();
         try
         {
            Point pos =
               gs.getCoordinates( place.getStreet(), place.getCity(), place
                  .getPostalCode(), place.getCountryCode() );
            place.setLatitude( pos.getLatitude() );
            place.setLongitude( pos.getLongitude() );
            place.setAccuracy( 50.0 ); // default
            logger.info( "Geocoded address of " + place.getName() + ": " + pos );
         }
         catch (Exception e)
         {
            logger.error( "Failed to geocode address of " + place.getName(), e );
         }
      }

      return place;
   }
}
