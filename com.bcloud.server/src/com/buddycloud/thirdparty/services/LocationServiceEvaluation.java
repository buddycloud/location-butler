/**
 * 
 */

package com.buddycloud.thirdparty.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.buddycloud.common.GeneralLocation;
import com.buddycloud.geoid.Point;

/**
 * An tool for evaluating the data quality of reverse geocoding services
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

public class LocationServiceEvaluation
{

   private GoogleMapsWebService google;

   private GeonamesWebService geonames;

   private CloudMadeWebService cloudmade;

   private int googleScore;

   private int cloudmadeScore;

   private int geonamesScore;


   public LocationServiceEvaluation()
   {
      google = new GoogleMapsWebService();
      geonames = new GeonamesWebService();
      cloudmade = new CloudMadeWebService();
   }


   public static void main(String args[])
   {
      if (args.length == 1)
      {
         LocationServiceEvaluation ev = new LocationServiceEvaluation();
         ev.testFromFile( args[0] );
      }
      else
      {
         System.out.println( "No test file supplied" );
      }

   }


   public void doTest(String refCountry, String refCity, double lat, double lon,
      String[] alternateNames)
   {
      Point p = new Point( lat, lon );
      try
      {
         GeneralLocation googleRes = google.getLocation( p ).getGeneralLocation();
         GeneralLocation geonamesRes = geonames.getLocation( p ).getGeneralLocation();
         GeneralLocation cloudmadeRes = cloudmade.getGeneralLocation( p );

         int gooScore = getScore( refCountry, refCity, alternateNames, googleRes );
         int gnmScore = getScore( refCountry, refCity, alternateNames, geonamesRes );
         int clmScore = getScore( refCountry, refCity, alternateNames, cloudmadeRes );

         System.out.print( "Test case   : " + refCity + ", " + refCountry +" [");
         for(String s : alternateNames)
            System.out.print( s+", " );
         System.out.println("]" );
         System.out.println( "Coordinates : " + p );
         System.out.println( "Google      : " + toResultString( googleRes ) + " ("
                             + gooScore + " pts)" );
         System.out.println( "GeoNames    : " + toResultString( geonamesRes ) + " ("
                             + gnmScore + " pts)" );
         System.out.println( "CloudMade   : " + toResultString( cloudmadeRes ) + " ("
                             + clmScore + " pts)" );
         System.out.println( "Map         : http://maps.google.com/staticmap?center="
                             + p.getLatitude() + "," + p.getLongitude()
                             + "&zoom=11&size=512x512&markers=" + lat + "," + lon );
         System.out.println();

         googleScore += gooScore;
         geonamesScore += gnmScore;
         cloudmadeScore += clmScore;

      }
      catch (Exception e)
      {
         System.err.println( "Test failed: " + refCountry + ", " + refCity + ": "
                             + e.getMessage() );
      }
   }


   public String toResultString(GeneralLocation l)
   {
      String s = "";
      if (l.getArea() != null && l.getArea().length() > 0)
      {
         s += l.getArea() + "(n)";
      }
      if (l.getCity() != null && l.getCity().length() > 0)
      {
         if (s.length() > 0)
            s += ", ";
         s += l.getCity() + "(c)";
      }
      if (l.getRegion() != null && l.getRegion().length() > 0)
      {
         if (s.length() > 0)
            s += ", ";
         s += l.getRegion() + "(r)";
      }
      if (l.getCountryCode() != null)
      {
         if (s.length() > 0)
            s += ", ";
         s += l.getCountryCode().getEnglishCountryName();
      }
      return s;
   }


   /**
    * @param refCountry
    * @param refCity
    * @param googleRes
    * @return
    */
   private int getScore(String refCountry, String refCity, String[] alternateNames,
      GeneralLocation result)
   {
      int score = 0;
      if (refCity == null || result.getCity() == null)
      {
         return 0;
      }
      if (refCity.length() == 0 || result.getCity().length() == 0)
      {
         return 0;
      }

      // 2 points for correct city

      if (refCity.contains( result.getCity() ) || result.getCity().contains( refCity ))
      {
         score += 2;
      }
      if (score == 0 && alternateNames != null && alternateNames.length > 0)
      {
         for (String s : alternateNames)
         {
            if (s != null && s.length() > 0)
            {
               if (score == 0 && s.contains( result.getCity() ) || result.getCity().contains( s ))
               {
                  score += 2;
               }
            }
         }
      }

      // bonus point for neighborhood if city correct
      if (score == 2 && result.getArea() != null && result.getArea().length() > 0)
      {
         score += 1;
      }
      return score;
   }


   /**
    * Run tests with test points defined in text file Expected format: tab separated
    * columns and new-line separated rows specifying country, city, latitude and
    * longitude. the two latter in decimal degrees range -180 to 180 and -90 to 90
    * respectively
    * 
    * @param fileName
    */
   public void testFromFile(String fileName)
   {
      try
      {
         FileInputStream fis = new FileInputStream( fileName );
         InputStreamReader isr = new InputStreamReader( fis, "UTF-8" );
         BufferedReader reader = new BufferedReader( isr );

         try
         {
            String line = null;
            while (( line = reader.readLine() ) != null)
            {

               try
               {
                  String[] splits = line.split( "\t" );
                  String country = splits[0];
                  String city = splits[1];
                  String[] alternateNames = null;
                  double lat = Double.parseDouble( splits[2] );
                  double lon = Double.parseDouble( splits[3] );
                  if (splits.length > 4)
                  {
                     alternateNames = splits[4].split( ", " );
                  }
                  doTest( country, city, lat, lon, alternateNames );
               }
               catch (Exception e)
               {
                  System.err.println( "Failed to parse line: " + line );
               }
            }
         }
         finally
         {
            reader.close();
         }
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }

      System.out.println( "Scores:" );
      System.out.println( "Geonames  : " + geonamesScore );
      System.out.println( "Cloudmade : " + cloudmadeScore );
      System.out.println( "Google    : " + googleScore );

   }

}
