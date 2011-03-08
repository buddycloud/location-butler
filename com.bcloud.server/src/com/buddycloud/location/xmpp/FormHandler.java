
package com.buddycloud.location.xmpp;

import java.util.Collection;

import org.jabberstudio.jso.Packet;

import com.buddycloud.common.xmpp.PacketFilter;
import com.buddycloud.common.xmpp.PacketHandler;

/**
 * Handler for the location management forms.
 * @deprecated Forms are being phased out in favor of specificly designed stanzas (nearby and placemanagement). This packet handler will be phased out when no old clients require it any more.
 *  
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

public class FormHandler extends PacketHandler
{

   /* (non-Javadoc)
    * @see com.buddycloud.common.xmpp.PacketHandler#getHandledPacketFilters()
    */
   @Override
   public Collection<PacketFilter> getHandledPacketFilters()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see com.buddycloud.common.xmpp.PacketHandler#handlePacket(org.jabberstudio.jso.Packet)
    */
   @Override
   protected Packet handlePacket(Packet p)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see com.buddycloud.common.xmpp.PacketHandler#handleStop()
    */
   @Override
   protected void handleStop()
   {
      // TODO Auto-generated method stub
      
   }

//   private static final int NEARBY_PLACES_MAX_DISTANCE = 500;
//
//   private static final int MAX_NEARBY_USERS = 10;
//
//   private LocationDbAccess lDB;
//   
//   private LocationEngine locationEngine;
//
//   private LocationEventHandlerRegistry locationEventHub;
//
//
//   public FormHandler()
//   {
//
//      super( );
//
//      this.lDB = new LocationDbAccess();
//      locationEngine = new LocationEngine( lDB );
//      locationEventHub = LocationEventHandlerRegistry.getInstance( );
//
//   }
//
//
//   @Override
//   public Collection<PacketFilter> getHandledPacketFilters()
//   {
//
//      ArrayList<PacketFilter> filters = new ArrayList<PacketFilter>();
//      PacketFilter setFilter = new PacketFilter();
//      setFilter.addNamespace( "commands", "http://jabber.org/protocol/commands" );
//      setFilter.setXPath( "jabber:iq[@type='set']/commands:command" );
//
//      PacketFilter getFilter = new PacketFilter();
//      getFilter.addNamespace( "commands", "http://jabber.org/protocol/commands" );
//      getFilter.setXPath( "jabber:iq[@type='get']/commands:command" );
//
//      filters.add( setFilter );
//      filters.add( getFilter );
//      return filters;
//   }
//
//
//   private Place getPlace(XDataForm locationForm) throws IllegalArgumentException
//   {
//
//      // verbose
//      logger.info( "Place:" );
//      logger.info( "   id (deprecated): " + locationForm.getFieldValue( "id" ) );
//      logger.info( "   place_id       : " + locationForm.getFieldValue( "place_id" ) );
//      logger.info( "   name           : " + locationForm.getFieldValue( "name" ) );
//      logger.info( "   label          : " + locationForm.getFieldValue( "label" ) );
//      logger.info( "   description    : " + locationForm.getFieldValue( "description" ) );
//      logger.info( "   street         : " + locationForm.getFieldValue( "street" ) );
//      logger.info( "   area           : " + locationForm.getFieldValue( "area" ) );
//      logger.info( "   city           : " + locationForm.getFieldValue( "city" ) );
//      logger.info( "   postalCode     : " + locationForm.getFieldValue( "postalcode" ) );
//      logger.info( "   district (depr): " + locationForm.getFieldValue( "district" ) );
//      logger.info( "   region         : " + locationForm.getFieldValue( "region" ) );
//      logger.info( "   country        : " + locationForm.getFieldValue( "country" ) );
//      logger.info( "   latitude       : " + locationForm.getFieldValue( "latitude" ) );
//      logger.info( "   longitude      : " + locationForm.getFieldValue( "longitude" ) );
//      logger.info( "   wikiurl        : " + locationForm.getFieldValue( "wikiurl" ) );
//      logger.info( "   siteurl        : " + locationForm.getFieldValue( "siteurl" ) );
//      logger.info( "   public         : " + locationForm.getFieldValue( "public" ) );
//
//      // backwards compatibility
//      String pid = locationForm.getFieldValue( "place_id" );
//      if (pid == null || pid.length() == 0)
//         pid = locationForm.getFieldValue( "id" );
//
//      String name = trim( locationForm.getFieldValue( "name" ) );
//      if (name == null || name.length() == 0)
//         name = trim( locationForm.getFieldValue( "label" ) );
//
//      Place place = new Place();
//      try
//      {
//         place.setId( Integer.parseInt( pid ) );
//      }
//      catch (Exception e)
//      {
//      }
//
//      place.setName( name );
//
//      if (( place.getName() == null || place.getName().length() == 0 )
//          && place.getId() < 1)
//      {
//
//         logger.error( "Name or ID not found in location form. Got:" );
//         for (Object field : locationForm.listFields())
//         {
//            logger.error( "  " + field );
//         }
//         throw new IllegalArgumentException( "Place must have a name or an ID: "
//                                             + locationForm );
//      }
//      place.setDescription( trim( locationForm.getFieldValue( "description" ) ) );
//      place.setStreet( trim( locationForm.getFieldValue( "street" ) ) );
//      place.setArea( trim( locationForm.getFieldValue( "area" ) ) );
//      place.setCity( trim( locationForm.getFieldValue( "city" ) ) );
//      place.setPostalCode( trim( locationForm.getFieldValue( "postalcode" ) ) );
//      place.setRegion( trim( locationForm.getFieldValue( "region" ) ) );
//      place.setCountryCode( CountryCode.getInstance( locationForm
//         .getFieldValue( "country" ) ) );
//
//      String cityAndPostalString = locationForm.getFieldValue( "district" );
//      if(cityAndPostalString!=null && cityAndPostalString.length()>0){
//         logger.error( "Deprecated field 'district' with value '"+cityAndPostalString+"' ignored" );
//      }
//
//      try
//      {
//         double lat = Double.parseDouble( locationForm.getFieldValue( "latitude" ) );
//         double lon = Double.parseDouble( locationForm.getFieldValue( "longitude" ) );
//         place.setLatitude( lat );
//         place.setLongitude( lon );
//         place.setAccuracy( 50.0 ); // default
//      }
//      catch (Exception e)
//      {
//         logger.error( "Failed to parse lat/lon: ("
//                       + locationForm.getFieldValue( "latitude" ) + ", "
//                       + locationForm.getFieldValue( "longitude" ) + "), data ignored" );
//      }
//
//      place.setWikiUrl( locationForm.getFieldValue( "wikiurl" ) );
//      place.setSiteUrl( locationForm.getFieldValue( "siteurl" ) );
//
//      String s = locationForm.getFieldValue( "public" );
//      place.setPublic( s != null && ( s.equals( "true" ) || s.equals( "1" ) ) );
//
//      // if address set, but not position, try to obtain one
//      if (place.getLatitude() == 0.0 && place.getLongitude() == 0.0
//          && place.getStreet() != null && place.getStreet().length() > 0)
//      {
//         GoogleLocationService gs = new GoogleLocationService();
//         try
//         {
//            Point pos =
//               gs.getCoordinates( place.getStreet(), place.getCity(), place
//                  .getPostalCode(), place.getCountryCode() );
//            place.setLatitude( pos.getLatitude() );
//            place.setLongitude( pos.getLongitude() );
//            place.setAccuracy( 50.0 ); // default
//            logger.info( "Geocoded address of " + place.getName() + ": " + pos );
//         }
//         catch (Exception e)
//         {
//            logger.error( "Failed to geocode address of " + place.getName(), e );
//         }
//      }
//
//      return place;
//   }
//
//
//   /**
//    * Trims leading and trailing white spaces of the supplied string if it is not null. If
//    * the resulting string is zero-length, null is returned
//    * 
//    * @param s
//    *           The string to trim
//    * @return trimmed string or null
//    */
//   private String trim(String s)
//   {
//      if (s == null)
//         return null;
//      s = s.trim();
//      if (s.length() == 0)
//         return null;
//      else
//         return s;
//   }
//
//
//   protected InfoQuery handleAddPlaceForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      Place place;
//
//      try
//      {
//         place = getPlace( form );
//      }
//      catch (Exception e)
//      {
//         return (InfoQuery) XmppUtils.createBadRequestError( iq, e.getMessage() );
//      }
//
//      place.setOwnerId( user.getId() );
//
//      Place existingPlace = null;
//
//      for (Place p : lDB.getPlaces( place.getName() ))
//      {
//         if (p.getOwnerId() == user.getId())
//         {
//            existingPlace = p;
//         }
//         else if (p.getStreet() != null && p.getStreet().equals( place.getStreet() ))
//         {
//            existingPlace = p;
//         }
//      }
//
//      if (existingPlace != null)
//      {
//         place.setId( existingPlace.getId() );
//         lDB.updatePlace( place, existingPlace.getRevision()+1 );
//         logger.debug( user + ": Place '" + place.getName() + "' (ID " + place.getId()
//                       + ") updated in database. (Revision "+(existingPlace.getRevision()+1) );
//      }
//
//      // if there are no places with the same name and address defined by this user, add
//      // to DB
//      else
//      {
//         int placeId = lDB.addPlace( place );
//         place.setId( placeId );
//         logger.debug( user + ": Place '" + place.getName() + "' (ID " + place.getId()
//                       + ") added to database." );
//      }
//
//      // create response iq
//      return XmppUtils.createSimpleResult( iq );
//
//   }
//
//
//   protected InfoQuery handleDeletePlaceForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      // get location id
//      String locationIdString = form.getFieldValue( "id" );
//
//      // convert it int
//      int locationId;
//      try
//      {
//         locationId = Integer.parseInt( locationIdString );
//      }
//      catch (Exception e)
//      {
//         logger.error( user + ": Malformed location ID: " + locationIdString );
//         return (InfoQuery) XmppUtils.createBadRequestError( iq,
//            "Place ID not an integer" );
//      }
//
//      // check that location exists
//      Place l = lDB.getPlace( locationId );
//      if (l == null)
//      {
//         logger.error( user + ": Place with ID " + locationId + ". " + iq.getFrom()
//                       + " not found in db." );
//         return (InfoQuery) XmppUtils
//            .createItemNotFoundError( iq, "Place does not exist" );
//      }
//
//      // check that submitter = owner
//      if (l.getOwnerId() != user.getId())
//      {
//         logger.error( user + ": Could not delete location with ID " + l.getId() + ". "
//                       + iq.getFrom() + " not owner." );
//         return (InfoQuery) XmppUtils.createNotAuthorizedError( iq, "Not location owner" );
//      }
//
//      // delete location
//      lDB.deletePlace( locationId );
//
//      // TODO push current and previous location after deleting place
//
//      // create response iq
//      return XmppUtils.createSimpleResult( iq );
//   }
//
//
//   protected InfoQuery handleEditPlaceForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      // extract location attributes from form
//      Place newPlace;
//      try
//      {
//         newPlace = getPlace( form );
//      }
//      catch (Exception e)
//      {
//         return (InfoQuery) XmppUtils.createBadRequestError( iq, e.getMessage() );
//      }
//
//      // get the existing location of this ID
//      Place oldPlace = lDB.getPlace( newPlace.getId() );
//
//      // check that it exists
//      if (oldPlace == null)
//      {
//         logger.error( user + " Place with ID " + newPlace.getId() + ". " + iq.getFrom()
//                       + " not found in db." );
//         return (InfoQuery) XmppUtils
//            .createItemNotFoundError( iq, "Place does not exist" );
//      }
//
//      // check that the submitting user can modify this location
//      if (oldPlace.getOwnerId() != user.getId())
//      {
//         logger.error( user + ": Could not edit place with ID "
//                       + newPlace.getId() + ". " + iq.getFrom() + " not owner." );
//         return (InfoQuery) XmppUtils.createNotAuthorizedError( iq, "Not owner" );
//      }
//
//      // update location in DB
//      lDB.updatePlace( newPlace, oldPlace.getRevision()+1 );
//
//      if (!oldPlace.getName().equals( newPlace.getName() ))
//      {
//         logger.info( user + ": Place " + newPlace.getId() + " renamed: "
//                      + oldPlace.getName() + " -> " + newPlace.getName() +" (Revision "+(oldPlace.getRevision()+1)+")");
//      }
//      else
//      {
//         logger.info( user + ": Place " + newPlace.getName() + "(ID " + newPlace.getId()
//                      + ") edited (Revision "+(oldPlace.getRevision()+1)+")" );
//      }
//
//      // send PEP update to all users currently at this place
//      Collection<Integer> userIds = lDB.getUsersAtPlace( newPlace.getId() );
//      for (Integer uId : userIds)
//      {
//         if (uId != null && uId > 0)
//         {
//            LocationUser u = lDB.getLocationUser( uId );
//            Location newCurrLoc = lDB.getCurrentLocation( user.getId() );
//            newCurrLoc.setPlace( newPlace );
//
//            InfoQuery locationUpdate =
//               XmppExtensionsFactory.createCurrentLocationPubsubPacket( iq
//                  .getDataFactory(), u.getJid(), newCurrLoc );
//
//            logger.info( user + ": Pushing current location PEP udate to " + u + ": "
//                         + newCurrLoc.getLabel() );
//
//            outQ.enque( locationUpdate );
//
//            // update 3rd party apps
//            locationEventHub.userPreviousLocationChanged( u, newCurrLoc );
//         }
//      }
//
//      // if this was the previous place of the user submitting the update, update his prev
//      // location PEP node
//      Location submittingUserPrevLoc =
//         lDB.getPreviousLocation( user.getId(), lDB.getCurrentLocation( user.getId() )
//            .getPlaceId() );
//      if (submittingUserPrevLoc != null
//          && submittingUserPrevLoc.getPlaceId() == newPlace.getId())
//      {
//         submittingUserPrevLoc.setPlace( newPlace );
//
//         InfoQuery locationUpdate =
//            XmppExtensionsFactory.createPreviousLocationPubsubPacket(
//               iq.getDataFactory(), user.getJid(), submittingUserPrevLoc );
//
//         logger.info( user + ": Pushing previous location PEP udate : "
//                      + submittingUserPrevLoc.getLabel() );
//
//         outQ.enque( locationUpdate );
//
//         // update 3rd party apps
//         locationEventHub.userPreviousLocationChanged( user, submittingUserPrevLoc );
//      }
//
//      // create response iq
//      return XmppUtils.createSimpleResult( iq );
//   }
//
//
//   protected InfoQuery handleGetPlaceDetailsForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      String locationIdString = form.getFieldValue( "id" );
//      if (locationIdString != null)
//      {
//         int locationId = Integer.parseInt( locationIdString );
//         logger.info( "Getting locations near location " + locationId );
//         Place l = lDB.getPlace( locationId );
//         if (l == null)
//         {
//            return (InfoQuery) XmppUtils.createBadRequestError( iq,
//               "No location with ID " + locationId + "." );
//         }
//         if (!l.isPublic() && l.getOwnerId() != user.getId())
//         {
//            return (InfoQuery) XmppUtils.createNotAuthorizedError( iq, "Place with ID "
//                                                                       + locationId
//                                                                       + " is private." );
//         }
//
//         return XmppExtensionsFactory.createPlaceDetailsStanza( iq, l );
//      }
//      else
//      {
//         return (InfoQuery) XmppUtils.createBadRequestError( iq,
//            "Place ID not an integer: " + locationIdString + "." );
//      }
//
//   }
//
//
//   protected InfoQuery handleNearbyPeopleForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      Collection<LocationUser> nearbyUsers = new ArrayList<LocationUser>();
//      Collection<String> nearbyUserLocationInfo = new ArrayList<String>();
//
//      Location l = lDB.getCurrentLocation( user.getId() );
//      if (l == null)
//      {
//         logger.error( user + ": No current location. Can't find any nearby places." );
//         return (InfoQuery) XmppUtils.createBadRequestError( iq,
//            "Current location unknown" );
//      }
//      Point p = new Point( l.getLatitude(), l.getLongitude() );
//      if (p.isDefault())
//      {
//         logger
//            .info( user
//                   + ": NEARBY: Position not known, can't find any nearby users with this method at least" );
//      }
//      else
//      {
//
//         logger.info( user + ": NEARBY: center " + p );
//         Map<Integer, String> userLocations =
//            lDB.getNNearestUsers( user.getId(), MAX_NEARBY_USERS );
//
//         logger.info( user + ": NEARBY: Result:" );
//
//         for (int userId : userLocations.keySet())
//         {
//            LocationUser u2 = lDB.getLocationUser( userId );
//            String info = userLocations.get( userId );
//            logger.info( user + ": NEARBY: user: " + u2 + ", " + info );
//            nearbyUsers.add( u2 );
//            nearbyUserLocationInfo.add( info );
//         }
//
//      }
//      // get location and create results package
//      InfoQuery response =
//         XmppExtensionsFactory.createNearbyUserListResultForm( iq, nearbyUsers,
//            nearbyUserLocationInfo );
//
//      return response;
//
//   }
//
//
//   protected InfoQuery handleNearbyPlaceForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      Collection<String> names = new ArrayList<String>();
//      Collection<String> addresses = new ArrayList<String>();
//      Collection<Integer> ids = new ArrayList<Integer>();
//      Collection<Integer> populations = new ArrayList<Integer>();
//      
//      Map<Integer, String[]> placeMap = lDB.getNearestPlaces( user.getId(), 10 );
//      for(int pid : placeMap.keySet()){
//         String name = placeMap.get( pid )[0];
//         String address = placeMap.get( pid )[1];
//         int population = lDB.getUsersAtPlace( pid ).size();
//         
//         logger.info( user+": NEARBY: Place: "+name + " (id "+pid+") "+address+", population: "+population );
//         ids.add( pid );
//         names.add( name );
//         addresses.add( address );
//         populations.add( population );
//      }
//      InfoQuery response =
//         XmppExtensionsFactory.createPlaceResultForm( iq, ids, names, addresses, populations );
//      return response;
//
//   }
//
//
//   protected InfoQuery OLD_handleNearbyPlaceForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      Collection<Place> nearbyPlaces = getNearbyPlaces( lDB, user );
//      ArrayList<Integer> populations = new ArrayList<Integer>();
//      for (Place l : nearbyPlaces)
//      {
//         if (l == null)
//         {
//            logger
//               .error( user
//                       + ": LocationUtils.getNearbyLocations() returned a null location. Make sure this does not happen again!" );
//            populations.add( 0 );
//         }
//         else
//         {
//            int lid = l.getId();
//            int p = lDB.getUsersAtPlace( lid ).size();
//            populations.add( p );
//         }
//      }
//      InfoQuery response =
//         XmppExtensionsFactory.createPlaceResultForm( iq, nearbyPlaces, populations );
//      return response;
//
//   }
//
//
//   @Override
//   protected Packet handlePacket(Packet p)
//   {
//
//      // make sure that the packet was not sent from a conference
//      if (!isUser( p.getFrom() ))
//      {
//         logger.error( "Packet received from non-user: " + p.getFrom() + ". Ignored" );
//      }
//
//      if (!( p instanceof InfoQuery ))
//      {
//         throw new IllegalArgumentException( "Unsupported package class: "
//                                             + p.getClass().getName() );
//      }
//
//      InfoQuery iq = (InfoQuery) p;
//
//      // Prepare default reply IQ
//      InfoQuery response = (InfoQuery) iq.copy();
//      response.setID( iq.getID() );
//      response.setTo( iq.getFrom() );
//      response.setFrom( iq.getTo() );
//      response.setType( InfoQuery.RESULT );
//
//      XDataForm form;
//      String formType;
//      try
//      {
//         StreamElement commandElement = iq.getFirstElement( "command" );
//         StreamElement x = commandElement.getFirstElement( "x" );
//         form = (XDataForm) x;
//         formType = form.getFieldValue( "FORM_TYPE" );
//      }
//      catch (Exception e)
//      {
//         logger.error( "Info query not a form. Ignored" );
//         response.setError( iq.getDataFactory().createPacketError( PacketError.CANCEL,
//            PacketError.BAD_REQUEST_CONDITION ) );
//         return response;
//      }
//
//      logger.info( iq.getFrom() + ": Got form. Type: " + formType );
//      try
//      {
//         // get the user id corresponding to the submitter's JID
//         // Note: will NOT create a new DB entry if submitter is federated user not already
//         // known
//         LocationUser user = lDB.getLocationUser( iq.getFrom() );
//
//         if (user == null)
//         {
//            logger.error( "Unknown user: " + iq.getFrom() + ". Form ignored" );
//            return XmppUtils.createNotAuthorizedError( iq, "Unknown JID " + iq.getFrom() );
//         }
//
//         // location edit form
//         if (formType.trim().equals( "buddycloud:location:add" ))
//         {
//            response = handleAddPlaceForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:add" ))
//         {
//            response = handleAddPlaceForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:delete" ))
//         {
//            response = handleDeletePlaceForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:delete" ))
//         {
//            response = handleDeletePlaceForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:edit" ))
//         {
//            response = handleEditPlaceForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:edit" ))
//         {
//            response = handleEditPlaceForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:get" ))
//         {
//            response = handleGetPlaceDetailsForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:get" ))
//         {
//            response = handleGetPlaceDetailsForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:search" ))
//         {
//            response = handlePlaceSearchForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:search" ))
//         {
//            response = handlePlaceSearchForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:set_current" ))
//         {
//            response = handleSetCurrentLocationForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:set_next" ))
//         {
//            response = handleSetNextLocationForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:locations_near" ))
//         {
//            response = handleNearbyPlaceForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:location:places_near" ))
//         {
//            response = handleNearbyPlaceForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:people_near" ))
//         {
//            response = handleNearbyPeopleForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:history" ))
//         {
//            response = handlePlaceHistoryForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:history" ))
//         {
//            response = handlePlaceHistoryForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:subscribe" ))
//         {
//            response = handlePlaceSubscribeForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:subscribe" ))
//         {
//            response = handlePlaceSubscribeForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:unsubscribe" ))
//         {
//            response = handlePlaceUnsubscribeForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:unsubscribe" ))
//         {
//            response = handlePlaceUnsubscribeForm( iq, user, form );
//         }
//
//         else if (formType.trim().equals( "buddycloud:location:subscriptions" ))
//         {
//            response = handlePlaceSubscriptionsForm( iq, user, form );
//         }
//         else if (formType.trim().equals( "buddycloud:place:subscriptions" ))
//         {
//            response = handlePlaceSubscriptionsForm( iq, user, form );
//         }
//
//         else
//         {
//            logger.error( "Unsupported form type: " + formType );
//            response.setError( iq.getDataFactory().createPacketError( PacketError.WAIT,
//               PacketError.UNEXPECTED_REQUEST_CONDITION ) );
//         }
//
//      }
//      catch (Exception e)
//      {
//         logger.error( "Failed to handle form " + formType, e );
//         response.setError( iq.getDataFactory().createPacketError( PacketError.WAIT,
//            PacketError.INTERNAL_SERVER_ERROR_CONDITION ) );
//      }
//
//      return response;
//   }
//
//
//   protected InfoQuery handlePlaceHistoryForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      int max = 30;
//      String maxString = form.getFieldValue( "max" );
//      if (maxString != null)
//      {
//         try
//         {
//            max = Integer.parseInt( maxString );
//         }
//         catch (Exception e)
//         {
//            logger.error( user + ": Malformated value of field \"max\": " + maxString
//                          + ". Integer expected. Using default value (" + max + ")" );
//         }
//      }
//
//      ArrayList<Place> places = lDB.getPlaceHistory( user.getId(), max );
//      ArrayList<Integer> populations = new ArrayList<Integer>();
//      for (Place p : places)
//      {
//         Collection<Integer> userIds = lDB.getUsersAtPlace( p.getId() );
//         logger.debug( "Users at " + p.getName() );
//         for (Integer uId : userIds)
//         {
//            LocationUser u = lDB.getLocationUser( uId );
//            logger.debug( "   " + u );
//         }
//         populations.add( userIds.size() );
//      }
//      InfoQuery response =
//         XmppExtensionsFactory.createPlaceResultForm( iq, places, populations );
//
//      return response;
//
//   }
//
//
//   protected InfoQuery handlePlaceSearchForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      // extract place details
//      String name = form.getFieldValue( "name" );
//      String country = form.getFieldValue( "country" );
//      String region = form.getFieldValue( "region" );
//      String city = form.getFieldValue( "city" );
//      String area = form.getFieldValue( "area" );
//      String street = form.getFieldValue( "street" );
//      String postalCode = form.getFieldValue( "postalcode" );
//      if (postalCode == null)
//      {
//         // deprecated format with capital c
//         postalCode = form.getFieldValue( "postalCode" );
//      }
//
//      // parse old school city and postal composite if not given separately
//      String district = form.getFieldValue( "district" );
//      if (( city == null || city.trim().length() == 0 || postalCode == null || postalCode
//         .trim().length() == 0 )
//          && district != null && district.trim().length() > 0)
//      {
//         city = district;
//      }
//
//      logger.debug( user + ": Place Search: given: " + name + " | " + street + " | "
//                    + area + " | " + city + " | " + region + " | " + postalCode + " | "
//                    + country );
//
//      // augment missing fields with what we may know
//      Location l = lDB.getCurrentLocation( user.getId() );
//
//      if (l != null)
//      {
//         if (area == null || area.trim().length() == 0)
//            area = l.getArea();
//
//         if (city == null || city.trim().length() == 0)
//            city = l.getCity();
//
//         if (region == null || region.trim().length() == 0)
//            region = l.getRegion();
//
//         if (l.getCountryCode() != null)
//            country = l.getCountryCode().getEnglishCountryName();
//      }
//
//      logger.debug( user + ": Place Search: augmented : " + name + " | " + street + " | "
//                    + area + " | " + city + " | " + region + " | " + postalCode + " | "
//                    + country );
//
//      String localHints = "";
//
//      if (street != null && street.trim().length() > 0)
//         localHints += street.trim() + " ";
//
//      if (area != null && area.trim().length() > 0)
//         localHints += area.trim() + " ";
//
//      if (city != null && city.trim().length() > 0)
//         localHints += city.trim() + " ";
//
//      if (postalCode != null && postalCode.trim().length() > 0)
//         localHints += postalCode.trim() + " ";
//
//      if (region != null && region.trim().length() > 0)
//         localHints += region.trim() + " ";
//
//      localHints = localHints.trim();
//
//      Collection<Place> results = new ArrayList<Place>();
//      if (localHints.length() > 0)
//      {
//
//         logger.info( user + ": Triggering google search for '" + name
//                      + "' with local hints '" + localHints + "' in '" + country + "'" );
//
//         GoogleLocationService google = new GoogleLocationService();
//         try
//         {
//
//            results = google.findPlaces( name, localHints, country );
//
//         }
//         catch (IOException e)
//         {
//            logger.error( user + ": Place search failed", e );
//         }
//
//         // insert area and region if missing
//         for (Place p : results)
//         {
//            if (( p.getArea() == null || p.getArea().trim().length() == 0 )
//                && area != null)
//            {
//               p.setArea( area );
//               logger.debug( user + ": Added area/neighbourhood '" + area
//                             + "' to result '" + p.getName() + "'" );
//            }
//
//            if (( p.getRegion() == null || p.getRegion().trim().length() == 0 )
//                && region != null)
//            {
//               p.setRegion( region );
//               logger.debug( user + ": Added region/province '" + region
//                             + "' to result '" + p.getName() + "'" );
//            }
//         }
//      }
//
//      InfoQuery response =
//         XmppExtensionsFactory.createPlaceSearchResultForm( iq, results );
//
//      return response;
//   }
//
//
//   protected InfoQuery handlePlaceSubscribeForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      int placeId = Integer.parseInt( form.getFieldValue( "id" ) );
//
//      Place p = lDB.getPlace( placeId );
//      if (p == null)
//      {
//         String msg = "Place with ID " + placeId + " does not exist."; 
//         logger.error( iq.getFrom()+": "+msg );
//         return (InfoQuery) XmppUtils.createBadRequestError( iq, msg );
//      }
//      if (!p.isPublic() && p.getOwnerId() != user.getId())
//      {
//         String msg = "Place with ID " + placeId + " is private."; 
//         logger.error( iq.getFrom()+": "+msg );
//         return (InfoQuery) XmppUtils.createNotAuthorizedError( iq, msg );
//      }
//
//      lDB.addPlaceSubscription( user.getId(), placeId );
//
//      InfoQuery response = XmppUtils.createSimpleResult( iq );
//
//      return response;
//
//   }
//
//
//   protected InfoQuery handlePlaceSubscriptionsForm(InfoQuery iq, LocationUser user,
//      XDataForm form) throws SQLException
//   {
//
//      Collection<Place> locations = lDB.getPlaceSubscriptions( user.getId() );
//      ArrayList<Integer> populations = new ArrayList<Integer>();
//      for (Place l : locations)
//      {
//         int p = lDB.getUsersAtPlace( l.getId() ).size();
//         populations.add( p );
//      }
//      InfoQuery response =
//         XmppExtensionsFactory.createPlaceResultForm( iq, locations, populations );
//
//      return response;
//
//   }
//
//
//   protected InfoQuery handlePlaceUnsubscribeForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      int locationId = Integer.parseInt( form.getFieldValue( "id" ) );
//
//      lDB.deletePlaceSubscription( user.getId(), locationId );
//
//      InfoQuery response = XmppUtils.createSimpleResult( iq );
//
//      return response;
//
//   }
//
//
//   protected InfoQuery handleSetCurrentLocationForm(InfoQuery iq, LocationUser user,
//      XDataForm form) throws SQLException
//   {
//
//      // generate current beacon pattern
//      Location currentLocation = locationEngine.getLocation( user );
//      BeaconPattern cellPattern = locationEngine.getCellPattern();
//      BeaconPattern wifiPattern = locationEngine.getWifiPattern();
//
//      Place place;
//      try
//      {
//         place = getPlace( form );
//      }
//      catch (Exception e)
//      {
//         logger.error( user + ": Failed to set current place.", e );
//         return (InfoQuery) XmppUtils.createBadRequestError( iq, e.getMessage() );
//      }
//
//      String s = form.getFieldValue( "store" );
//      boolean store = s != null && ( s.equals( "true" ) || s.equals( "1" ) );
//      logger.debug( user + ": Store=\"" + s + "\" (" + store + ")" );
//
//      // if a place id has been submitted, use this to set the current
//      // location to an existing place
//      if (place.getId() > 0)
//      {
//
//         int pid = place.getId();
//
//         // get the existing location from the database (if it exists)
//         place = lDB.getPlace( pid );
//
//         if (place == null)
//         {
//            logger.error( user + ": Place with ID " + pid + ". " + iq.getFrom()
//                          + " not found in db." );
//            return (InfoQuery) XmppUtils.createItemNotFoundError( iq,
//               "Place does not exist" );
//         }
//
//         // if location is private, check that it is owned by the submitting
//         // user
//         if (!place.isPublic() && place.getOwnerId() != user.getId())
//         {
//            logger.error( user + ": Place with ID " + place.getId() + " is private. "
//                          + iq.getFrom() + " not owner." );
//            return (InfoQuery) XmppUtils
//               .createNotAuthorizedError( iq, "Place is private" );
//         }
//
//      }
//
//      // if no location id has been submitted, define a location using the
//      // remaining submitted
//      // attributes and use this as the new current location
//      else if (store)
//      {
//         place.setOwnerId( user.getId() );
//
//         if (place.getCountryCode() == null)
//         {
//            logger.info( user + ": Adding place country code'"
//                         + currentLocation.getCountryCode()
//                         + "' from user's current location" );
//            place.setCountryCode( currentLocation.getCountryCode() );
//         }
//         if (place.getRegion() == null)
//         {
//            logger.info( user + ": Adding place region '" + currentLocation.getRegion()
//                         + "' from user's current location" );
//            place.setRegion( currentLocation.getRegion() );
//         }
//         if (place.getCity() == null)
//         {
//            logger.info( user + ": Adding place city '" + currentLocation.getCity()
//                         + "' from user's current location" );
//            place.setCity( currentLocation.getCity() );
//         }
//
//         if (place.getLatitude() == 0 && place.getLongitude() == 0
//             && !currentLocation.getPoint().isDefault()
//             && currentLocation.getAccuracy() < 2000)
//         {
//            logger.info( user + ": Adding place coordinates ("
//                         + currentLocation.getLatitude() + ", "
//                         + currentLocation.getLongitude() + " @ "
//                         + currentLocation.getAccuracy() + " m)"
//                         + " from user's current location" );
//            place.setLatitude( currentLocation.getLatitude() );
//            place.setLongitude( currentLocation.getLongitude() );
//            place.setAccuracy( currentLocation.getAccuracy() );
//         }
//
//         // check for existing places with same name
//         Collection<Place> existingPlaces = lDB.getPlaces( place.getName() );
//         Place existingPlace = null;
//
//         for (Place p : existingPlaces)
//         {
//
//            if (p.getOwnerId() == place.getOwnerId())
//            {
//               existingPlace = p;
//               logger.info( user + ": Place " + place.getName() + " with id " + p.getId()
//                            + ": same owner, merging" );
//            }
//            else if (p.isPublic())
//            {
//               if (p.getStreet() != null && p.getStreet().length() > 0
//                   && p.getStreet().equals( place.getStreet() ))
//               {
//                  existingPlace = p;
//                  logger.info( user + ": Place " + place.getName() + " with id "
//                               + p.getId() + ": same address (" + p.getStreet()
//                               + "), merging" );
//               }
//            }
//
//         }
//
//         if (existingPlace == null)
//         {
//            logger.debug( user + ": No name conflict with existing places found." );
//            int placeId = lDB.addPlace( place );
//            place.setId( placeId );
//            logger.debug( user + ": Place '" + place.getName() + "' (ID " + place.getId()
//                          + ") added to database." );
//         }
//         else
//         {
//            place.setId( existingPlace.getId() );
//            lDB.updatePlace( place, existingPlace.getRevision()+1 );
//            logger.debug( user + ": Place '" + place.getName() + "' (ID " + place.getId()
//                          + ") updated in database. (Revision "+(existingPlace.getRevision()+1)+")" );
//         }
//
//         // add user as subscriber
//         lDB.addPlaceSubscription( user.getId(), place.getId() );
//         logger.debug( user + ": Added location subscription to location "
//                       + place.getName() );
//
//      }
//
//      // get the currently set location
//      Location overridden = lDB.getCurrentLocation( user.getId() );
//
//      Location overriding = new Location();
//      overriding.setPlace( place );
//      overriding.setMotionState( Location.MotionState.STATIONARY );
//      overriding.setAccuracy( Location.Layer.PLACE.getDefaultError() );
//      overriding.setPatternMatch( 100 );
//
//      if (overridden != null)
//      {
//         logger.debug( user + ": Overriding current place " + overridden.getLabel()
//                       + " (ID " + overridden.getPlaceId() + " ) to '"
//                       + overriding.getLabel() + "' (ID " + overriding.getPlaceId()
//                       + ")." );
//      }
//      else
//      {
//         logger
//            .debug( user
//                    + ": Current location is null... no patterns need to be overridden" );
//      }
//
//      // if we have confidence in the pattern, assign it to the location
//      if (store && place.getId() > 0)
//      {
//         int overridingPatternId = 0;
//         if (cellPattern != null && cellPattern.getElements().size() > 0)
//         {
//            overridingPatternId = lDB.addBeaconPattern( cellPattern, place.getId() );
//            logger.info( user + ": Current cell pattern assigned to location "
//                         + place.getId() );
//         }
//         if (wifiPattern != null && wifiPattern.getElements().size() > 0)
//         {
//            overridingPatternId = lDB.addBeaconPattern( wifiPattern, place.getId() );
//            logger.info( user + ": Current wifi assigned to location " + place.getId() );
//         }
//         overriding.setPatternId( overridingPatternId );
//
//         // if user was marked (falsely) at some place, override this entry in
//         // history
//         if (overridden != null && overridden.getPlaceId() > 0
//             && overridden.isStationary() && !overridden.isPlaceNearby())
//         {
//
//            lDB.updateLastLocationHistoryEntry( user.getId(), overriding );
//
//            // add override entry (manually set place will be given
//            // selection bonus when matching patterns and frequently overridden patterns
//            // will be deleted)
//            lDB.addManualPlaceOverride( user.getId(), overridden.getPatternId(),
//               overriding.getPatternId() );
//         }
//
//         // otherwise, add to database
//         else
//         {
//            lDB.addLocationHistory( user.getId(), overriding );
//
//            // add override entry even if no pattern has been overridden, this will give
//            // the manually set place an advantage during place selection and thus reduce
//            // toggling when patterns are not very good
//            lDB.addManualPlaceOverride( user.getId(), -1, overriding.getPatternId() );
//         }
//
//         // add to current location
//         lDB.setCurrentLocation( user.getId(), overriding );
//
//         // notify 3rd party apps
//         locationEventHub.userDefinedNewPlace( user, overriding );
//         locationEventHub.userEnteredPlace( user, overriding );
//
//         // if the old pattern has caused trouble before, delete it
//         if (overridden != null
//             && lDB.getManuallyOverriddenCount( overridden.getPatternId() ) > 1)
//         {
//            lDB.deleteBeaconPattern( overridden.getPatternId() );
//            logger.info( user + ": Deleting pattern with ID " + overridden.getPatternId()
//                         + " that has repeatedly resulted in a false fix" );
//         }
//
//      }
//      else if (store)
//      {
//         logger.error( user + ": Place was not assigned an ID, something went wrong :(" );
//         InfoQuery response = (InfoQuery) iq.copy();
//         response.setID( iq.getID() );
//         response.setTo( iq.getFrom() );
//         response.setFrom( iq.getTo() );
//         response.setType( InfoQuery.ERROR );
//         response.setError( iq.getDataFactory().createPacketError( PacketError.CANCEL,
//            PacketError.INTERNAL_SERVER_ERROR_CONDITION ) );
//         return response;
//      }
//
//      // Create a location presence info query packet for the new current
//      // location
//      InfoQuery currLocPacket =
//         XmppExtensionsFactory.createCurrentLocationPubsubPacket(
//            stream.getDataFactory(), iq.getFrom(), overriding );
//
//      // post it
//      outQ.enque( currLocPacket );
//
//      // Notify third party apps
//      locationEventHub.userCurrentLocationChanged( user, overriding );
//
//      Location newPreviousLocation =
//         lDB.getPreviousLocation( user.getId(), overriding.getPlaceId() );
//
//      // Create a location presence info query packet for the new current
//      // location
//      InfoQuery prevLocPacket =
//         XmppExtensionsFactory.createPreviousLocationPubsubPacket( stream
//            .getDataFactory(), iq.getFrom(), newPreviousLocation );
//
//      outQ.enque( prevLocPacket );
//
//      // Notify third party apps
//      locationEventHub.userPreviousLocationChanged( user, newPreviousLocation );
//
//      // create response iq
//      return XmppExtensionsFactory.createSetCurrentLocationResult( iq, place.getId() );
//
//   }
//
//
//   protected InfoQuery handleSetNextLocationForm(InfoQuery iq, LocationUser user, XDataForm form)
//      throws SQLException
//   {
//
//      String label = null;
//      int placeId = -1;
//      try
//      {
//         label = form.getFieldValue( "label" );
//      }
//      catch (Exception e)
//      {
//      }
//
//      try
//      {
//         placeId = Integer.parseInt( form.getFieldValue( "place_id" ) );
//      }
//      catch (Exception e)
//      {
//      }
//
//      if (placeId <= 0 && label == null)
//      {
//         logger.error( user + ": NEXT: No label or place id in set next location form" );
//         return (InfoQuery) XmppUtils.createBadRequestError( iq,
//            "One of the fields 'place_id' or 'label' is required" );
//      }
//
//      Location nextLocation = new Location();
//      nextLocation.setPatternMatch( 100 );
//      nextLocation.setMotionState( Location.MotionState.STATIONARY );
//      if (placeId > 0)
//      {
//         Place p = lDB.getPlace( placeId );
//         nextLocation.setPlace( p );
//
//         // use label as place name to preserve additional info like time micro formats
//         // etc...
//         if (label != null && label.length() > 0)
//         {
//            nextLocation.setPlaceName( label );
//         }
//         if (label == null || label.length() == 0)
//         {
//            label = p.getName();
//         }
//      }
//      else if (label != null)
//      {
//         // TODO: consider parsing for time micro formats (e.g. "Home @ 19:30" or
//         // "Barcelona @ Tuesday")
//         nextLocation.setPlaceName( label );
//      }
//
//      // add label to db
//      lDB.setNextLocation( user.getId(), label );
//
//      Collection<String> nextLocInfo = nextLocation.toStrings();
//      for (String s : nextLocInfo)
//      {
//         logger.info( user + ": NEXT: '" + s + "'" );
//      }
//      if (nextLocInfo.size() == 0)
//      {
//         logger.info( user + ": NEXT: ''" );
//      }
//
//      // update third party apps
//      locationEventHub.userNextLocationChanged( user, nextLocation );
//
//      // Create a location presence info query packet for the new current
//      // location
//      InfoQuery nextLocPacket =
//         XmppExtensionsFactory.createNextLocationPubsubPacket( stream.getDataFactory(),
//            iq.getFrom(), nextLocation );
//
//      // post it
//      outQ.enque( nextLocPacket );
//
//      // create response iq
//      return XmppUtils.createSimpleResult( iq );
//
//   }
//
//
//   /**
//    * Returns a list of locations sharing at least one beacon with the supplied location
//    * 
//    * @param lDB
//    *           The database interface to use
//    * @param l
//    *           The central location
//    * @return Any nearby locations found
//    * @throws SQLException
//    */
//   private Collection<Place> getNearbyPlaces(LocationDbAccess lDB, LocationUser user)
//      throws SQLException
//   {
//
//      Collection<Place> nearishPlaces = new ArrayList<Place>();
//      Collection<Place> nearbyPlaces = new ArrayList<Place>();
//
//      Location l = lDB.getCurrentLocation( user.getId() );
//      if (l == null)
//      {
//         logger.error( user + ": No current location. Can't find any nearby places." );
//         return new ArrayList<Place>();
//      }
//      Place place = null;
//      if (l.getPlaceId() > 0)
//      {
//         place = lDB.getPlace( l.getPlaceId() );
//      }
//
//      Point userPos = new Point( l.getLatitude(), l.getLongitude() );
//      if (userPos.isDefault() && place != null)
//      {
//         userPos.setLatitude( place.getLatitude() );
//         userPos.setLongitude( place.getLongitude() );
//      }
//
//      // method 1: geographic distance
//      if (!userPos.isDefault())
//      {
//         // How far away can a nearby place be
//         double deltaKm = 0.5;
//
//         // define rectangle that is big enough for all geographically nearby places to
//         // fit inside.
//         double deltaLonAtEqator = 360 * deltaKm / 40000;
//         double deltaLat = deltaLonAtEqator;
//         // delta lon corresponding to 0.5km at user latitude
//         double deltaLon =
//            deltaLonAtEqator / Math.cos( userPos.getLatitude() * Math.PI / 180 );
//         double latmin = userPos.getLatitude() - deltaLat;
//         double latmax = userPos.getLatitude() + deltaLat;
//         double lonmin = userPos.getLongitude() - deltaLon;
//         double lonmax = userPos.getLongitude() + deltaLon;
//         logger.debug( user + ": Nearby Places: Looking at " + userPos
//                       + " with delta lat/lon (" + deltaLat + ", " + deltaLon + ")" );
//         nearishPlaces.addAll( lDB.getPlaces( latmin, lonmin, latmax, lonmax ) );
//         logger.debug( user + ": Nearby Places: First cut: " + nearishPlaces.size()
//                       + " places found. Checking actual distances..." );
//      }
//
//      // method 2: places with patterns referencing most recently seen beacons
//      LocationQuery q = lDB.getMostRecentLocationQuery( user.getId() );
//      if (q != null)
//      {
//         for (Beacon b : q.getBeacons())
//         {
//            if (b != null && b.isFixed())
//            {
//               for (BeaconPattern pattern : lDB.getBeaconPatternsForBeacon( b.getId() ))
//               {
//                  int pid = lDB.getPlaceId( pattern.getId() );
//                  Place p = lDB.getPlace( pid );
//                  if (!nearishPlaces.contains( p ))
//                  {
//                     nearishPlaces.add( p );
//                  }
//               }
//            }
//         }
//      }
//
//      // method 3: places with patterns sharing beacons with user's current place
//      if (place != null)
//      {
//         for (BeaconPattern pattern : lDB.getBeaconPatternsForPlace( place.getId() ))
//         {
//            for (BeaconPattern.BeaconPatternElement e : pattern.getElements())
//            {
//               for (BeaconPattern nearbyPattern : lDB
//                  .getBeaconPatternsForBeacon( e.beacon.getId() ))
//               {
//                  int pid = lDB.getPlaceId( nearbyPattern.getId() );
//                  Place p = lDB.getPlace( pid );
//                  if (!nearishPlaces.contains( p ))
//                  {
//                     nearishPlaces.add( p );
//                  }
//               }
//            }
//         }
//      }
//
//      // weed out places that are too far away or not visible to user
//      for (Place p : nearishPlaces)
//      {
//         if (p != null)
//         {
//            Point placePos = new Point( p.getLatitude(), p.getLongitude() );
//            int dist = 500;
//            if (!userPos.isDefault() && !placePos.isDefault())
//            {
//               dist = (int) userPos.getDistanceTo( placePos );
//            }
//
//            if (dist <= NEARBY_PLACES_MAX_DISTANCE)
//            {
//               if (p.isPublic() || p.getOwnerId() == user.getId())
//               {
//                  if (!nearbyPlaces.contains( p ) && dist <= NEARBY_PLACES_MAX_DISTANCE)
//                  {
//                     nearbyPlaces.add( p );
//                     logger.debug( user + ": Nearby Places: " + p.getName() + " (ID "
//                                   + p.getId() + ") " + " dist " + dist + "m: OK" );
//                  }
//               }
//               else
//               {
//                  logger.debug( user + ": Nearby Places: " + p.getName() + " (ID "
//                                + p.getId() + ") " + " dist " + dist
//                                + "m: Discarded, not visible to user" );
//               }
//            }
//            else
//            {
//               logger.debug( user + ": Nearby Places: " + p.getName() + " (ID "
//                             + p.getId() + ") " + " dist " + dist
//                             + "m: Discarded, too far away" );
//            }
//         }
//      }
//
//      return nearbyPlaces;
//
//   }
//
//
//   @Override
//   protected void handleStop()
//   {
//
//      // gracefully shutdown DB connection
//      try
//      {
//         logger.debug( "Closing DB conncetion..." );
//         lDB.disconnect();
//         logger.debug( "DB conncetion closed." );
//      }
//      catch (SQLException sqle)
//      {
//         logger.error( "Failed to close DB conncetion.", sqle );
//      }
//   }
//
}
