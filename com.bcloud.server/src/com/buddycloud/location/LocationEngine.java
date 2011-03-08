/**
 * 
 */

package com.buddycloud.location;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.buddycloud.common.GeneralLocation;
import com.buddycloud.common.Location;
import com.buddycloud.common.LocationConstants;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.Place;
import com.buddycloud.common.Location.MotionState;
import com.buddycloud.geoid.Point;
import com.buddycloud.geoid.Position;
import com.buddycloud.geoid.PositionVoterMonitor;
import com.buddycloud.location.BeaconPattern.BeaconPatternElement;
import com.buddycloud.location.sql.LocationDbAccess; // import

/**
 * The location engine resolves the location of a specified user, based on LocationQueries
 * stored in the location database
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
public class LocationEngine
{

   private enum PlaceSource {
      BT, CELL, GPS, WIFI
   }

   private enum PositionSource {
      PLACE, CELL, GPS, WIFI, GENLOC
   }

   private static final long BLUETOOTH_BEACON_MAX_AGE = 5 * 60 * 1000L;

   /**
    * How big a range a beacon may have and still be included in a pattern. As wifi beacon
    * ranges are derived from cell data, this is same for both types.
    */
   private static final int BEACON_RANGE_LIMIT = 10000;

   /**
    * How far back to search for patterns.
    */
   private static final long CELL_BEACON_MAX_AGE = 21 * 60 * 1000L;

   /**
    * How far back in time a beacon can be last seen and still counted as stable.
    */
   private static final long CELL_MAX_STABLE_AGE = 5 * 60 * 1000L;

   /**
    * Minimal observation time required before a beacon is counted
    */
   private static final long CELL_MIN_OBSERVATION_TIME = 60 * 1000L;

   /**
    * The minimum length of a beacon pattern that can claim 100% confidence
    */
   private static final long CELL_STABLE_PATTERN_LENGTH = 15 * 60 * 1000L;

   private static final long GPS_POSITION_MAX_AGE = 15 * 60 * 1000L;

   /**
    * How long to give manually set places an advantage when selecting current place from
    * a list of candidates
    */
   private static final int MANUAL_OVERRIDE_EXPIRATION_HRS = 12;

   /**
    * The maximum distance from user, to a place found by wifi mac. Used to filter out
    * errouneous places from access points with non-unique macs
    */
   private static final double MAX_WIFI_PLACE_TOUSER_POS_DISTANCE = 50000;

   /**
    * The minimum signal strength a cell needs to be considered in a pattern.
    */
   private static final long MIN_SIGNAL_STRENGTH = -90;

   private static final long WIFI_BEACON_MAX_AGE = 11 * 60 * 1000L;

   private static final double CELL_POSITION_VOTING_GROUP_RADIUS = 25000;

   private static final double WIFI_POSITION_VOTING_GROUP_RADIUS = 25000;

   private static final double MAX_REGION_POSITION_ERROR = 100000;

   private static final double MAX_CITY_POSITION_ERROR = 20000;

   private static final double MAX_AREA_POSITION_ERROR = 2000;

   private BeaconPatternMatch bestPlaceMatch;

   private CountryCode cellCountryCode;

   private BeaconPattern cellPattern;

   private Collection<BeaconPatternMatch> cellPatternMatches;

   private Collection<BeaconPatternMatch> gpsPatternMatches;

   private Position gpsPosition;

   private ArrayList<Position> gpsTrack;

   private LocationDbAccess lDB;

   private CellTowerPositionService cellPositionService;

   private GeocodingService geoService;

   private ReverseGeocodingService revGeoService;

   private Location location;

   private Logger logger;

   private String logPrefix;

   private Location oldLocation;

   private PlaceSource placeSource;

   private PositionSource positionSource;

   private List<LocationQuery> queries;

   private BeaconPatternMatch secondBestPlaceMatch;

   private LocationUser user;

   private BeaconPattern wifiPattern;

   private Collection<BeaconPatternMatch> wifiPatternMatches;

   private boolean locationChangedSinceLastQuery;

   private MotionState wifiMotionState;


   // private static final long GENERAL_LOCATION_CACHE_EXP_TIME = 3600 * 1000 * 24 * 365 *
   // 5;
   //
   // private static final double GENERAL_LOCATION_CACHE_RESOLUTION_DEG = 0.05;
   //
   // private static final int GENERAL_LOCATION_SOURCE_GEONAMES = 0x00;
   //
   // private static final int GENERAL_LOCATION_SOURCE_GOOGLE = 0x11;
   //
   // private static final int GENERAL_LOCATION_SOURCE = GENERAL_LOCATION_SOURCE_GEONAMES;

   /**
    * @param lDB
    *           Location Database object
    * @param revGeoService
    *           The reverse geocoding service to use
    */
   public LocationEngine(LocationDbAccess lDB)
   {
      this.lDB = lDB;
      logger = Logger.getLogger( this.getClass() );

      // instantiate configured geocoding service and reverse geocoding service
      Preferences prefs = Preferences.userNodeForPackage( getClass() );

      String cellServiceImplName = prefs.get( "cell_tower_position_service", "" );
      String geoServiceImplName = prefs.get( "geocoding_service", "" );
      String revGeoServiceImplName = prefs.get( "reverse_geocoding_service", "" );
      try
      {
         cellPositionService =
            (CellTowerPositionService) Class.forName( cellServiceImplName ).newInstance();
         logger.debug( "Cell tower position service in use: "
                       + cellPositionService.getName() );
      }
      catch (Exception e)
      {
         String msg =
            "Failed to instantiate cell tower position service class '"
               + cellServiceImplName
               + "'. Make sure the value of 'cell_tower_position_service' is set in the preferences and that the corresponding class implements the interface com.buddycloud.location.CellTowerPositionService";
         logger.error( msg, e );
         throw new RuntimeException( msg );
      }

      try
      {
         geoService =
            (GeocodingService) Class.forName( geoServiceImplName ).newInstance();
         logger.debug( "Geocoding service in use: " + geoService.getName() );
      }
      catch (Exception e)
      {
         String msg =
            "Failed to instantiate geocoding service class '"
               + geoServiceImplName
               + "'. Make sure the value of 'geocoding_service' is set in the preferences and that the corresponding class implements the interface com.buddycloud.location.GeocodingService";
         logger.error( msg, e );
         throw new RuntimeException( msg );
      }

      try
      {
         revGeoService =
            (ReverseGeocodingService) Class.forName( revGeoServiceImplName )
               .newInstance();
         logger.debug( "Reverse geocoding service in use: " + revGeoService.getName() );
      }
      catch (Exception e)
      {
         String msg =
            "Failed to instantiate reverse geocoding service class '"
               + revGeoServiceImplName
               + "'. Make sure the value of 'reverse_geocoding_service' is set in the preferences and that the corresponding class implements the interface com.buddycloud.location.ReverseGeocodingService";
         logger.error( msg, e );
         throw new RuntimeException( msg );
      }
   }


   /**
    * Sets location place name, place id, country, city, area, street, and postal code to
    * that specified by selected place if these are not null.
    */
   private void applyPlaceInfo()
   {
      location.setPatternId( bestPlaceMatch.getPatternId() );
      location.setPatternMatch( bestPlaceMatch.getPatternMatch() );
      // location.setCellPatternQuality( cellPatternQuality );
      Place place = bestPlaceMatch.getPlace();
      location.setPlaceName( place.getName() );
      location.setPlaceId( place.getId() );
      // if (place.getCountryCode() != null)
      // location.setCountryCode( place.getCountryCode() );
      // if (place.getRegion() != null)
      // location.setRegion( place.getRegion() );
      // if (place.getCity() != null)
      // location.setCity( place.getCity() );
      // if (place.getArea() != null)
      // location.setArea( place.getArea() );
      // if (place.getStreet() != null && place.isPublic())
      // location.setStreet( place.getStreet() );
      // if (place.getPostalCode() != null && place.isPublic())
      // location.setPostalCode( place.getPostalCode() );

   }


   /**
    * 
    */
   private void averageAndOverwriteCurrentPositionFromRecentGpsQueries()
   {
      gpsPosition = null;
      if (gpsTrack.size() > 0)
      {
         if (location.getMotionState() == Location.MotionState.MOVING)
         {
            logger.debug( logPrefix + "GPS: Motion state is " + location.getMotionState()
                          + ": Using latest values" );
            gpsPosition = gpsTrack.get( 0 );
         }
         else
         {
            logger.debug( logPrefix + "GPS: Motion state is " + location.getMotionState()
                          + ": Averaging the " + gpsTrack.size() + " latest values" );
            double avgLat = 0;
            double avgLon = 0;
            double minAcc = Double.MAX_VALUE;
            for (Position p : gpsTrack)
            {
               avgLat += p.getLatitude();
               avgLon += p.getLongitude();
               if (p.getAccuracy() < minAcc)
               {
                  minAcc = p.getAccuracy();
               }
            }
            avgLat /= gpsTrack.size();
            avgLon /= gpsTrack.size();

            gpsPosition = new Position( avgLat, avgLon, minAcc );
         }

      }

      if (gpsPosition != null && !gpsPosition.isDefault())
      {
         location.setLatitude( gpsPosition.getLatitude() );
         location.setLongitude( gpsPosition.getLongitude() );
         location.setAccuracy( gpsPosition.getAccuracy() );
         positionSource = PositionSource.GPS;
         logger.debug( logPrefix + "GPS: Position: " + gpsPosition );
      }
   }


   /**
    * Checks the country code of the current location against that derived from cell MCC.
    * If they do not match, the location is reset and its country code set to that derived
    * from cell MCC
    */
   private void checkGeneralLocationAgainstMccCountryCode()
   {
      if (location.getCountryCode() != null && cellCountryCode != null
          && !( location.getCountryCode() == cellCountryCode ))
      {
         // special case
         if (cellCountryCode == CountryCode.HK
             && location.getCountryCode() == CountryCode.CN)
         {
            logger.warn( logPrefix + "GEO WARNING: location country code '"
                         + location.getCountryCode() + " does not match cell MCC '"
                         + cellCountryCode + "'. This is probably OK" );
         }
         else
         {
            logger.error( logPrefix + "GEO ERROR: location country code '"
                          + location.getCountryCode() + " does not match cell MCC '"
                          + cellCountryCode + "'. Position source was " + positionSource
                          + ". Reverting to MCC" );
            if (positionSource == PositionSource.WIFI)
            {
               logger.error( logPrefix + "GEO ERROR: WIFI pattern:" );
               if (wifiPattern == null || wifiPattern.getElements() == null
                   || wifiPattern.getElements().size() == 0)
               {
                  logger.error( logPrefix + "GEO ERROR: WIFI none" );
               }
               else
               {
                  for (BeaconPatternElement w : wifiPattern.getElements())
                  {
                     logger.error( logPrefix + "GEO ERROR: " + w.beacon );
                  }
               }
            }
            else if (positionSource == PositionSource.CELL)
            {
               if (cellPattern == null || cellPattern.getElements() == null
                   || cellPattern.getElements().size() == 0)
               {
                  logger.error( logPrefix + "GEO ERROR: CELL none" );
               }
               else
               {
                  for (BeaconPatternElement c : cellPattern.getElements())
                  {
                     logger.error( logPrefix + "GEO ERROR: " + c.beacon );
                  }
               }
            }
            else if (positionSource == PositionSource.GPS)
            {
               logger.error( logPrefix + "GEO ERROR: Gps pos " + location.getPosition() );
            }
            else if (positionSource == PositionSource.PLACE)
            {
               logger.error( logPrefix + "GEO ERROR: Place " + location.getPlaceName()
                             + " (ID=" + location.getPlaceId() + ")" );
            }
            location.setArea( null );
            location.setCity( null );
            location.setRegion( null );
//            location.setLatitude( 0 );
//            location.setLongitude( 0 );
//            location.setAccuracy( Location.Layer.COUNTRY.getDefaultError() );
            location.setCountryCode( cellCountryCode );
         }
      }

   }


   /**
    * Collects beacon pattern matches (place, match quality, pattern id, beacon type) from
    * the provided pattern
    */
   private Collection<BeaconPatternMatch> collectPlaceCandidatesFromBeaconPattern(
      BeaconPattern targetPattern)
   {
      ArrayList<BeaconPatternMatch> patternMatches = new ArrayList<BeaconPatternMatch>();
      ArrayList<Integer> placeIds = new ArrayList<Integer>();

      Beacon.Type type = targetPattern.getBeaconType();
      String prefix = logPrefix + type + " ";
      if (targetPattern == null)
      {
         logger.debug( prefix + ": Pattern is null, no places found" );
         return patternMatches;
      }
      if (targetPattern.getElements().size() == 0)
      {
         logger.debug( prefix + ": Pattern is empty, no places found" );
         return patternMatches;
      }

      // get all patterns that references one or more of the observed beacons
      ArrayList<BeaconPattern> storedPatterns = new ArrayList<BeaconPattern>();

      // collect stored patterns that contain one or more of the beacons in the observed
      // pattern
      for (BeaconPatternElement e : targetPattern.getElements())
      {
         try
         {
            Collection<BeaconPattern> patterns =
               lDB.getBeaconPatternsForBeacon( e.beacon.getId() );
            if (patterns != null)
            {
               storedPatterns.addAll( patterns );
            }
         }
         catch (Exception ex)
         {
            logger.error( prefix + "Failed to get patterns for beacon pattern element "
                          + e.beacon, ex );
         }
      }

      // iterate through all stored patterns
      for (BeaconPattern storedPattern : storedPatterns)
      {
         double match = 0.0;
         double storedParallelity = 0.0;
         double observedParallelity = 0.0;

         // iterate through all elements in stored patterns
         for (BeaconPattern.BeaconPatternElement storedPatternElement : storedPattern
            .getElements())
         {

            // iterate through all elements in target pattern
            for (BeaconPattern.BeaconPatternElement targetPatternElement : targetPattern
               .getElements())
            {

               int observedBeaconId = targetPatternElement.beacon.getId();
               int storedBeaconId = storedPatternElement.beacon.getId();
               if (observedBeaconId == storedBeaconId)
               {
                  // only consider fixed beacons for now...
                  // if (storedPatternElement.beacon.isFixed())
                  // {

                  double storedTimeFraction = storedPatternElement.timeFraction;
                  double storedSignalStrength = storedPatternElement.avgSignalStrength;
                  double observedSignalStrength = targetPatternElement.avgSignalStrength;
                  double observedTimeFraction = targetPatternElement.timeFraction;

                  // Catch historic errors (we thought sig strength would be positive
                  // at one point, silly us)
                  if (storedSignalStrength > 0)
                     storedSignalStrength *= -1;
                  if (observedSignalStrength > 0)
                     observedSignalStrength *= -1;

                  // if observed time is zero, (such as in a single log slice) treat
                  // all beacons equally
                  if (storedTimeFraction == 0 || observedTimeFraction == 0)
                  {
                     storedTimeFraction = 1.0 / storedPattern.getElements().size();
                     observedTimeFraction = 1.0 / targetPattern.getElements().size();
                  }

                  storedParallelity += storedTimeFraction;
                  observedParallelity += observedTimeFraction;

                  // create a factor for the signal strength match
                  double signalStrengthFactor =
                     1.0 - Math.abs( storedSignalStrength - observedSignalStrength ) / 100.0;

                  // limit this factor to 0-1 (sig strength can drop below -100dBM
                  if (signalStrengthFactor < 0)
                     signalStrengthFactor = 0;

                  // the "weight" of the beacon is the minimum of the observed and
                  // stored time fractions
                  double timeFactor = Math.min( storedTimeFraction, observedTimeFraction );

                  // add to match
                  match += signalStrengthFactor * timeFactor;
                  // }
                  // else
                  // {
                  // logger.debug( "Ignored movable beacon "
                  // + storedPatternElement.beacon );
                  // }
               }
            }
         }

         // correct for multiple beacons observed in parallel
         double parallelity = Math.max( storedParallelity, observedParallelity );
         if (storedParallelity > 1)
            match /= parallelity;

         Place place;
         try
         {
            int placeId = lDB.getPlaceId( storedPattern.getId() );
            place = lDB.getPlace( placeId );
         }
         catch (SQLException e)
         {
            logger.error( prefix + "Failed to get place id for pattern with id "
                          + storedPattern.getId(), e );
            place = null;
         }

         // sanity check
         if (place != null && cellCountryCode != null && place.getCountryCode() != null
             && !( place.getCountryCode() == cellCountryCode ))
         {
            logger.warn( prefix + "Place country code does not match mcc, ignored: "
                         + place + ", " + place.getCountryCode() + " != "
                         + cellCountryCode );
            place = null;
         }

         if (place != null)
         {
            BeaconPatternMatch placeMatch = new BeaconPatternMatch();
            placeMatch.setPlace( place );
            placeMatch.setPatternId( storedPattern.getId() );
            placeMatch.setBeaconType( type );

            // places matched by wifi and 5 min stable patterns are always fixes, due to
            // its
            // short range
            if (type == Beacon.Type.WIFI
                && targetPattern.getStableTime() > 5 * 60 * 1000L)
            {
               int min = LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH;
               placeMatch.setPatternMatch( (int) ( min + match * ( 100 - min ) ) );
            }
            // cell pattern matches use entire scale
            else
            {
               placeMatch.setPatternMatch( (int) ( 100 * match ) );
            }

            // check if we already have a match for this location
            if (placeIds.contains( placeMatch.getPlace().getId() ))
            {
               int index = placeIds.indexOf( placeMatch.getPlace().getId() );
               BeaconPatternMatch old = patternMatches.get( index );

               // if the new match is better, replace the old one
               if (placeMatch.getPatternMatch() > old.getPatternMatch())
               {
                  patternMatches.set( index, placeMatch );
               }
            }
            else
            {
               patternMatches.add( placeMatch );
               placeIds.add( placeMatch.getPlace().getId() );
            }
         }
      }

      // debug
      logger.debug( prefix + ": Places found:" );
      for (BeaconPatternMatch m : patternMatches)
      {
         logger.debug( prefix + "   Place '" + m.getPlace().getName() + "' (ID "
                       + m.getPlace().getId() + ") pattern ID " + m.getPatternId() + ": "
                       + m.getPatternMatch() + "%" );
      }
      if (patternMatches.size() == 0)
      {
         logger.debug( prefix + "   none" );
      }

      // only consider places visible to user
      ArrayList<BeaconPatternMatch> visibleMatches = new ArrayList<BeaconPatternMatch>();
      for (BeaconPatternMatch patternMatch : patternMatches)
      {
         // don't consider other people's private places
         if (patternMatch.getPlace().getOwnerId() == user.getId()
             || patternMatch.getPlace().isPublic())
         {
            visibleMatches.add( patternMatch );
         }

      }

      return visibleMatches;

   }


   /**
    * 
    */
   private void collectPlaceCandidatesFromCellPattern()
   {
      cellPatternMatches = collectPlaceCandidatesFromBeaconPattern( cellPattern );

   }


   /**
    * 
    */
   private void collectPlaceCandidatesFromGpsPosition()
   {
      gpsPatternMatches = new ArrayList<BeaconPatternMatch>();
      try
      {
         if (gpsPosition != null && !gpsPosition.isDefault())
         {
            double latmin = gpsPosition.getLatitude() - 0.01;
            double latmax = gpsPosition.getLatitude() + 0.01;
            double lonmin = gpsPosition.getLongitude() - 0.01;
            double lonmax = gpsPosition.getLongitude() + 0.01;
            Collection<Place> nearishPlaces =
               lDB.getPlaces( latmin, lonmin, latmax, lonmax );
            for (Place place : nearishPlaces)
            {
               Point p = new Point( place.getLatitude(), place.getLongitude() );
               double d = gpsPosition.getDistanceTo( p );
               if (d < 100)
               {
                  logger.debug( logPrefix + "GPS: Added place '" + place.getName() + "' "
                                + d + "m away" );
                  BeaconPatternMatch m = new BeaconPatternMatch();
                  m.setPatternMatch( (int) ( 100 - d ) );
                  m.setPlace( place );
                  gpsPatternMatches.add( m );
               }
               else
               {
                  logger.debug( logPrefix + "GPS: Ignored place '" + place.getName()
                                + "' " + d + "m away (100m limit)" );
               }
            }
         }
      }
      catch (SQLException e)
      {
         logger.error( logPrefix + "GPS: Failed to get nearby places: " + e.getMessage() );
      }

   }


   /**
    * Collects beacon pattern matches for the current cell pattern
    */
   private void collectPlaceCandidatesFromWifiPattern()
   {
      wifiPatternMatches = collectPlaceCandidatesFromBeaconPattern( wifiPattern );

   }


   /**
    * Derives a position from the general location, e.g. center and "radius" of area, city
    * or country (area if known as this is smallest, if not city or country)
    */
   private void derivePositionFromGeneralLocation()
   {
      GeneralLocation gl = new GeneralLocation();
      gl.setCountryCode( location.getCountryCode() );
      gl.setRegion( location.getRegion() );
      gl.setCity( location.getCity() );
      gl.setArea( location.getArea() );
      try
      {
         Position p = geoService.getPosition( gl );
         if (p != null)
         {
            location.setLatitude( p.getLatitude() );
            location.setLongitude( p.getLongitude() );
            location.setAccuracy( p.getAccuracy() );
            positionSource = PositionSource.GENLOC;
            logger.debug( logPrefix + "Position of " + location.getGeneralLocation()
                          + ": " + p );
         }
         if (p == null)
         {
            logger
               .debug( logPrefix + "No position found for "
                       + ( location.getArea() == null ? "" : location.getArea() + ", " )
                       + ( location.getCity() == null ? "" : location.getCity() + ", " )
                       + location.getCountryCode().getEnglishCountryName() );
         }
      }
      catch (IOException e)
      {
         logger.debug( logPrefix + "Failed to derive position from "
                       + ( location.getArea() == null ? "" : location.getArea() + ", " )
                       + ( location.getCity() == null ? "" : location.getCity() + ", " )
                       + location.getCountryCode().getEnglishCountryName() + ": "
                       + e.getMessage() );
      }
   }


   /**
    * Compare current position (derived from cell tower positions) with stored wifi
    * positions and set as movable if distance is above a reasonable limit
    * 
    * @param posSource
    *           only consider beacons whose old position was obtained from this position
    *           source (don't compare apples with oranges)
    */
   private void disableWifiBeaconsSeenFarApart(Beacon.PositionSource posSource)
   {
      // get the wifi beacons seen in the most recent query
      LocationQuery q = queries.get( 0 );
      Collection<Beacon> wifis = q.getBeacons( Beacon.Type.WIFI );

      // get the users position (as determined by cell towers)
      Position newPos = location.getPosition();

      GeneralLocation newPosLoc = null;
      GeneralLocation oldPosLoc = null;

      // check all wifi beacons in most recent query
      for (Beacon b : wifis)
      {
         if (b.isFixed() && b.getPositionSource() == posSource)
         {
            Position oldPos =
               new Position( b.getLatitude(), b.getLongitude(), b.getRange() );

            double dist = oldPos.getDistanceTo( newPos );

            // if seen far apart, mark as movable (or non-unique)
            if (!oldPos.isDefault() && !newPos.isDefault() && dist > 15000
                && dist > 1.5 * ( newPos.getAccuracy() + oldPos.getAccuracy() ))
            {
               // get human readable location info about old and new position (needed for
               // reporting only)
               if (newPosLoc == null)
               {
                  newPosLoc = getGeneralLocation( newPos );
               }
               try
               {
                  oldPosLoc = getGeneralLocation( oldPos );
               }
               catch (Exception e)
               {
               }

               String msg =
                  String
                     .format(
                        "%sBeacon %s: %s @%.0fm (%s) now seen @ %s (%s), %.0fm apart, disabling.",
                        logPrefix, b, oldPos, b.getRange(), oldPosLoc, newPos, newPosLoc,
                        dist );

               b.setFixed( false );
               logger.error( msg );
               try
               {
                  lDB.addOrUpdateBeacon( b );
               }
               catch (SQLException e)
               {
                  logger.error( String.format( "%sFailed to update beacon %s in DB",
                     logPrefix, b.toString() ) );
               }
            }
         }
      }
   }


   /**
    * Evaluates if the new location is different from the previous location of the current
    * user
    */
   private void evaluateLocationChangedSinceLastQuery()
   {
      if (location == null)
      {
         logger.debug( logPrefix+"location is null, no change" );
         locationChangedSinceLastQuery = false;
      }
      else
      {
         locationChangedSinceLastQuery = false;
         String change = null;
         // int dist = 0;
         // if (oldLocation != null && !oldLocation.getPoint().isDefault())
         // {
         // dist = (int) location.getPoint().getDistanceTo( oldLocation.getPoint() );
         // }

         if (oldLocation == null)
         {
            locationChangedSinceLastQuery = true;
            change = "no old location";
         }
         else if (!location.getLabel().equals( oldLocation.getLabel() ))
         {
            logger.debug( logPrefix+"label is different, location changed" );
            locationChangedSinceLastQuery = true;
            change = "label";
         }
         else if (oldLocation.getPlaceId() > 0 && location.getPlaceId() > 0
                  && oldLocation.getPlaceId() != location.getPlaceId())
         {
            logger.debug( logPrefix+"place id is different, location changed" );
            locationChangedSinceLastQuery = true;
            change = "place ID";
         }
         // else if (location.getAccuracy() > 10
         // && oldLocation.getAccuracy() > 10
         // && (double) location.getAccuracy() / (double) oldLocation.getAccuracy() <=
         // 0.5)
         // {
         // locationChangedSinceLastQuery = true;
         // change =
         // "accuracy " + oldLocation.getAccuracy() + "m -> " + location.getAccuracy()
         // + "m)";
         // }
         // else if (dist > 1000 && dist > location.getAccuracy()
         // && dist > oldLocation.getAccuracy())
         // {
         // locationChangedSinceLastQuery = true;
         // change = "position " + dist + "m";
         // }
         if (locationChangedSinceLastQuery)
         {
            logger.debug( logPrefix + "Location has changed since last result (" + change
                          + ")" );
         }
         else
         {
            logger.debug( logPrefix + "Location has not changed since last result" );
         }
         
      }

      if (locationChangedSinceLastQuery && location != null && oldLocation != null
          && location.getLabel().equals( "Near " + oldLocation.getLabel() ))
      {
         logger
            .info( logPrefix
                   + "New location label is \"Near\" oldlocation label, ignoring changes" );
         locationChangedSinceLastQuery = false;

      }
   }


   /**
    * Generates a cell pattern from the recently seen cells. Note: this is a big ass
    * method that might deserve it's own class..?
    */
   private void generateCurrentCellPattern()
   {
      // note: This method was written to be independent of beacon type, but here it is
      // used only for cells. Maybe this a sub-optimal implementation
      Beacon.Type beaconType = Beacon.Type.CELL;

      String prefix = logPrefix + beaconType + ": "; // for grep'ing by type

      long maxBeaconAge = CELL_BEACON_MAX_AGE;
      long maxLastSeenAgo = CELL_MAX_STABLE_AGE;
      long reqStableTime = CELL_STABLE_PATTERN_LENGTH;
      long minObsTime = CELL_MIN_OBSERVATION_TIME;
      long patternEndTime = System.currentTimeMillis();

      cellPattern = new BeaconPattern();

      // filter out queries that are too old, and beacons of the wrong type
      ArrayList<LocationQuery> filteredQueries = new ArrayList<LocationQuery>();

      for (LocationQuery q : queries)
      {
         ArrayList<Beacon> filteredBeacons = new ArrayList<Beacon>();
         ArrayList<Integer> filteredSignalStrengths = new ArrayList<Integer>();
         long queryAge = patternEndTime - q.getTime();
         if (queryAge <= maxBeaconAge)
         {
            Iterator<Beacon> beaconIter = q.getBeacons().iterator();
            Iterator<Integer> sigstrIter = q.getSignalStrengths().iterator();
            while (beaconIter.hasNext())
            {
               Beacon beacon = beaconIter.next();
               Integer sigstr = sigstrIter.next();
               if (!beacon.isFixed())
               {
                  logger.debug( logPrefix + "Ignored movable or non unique beacon: "
                                + beacon.toLongString() );
               }
               else if (beacon.getRange() > BEACON_RANGE_LIMIT)
               {
                  logger.debug( logPrefix + "Ignored very long-range beacon: "
                                + beacon.toLongString() );
               }
               else if (beacon.getType() == beaconType)
               {
                  filteredBeacons.add( beacon );
                  filteredSignalStrengths.add( sigstr );
               }
            }
         }
         else
         {
            logger.debug( prefix + "Query (age: " + ( queryAge / 60000 )
                          + " minutes) is too old, ignored." );
         }

         if (filteredBeacons.size() > 0)
         {
            LocationQuery copy = new LocationQuery();
            copy.setTime( q.getTime() );
            copy.setId( q.getId() );
            copy.setLatitude( q.getLatitude() );
            copy.setLongitude( q.getLongitude() );
            // copy.setUserId( l.getUserId() );
            copy.setBeacons( filteredBeacons );
            copy.setSignalStrengths( filteredSignalStrengths );
            filteredQueries.add( copy );
            logger.debug( prefix + "Query (age "
                          + ( ( patternEndTime - q.getTime() ) / 60000 ) + " minutes): "
                          + filteredBeacons.size() + " beacons" );
         }
         else
         {
            logger.debug( prefix + "Query (age: " + ( queryAge / 60000 )
                          + " minutes) has no relevant beacons, ignored." );
         }
      }

      if (filteredQueries.size() == 0)
      {
         logger.debug( prefix + "No beacons seen the last " + ( maxBeaconAge / 60000 )
                       + " minutes:" );
         return;
      }

      // get average beacons per query
      double beaconsPerQuery = 0;
      for (LocationQuery l : filteredQueries)
      {
         beaconsPerQuery += l.getBeacons().size();
      }
      beaconsPerQuery /= filteredQueries.size();
      if (beaconsPerQuery > 1.0)
         logger.debug( prefix + "Avg. beacons per query: " + beaconsPerQuery );

      // format into unique beacons and number of times seen in pattern and sort after
      // first time seen
      HashMap<Integer, BeaconObservation> observationsMap =
         new HashMap<Integer, BeaconObservation>();
      for (int i = 0; i < filteredQueries.size(); i++)
      {
         long t1 = filteredQueries.get( i ).getTime();
         long t0 = t1;
         if (i < filteredQueries.size() - 1)
            t0 = filteredQueries.get( i + 1 ).getTime();

         Iterator<Beacon> beacons = filteredQueries.get( i ).getBeacons().iterator();
         Iterator<Integer> signalStrengths =
            filteredQueries.get( i ).getSignalStrengths().iterator();

         while (beacons.hasNext())
         {
            Beacon beacon = beacons.next();
            int signalStrength = signalStrengths.next();
            if (observationsMap.containsKey( beacon.getId() ))
            {
               BeaconObservation obs = observationsMap.get( beacon.getId() );
               obs.addSighting( t0, t1, signalStrength );
            }
            else
            {
               BeaconObservation obs = new BeaconObservation( beacon );
               obs.addSighting( t0, t1, signalStrength );
               observationsMap.put( beacon.getId(), obs );
            }
         }
      }

      // sort such that the observation for the most recently seen beacon is
      // last
      ArrayList<BeaconObservation> beaconObservations =
         new ArrayList<BeaconObservation>();
      beaconObservations.addAll( observationsMap.values() );
      Collections.sort( beaconObservations );

      // verbose
      if (beaconObservations.size() > 0)
      {
         logger.debug( prefix + "Beacons seen the last " + ( maxBeaconAge / 60000 )
                       + " minutes:" );
         for (BeaconObservation obs : beaconObservations)
         {
            logger.debug( prefix + "  " + obs );
         }
      }
      else
      {
         // diganostic to catch a bug
         logger.error( prefix + "BUG: Hmmm... I've seen to lost my beacons!" );
         for (LocationQuery q : filteredQueries)
         {
            for (Beacon b : q.getBeacons())
            {
               logger.error( logPrefix + "BUG: Lost " + b + " seen "
                             + ( ( System.currentTimeMillis() - q.getTime() ) / 1000 )
                             + " sec ago." );
            }
         }
      }

      // get total pattern time
      long patternTime =
         filteredQueries.get( 0 ).getTime()
            - filteredQueries.get( filteredQueries.size() - 1 ).getTime();

      // calculate some statistics
      int numBeaconsSeenMultipleTimes = 0;
      int numTrailingSingleSightingBeacons = 0;
      int maxSightings = 0;
      double avgSightings = 0;
      BeaconObservation mostRecent = null;
      Iterator<BeaconObservation> iter = beaconObservations.iterator();
      while (iter.hasNext())
      {
         BeaconObservation obs = iter.next();
         mostRecent = obs;
         avgSightings += obs.getNumberOfSightings();
         if (obs.getNumberOfSightings() > maxSightings)
            maxSightings = obs.getNumberOfSightings();

         if (obs.getNumberOfSightings() == 1)
            numTrailingSingleSightingBeacons++;
         else
         {
            numBeaconsSeenMultipleTimes++;
            numTrailingSingleSightingBeacons = 0;
         }
      }
      avgSightings /= beaconObservations.size();

      // correct for beacons seen in parallel
      if (beaconsPerQuery > 1)
      {
         numTrailingSingleSightingBeacons /= beaconsPerQuery;
      }

      long stableTime = 0;

      // if a cell pattern ends with 3 or more beacons seen only once, we're
      // most likely moving
      if (beaconType == Beacon.Type.CELL && filteredQueries.size() > 1
          && numTrailingSingleSightingBeacons >= 3)
      {
         stableTime = -patternTime;
      }
      else
      {

         // Unless the pattern contains only one beacon, consider only beacons that has
         // been visited at least maxSightings/2, rounded up
         int minSightings = 1;
         if (filteredQueries.size() > 1)
            minSightings = (int) Math.max( 2, Math.floor( maxSightings / 2.0 ) );

         ArrayList<Integer> stableBeaconIds = new ArrayList<Integer>();
         ArrayList<Integer> unstableBeaconIds = new ArrayList<Integer>();
         iter = beaconObservations.iterator();
         while (iter.hasNext())
         {

            BeaconObservation obs = iter.next();

            long obsAge = patternEndTime - obs.getLastSeen();

            // only consider recently seen beacons of the specified beacon type
            if (obs.getBeacon().getType() == beaconType && obsAge <= maxLastSeenAgo)
            {
               if (!obs.getBeacon().isFixed())
               {
                  logger.debug( prefix + "Ignored movable beacon " + obs.getBeacon() );
               }
               else if (obs.getBeacon().getRange() > BEACON_RANGE_LIMIT)
               {
                  logger.debug( prefix + "Ignored long-range beacon " + obs.getBeacon() );
               }
               else
               {
                  long timeObserved = obs.getTimeObserved();
                  int numberOfSightings = obs.getNumberOfSightings();
                  double signalStrengthMean = obs.getSignalStrengthMean();
                  // double signalStrengthStddev = obs.getSignalStrengthStddev();

                  // only consider cells stable that have been seen for at minObsTime and
                  // at least minVisit times, and has a mean signal strength above a
                  // defined minimum level
                  if (timeObserved >= minObsTime && numberOfSightings >= minSightings
                      && signalStrengthMean >= MIN_SIGNAL_STRENGTH)
                  // && signalStrengthStddev <= 10.0)
                  {

                     // add the observation to the list of observations counting towards
                     // stability
                     stableBeaconIds.add( obs.getBeacon().getId() );
                  }

                  // recent observations that do not match these criteria count as
                  // negative
                  else
                  {
                     unstableBeaconIds.add( obs.getBeacon().getId() );
                  }
               }
            }
         }
         // assess stable time
         for (int i = 0; i < filteredQueries.size(); i++)
         {
            LocationQuery query = filteredQueries.get( i );
            long t1 = query.getTime();
            long t0 = t1;
            if (i < filteredQueries.size() - 1)
               t0 = filteredQueries.get( i + 1 ).getTime();

            boolean stable = false;
            for (Beacon b : query.getBeacons())
            {
               if (stableBeaconIds.contains( b.getId() ))
                  stable = true;
            }
            if (stable)
               stableTime += ( t1 - t0 );

         }

         // build pattern from stable observations
         for (BeaconObservation obs : beaconObservations)
         {
            if (stableBeaconIds.contains( obs.getBeacon().getId() ))
            {
               double timeFraction = (double) obs.getTimeObserved() / (double) stableTime;
               if (obs.getTimeObserved() == 0)
                  timeFraction = 1.0 / beaconObservations.size();
               cellPattern.addElement( obs.getBeacon(), timeFraction, obs
                  .getSignalStrengthMean() );
            }
         }

      }

      // if no stable observations, add the last seen cell for good measure
      if (cellPattern.getElements().size() == 0 && mostRecent != null)
      {
         logger
            .info( logPrefix
                   + "No stable beacons seen, setting pattern equal to beacons of most recent query." );
         for (Beacon b : queries.get( 0 ).getBeacons( beaconType ))
         {
            if (b.isFixed())
            {
               cellPattern.addElement( b, 0.0, 0 );
            }
            else
            {
               logger.debug( logPrefix + "Beacon " + b + " is not fixed, skipped." );
            }
         }
      }

      cellPattern.setStableTime( stableTime );
      cellPattern.setTotalTime( patternTime );

      // assign a confidence level to the observed pattern
      long observedPatternTime = cellPattern.getTotalTime();
      int cellPatternQuality = 100;
      if (reqStableTime > 0)
      {
         cellPatternQuality = (int) ( 100 * stableTime / reqStableTime );
         if (cellPatternQuality > 100)
            cellPatternQuality = 100;
         if (cellPatternQuality < -100)
            cellPatternQuality = -100;
      }

      location.setCellPatternQuality( cellPatternQuality );

      // some extra debug info
      if (beaconObservations.size() > 0)
      {
         logger.debug( prefix + " 100% req pattern time: " + ( reqStableTime / 1000 / 60 )
                       + " min " + ( reqStableTime / 1000 % 60 ) + " sec" );
         logger.debug( prefix + "Observed pattern time: "
                       + ( observedPatternTime / 1000 / 60 ) + " min "
                       + ( observedPatternTime / 1000 % 60 ) + " sec" );
         logger.debug( prefix + "Stable   pattern time: " + ( stableTime / 1000 / 60 )
                       + " min " + ( stableTime / 1000 % 60 ) + " sec" );
         logger.debug( prefix + "Pattern (confidence " + cellPatternQuality + "%):" );
         for (BeaconPattern.BeaconPatternElement e : cellPattern.getElements())
         {
            logger
               .info( prefix + e.beacon.getMac() + ": time="
                      + (int) ( 100 * e.timeFraction ) + "%, dbid=" + e.beacon.getId() );
         }
      }

   }


   /**
    * Generates the current wifi pattern. This is equal to the wifi beacons seen in the
    * last query, minus those tagged as movable (which also includes those with range too
    * big to be trusted)
    */
   private void generateCurrentWifiPattern()
   {
      wifiPattern = new BeaconPattern();

      if (queries.size() > 0)
      {
         String prefix = logPrefix + "WIFI: "; // for grep'ing by type

         // collect relevant beacons from last query only
         LocationQuery lastQuery = queries.get( 0 );
         Iterator<Beacon> beaconIter = lastQuery.getBeacons().iterator();
         Iterator<Integer> sigstrIter = lastQuery.getSignalStrengths().iterator();
         ArrayList<Beacon> wifiBeacons = new ArrayList<Beacon>();
         ArrayList<Integer> wifiSignalStrengths = new ArrayList<Integer>();
         while (beaconIter.hasNext())
         {
            Beacon beacon = beaconIter.next();
            Integer sigstr = sigstrIter.next();
            if (beacon.getType() == Beacon.Type.WIFI)
            {
               if (!beacon.isFixed())
               {
                  logger
                     .debug( prefix + "Ignored movable or non-unique beacon " + beacon );
               }
               else if (beacon.getRange() > BEACON_RANGE_LIMIT)
               {
                  logger.debug( prefix + "Ignored long-range beacon " + beacon );
               }
               else
               {
                  wifiBeacons.add( beacon );
                  wifiSignalStrengths.add( sigstr );
               }
            }
         }

         if (wifiBeacons.size() == 0)
         {
            logger.debug( prefix + "No beacons seen in last location query" );
            return;
         }

         // verbose
         logger.debug( prefix + "Beacons in last query:" );
         for (int i = 0; i < wifiBeacons.size(); i++)
         {
            logger.debug( prefix + "  " + wifiBeacons.get( i ).getMac() + " @ -"
                          + wifiSignalStrengths.get( i ) + "dBM" );
         }

         // build pattern
         for (int i = 0; i < wifiBeacons.size(); i++)
         {
            wifiPattern
               .addElement( wifiBeacons.get( i ), 1, wifiSignalStrengths.get( i ) );
         }

         wifiPattern.setStableTime( 0 );
         wifiPattern.setTotalTime( 0 );

         for (BeaconPattern.BeaconPatternElement e : wifiPattern.getElements())
         {
            logger
               .info( prefix + " mac=" + e.beacon.getMac() + ", time="
                      + (int) ( 100 * e.timeFraction ) + "%, dbid=" + e.beacon.getId() );
         }

      }
      else
      {
         logger.debug( logPrefix + "WIFI: No beacons seen in last location query" );
      }
   }


   /**
    * Accessor for cell pattern corresponding to last location result
    * 
    * @return The cell pattern
    */
   public BeaconPattern getCellPattern()
   {
      if (location == null)
      {
         throw new RuntimeException(
            "getLocation(userId) must be called before a cell pattern can be returned." );
      }
      return cellPattern;
   }


   /**
    * Gets the country code corresponding to the MCC of the most recently seen cell beacon
    */
   private void getCountryCodeFromCellMcc()
   {
      for (LocationQuery q : queries)
      {
         for (Beacon b : q.getBeacons( Beacon.Type.CELL ))
         {
            {
               int mcc = b.getMcc();
               cellCountryCode = CountryCode.getInstanceFromMCC( mcc );
               logger.debug( logPrefix + " Country code from MCC " + mcc + ": "
                             + cellCountryCode + " ("
                             + cellCountryCode.getEnglishCountryName() + ")" );
               return;
            }
         }
      }
   }


   private GeneralLocation getGeneralLocation(Point p)
   {
      GeneralLocation gl = null;
      
      if (oldLocation != null && p.equals( oldLocation.getPoint() ))
      {
         gl = oldLocation.getGeneralLocation();
         logger.info( logPrefix
                      + "Same lat/lon as last result, reusing general location (" + gl
                      + ")" );
      }

      else
      {
         try
         {
            gl = revGeoService.getGeneralLocation( p );
         }
         catch (Exception e)
         {
            logger.error( logPrefix + "Failed to get general location for point " + p
                          + ": " + e.getMessage() );
         }
      }

      return gl;
   }


   /**
    * Sets the location country equal to that derived from the mcc code of a recently seen
    * cell
    */
   private void getGeneralLocationFromCellMcc()
   {
      if (cellCountryCode.getEnglishCountryName() != null)
      {
         logger.debug( logPrefix + "Using country derived from MCC:"
                       + cellCountryCode.getEnglishCountryName() );
         location.setCountryCode( cellCountryCode );
      }
      else
      {
         logger.error( logPrefix + "No cell country code found" );
      }

   }


   /**
    * Look through all place candidates to try to find a general location
    */
   private void getGeneralLocationFromNearbyPlaces()
   {
      if (location.getArea() == null && gpsPatternMatches != null)
      {
         for (BeaconPatternMatch p : gpsPatternMatches)
         {
            if (p.getPlace().getArea() != null)
            {
               location.setArea( p.getPlace().getArea() );
            }
            if (p.getPlace().getCity() != null)
            {
               location.setCity( p.getPlace().getCity() );
            }
            if (p.getPlace().getRegion() != null)
            {
               location.setRegion( p.getPlace().getRegion() );
            }
            if (p.getPlace().getCountryCode() != null)
            {
               location.setCountryCode( p.getPlace().getCountryCode() );
            }
         }
      }

      if (location.getArea() == null && wifiPatternMatches != null)
      {
         for (BeaconPatternMatch p : wifiPatternMatches)
         {
            if (cellCountryCode != null
                && cellCountryCode == p.getPlace().getCountryCode())
            {
               if (p.getPlace().getArea() != null)
               {
                  location.setArea( p.getPlace().getArea() );
               }
               if (p.getPlace().getCity() != null)
               {
                  location.setCity( p.getPlace().getCity() );
               }
               if (p.getPlace().getRegion() != null)
               {
                  location.setRegion( p.getPlace().getRegion() );
               }
               if (p.getPlace().getCountryCode() != null)
               {
                  location.setCountryCode( p.getPlace().getCountryCode() );
               }
            }
         }
      }

      if (location.getCity() == null && cellPatternMatches != null)
      {
         for (BeaconPatternMatch p : cellPatternMatches)
         {
            if (p.getPlace().getArea() != null)
            {
               location.setArea( p.getPlace().getArea() );
            }
            if (p.getPlace().getCity() != null)
            {
               location.setCity( p.getPlace().getCity() );
            }
            if (p.getPlace().getRegion() != null)
            {
               location.setRegion( p.getPlace().getRegion() );
            }
            if (p.getPlace().getCountryCode() != null)
            {
               location.setCountryCode( p.getPlace().getCountryCode() );
            }
         }
      }

      String gl = location.toString( 4, Location.Layer.AREA );
      if (gl != null && gl.length() > 0)
      {
         logger.debug( logPrefix
                       + "Got general location from other people's nearby place: " + gl );
      }

   }


   /**
    * Gets the general location (country, region, city, area) corresponding to current
    * location lat/lon.
    */
   private void getGeneralLocationFromPosition()
   {
      // GeonamesWebService geonamesWeb = GeonamesWebService.getInstance();
      // GeonamesLocalService geonamesLocal = GeonamesLocalService.getInstance();

      Position p = location.getPosition();
      if (!p.isDefault())
      {
         GeneralLocation gl = null;
         if (oldLocation != null && p.equals( oldLocation.getPoint() ))
         {
            logger
               .info( logPrefix
                      + "GEO: Position identical to last result, re-using general location" );
            gl = oldLocation.getGeneralLocation();
         }
         else
         {
            gl = getGeneralLocation( p );
         }

         if (gl != null && gl.getCountryCode() != null)
         {
            location.setCountryCode( gl.getCountryCode() );
            if (p.getAccuracy() <= MAX_REGION_POSITION_ERROR)
            {
               location.setRegion( gl.getRegion() );
            }
            else if (gl.getRegion() != null)
            {
               logger.debug( logPrefix + "GEO: Position accuracy is " + p.getAccuracy()
                             + "m. Region " + gl.getRegion() + " not used." );
            }
            if (p.getAccuracy() <= MAX_CITY_POSITION_ERROR)
            {
               location.setCity( gl.getCity() );
            }
            else if (gl.getCity() != null)
            {
               logger.debug( logPrefix + "GEO: Position accuracy is " + p.getAccuracy()
                             + "m. City " + gl.getCity() + " not used." );
            }
            if (p.getAccuracy() <= MAX_AREA_POSITION_ERROR)
            {
               location.setArea( gl.getArea() );
            }
            else if (gl.getArea() != null)
            {
               logger.debug( logPrefix + "GEO: Position accuracy is " + p.getAccuracy()
                             + "m. Area " + gl.getArea() + " not used." );
            }
            logger.debug( logPrefix + "GEO: " + p );
            logger.debug( logPrefix + "GEO: Country: "
                          + location.getCountryCode().getEnglishCountryName() );
            logger.debug( logPrefix + "GEO: Region: " + location.getRegion() );
            logger.debug( logPrefix + "GEO: City: " + location.getCity() );
            logger.debug( logPrefix + "GEO: Area: " + location.getArea() );
         }
         else
         {
            logger.debug( logPrefix + "GEO: No location names found for position " + p );
         }
      }
   }


   /**
    * Precondition: Recent location queries and referenced beacons stored in db
    * 
    * @param user
    *           The user for which a location shall be derived
    * @return The user's location
    */
   public Location getLocation(LocationUser user)
   {
      // ---------------------------------
      // Part 1: Init
      // ---------------------------------

      resetObjects( user );

      if (!isUserObjectValid())
      {
         logicLog( "Unknown user, returning null" );
         return null;
      }

      // DB tables queried: current_locations (r)
      getOldLocationFromDatabase();

      // DB tables queried: queries, query_beacons, beacons (r)
      getUsersRecentLocationQueriesFromDatabase();

      // ---------------------------------
      // Part 2: Fail gracefully if no prerequisite data
      // ---------------------------------

      if (!hasRecentQueries())
      {
         logicLog( "No recent location queries" );
         if (hasOldLocation())
         {
            logicLog( "Returning last result" );
            return getOldLocationObject();
         }
         else
         {
            logicLog( "No previous result, returning empty location" );
            return new Location();
         }
      }

      // ---------------------------------
      // Part 4: Update country of new cells and wifi beacons and get position of new
      // cells
      // ---------------------------------

      if (hasRecentCellQueries())
      {
         logicLog( "Has recent cell queries, getting country from MCC and position of never befor seen cells" );

         // reconsider if this is really needed any more. Was used to detect movable
         // beacons, but newer methods do this better
         // updateCountryCodeOfNeverBeforeSeenBeaconsAccordingToMCC();

         getCountryCodeFromCellMcc();

         // DB tables queried: beacon_info_3rdparty (r+w)
         getPositionOfNeverBeforeSeenCellsFromGoogle();

      }

      // ---------------------------------
      // Part 5: Generate beacon patterns
      // ---------------------------------

      if (hasRecentCellQueries())
      {
         generateCurrentCellPattern();
      }
      if (hasRecentWifiQueries())
      {
         generateCurrentWifiPattern();
      }

      // ---------------------------------
      // Part 6: determine motion state
      // ---------------------------------

      // motion state from wifi beacons (needs to be improved)
      if (hasRecentWifiQueries())
      {
         logicLog( "Has recent wifi queries, getting motion state from wifi" );
         getMotionStateFromCurrentWifiPattern();
      }
      // Override with motion state from Cell beacons (quite good)
      if (hasRecentCellQueries())
      {
         logicLog( "Has recent wifi queries, getting motion state from cell" );
         getMotionStateFromCurrentCellPattern();

         if (hasRecentWifiQueries() && isMoving() && isWifiMotionStateStationary())
         {
            logicLog( "Motion state from cell is 'moving' but motion state from wifi is 'stationary'." );
            logicLog( "Using wifi motion state." );
            useWifiMotionState();
         }
      }
      // Override with motion state from GPS coordinates if available
      if (hasRecentGpsQueries())
      {
         logicLog( "Has recent gps queries, getting motion state from gps" );
         getMotionStateFromRecentGpsQueries();
      }

      // ---------------------------------
      // Part 7: Determine user's position/accuracy
      // ---------------------------------

      // determine user's position
      if (hasRecentCellQueries())
      {
         logicLog( "Has recent cell queries, getting position from cell" );
         triangulateCurrentPositionFromPositionedCellBeacons();
      }
      if (hasRecentWifiQueries())
      {
         if (hasCurrentWifiQueries() && hasPosition())
         {
            logicLog( "Has recent wifi queries and position, disabling wifi beacons seen far appart" );
            // DB tables queried: beacons (w)
            disableWifiBeaconsSeenFarApart( Beacon.PositionSource.GENERAL );
            // Note: GENERAL in this context means derived from cell position

         }
         if (!hasPosition())
         {
            logicLog( "No position yet, getting position from wifi" );
            triangulateCurrentPositionFromPositionedWifiBeacons();
         }
      }
      if (hasRecentGpsQueries())
      {
         logicLog( "Has recent gps queries, getting position from gps" );
         averageAndOverwriteCurrentPositionFromRecentGpsQueries();
      }

      // ---------------------------------
      // Part 8: get general location (country, region, city, area)
      // ---------------------------------

      // if a position has been derived, use this
      if (hasPosition())
      {
         logicLog( "Has position, getting general location" );
         getGeneralLocationFromPosition();
      }
      // if no general location could be obtained derive general location from nearby
      // places (other people's places)
      if (!hasGeneralLocation())
      {
         logicLog( "No general location found, getting general location from nearby places" );
         getGeneralLocationFromNearbyPlaces();
      }

      // if still no location, use country only which is known from cell MCC
      if (!hasGeneralLocation() && hasRecentCellQueries())
      {
         logicLog( "No general location found, getting general location from cell MCC" );
         getGeneralLocationFromCellMcc();
      }

      // sanity check
      if (hasGeneralLocation())
      {
         logicLog( "General location found" );
         if (hasRecentCellQueries())
         {
            logicLog( "Sanity checking general location against cell MCC" );
            checkGeneralLocationAgainstMccCountryCode();
         }
         else
         {
            logicLog( "No recent cell queries, can't sanity check general location against cell MCC" );
         }
      }

      // ---------------------------------
      // Part 9: Find place if not moving
      // ---------------------------------
      if (!isMoving())
      {
         logicLog( "Motion state is not 'Moving'" );

         if (hasGpsTrack())
         {
            logicLog( "Has gps track, getting place candidates from gps position" );

            // DB tables queried: places (r)
            collectPlaceCandidatesFromGpsPosition();
         }
         if (hasWifiPattern())
         {
            logicLog( "Has wifi pattern, getting place candidates from wifi pattern" );

            // DB tables queried: beacon_pattern_beacons, beacon_patterns, places (r)
            collectPlaceCandidatesFromWifiPattern();

            removeFalsePlaceCandidatesFromNonUniqueWifiMacs();
         }
         if (hasCellPattern())
         {
            logicLog( "Has cell pattern, getting place candidates from cell pattern" );

            // DB tables queried: beacon_pattern_beacons, beacon_patterns, places (r)
            collectPlaceCandidatesFromCellPattern();
         }

         // Best method: WIFI
         if (hasWifiPattern())
         {
            logicLog( "Looking for wifi-referenecd place equal to currently set next" );

            // DB tables queried: next_locations (r)
            selectPlaceMatchingCurrentlySetNextLocationFromWifiPattern();

            if (!isPlaceFound())
            {
               logicLog( "Looking for wifi-referenecd place equal most recently manually set" );

               // DB tables queried: place_overrides (r)
               selectPlaceEqualToLastManualOverrideFromWifiPattern();
            }

            if (!isPlaceFound() && hasCellPattern())
            {
               logicLog( "Looking for wifi-referenecd place also referenced by cell pattern" );

               // DB tables queried: place_subscriptions (r)
               selectSubscribedPlaceFromWifiAndCellPattern();
            }

            if (!isPlaceFound())
            {
               logicLog( "Looking for any wifi-referenecd place" );

               // DB tables queried: place_subscriptions (r)
               selectSubscribedPlaceFromWifiPattern();
            }

            if (!isPlaceFound() && hasCellPattern() && hasOldLocation()
                && wasLastLocationResultPlaceFix()
                && isPlaceFromLastResultStillReferencedByCurrentCellPattern()
                && isStillSubscribingToPlaceOfLastresult())
            {
               logicLog( "No place wifi-referenced place found." );
               logicLog( "Last result was place-fix and place is still referenced by current cell pattern." );
               logicLog( "Reusing last result." );

               reuseLastLocationResult();
               return getLocationObject();
            }

         }

         // 2nd best method: Bluetooth
         if (!isPlaceFound() && hasRecentBluetoothQueries())
         {
            logicLog( "Looking for place of friends in bluetooth range." );

            // DB tables queried: queries, query_beacons, location_users (r)
            getPlaceFromFriendsInBluetoothRange();
         }

         // 3rd best method: GPS
         if (!isPlaceFound() && hasGpsTrack())
         {

            logicLog( "Looking for gps-referenecd place equal to currently set next" );

            // DB tables queried: next_locations (r)
            selectPlaceMatchingCurrentlySetNextLocationFromGpsPattern();

            if (!isPlaceFound())
            {
               logicLog( "Looking for gps-referenecd place equal to most recently manually set" );

               // DB tables queried: place_overrides (r)
               selectPlaceEqualToLastManualOverrideFromGpsPosition();
            }

            if (!isPlaceFound())
            {

               logicLog( "Looking for any gps-referenecd place" );

               // DB tables queried: place_subscriptions (r)
               selectSubscribedPlaceFromGpsPosition();
            }

            if (!isPlaceFound() && hasCellPattern() && hasOldLocation()
                && wasLastLocationResultPlaceFix()
                && isPlaceFromLastResultStillReferencedByCurrentCellPattern()
                && isStillSubscribingToPlaceOfLastresult())
            {
               logicLog( "No place gps-referenced place found." );
               logicLog( "Last result was place-fix and place is still referenced by current cell pattern." );
               logicLog( "Reusing last result." );
               reuseLastLocationResult();
               return getLocationObject();
            }

         }

         // Worst method: Cell ID
         if (!isPlaceFound() && hasCellPattern())
         {
            // if last result was place fix, and it is still referenced by a a cell in
            // current pattern, re-use old location even if not best pattern match. This
            // ads stability.
            if (hasWifiPattern() && wasLastLocationResultPlaceFix()
                && isStillSubscribingToPlaceOfLastresult()
                && isPlaceFromLastResultStillReferencedByCurrentCellPattern())
            {
               logicLog( "Last result was place-fix and place is still referenced by current cell pattern." );
               logicLog( "Reusing last result." );
               reuseLastLocationResult();
               return getLocationObject();
            }

            if (!isPlaceFound())
            {
               logicLog( "Looking for cell-referenecd place equal to currently set next" );

               // DB tables queried: next_locations (r)
               selectPlaceMatchingCurrentlySetNextLocationFromCellPattern();
            }

            if (!isPlaceFound())
            {
               logicLog( "Looking for cell-referenecd place equal to most recently manually set" );

               // DB tables queried: place_overrides (r)
               selectPlaceEqualToLastManualOverrideFromCellPattern();
            }

            if (!isPlaceFound() && !hasWifiPattern())
            {
               logicLog( "Wifi pattern is empty. Looking for any cell-referenecd place" );

               // DB tables queried: place_subscriptions (r)
               selectSubscribedPlaceFromCellPattern();
            }

         }

         if (isPlaceFoundFromCellPattern() && isOnlyOneCellPlaceFound())
         {
            logicLog( "Place was found from cell pattern and is only candidate. Setting pattern match to 100%" );
            overridePlacePatternMatchToMax();
         }

      }

      // ---------------------------------
      // Part 10: Apply place info and modify motion state depending on time at place
      // ---------------------------------
      if (isPlaceFound())
      {

         logicLog( "Place was found." );
         applyPlaceInfo();

         // wait at least 10 minutes before latching on to a place
         if (isStationary() && minutesAtPlace() < 10)
         {
            logicLog( "Motion state is 'stationary', but time at place is < 10 min. Setting motion state to 'restless'" );
            overrideMotionStateToRestless();
         }

         // force latching on to place if more than 10 minutes there
         if (!isPlaceFix() && minutesAtPlace() >= 10)
         {
            logicLog( "Place match was not good enough to snap, but time at place is > 10 min. Snapping to place." );
            snapToPlace();
         }

         // override position with values from place and accuracy derived from place
         // source
         if (isPlacePositioned())
         {
            logicLog( "Place has position, overriding location position to place position" );
            setPositionToPlacePosition();

            // if moving or restless decrease accuracy (i.e. set a higher value, duh)
            if (isRestless())
            {
               logicLog( "Motion state is 'restless', dubling location accuracy value" );
               multiplyAccuracy( 2 );
            }

         }

      }

      // ---------------------------------
      // Part 12: Last resort method of getting a coordinate
      // ---------------------------------
      // if no position was derived and general location was obtained from nearby
      // beacons, use this to derive a (very poor) position
      if (!hasPosition() && hasGeneralLocation())
      {
         logicLog( "No position found, but has general location" );

         if (hasGeneralLocationCountryOnly() && isCountrySameAsOldCountry() && hasOldLocationPosition())
         {
            logicLog( "General location is country-level only and country is same as last result, reusing last result" );
            reuseLastLocationResult();
            return getLocationObject();
         }
         else
         {
            logicLog( "Deriving position from general location" );
            derivePositionFromGeneralLocation();
         }
      }

      // ---------------------------------
      // Part 13: Hysteresis
      // ---------------------------------
      
      // prevent drifting away from place to "Near " place
      if (isPlaceFound() && hasOldLocation() && wasLastLocationResultPlaceFix()
          && isStillSubscribingToPlaceOfLastresult())
      {
         logicLog( "Place found. Last result was place fix." );

         if (isPlaceAlmostIndistinguishableFromOldLocation())
         {
            logicLog( "Place match is neglibly better than that of the place that of last result." );
            logicLog( "Reusing last result" );

            reuseLastLocationResult();
            return getLocationObject();
         }
         else if (isPlaceSameAsLastLocationResult())
         {
            logicLog( "Place is same as that of last result." );
            logicLog( "Reusing last result" );

            reuseLastLocationResult();
            return getLocationObject();
         }
      }
      
      // prevent reduction of info to country only
      if(isCountrySameAsOldCountry() && hasGeneralLocationCountryOnly() && hasOldLocationCityOrRegion()){
         logicLog( "Location has country info only, last result had more info.");
         logicLog( "Reusing last result" );

         reuseLastLocationResult();
         return getLocationObject();
      }
      
      
      if (isLocationFound())
      {
         logicLog("Location found");
         evaluateLocationChangedSinceLastQuery();

         if (hasLocationChangedSinceLastQuery())
         {
            logicLog( "Location changed since last result" );

            // DB tables queried: current_locations (w)
            storeCurrentLocationToDatabase();
         }
         else
         {
            logicLog( "No location change since last result" );
         }
      }
      else
      {
         logicLog( "No location found" );
      }

      // ---------------------------------
      // Part 12: Improve stored data
      // ---------------------------------
      if (hasPosition() && hasRecentWifiQueries() && isPositionSourceCell())
      {
         logicLog( "Location with position found from cell. Has recent wifi queries. Checking if wifi beacon positions can be improved." );

         // DB tables queried: beacons (w)
         updatePositionAndRangeOfWifiBeaconsUsingCurrentPositionAndAccuracyIfBetterThanStored();
      }

      // make damn sure that the result fits the country code from mcc
      if (location != null && hasGeneralLocation())
      {
         checkGeneralLocationAgainstMccCountryCode();
      }

      return getLocationObject();
   }


   /**
    * @return Returns true if the old location has either city or region info
    */
   private boolean hasOldLocationCityOrRegion()
   {
      if(oldLocation == null) return false;
      if(oldLocation.getCity() != null) return true;
      if(oldLocation.getRegion() != null) return true;
      return false;
   }


   /**
    * @return Returns the location object that all private methods of this class operates
    *         on
    */
   private Location getLocationObject()
   {
      if (location != null)
      {
         for (String s : location.toStrings( 10, Location.Layer.COORDINATES, true, true,
            true ))
         {
            logger.info( logPrefix + "RESULT: " + s );
         }
         logger.info( logPrefix + "RESULT: LABEL: " + location.getLabel() );
      }
      else
      {
         logger.info( logPrefix + "RESULT: " + null );
      }
      return location;
   }


   /**
    * Derives motion state from the current cell pattern confidence
    */
   private void getMotionStateFromCurrentCellPattern()
   {
      if (location.getCellPatternQuality() <= LocationConstants.MOTION_STATE_MOVING_CONFIDENCE_LIMIT)
         location.setMotionState( Location.MotionState.MOVING );
      else if (location.getCellPatternQuality() >= LocationConstants.MOTION_STATE_STATIONARY_CONFIDENCE_LIMIT)
         location.setMotionState( Location.MotionState.STATIONARY );
      else
         location.setMotionState( Location.MotionState.RESTLESS );

      logger.debug( logPrefix + "CELL: MOTION STATE: Pattern quality: "
                    + location.getCellPatternQuality() );
      logger.debug( logPrefix + "CELL: MOTION STATE: " + location.getMotionState() );
   }


   /**
    * Derives motion state from the number of beacons seen only once or more than three
    * times recently
    */
   private void getMotionStateFromCurrentWifiPattern()
   {
      // if less than 3 queries recently we can't make a good decision
      if (queries.size() < 3)
      {
         location.setMotionState( Location.MotionState.RESTLESS );
         wifiMotionState = MotionState.RESTLESS;
      }
      else
      {
         // otherwise, count the number of times each wifi beacon has been seen
         HashMap<Integer, Integer> beaconSightings = new HashMap<Integer, Integer>();
         for (LocationQuery q : queries)
         {
            for (Beacon wifi : q.getBeacons( Beacon.Type.WIFI ))
            {
               int sightings = 0;
               if (beaconSightings.containsKey( wifi.getId() ))
               {
                  sightings = beaconSightings.get( wifi.getId() );
               }
               sightings++;
               beaconSightings.put( wifi.getId(), sightings );
            }
         }

         // get the number of beacons seen only once, and the number seen three times or
         // more
         int seenOnce = 0;
         int seenTwice = 0;
         int seenThrice = 0;
         for (int beaconId : beaconSightings.keySet())
         {
            int sightings = beaconSightings.get( beaconId );
            if (sightings == 1)
               seenOnce++;
            else if (sightings == 2)
               seenTwice++;
            else if (sightings >= 3)
               seenThrice++;
         }

         logger.debug( logPrefix + "WIFI: MOTION STATE: beacons recently seen "
                       + beaconSightings.size() );
         logger.debug( logPrefix + "WIFI: MOTION STATE: beacons recently seen once "
                       + seenOnce );
         logger.debug( logPrefix + "WIFI: MOTION STATE: beacons recently seen twice "
                       + seenTwice );
         logger
            .debug( logPrefix
                    + "WIFI: MOTION STATE: beacons recently seen three times or more: "
                    + seenThrice );
         if (beaconSightings.size() >= 6 && seenOnce >= 3 && seenThrice == 0)
         {
            location.setMotionState( Location.MotionState.MOVING );
            wifiMotionState = MotionState.MOVING;
         }
         else if (seenThrice > 0 && seenThrice >= seenOnce)
         {
            location.setMotionState( Location.MotionState.STATIONARY );
            wifiMotionState = MotionState.STATIONARY;
         }
         else
         {
            location.setMotionState( Location.MotionState.RESTLESS );
            wifiMotionState = MotionState.RESTLESS;
         }
      }
      logger.debug( logPrefix + "WIFI: MOTION STATE: " + location.getMotionState() );
   }


   /**
    * Extracts a track of at least 5 gps positions and uses this to determine motion state
    */
   private void getMotionStateFromRecentGpsQueries()
   {
      gpsTrack = new ArrayList<Position>();
      ArrayList<Long> timestamps = new ArrayList<Long>();
      long t1 = System.currentTimeMillis();
      long t0 = t1;

      for (LocationQuery q : queries)
      {

         if (gpsTrack.size() < 5 && t1 - q.getTime() <= GPS_POSITION_MAX_AGE)
         {

            if (q.getLatitude() != 0 || q.getLongitude() != 0)
            {

               if (q.getTime() < t0)
               {

                  t0 = q.getTime();

               }

               Position p = new Position();
               p.setLatitude( q.getLatitude() );
               p.setLongitude( q.getLongitude() );
               p.setAccuracy( q.getAccuracy() );
               gpsTrack.add( p );
               timestamps.add( q.getTime() );
               logger.debug( logPrefix + "GPS: " + p + " (" + ( t1 - q.getTime() ) / 1000
                             + " secs ago)" );

            }

         }

      }

      // determine motion

      if (gpsTrack.size() == 1)
      {

         location.setMotionState( Location.MotionState.RESTLESS );

      }

      if (gpsTrack.size() > 1)
      {

         double totDist = 0;
         double totDirChange = 0;
         int totDirChanges = 0;

         for (int i = 1; i < gpsTrack.size(); i++)
         {

            Point c1 = gpsTrack.get( i - 1 );
            Point c2 = gpsTrack.get( i );
            totDist += c1.getDistanceTo( c2 );

            if (i < gpsTrack.size() - 1)
            {

               Point c3 = gpsTrack.get( i + 1 );
               double dir1 = c1.getDirectionTo( c2 );
               double dir2 = c2.getDirectionTo( c3 );
               double delta = dir2 - dir1;

               if (delta < 0)
                  delta = -delta;
               if (delta > 180)
                  delta = 360 - delta;

               totDirChange += delta;
               totDirChanges++;

            }

         }

         double dt = ( t1 - t0 ) / 1000;
         double speed = totDist / dt;
         double bounciness = ( totDirChange / totDirChanges ) / 1.80;

         if (speed >= 10)
         {

            // high speed: two fixes are enough
            location.setMotionState( Location.MotionState.MOVING );

         }

         else if (speed >= 2 && gpsTrack.size() >= 3 && bounciness <= 25)
         {

            // low speed: need to check bounciness (three fixes required)
            location.setMotionState( Location.MotionState.MOVING );

         }

         else if (gpsTrack.size() >= 3 && speed <= 0.5)
         {

            // very low speed and at least three fixes needed to be
            // stationary
            location.setMotionState( Location.MotionState.STATIONARY );

         }

         else
         {

            location.setMotionState( Location.MotionState.RESTLESS );

         }

         logger.debug( logPrefix + "GPS: time         : " + (int) ( dt / 60 ) + " min" );
         logger.debug( logPrefix + "GPS: dist         : " + (int) totDist + " m ("
                       + gpsTrack.size() + " fixes)" );
         logger.debug( logPrefix + "GPS: speed        : " + (int) speed + " m/s ("
                       + (int) ( speed * 3.6 ) + "km/h)" );
         logger.debug( logPrefix + "GPS: bounciness   : " + (int) bounciness + " %" );
         logger.debug( logPrefix + "GPS: MOTION STATE : " + location.getMotionState() );

      }
   }


   /**
    * Gets the current location for the current user from the database
    */
   private void getOldLocationFromDatabase()
   {
      try
      {
         oldLocation = lDB.getCurrentLocation( user.getId() );
         logger.debug( logPrefix + "Old Location: " + oldLocation );
      }
      catch (SQLException e)
      {
         logger.error( logPrefix + "Failed to get current location for user: "
                       + e.getMessage() );
      }

   }


   /**
    * @return Returns the old location (from last call to <code>getLocation()</code> of
    *         the current user)
    */
   private Location getOldLocationObject()
   {
      return this.oldLocation;
   }


   /**
    * Finds the place with the best pattern match from the current locations of all users
    * that has seen one or more of the same bluetooth beacons as the current user the last
    * 5 minutes
    */
   private void getPlaceFromFriendsInBluetoothRange()
   {
      logger.debug( logPrefix + "SELECT: BT: beacons seen the last "
                    + ( BLUETOOTH_BEACON_MAX_AGE / 60000 ) + " min:" );
      ArrayList<Beacon> btBeacons = new ArrayList<Beacon>();
      ArrayList<Integer> btBeaconIds = new ArrayList<Integer>();

      // get beacon logs for the last five minutes
      long time = System.currentTimeMillis();

      for (LocationQuery q : queries)
      {
         if (time - q.getTime() <= BLUETOOTH_BEACON_MAX_AGE)
         {

            for (Beacon b : q.getBeacons( Beacon.Type.BLUETOOTH ))
            {
               if (!btBeaconIds.contains( b.getId() ))
               {
                  btBeacons.add( b );
                  btBeaconIds.add( b.getId() );
               }
            }
         }
      }

      // get user ids of everyone who have recently logged the same bluetooth beacons
      ArrayList<Integer> userIds = new ArrayList<Integer>();

      for (Beacon bt : btBeacons)
      {

         logger.debug( logPrefix + "SELECT: BT:    " + bt + "(ID " + bt.getId() + ")" );
         if (bt.getId() > 0)
         {

            Collection<Integer> ids;
            try
            {
               ids =
                  lDB.getUsersNearBeacon( bt.getId(), time - BLUETOOTH_BEACON_MAX_AGE,
                     time );
            }
            catch (SQLException e)
            {
               ids = new ArrayList<Integer>();
               logger
                  .error( logPrefix
                          + "SELECT: BT: Failed to get users that recently saw beacon with id "
                          + bt.getId() + ": " + e.getMessage() );
            }

            for (int id : ids)
            {
               if (!userIds.contains( id ) && id != user.getId())
                  userIds.add( id );
            }
         }

      }

      if (userIds.size() == 0)
      {
         logger.debug( logPrefix + "SELECT: BT: no users in range" );
      }

      // get current place off all users in range
      // int synchronizedUserId = -1;
      Location synchedLocation = null;

      for (int id : userIds)
      {

         try
         {
            LocationUser u = lDB.getLocationUser( id );
            Location l = lDB.getCurrentLocation( id );

            if (l != null)
            {

               logger.debug( logPrefix + "SELECT: BT: user in range: " + u + "("
                             + l.getLabel() + ")" );

               if (l.getPlaceId() < 0)
               {

                  logger.debug( logPrefix
                                + "SELECT: BT: ignoring location with undefined place: "
                                + l.getLabel() );
               }

               else if (!l.isPlaceFix())
               {

                  logger
                     .debug( logPrefix
                             + "SELECT: BT: ignoring location with place nearby only: "
                             + l.getLabel() + " (" + l.getPatternMatch() + ")" );
               }

               else if (lDB.isPrivatePlace( l.getPlaceId() ))
               {

                  logger.debug( logPrefix
                                + "SELECT: BT: ignoring location with private place: "
                                + l.getLabel() );

               }

               else if (synchedLocation == null
                        || l.getAccuracy() < synchedLocation.getAccuracy())
               {

                  bestPlaceMatch = new BeaconPatternMatch();
                  bestPlaceMatch.setPlace( lDB.getPlace( l.getPlaceId() ) );
                  bestPlaceMatch.setPatternId( l.getPatternId() );
                  bestPlaceMatch.setPatternMatch( l.getPatternMatch() );
                  placeSource = PlaceSource.BT;
                  synchedLocation = l;
                  logger.debug( logPrefix + "SELECT: BT: place synch'ed to " + u + " @ "
                                + bestPlaceMatch.getPlace().getName() );

               }
            }
         }
         catch (SQLException e)
         {
            logger.error( logPrefix + "SELECT: BT: Failed to synch to user with id " + id
                          + ": " + e.getMessage() );
         }
      }
   }


   /**
       * 
       */
   private void getPositionOfNeverBeforeSeenCellsFromGoogle()
   {
      // check that the last logged cell has a coordinate, if not try to get from google
      // gears location API
      if (queries.size() == 0)
         return;

      // String mccCountry = null;

      LocationQuery mostRecentQuery = queries.get( 0 );
      Collection<Beacon> lastCells = mostRecentQuery.getBeacons( Beacon.Type.CELL );
      if (lastCells.size() > 0)
      {
         for (Beacon b : lastCells)
         {
            if (b.getPositionSource() != Beacon.PositionSource.GOOGLE)
            {

               // check if beacon is already cached
               Position p = null;
               try
               {
                  p = lDB.getCachedBeaconPosition( b.getId() );
               }
               catch (Exception e)
               {
                  logger
                     .error( logPrefix
                             + "Failed to get cached beacon position info from 'beacon_info_3rdparty'" );
               }

               // if it is, restore cached data if valid
               if (p != null)
               {
                  if (!p.isDefault())
                  {
                     // check that data is correct
                     try
                     {
                        // sanity check the data, it might have been ignored for a
                        // reason
                        CountryCode cc1 = b.getCountryCode();
                        if (cc1 == null)
                        {
                           throw new RuntimeException( "Beacon with ID " + b.getId()
                                                       + " and MAC " + b.getMac()
                                                       + " has no MCC. That is very odd." );
                        }
                        CountryCode cc2 = null;
                        GeneralLocation gc2 = getGeneralLocation( p );
                        if (gc2 != null)
                           cc2 = gc2.getCountryCode();

                        if (cc1.equals( cc2 ))
                        {
                           b.setPositionSource( Beacon.PositionSource.GOOGLE );
                           b.setLatitude( p.getLatitude() );
                           b.setLongitude( p.getLongitude() );
                           b.setRange( p.getAccuracy() );
                           logger.debug( logPrefix
                                         + "GEO: Restored beacon position from cache:" );
                           logger.debug( logPrefix + "GEO: " + b.getType() + " "
                                         + b.getMac() + ": " + p );
                           lDB.addOrUpdateBeacon( b );
                        }
                        else
                        {
                           String cc1Name = null;
                           String cc2Name = null;
                           if (cc1 != null)
                              cc1Name = cc1.getEnglishCountryName();
                           if (cc2 != null)
                              cc2Name = cc2.getEnglishCountryName();
                           String msg =
                              String
                                 .format(
                                    "Incorrect beacon data: ID %10d, MAC %20s has MCC %s (%s) but stored in 'beacon_info_3rdparty' with country code %s (%s). This is obviously wrong. If you think that google may have better data on this beacon now. simply delete the corresponding row, and it will be re-queried next time a bc user is in this cell. (%s)",
                                    b.getId(), b.getMac(), cc1, cc1Name, cc2, cc2Name,
                                    logPrefix );

                           logger.warn( msg );

                        }
                     }
                     catch (NullPointerException e)
                     {
                        logger
                           .error( logPrefix
                                   + "Could not restore cached beacon position: NullPointerException: " );
                        for (StackTraceElement ste : e.getStackTrace())
                        {
                           logger.error( logPrefix + " " + ste );
                        }
                     }
                     catch (Exception e)
                     {
                        logger.error( logPrefix
                                      + "Could not restore cached beacon position: "
                                      + e.getMessage() );
                     }

                  }
               }
               else
               {

                  // get coordinates and range from cell tower position service
                  try
                  {

                     p =
                        cellPositionService.getPosition( b.getMcc(), b.getMnc(), b
                           .getLac(), b.getCid() );

                     // add result to cache so that we won't query the service again
                     // for this beacon any time soon
                     lDB.addBeacon3drPartyInfo( b.getId(), Beacon.PositionSource.GOOGLE,
                        p.getLatitude(), p.getLongitude(), (int) p.getAccuracy() );

                     if (!p.isDefault())
                     {

                        // sanity check the data
                        CountryCode cc1 = b.getCountryCode();
                        CountryCode cc2 = null;
                        GeneralLocation gc2 = getGeneralLocation( p );
                        if (gc2 != null)
                           cc2 = gc2.getCountryCode();

                        // if country codes match, keep the data
                        if (cc1.equals( cc2 ))
                        {
                           b.setPositionSource( Beacon.PositionSource.GOOGLE );
                           b.setLatitude( p.getLatitude() );
                           b.setLongitude( p.getLongitude() );
                           b.setRange( p.getAccuracy() );
                           logger.debug( logPrefix + "GEO: New cell pos: " + b.getMac()
                                         + " (" + b.getLatitude() + ", "
                                         + b.getLongitude() + "), range " + b.getRange()
                                         + "m" );
                           logger.debug( logPrefix + "GEO New cell pos cc: "
                                         + cc1.getEnglishCountryName() );

                           lDB.addOrUpdateBeacon( b );
                        }

                        // otherwise, log and ignore
                        else
                        {
                           logger
                              .info( logPrefix
                                     + "GEO: Country code '"
                                     + cc2
                                     + "' from Google position and geonames lookup does not match country code from MCC '"
                                     + b.getCountryCode()
                                     + "'. Beacon position not added to table 'beacons'!" );

                        }

                     }
                     else
                     {
                        logger.debug( logPrefix + "GEO: No position for '" + b
                                      + "' found by Google" );
                     }

                  }
                  catch (Exception e)
                  {
                     logger.error( logPrefix + "GEO: Failed to update " + b
                                   + "with Google gears info: " + e.getMessage() );
                  }
               }
            }
         }
      }
   }


   /**
    * Fetches the recent location queries for the current user from the database
    */
   private void getUsersRecentLocationQueriesFromDatabase()
   {
      long maxAge =
         Math.max( Math.max( CELL_BEACON_MAX_AGE, WIFI_BEACON_MAX_AGE ),
            GPS_POSITION_MAX_AGE );
      try
      {
         queries = lDB.getLocationQueries( user.getId(), maxAge );
         if (queries.size() > 0)
         {
            logger.debug( logPrefix
                          + "Oldest query: "
                          + ( System.currentTimeMillis() - queries.get(
                             queries.size() - 1 ).getTime() ) / 1000 + " secs" );
            logger.debug( logPrefix + "Newest query: "
                          + ( System.currentTimeMillis() - queries.get( 0 ).getTime() )
                          / 1000 + " secs" );

         }
      }
      catch (SQLException e)
      {
         logger.error( logPrefix + "Failed to get recent queries from database: "
                       + e.getMessage() );
      }
   }


   /**
    * Accessor for wifi pattern corresponding to last location result
    * 
    * @return The cell pattern
    */
   public BeaconPattern getWifiPattern()
   {
      if (location == null)
      {
         throw new RuntimeException(
            "getLocation(userId) must be called before a wifi pattern can be returned." );
      }
      return wifiPattern;
   }


   /**
    * @return Returns true if a cell pattern with one or more beacons exists
    */
   private boolean hasCellPattern()
   {
      if (cellPattern == null)
         return false;
      if (cellPattern.getElements() == null)
         return false;
      if (cellPattern.getElements().size() == 0)
         return false;
      return true;
   }


   /**
    * @return True if two most recent queries has at least one wifi beacon
    */
   private boolean hasCurrentWifiQueries()
   {
      if (queries.size() == 0)
      {
         return false;
      }
      LocationQuery mostRecent = queries.get( 0 );
      if (System.currentTimeMillis() - mostRecent.getTime() <= WIFI_BEACON_MAX_AGE
          && mostRecent.getBeacons( Beacon.Type.WIFI ).size() > 0)
         return true;

      return false;
   }


   /**
    * @return True if the current location has been asigned at least country level general
    *         location
    */
   private boolean hasGeneralLocation()
   {
      return location.getCountryCode() != null;
   }


   /**
    * @return returns true if the current location contains no region, city or area
    *         information
    */
   private boolean hasGeneralLocationCountryOnly()
   {
      return location != null && location.getRegion() == null
             && location.getCity() == null && location.getArea() == null;
   }


   /**
    * @return Returns true if a gps track with at least one position exists
    */
   private boolean hasGpsTrack()
   {
      if (gpsTrack == null)
         return false;
      if (gpsTrack.size() == 0)
         return false;
      return true;
   }


   /**
    * @return true if the new location is different from the previous location of the
    *         current user
    */
   public boolean hasLocationChangedSinceLastQuery()
   {
      return locationChangedSinceLastQuery;
   }


   /**
    * @return True if the old location (from previous getLocation() call for this user) is
    *         not null
    */
   private boolean hasOldLocation()
   {
      if (oldLocation == null)
      {
         logger.debug( logPrefix + "No old location from previous query" );
         return false;
      }
      else
         return true;
   }


   /**
    * @return returns true if the location result from last location query contains a
    *         latitude and longitude
    */
   private boolean hasOldLocationPosition()
   {
      return oldLocation != null && !oldLocation.getPosition().isDefault();
   }


   /**
    * @return True if either latitude or longitude is not 0.0
    */
   private boolean hasPosition()
   {
      return location != null
             && ( location.getLatitude() != 0 || location.getLongitude() != 0 );
   }


   // /**
   // * @return Returns true if any place references were found from current cell pattern
   // */
   // private boolean hasCellPlaceCandidates()
   // {
   // return cellPatternMatches != null && cellPatternMatches.size() > 0;
   // }

   /**
    * @return True if at least one of the two most recent queries has at least one
    *         bluetooth beacon
    */
   private boolean hasRecentBluetoothQueries()
   {
      if (queries.size() == 0)
         return false;
      LocationQuery mostRecent = queries.get( 0 );
      if (System.currentTimeMillis() - mostRecent.getTime() <= BLUETOOTH_BEACON_MAX_AGE
          && mostRecent.getBeacons( Beacon.Type.BLUETOOTH ).size() > 0)
         return true;

      if (queries.size() < 2)
         return false;
      LocationQuery secondMostRecent = queries.get( 1 );
      if (System.currentTimeMillis() - mostRecent.getTime() <= BLUETOOTH_BEACON_MAX_AGE
          && secondMostRecent.getBeacons( Beacon.Type.BLUETOOTH ).size() > 0)
         return true;

      return false;
   }


   /**
    * @return True if any of the recent queries has at least one cell beacon
    */
   private boolean hasRecentCellQueries()
   {
      boolean found = false;
      for (LocationQuery q : queries)
      {
         found = q.getBeacons( Beacon.Type.CELL ).size() > 0;
         if (found)
            break;
      }
      if (!found)
      {
         logger
            .warn( logPrefix
                   + "CELL: No recent cell queries. Check that this is consitnet with logged beacons (there might be a bug here)" );
         logger.warn( logPrefix + "CELL: Number of queries: " + queries.size() );
         for (LocationQuery q : queries)
         {
            logger.warn( logPrefix + "CELL: Query at "
                         + ( System.currentTimeMillis() - q.getTime() ) / 1000
                         + " secs ago: " + q.getBeacons().size() + " beacons ("
                         + q.getBeacons( Beacon.Type.CELL ).size() + " cell)" );
         }
      }
      return found;
   }


   // /**
   // * @return Returns true if any place references were found from current cell pattern
   // */
   // private boolean hasGpsPlaceCandidates()
   // {
   // return gpsPatternMatches != null && gpsPatternMatches.size() > 0;
   // }

   // /**
   // * @return Returns true if the old location has coordinates and accuracy is equal or
   // * better than the new location
   // */
   // private boolean hasOldLocationEqualOrBetterAccuracy()
   // {
   // boolean b = true;
   // if (oldLocation == null)
   // {
   // b = false;
   // }
   // else if (oldLocation.getLatitude() == 0 && oldLocation.getLongitude() == 0
   // && ( location.getLatitude() != 0 || location.getLongitude() != 0 ))
   // {
   // b = false;
   // logger.debug( logPrefix + "Old location had no position, new has" );
   // }
   // else if (location.getAccuracy() < oldLocation.getAccuracy())
   // {
   // b = false;
   // logger.debug( logPrefix + "New location has better position accuracy ("
   // + location.getAccuracy() + " < " + oldLocation.getAccuracy() + ")" );
   // }
   //
   // else
   // {
   // logger.debug( logPrefix + "Old location has equal or better position accuracy." );
   // }
   // return b;
   // }

   /**
    * @return True if at least one of the two most recent queries has at least one GPS
    *         coordinate
    */
   private boolean hasRecentGpsQueries()
   {
      if (queries.size() == 0)
         return false;

      long t1 = System.currentTimeMillis();
      boolean b = false;
      for (LocationQuery q : queries)
      {

         if (t1 - q.getTime() <= GPS_POSITION_MAX_AGE)
         {

            if (q.getLatitude() != 0 || q.getLongitude() != 0)
            {
               b = true;

            }

         }

      }
      return b;

   }


   /**
    * @return Returns true if a non-empty set of recent location queries exist for the
    *         current user
    */
   private boolean hasRecentQueries()
   {
      boolean b = queries != null && queries.size() > 0;
      if (!b)
      {
         logger
            .info( logPrefix
                   + "No location queries seen recently. Cannot compute new location." );
      }
      return b;
   }


   /**
    * @return True if at least one of the two most recent queries has at least one wifi
    *         beacon
    */
   private boolean hasRecentWifiQueries()
   {
      if (queries.size() == 0)
      {
         return false;
      }
      LocationQuery mostRecent = queries.get( 0 );
      if (System.currentTimeMillis() - mostRecent.getTime() <= WIFI_BEACON_MAX_AGE
          && mostRecent.getBeacons( Beacon.Type.WIFI ).size() > 0)
         return true;

      if (queries.size() < 2)
         return false;
      LocationQuery secondMostRecent = queries.get( 1 );
      if (System.currentTimeMillis() - secondMostRecent.getTime() <= WIFI_BEACON_MAX_AGE
          && secondMostRecent.getBeacons( Beacon.Type.WIFI ).size() > 0)
         return true;

      return false;
   }


   /**
    * @return returns true if wifi pattern is null or has no beacons
    */
   private boolean hasWifiPattern()
   {
      if (wifiPattern == null)
         return false;
      if (wifiPattern.getElements() == null)
         return false;
      if (wifiPattern.getElements().size() == 0)
         return false;
      return true;
   }


   /**
    * @return Returns true if the country code is same as that from last result, and
    *         neither is null
    */
   private boolean isCountrySameAsOldCountry()
   {
      return location.getCountryCode() != null && location.getCountryCode() != null
             && oldLocation != null
             && location.getCountryCode().equals( oldLocation.getCountryCode() );
   }


   /**
    * @return Returns true if a location to at least country level accuracy was found
    */
   private boolean isLocationFound()
   {
      if (location.getCountryCode() == null)
      {
         logger.error( logPrefix + " No location could be found. Returning null" );
         location = null;
         return false;
      }
      return true;
   }


   /**
    * @return True if current location motion state is MOVING
    */
   private boolean isMoving()
   {
      return location.getMotionState() == Location.MotionState.MOVING;
   }


   /**
    * @return True if only one place was found from current cell pattern
    */
   private boolean isOnlyOneCellPlaceFound()
   {
      return cellPatternMatches.size() == 1;
   }


   // /**
   // * @return Returns true if any place references were found from current cell pattern
   // */
   // private boolean hasWifiPlaceCandidates()
   // {
   // return wifiPatternMatches != null && wifiPatternMatches.size() > 0;
   // }

   /**
    * @return Returns true if the best place match is less than 10 points better than the
    *         second best, and the second best is equal to the old location
    */
   private boolean isPlaceAlmostIndistinguishableFromOldLocation()
   {
      if (bestPlaceMatch != null && oldLocation != null && secondBestPlaceMatch != null
          && secondBestPlaceMatch.getPlace().getId() == oldLocation.getPlaceId())
      {
         int margin =
            bestPlaceMatch.getPatternMatch() - secondBestPlaceMatch.getPatternMatch();
         if (margin < 10)
         {
            logger.debug( logPrefix + " Best place macth '"
                          + bestPlaceMatch.getPlace().getName()
                          + "' is only marginally better (" + margin
                          + " points) than 2nd best '"
                          + secondBestPlaceMatch.getPlace().getName()
                          + "' which was previous best match." );
            return true;
         }
         else
            return false;
      }
      else
      {
         return false;
      }
   }


   /**
    * @return Returns true if motion state is STATIONARY and pattern match is >=
    *         LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH
    */
   private boolean isPlaceFix()
   {
      return location.isPlaceFix();
   }


   /**
    * @return True if place is not null
    */
   private boolean isPlaceFound()
   {
      return  bestPlaceMatch != null && bestPlaceMatch.getPlace() != null;
   }


   /**
    * @return true if the current place was found by cell pattern
    */
   private boolean isPlaceFoundFromCellPattern()
   {
      if (placeSource == PlaceSource.CELL)
      {
         return true;
      }
      else
      {
         return false;
      }
   }


   /**
    * Returns true if the place of the old location is still among the current cell places
    */
   private boolean isPlaceFromLastResultStillReferencedByCurrentCellPattern()
   {

      for (BeaconPatternMatch m : cellPatternMatches)
      {
         if (oldLocation != null && m.getPlace().getId() == oldLocation.getPlaceId())
         {
            return true;
         }
      }

      return false;

   }


   /**
    * @return True if either the latitude or the longitude of the current place is
    *         non-zero
    */
   private boolean isPlacePositioned()
   {
      boolean b =
         bestPlaceMatch.getPlace().getLatitude() != 0
            || bestPlaceMatch.getPlace().getLongitude() != 0;

      return b;
   }


   /**
    * @return True if the current place id is the same as that of the last location result
    *         of this user
    */
   private boolean isPlaceSameAsLastLocationResult()
   {
      if (bestPlaceMatch == null)
         return false;
      if (oldLocation == null)
         return false;
      boolean b = bestPlaceMatch.getPlace().getId() == oldLocation.getPlaceId();
      if (b)
      {
         logger.debug( logPrefix + "Place is same as result for last query." );
      }
      else
      {
         logger.debug( logPrefix + "Place is not same as result for last query." );
      }
      return b;
   }


   /**
    * @return True if the current user position was derived from cell towers
    */
   private boolean isPositionSourceCell()
   {
      if (positionSource == PositionSource.CELL)
         return true;
      return false;
   }


   // /**
   // * @return true if the current place was found by wifi pattern or GPS position
   // */
   // private boolean isPlaceFoundFromWifiOrGps()
   // {
   // logger.debug( logPrefix + "Place source: " + placeSource );
   // if (placeSource == PlaceSource.GPS || placeSource == PlaceSource.WIFI)
   // {
   // return true;
   // }
   // else
   // {
   // return false;
   // }
   // }

   /**
    * @return True if the current location motion state is RESTLESS
    */
   private boolean isRestless()
   {
      return location.getMotionState() == Location.MotionState.RESTLESS;
   }


   /**
    * @return True if the current location motion state is STATIONARY
    */
   private boolean isStationary()
   {
      return location.getMotionState() == Location.MotionState.STATIONARY;
   }


   /**
    * Check that the user still subscribes to the place he was at last query
    * 
    * @return true if still a subscribe, otherwise false
    */
   private boolean isStillSubscribingToPlaceOfLastresult()
   {
      boolean isSubscriber;
      try
      {
         isSubscriber = lDB.isPlaceSubscriber( user.getId(), oldLocation.getPlaceId() );
      }
      catch (Exception e)
      {
         logger.error( logPrefix
                       + "Failed to verify that  user still subscribes to place '"
                       + oldLocation.getPlaceName() + "'" );
         isSubscriber = true;
      }
      if (!isSubscriber)
      {
         logger.debug( logPrefix
                       + "User no longer subscribes to place of old location: '"
                       + oldLocation.getPlaceName() + "'" );
      }
      return isSubscriber;
   }


   /**
    * @return True if the user object is not null and the user ID is 1 or higher
    */
   private boolean isUserObjectValid()
   {
      boolean valid = user != null && user.getId() > 0;
      if (valid)
      {
         logPrefix = user + ": ";
      }
      else
      {
         logger.error( "User is null or has zero or negative user ID" );
      }
      return valid;
   }


   /**
    * @return
    */
   private boolean isWifiMotionStateStationary()
   {
      return wifiMotionState != null && wifiMotionState == MotionState.STATIONARY;
   }


   /**
    * Logs the supplied string as info, prefixed with "$user: LOGIC: "
    * 
    * @param msg
    *           The message to be logged
    */
   private void logicLog(String msg)
   {
      logger.info( user + ": LOGIC: " + msg );
   }


   /**
    * @return The number of minutes the user has been at the current place (or 0 of not at
    *         a place)
    */
   private int minutesAtPlace()
   {
      int min = 0;
      if (oldLocation != null
          && bestPlaceMatch.getPlace().getId() == oldLocation.getPlaceId())
      {
         long millis = System.currentTimeMillis() - oldLocation.getEntryTime();
         min = (int) ( millis / 60000 );
      }
      logger.debug( logPrefix + "Time at place: " + min + " min" );
      return min;
   }


   // /**
   // * Sets the accuracy of the location object to the specified value
   // *
   // * @param accuracy
   // * The new accuracy value
   // */
   // private void overrideAccuracy(int accuracy)
   // {
   // location.setAccuracy( accuracy );
   // logger.debug( logPrefix + "Setting accuracy to " + accuracy + "m" );
   //
   // }

   /**
    * Multiplies the accuracy of the location object with the specified factor
    * 
    * @param factor
    *           The multiplication factor
    */
   private void multiplyAccuracy(int factor)
   {
      location.setAccuracy( location.getAccuracy() * factor );
      logger.debug( logPrefix + "Multiplying accuracy with " + factor + " -> "
                    + location.getAccuracy() + "m" );

   }


   /**
    * Sets the motion state of the current location object to RESTLESS
    */
   private void overrideMotionStateToRestless()
   {
      location.setMotionState( Location.MotionState.RESTLESS );

   }


   /**
    * Sets the beacon pattern match of the current location object to 100%
    */
   private void overridePlacePatternMatchToMax()
   {
      location.setPatternMatch( 100 );

   }


   /**
    * Checks all wifi pattern match places that they are located in same country or within
    * max (10 km or user position accuracy) of the users position and removes them from
    * list of candidates if not. This should filter out false matches found by non-unique
    * wifi mac addresses (e.g. all starbucks' using access points with identical macs)
    */
   private void removeFalsePlaceCandidatesFromNonUniqueWifiMacs()
   {
      Position userPos =
         new Position( location.getLatitude(), location.getLongitude(), location
            .getAccuracy() );
      ArrayList<BeaconPatternMatch> okMatches = new ArrayList<BeaconPatternMatch>();
      for (BeaconPatternMatch m : wifiPatternMatches)
      {
         Point placePos =
            new Point( m.getPlace().getLatitude(), m.getPlace().getLongitude() );
         boolean ok = true;
         CountryCode c1 = location.getCountryCode();
         CountryCode c2 = m.getPlace().getCountryCode();
         if (c1 != null & c2 != null && !c1.equals( c2 ))
         {
            logger.debug( logPrefix + "Place '" + m.getPlace().getName()
                          + "' is located in different country (" + c2
                          + "). Probably from a non-unique mac. Removed." );
            ok = false;
         }
         if (ok && !userPos.isDefault() && !placePos.isDefault())
         {
            double d = userPos.getDistanceTo( placePos );
            double maxDist =
               Math.max( userPos.getAccuracy(), MAX_WIFI_PLACE_TOUSER_POS_DISTANCE );
            if (d > maxDist)
            {
               logger.debug( logPrefix + "Place '" + m.getPlace().getName() + "' is "
                             + (int) ( d / 1000 ) + "km away. User pos accuracy is "
                             + userPos.getAccuracy() / 1000
                             + "km. Probably from a non-unique mac. Removed." );
               ok = false;
            }
         }
         if (ok)
         {
            okMatches.add( m );
         }
      }
      wifiPatternMatches = okMatches;

   }


   /**
    * Creates a new location object and resets all assisting variables
    */
   private void resetObjects(LocationUser user)
   {
      this.user = user;
      location = new Location();
      bestPlaceMatch = null;
      gpsTrack = null;
      cellPattern = null;
      cellPatternMatches = null;
      gpsPatternMatches = null;
      gpsPosition = null;
      logPrefix = null;
      oldLocation = null;
      queries = null;
      wifiPattern = null;
      wifiPatternMatches = null;
      cellCountryCode = null;
      placeSource = null;
      positionSource = null;
      locationChangedSinceLastQuery = false;

   }


   /**
    * Sets the current location object equal to the last location result of the current
    * user
    */
   private void reuseLastLocationResult()
   {

      location = oldLocation;
      locationChangedSinceLastQuery = false;
      // set position to something else than CELL to avoid it falsely being used to update
      // beacon positions
      // TODO Remove position source altogether as only cell positions shall be used
      positionSource = PositionSource.GENLOC;

   }


   /**
    * Selects the place equal to the most recently manually set place
    * 
    * @param user
    *           The user for which to select a place
    * @param candidates
    *           The candidate places to select from
    * @return The selected place or null if no match found
    */
   private BeaconPatternMatch selectPlaceEqualToLastManualOverride(
      Collection<BeaconPatternMatch> candidatePlaces)
   {

      if (candidatePlaces == null)
         throw new IllegalArgumentException( "candidates collection cannot be null" );

      BeaconPatternMatch selected = null;

      try
      {
         // get the id of the most recently manually set place, limited to the last 6 hrs
         // (-1 if none)
         int manuallySetPlaceId =
            lDB.getMostRecentManualPlaceOverride( user.getId(),
               MANUAL_OVERRIDE_EXPIRATION_HRS * 3600L * 1000L );
         if (manuallySetPlaceId > 0)
         {
            logger.debug( logPrefix + "SELECT (MANU): Last manually set place id = "
                          + manuallySetPlaceId );

            // Make sure user is subscriber, could have removed after setting manually
            boolean isSubscriber =
               lDB.isPlaceSubscriber( user.getId(), manuallySetPlaceId );

            if (isSubscriber)
            {

               for (BeaconPatternMatch m : candidatePlaces)
               {
                  if (m.getPlace().getId() == manuallySetPlaceId)
                  {
                     selected = m;
                     logger
                        .info( logPrefix
                               + "SELECT (MANU): Place selected from recently manually set: "
                               + selected.getPlace().getId() );
                  }
               }
            }
            else
            {
               logger
                  .info( logPrefix + "SELECT (MANU): Last manually set place (ID "
                         + manuallySetPlaceId + ") was unsubscribed to. Not selected." );
            }
         }
         else
         {
            logger.debug( logPrefix + "SELECT (MANU): No manually set places the last "
                          + MANUAL_OVERRIDE_EXPIRATION_HRS + " hrs." );
         }
      }
      catch (Exception e)
      {
         logger.error( "Failed to check for recently manually set places", e );
      }

      return selected;
   }


   /**
    * Selects the place from the list of CELL places that is identical to the most
    * recently manually set place by this user (if any)
    */
   private void selectPlaceEqualToLastManualOverrideFromCellPattern()
   {
      bestPlaceMatch = selectPlaceEqualToLastManualOverride( cellPatternMatches );
      if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
      {
         placeSource = PlaceSource.CELL;
      }
   }


   /**
    * Selects the place from the list of GPS places that is identical to the most recently
    * manually set place by this user (if any)
    */
   private void selectPlaceEqualToLastManualOverrideFromGpsPosition()
   {
      bestPlaceMatch = selectPlaceEqualToLastManualOverride( gpsPatternMatches );
      if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
      {
         placeSource = PlaceSource.GPS;
      }

   }


   /**
    * Selects the place from the list of WIFI places that is identical to the most
    * recently manually set place by this user (if any)
    */
   private void selectPlaceEqualToLastManualOverrideFromWifiPattern()
   {
      bestPlaceMatch = selectPlaceEqualToLastManualOverride( wifiPatternMatches );
      if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
      {
         placeSource = PlaceSource.WIFI;
      }
   }


   /**
    * Selects a place from the supplied list whose name is similar to the user's currently
    * set 'next location'
    * 
    * @param user
    *           The user for which to select a place
    * @param candidates
    *           The candidate places to select from
    * @return The selected place or null if no match found
    */
   private BeaconPatternMatch selectPlaceFromCurrentlySetNextLocation(
      Collection<BeaconPatternMatch> candidates)
   {
      String nextLocation = null;
      try
      {
         nextLocation = lDB.getNextLocation( user.getId() );
      }
      catch (SQLException e)
      {
         logger.error( logPrefix + "SELECT (NEXT): Failed to get next place" );
      }

      // if no next place there is nothing we can do here
      if (nextLocation == null || nextLocation.length() == 0)
      {
         logger.debug( logPrefix + "SELECT (NEXT): No next place set" );
         return null;
      }

      // look for exact matches
      for (BeaconPatternMatch m : candidates)
      {
         String placeName = m.getPlace().getName();
         if (placeName != null && placeName.equals( nextLocation ))
         {

            // boost match by 30% to make it lock on a bit more easily
            // m.setPatternMatch( (int) Math.min( m.getPatternMatch() * 1.3, 100 ) );

            logger.debug( logPrefix + "SELECT (NEXT): Place (" + placeName
                          + ") selected from next place (" + nextLocation + ")" );
            return m;
         }
      }

      // look for equal ignoring case
      for (BeaconPatternMatch m : candidates)
      {
         String placeName = m.getPlace().getName();
         if (placeName != null
             && placeName.toLowerCase().equals( nextLocation.toLowerCase() ))
         {

            logger.debug( logPrefix + "SELECT (NEXT): Place (" + placeName
                          + ") selected from next place (" + nextLocation + ")" );
            return m;
         }
      }

      // look for similar
      for (BeaconPatternMatch m : candidates)
      {
         String placeName = m.getPlace().getName();
         if (placeName != null
             && placeName.toLowerCase().contains( nextLocation.toLowerCase().trim() ))
         {

            logger.debug( logPrefix + "SELECT (NEXT): Place (" + placeName
                          + ") selected from next place (" + nextLocation + ")" );
            return m;
         }
         if (placeName != null
             && nextLocation.toLowerCase().contains( placeName.toLowerCase().trim() ))
         {

            logger.debug( logPrefix + "SELECT (NEXT): Place (" + placeName
                          + ") selected from next place (" + nextLocation + ")" );
            return m;
         }
      }

      logger.debug( logPrefix + "SELECT (NEXT): No places similar to next place '"
                    + nextLocation + "'" );

      // none found, return null
      return null;
   }


   private void selectPlaceMatchingCurrentlySetNextLocationFromCellPattern()
   {
      if (bestPlaceMatch == null)
      {
         bestPlaceMatch = selectPlaceFromCurrentlySetNextLocation( cellPatternMatches );
         if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
         {
            placeSource = PlaceSource.CELL;
         }
      }

   }


   private void selectPlaceMatchingCurrentlySetNextLocationFromGpsPattern()
   {
      if (bestPlaceMatch == null)
      {
         bestPlaceMatch = selectPlaceFromCurrentlySetNextLocation( gpsPatternMatches );
         if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
         {
            placeSource = PlaceSource.GPS;
         }
      }
   }


   private void selectPlaceMatchingCurrentlySetNextLocationFromWifiPattern()
   {
      bestPlaceMatch = selectPlaceFromCurrentlySetNextLocation( wifiPatternMatches );
      if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
      {
         placeSource = PlaceSource.WIFI;
      }
   }


   /**
    * Selects a place to which the user subscribes. If no single subscribed place stands
    * out in terms of place confidence (= pattern match) no place will be selected.
    * 
    * @param user
    *           The user for which to select a place
    * @param candidates
    *           The candidate places to select from
    * @return The best subscribed place or null if none found.
    */
   private void selectSubscribedPlace(Collection<BeaconPatternMatch> candidates,
      PlaceSource source)
   {
      // BeaconPatternMatch bestMatch = null;
      // int secondBestMatch = 0;
      bestPlaceMatch = null;
      secondBestPlaceMatch = null;
      for (BeaconPatternMatch candidate : candidates)
      {
         try
         {
            if (lDB.isPlaceSubscriber( user.getId(), candidate.getPlace().getId() ))
            {
               // check that place is in same region as user (helps to root out false hits
               // from non-unique wifi macs)
               CountryCode cc1 = location.getCountryCode();
               String ci1 = location.getCity();
               String re1 = location.getRegion();
               CountryCode cc2 = candidate.getPlace().getCountryCode();
               String re2 = candidate.getPlace().getRegion();
               String ci2 = candidate.getPlace().getCity();

               if (cc1 != null && cc2 != null && !cc1.equals( cc2 ))
               {
                  logger.debug( logPrefix + "SELECT: Country of place candidate '"
                                + candidate.getPlace().getName() + "' (" + cc2
                                + ") does not match that of users curr loc (" + cc1
                                + "). " );
               }
               else
               {
                  if (bestPlaceMatch == null)
                  {
                     bestPlaceMatch = candidate;
                  }
                  else if (candidate.getPatternMatch() > bestPlaceMatch.getPatternMatch())
                  {
                     secondBestPlaceMatch = bestPlaceMatch;
                     bestPlaceMatch = candidate;
                  }
                  else if (secondBestPlaceMatch != null
                           && candidate.getPatternMatch() > secondBestPlaceMatch
                              .getPatternMatch())
                  {
                     secondBestPlaceMatch = candidate;
                  }
                  if (ci1 != null && ci2 != null && !ci1.equals( ci2 ))
                  {
                     logger.warn( logPrefix + "SELECT: City of place candidate '"
                                  + candidate.getPlace().getName() + "' (" + ci2
                                  + ") does not match that of users curr loc (" + ci1
                                  + "). Potential false fix..." );
                  }
                  else if (re1 != null && re2 != null && !re1.equals( re2 ))
                  {
                     logger.warn( logPrefix + "SELECT: Region of place candidate '"
                                  + candidate.getPlace().getName() + "' (" + re2
                                  + ") does not match that of users curr loc (" + re1
                                  + "). Potential false fix..." );
                  }
               }
            }
         }
         catch (SQLException e)
         {
            logger.error( logPrefix + source
                          + " :SELECT (SUBS): Failed to check place subscription", e );
         }
      }

      if (bestPlaceMatch == null)
      {
         logger.debug( logPrefix + source + " SELECT (SUBS): No subscribed places found" );
      }
      else
      {
         logger.debug( logPrefix + source
                       + " SELECT (SUBS): Place selected from subscription: '"
                       + bestPlaceMatch.getPlace().getName() + "' (ID: "
                       + bestPlaceMatch.getPlace().getId() + "), Pattern match: "
                       + bestPlaceMatch.getPatternMatch() );

         // int margin = bestPlaceMatch.getPatternMatch() - secondBestMatch;
         // if (margin < 10)
         // {
         // logger.warn( logPrefix + source + " SELECT (SUBS): Best match is only "
         // + margin + " points better than the 2nd best. False fix ("
         // + bestMatch.getPlace().getName() + ") probable!" );
         // }
         //
         // return bestMatch;

      }
   }


   /**
    * Selects the place with the best pattern match from the list of CELL places to which
    * the current user is a subscriber (i.e. place is bookmarked)
    */
   private void selectSubscribedPlaceFromCellPattern()
   {
      selectSubscribedPlace( cellPatternMatches, PlaceSource.CELL );
      if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
      {
         placeSource = PlaceSource.CELL;
      }

   }


   /**
    * Selects the place closest to the user's GPS position to which the current user is a
    * subscriber (i.e. place is bookmarked)
    */
   private void selectSubscribedPlaceFromGpsPosition()
   {
      selectSubscribedPlace( gpsPatternMatches, PlaceSource.GPS );
      if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
      {
         placeSource = PlaceSource.GPS;
      }

   }


   /**
    * Selects the place with the best pattern match from the list of placed found both by
    * CELL and WIFI and to which the current user is a subscriber (i.e. place is
    * bookmarked)
    */
   private void selectSubscribedPlaceFromWifiAndCellPattern()
   {
      ArrayList<BeaconPatternMatch> cellAndWifiMatches =
         new ArrayList<BeaconPatternMatch>();
      for (BeaconPatternMatch wm : wifiPatternMatches)
      {
         int pid = wm.getPlace().getId();
         boolean both = false;
         for (BeaconPatternMatch cm : cellPatternMatches)
         {
            if (cm.getPlace().getId() == pid)
            {
               both = true;
            }
         }
         if (both)
         {
            cellAndWifiMatches.add( wm );
         }
      }

      selectSubscribedPlace( cellAndWifiMatches, PlaceSource.WIFI );
      if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
      {
         placeSource = PlaceSource.WIFI;
      }
   }


   /**
    * Selects the place with the best pattern match from the list of WIFI places to which
    * the current user is a subscriber (i.e. place is bookmarked)
    */
   private void selectSubscribedPlaceFromWifiPattern()
   {
      selectSubscribedPlace( wifiPatternMatches, PlaceSource.WIFI );
      if (bestPlaceMatch != null && bestPlaceMatch.getPlace() != null)
      {
         placeSource = PlaceSource.WIFI;
      }
   }


   /**
    * Sets the latitude and longitude of the location equal to that of the place.
    */
   private void setPositionToPlacePosition()
   {
      location.setLatitude( bestPlaceMatch.getPlace().getLatitude() );
      location.setLongitude( bestPlaceMatch.getPlace().getLongitude() );
      if (bestPlaceMatch.getPlace().getAccuracy() < 50)
      {
         location.setAccuracy( 50.0 );
      }
      else
      {
         location.setAccuracy( bestPlaceMatch.getPlace().getAccuracy() );
      }
      positionSource = PositionSource.PLACE;
   }


   /**
    * Overrides location pattern match and motion state such as to fix the location at the
    * current place (no "Near" prefix in label")
    */
   private void snapToPlace()
   {
      if (location.getMotionState() != Location.MotionState.STATIONARY)
      {
         location.setMotionState( Location.MotionState.STATIONARY );
         logger.debug( logPrefix + " overriding motion state to STATIONARY" );
      }
      if (location.getPatternMatch() < LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH)
      {
         location.setPatternMatch( LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH );
         logger.debug( logPrefix + " overriding pattern match to "
                       + LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH );
      }

   }


   /**
    * Stores the current location object to the database as the current user's current
    * location
    */
   private void storeCurrentLocationToDatabase()
   {
      if (location.getCountryCode() == null)
      {
         logger.error( logPrefix + "Location has no country code" );
         logger.error( logPrefix + "  Country code from mcc is " + cellCountryCode );
      }
      try
      {
         lDB.setCurrentLocation( user.getId(), location );
         logger.debug( logPrefix + "Location stored in DB: " + location );
      }
      catch (SQLException e)
      {
         logger.error( logPrefix + "Failed to update current location '"
                       + location.getLabel() + "' in database: " + e.getMessage() );
      }

   }


   private void triangulateCurrentPositionFromPositionedBeacons(
      Collection<Beacon> beacons, double acceptableDeviation)
   {

      // test the position voter monitor...
      if (beacons.size() > 2)
      {
         PositionVoterMonitor<Beacon> vm =
            new PositionVoterMonitor<Beacon>( beacons, acceptableDeviation );
         Collection<Beacon> deviantBeacons = vm.getLoosers();
         for (Beacon beacon : deviantBeacons)
         {
            logger.debug( logPrefix + "TRIANG: " + beacon.getType() + ": "
                          + beacon.getMac() + " (" + beacon.getLatitude() + ", "
                          + beacon.getLongitude() + " @ " + beacon.getRange()
                          + "m): Possible false position" );
         }
      }

      // TODO: Implement this properly, this is just a dummy averaging method
      int n = 0;
      double avgLat = 0;
      double avgLon = 0;
      double minAcc = Location.Layer.COUNTRY.getDefaultError();
      for (Beacon beacon : beacons)
      {
         if (beacon.getLatitude() != 0 || beacon.getLongitude() != 0
             && beacon.getRange() > 0 && beacon.isFixed())
         {
            avgLat += beacon.getLatitude();
            avgLon += beacon.getLongitude();
            if (beacon.getRange() > 0 && beacon.getRange() < minAcc)
            {
               minAcc = beacon.getRange();
            }
            n++;
            logger.debug( logPrefix + "TRIANG: " + beacon.getType() + ": "
                          + beacon.getMac() + " (" + beacon.getLatitude() + ", "
                          + beacon.getLongitude() + " @ " + beacon.getRange() + "m)" );
         }
      }
      if (n > 0)
      {
         avgLat /= n;
         avgLon /= n;
         location.setLatitude( avgLat );
         location.setLongitude( avgLon );
         location.setAccuracy( minAcc );
         logger.debug( logPrefix + "TRIANG: (" + location.getLatitude() + ", "
                       + location.getLongitude() + " @ " + location.getAccuracy() + "m)" );
      }
   }


   /**
    * Triangulates position from the beacons in the current cell pattern
    */
   private void triangulateCurrentPositionFromPositionedCellBeacons()
   {
      ArrayList<Beacon> cells = new ArrayList<Beacon>();
      for (BeaconPatternElement e : cellPattern.getElements())
      {
         cells.add( e.beacon );
      }
      logger.debug( logPrefix + "CELL: TRIANG: " + cells.size() + " beacons" );
      triangulateCurrentPositionFromPositionedBeacons( cells,
         CELL_POSITION_VOTING_GROUP_RADIUS );
      positionSource = PositionSource.CELL;

   }


   /**
    * Triangulates position from the beacons in the current wifi pattern
    */
   private void triangulateCurrentPositionFromPositionedWifiBeacons()
   {
      ArrayList<Beacon> wifis = new ArrayList<Beacon>();
      for (BeaconPatternElement e : wifiPattern.getElements())
      {
         if (e.beacon.isFixed())
         {
            wifis.add( e.beacon );
         }
         else
         {
            logger.debug( logPrefix + "WIFI: TRIANG: Ignoring non-unique beacon "
                          + e.beacon );
         }
      }
      logger.debug( logPrefix + "WIFI: TRIANG: " + wifis.size() + " beacons" );
      triangulateCurrentPositionFromPositionedBeacons( wifis,
         WIFI_POSITION_VOTING_GROUP_RADIUS );
      positionSource = PositionSource.WIFI;
   }


   // /**
   // * Analyses beacon observations and updates latitude, longitude, country, city, area
   // * and isMovable for the beacon with the supplied id
   // *
   // * @param beaconId
   // * The id of the beacon to be verified
   // * @return
   // */
   // private void updateBeaconFromObservations(Beacon beacon)
   // {
   // try
   // {
   // Collection<Point> observationPoints =
   // lDB.getBeaconObservations( beacon.getId(), MAX_GPS_OBSERVATIONS );
   //
   // Position pos = new Position();
   //
   // for (Point p : observationPoints)
   // {
   // // init position to coordinates of first observation point
   // if (pos.isDefault())
   // {
   // pos.setLatitude( p.getLatitude() );
   // pos.setLongitude( p.getLongitude() );
   // }
   // // expand range and shift center if observation point is outside of range
   // else
   // {
   // pos.addPoint( p );
   // }
   // }
   //
   // // update if needed (disregard sub-meter changes)
   // if (pos.getAccuracy() > beacon.getRange() + 1)
   // {
   // logger.debug( logPrefix + beacon + " update needed:" );
   // logger.debug( logPrefix + "   old position: (" + beacon.getLatitude() + ", "
   // + beacon.getLongitude() + "), range " + beacon.getRange() + "m" );
   // beacon.setLatitude( pos.getLatitude() );
   // beacon.setLongitude( pos.getLongitude() );
   // beacon.setRange( pos.getAccuracy() );
   // logger.debug( logPrefix + "   new position: (" + beacon.getLatitude() + ", "
   // + beacon.getLongitude() + "), range " + beacon.getRange() + "m" );
   //
   // try
   // {
   // logger
   // .info( logPrefix + "   old country code: " + beacon.getCountryCode() );
   // GeonamesWebService geonames = new GeonamesWebService();
   // GeneralLocation l = geonames.getGeneralLocation( pos );
   //
   // if (l != null && l.getCountryCode() != null)
   // beacon.setCountryCode( l.getCountryCode() );
   //
   // logger
   // .info( logPrefix + "   new country code: " + beacon.getCountryCode() );
   // }
   // catch (Exception e)
   // {
   // logger.error( logPrefix + "   Failed to get location for " + beacon
   // + " from geonames: " + e.getMessage() );
   // }
   //
   // beacon.setFixed( true );
   // if (beacon.getType() == Beacon.Type.WIFI
   // && beacon.getRange() > Beacon.RANGE_LIMIT_WIFI)
   // {
   // logger.debug( logPrefix + "   Beacon is movable (range = "
   // + (int) beacon.getRange() + " m > " + Beacon.RANGE_LIMIT_WIFI
   // + " m)" );
   // beacon.setFixed( false );
   // }
   //
   // if (beacon.getType() == Beacon.Type.CELL
   // && beacon.getRange() > Beacon.RANGE_LIMIT_CELL)
   // {
   // logger.debug( logPrefix + "   Beacon is movable (range = "
   // + (int) beacon.getRange() + " m > " + Beacon.RANGE_LIMIT_CELL
   // + " m)" );
   // beacon.setFixed( false );
   // }
   //
   // // Note: lDB only updates if beacon is new or contains more info
   // lDB.addOrUpdateBeacon( beacon );
   //
   // }
   //
   // }
   // catch (SQLException e)
   // {
   // logger.error( logPrefix + "Failed to get beacon observations for beacon "
   // + beacon, e );
   // }
   // }

   // /**
   // *
   // */
   // private void updateCountryCodeOfNeverBeforeSeenBeaconsAccordingToMCC()
   // {
   // int mcc = -1;
   // for (LocationQuery q : queries)
   // {
   // for (Beacon b : q.getBeacons( Beacon.Type.CELL ))
   // {
   // if (mcc < 0)
   // {
   // mcc = b.getMcc();
   // cellCountryCode = CountryCode.getInstance( mcc );
   // logger.debug( logPrefix + " Country code from MCC " + mcc + ": "
   // + cellCountryCode + " ("
   // + cellCountryCode.getEnglishCountryName() + ")" );
   // }
   // }
   // }
   //
   // if (mcc >= 0)
   // {
   // try
   // {
   // CountryCode cc = CountryCode.getInstance( mcc );
   // LocationQuery mostRecentQuery = queries.get( 0 );
   // for (Beacon b : mostRecentQuery.getBeacons())
   // {
   // if (b.isFixed())
   // {
   // if (b.getCountryCode() == null || !b.getCountryCode().equals( cc ))
   // {
   // logger.debug( logPrefix + "Updating country code from "
   // + b.getCountryCode() + " to " + cc + " (" + b + ")" );
   // b.setCountryCode( cc );
   // lDB.addOrUpdateBeacon( b );
   // }
   // }
   // }
   // }
   // catch (SQLException e)
   // {
   // logger.error( logPrefix + "Failed to get country name for mcc " + mcc );
   // }
   // }
   //
   // }

   /**
    * Check all wifi beacons seen on last query. If current position has better accuracy
    * than the stored range, or current position and accuracy is outside beacon position
    * and range, set beacon position and range equal to current position and accuracy.
    */
   private void updatePositionAndRangeOfWifiBeaconsUsingCurrentPositionAndAccuracyIfBetterThanStored()
   {
      // if accuracy is very bad, don't do anything
      if (location.getAccuracy() > BEACON_RANGE_LIMIT)
      {
         logger.debug( logPrefix + "Position accuracy is " + location.getAccuracy()
                       + " (bad). Observerd wifi beacon positions not updated." );
         return;
      }
      // don't trust users
      if (positionSource != PositionSource.CELL)
      {
         logger.debug( logPrefix + "Position source is " + positionSource
                       + " (untrusted). Observerd wifi beacon positions not updated." );
         return;
      }

      LocationQuery q = queries.get( 0 );
      Collection<Beacon> wifis = q.getBeacons( Beacon.Type.WIFI );
      Position newPos = location.getPosition();
      logger.debug( logPrefix + "Updating wifi beacon positions to position " + newPos
                    + " derived from " + positionSource );

      // never assume better than 50m accuracy
      if (newPos.getAccuracy() < 50)
      {
         newPos.setAccuracy( 50 );
      }

      for (Beacon b : wifis)
      {
         if (b.isFixed())
         {
            Position oldPos =
               new Position( b.getLatitude(), b.getLongitude(), b.getRange() );
            double dist = oldPos.getDistanceTo( newPos );

            // update if current location accuracy is better than stored range, beacons
            // has no position or if it has moved out of its range (note: this is just a
            // correction, true moveable beacons are caught elsewhere)
            if (newPos.getAccuracy() < oldPos.getAccuracy() || oldPos.isDefault()
                || dist > newPos.getAccuracy() + oldPos.getAccuracy())
            {
               b.setLatitude( newPos.getLatitude() );
               b.setLongitude( newPos.getLongitude() );
               b.setRange( newPos.getAccuracy() );
               if (positionSource == PositionSource.PLACE)
               {
                  b
                     .setPositionSource( com.buddycloud.location.Beacon.PositionSource.PLACE );
               }
               else if (positionSource == PositionSource.GPS)
               {
                  b.setPositionSource( com.buddycloud.location.Beacon.PositionSource.GPS );
               }
               else
               {
                  b
                     .setPositionSource( com.buddycloud.location.Beacon.PositionSource.GENERAL );
               }
               try
               {
                  lDB.addOrUpdateBeacon( b );
                  String s =
                     String.format( "%sUpdating beacon %s: %s -> %s (%.0fm)", logPrefix,
                        b, oldPos.toString(), newPos.toString(), dist );
                  logger.debug( s );
               }
               catch (SQLException e)
               {
                  logger.error( String.format( "%sFailed to update beacon %s in DB",
                     logPrefix, b.toString() ) );
               }
            }
         }

      }
   }


   /**
    * 
    */
   private void useWifiMotionState()
   {
      location.setMotionState( wifiMotionState );

   }


   /**
    * @return True if the last location result for the current user had motion state
    *         STATIONARY
    */
   private boolean wasLastLocationResultPlaceFix()
   {
      if (oldLocation != null && oldLocation.isPlaceFix())
      {
         return true;
      }
      else
      {
         return false;
      }
   }
}
