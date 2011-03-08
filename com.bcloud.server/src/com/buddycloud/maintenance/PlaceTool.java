/**
 * 
 */

package com.buddycloud.maintenance;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.prefs.Preferences;

import com.buddycloud.location.Beacon;
import com.buddycloud.location.BeaconPattern;
import com.buddycloud.location.sql.LocationDbAccess;
import com.buddycloud.common.Place;
import com.buddycloud.geoid.Point;

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
public class PlaceTool
{

   private LocationDbAccess lDB;


   /**
    * @param args
    */
   public static void main(String[] args)
   {
      if (args.length < 1)
      {
         printUsage();
         return;
      }

      // load preferences from configuration file
      String conf = "/opt/buddycloud-locationbutler/current/config.xml";
      try {
         Preferences.importPreferences(new FileInputStream(conf));
      }
      catch (Exception e) {
         System.err.println(e.toString());
         return;
      }

      
      PlaceTool pi = new PlaceTool();
      String cmd = args[0];
      if (cmd.toLowerCase().equals( "info" ))
      {

         if (args.length != 2)
         {
            printUsage( cmd );
            return;
         }

         try
         {
            pi.printPlaceInfo( Integer.parseInt( args[1] ) );
            pi.disconnect();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }

      }
      else if (cmd.toLowerCase().equals( "merge" ))
      {

         if (args.length != 3)
         {
            printUsage( cmd );
            return;
         }

         try
         {
            pi.merge( Integer.parseInt( args[1] ), Integer.parseInt( args[2] ) );
            pi.disconnect();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }

      }

   }


   public PlaceTool()
   {
      lDB = new LocationDbAccess();
//      lDB.setType( "postgresql" );
//      lDB.setHost( "your.database.server" );
//      lDB.setPort( "3306" );
//      lDB.setName( "location" );
//      lDB.setUser( "butler" );
//      lDB.setPass("verysecret");
      lDB.validateSettings();

   }


   /**
    * Prints usage
    */
   private static void printUsage()
   {
      System.out
         .println( "Usage: com.bcloud.maintenance.PlaceInfo command args" );
      System.out.println();
      System.out.println( "Command\targs" );
      System.out.println( "info\t[placeId]" );
      System.out.println( "merge\t[placeId] [placeId]" );
   }


   private static void printUsage(String cmd)
   {
      if (cmd.toLowerCase().equals( "info" ))
      {
         System.out.println( "Usage: PlaceInfo info [placeId]" );
      }
      if (cmd.toLowerCase().equals( "merge" ))
      {
         System.out.println( "Usage: PlaceInfo merge [placeId] [placeId]" );
      }
   }


   /**
    * Prints all details about the place with the specified id
    * 
    * @param placeId
    *           The place id
    * @throws SQLException
    */
   private void printPlaceInfo(int placeId) throws SQLException
   {
      printPlaceInfo( lDB.getPlace( placeId ) );
   }


   /**
    * Prints all details about the provided place
    * 
    * @param p
    *           The place
    * @throws SQLException
    */
   private void printPlaceInfo(Place p) throws SQLException
   {
      if(p==null) print("Error, place is null");
      
      print( p.getName() + " (ID " + p.getId()+")" );
      print( "Description ", p.getDescription() );
      print( "Latitude    ", p.getLatitude() + "" );
      print( "Longitude   ", p.getLongitude() + "" );
      print( "Street      ", p.getStreet() );
      print( "Area        ", p.getArea() );
      print( "Postal Code ", p.getPostalCode() );
      print( "City        ", p.getCity() );
      print( "Country     ", p.getCountryCode().getEnglishCountryName() );
      print( "Site        ", p.getSiteUrl() );
      print( "Wiki        ", p.getWikiUrl() );

      Point placePos =
         new Point( p.getLatitude(), p.getLongitude() );

      HashMap<Integer, Beacon> beacons = new HashMap<Integer, Beacon>();
      HashMap<Integer, String> beaconMap = new HashMap<Integer, String>();
      for (BeaconPattern pattern : lDB.getBeaconPatternsForPlace( p.getId() ))
      {
         for (BeaconPattern.BeaconPatternElement e : pattern.getElements())
         {
            String pids = "";
            if (beaconMap.containsKey( e.beacon.getId() ))
            {
               pids = beaconMap.get( e.beacon.getId() );
            }
            pids += pattern.getId() + " ";
            beaconMap.put( e.beacon.getId(), pids );
            beacons.put( e.beacon.getId(), e.beacon );
         }
      }

      for (int id : beaconMap.keySet())
      {
         Beacon b = beacons.get( id );
         print( b + " Patterns: " + beaconMap.get( id ) );
         printBeaconInfo( b, placePos );
      }
   }


   private void printBeaconInfo(Beacon beacon, Point placePos)
   {
      print( "      Country   ", beacon.getCountryCode().getEnglishCountryName() );
      print( "      Latitude  ", beacon.getLatitude() + "" );
      print( "      Longitude ", beacon.getLongitude() + "" );

      Point beaconPos =
         new Point( beacon.getLatitude(), beacon.getLongitude() );
      if (placePos != null && !placePos.isDefault() && !beaconPos.isDefault())
      {
         print( "      Distance  ", placePos.getDistanceTo( beaconPos ) + "m" );
      }
   }


   private void print(String name, String value)
   {
      if (value != null && value.trim().length() > 0)
      {
         print( name + ": " + value );
      }
   }


   /**
    * Wrapper for easy changes to other output means
    * 
    * @param s
    *           The string to be printed
    */
   private void print(String s)
   {
      System.out.println( s );
   }


   private void merge(int pid1, int pid2) throws SQLException
   {
      Place p1 = lDB.getPlace( pid1 );
      if(p1==null){
         System.err.println("Place with id "+pid1+" not in DB");
         return;
      }
      Place p2 = lDB.getPlace( pid2 );
      if(p2==null){
         System.err.println("Place with id "+pid2+" not in DB");
         return;
      }
      
         

      if (!p1.getName().equals( p2.getName() ))
      {
         System.err.println( "Place names do not match: " + p1.getName() + " != "
                             + p2.getName() + ". Won't merge!" );
      }

      // shift all patterns from place 2 to place 1
      for (BeaconPattern pattern : lDB.getBeaconPatternsForPlace( pid2 ))
      {
         System.out.println( "Moving pattern " + pattern.getId() + " from place " + pid2
                             + " to " + pid1 + ":" );
         lDB.addBeaconPattern( pattern, pid1 );
      }

      // udate place 1 with details from place 2
      p1.updateUnsetFields( p2 );

      // udate db
      System.out.println( "Updating place  " + pid1 );
      lDB.updatePlace( p1, p1.getRevision()+1 );

      // delete merged
      System.out.println( "deleting place  " + pid2 );
      lDB.deletePlaceRefsFromPlaceSubscriptions( pid2 );
      lDB.deletePlaceRefsFromPlaceHistory( pid2 );
      lDB.deletePlaceRefsFromLocationHistory( pid2 );
      for(BeaconPattern pattern : lDB.getBeaconPatternsForPlace( pid2 )){
         lDB.deleteBeaconPattern( pattern.getId() );
      }
      lDB.deletePlace( pid2 );

   }
   
   public void disconnect() throws SQLException{
      lDB.disconnect();
   }

}
