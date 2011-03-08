/**
 * 
 */

package com.buddycloud.thirdparty.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.buddycloud.common.GeneralLocation;
import com.buddycloud.common.Location;
import com.buddycloud.geoid.Point;
import com.buddycloud.geoid.Position;
import com.buddycloud.location.CountryCode;
import com.buddycloud.location.GeocodingService;
import com.buddycloud.location.ReverseGeocodingService;

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
 * limitations under the License.
 *
 *
 */ 

public class GeonamesWebService implements GeocodingService, ReverseGeocodingService
{

   /**
    * The distance from requested point to reported center of area above which area info
    * will be disregarded
    */
   private static final double MAX_AREA_CENTER_DISTANCE_FROM_POINT = 2000;

   private static final CountryCode[] COUNTRY_CODES_WHERE_REGION_IS_ADM1 =
   {
      CountryCode.US
   };

   // The timeout to use when calling the geonames web services
   private int timeoutMilliseconds = 4000;

   private final class Geoname
   {

      public String name;

      public double lat;

      public double lng;

      public long geonameId;

      public String countryCode;

      public String countryName;

      public String fcl;

      public String fcode;


      public String toString()
      {
         return "geonameId=" + geonameId + ", name=" + name + " fcode=" + fcode
                + ", fcl=" + fcl;
      }
   }


   public static void main(String[] args)
   {
      org.apache.log4j.BasicConfigurator.configure();
      GeonamesWebService service = new GeonamesWebService();
      try
      {
         GeneralLocation gl = new GeneralLocation();
         gl.setCountryCode( CountryCode.DE );
         gl.setRegion( "Bavaria" );
         gl.setCity( "Munich" );
         gl.setArea( "Schwabing" );
         System.out.println( gl + " ->" + service.getPosition( gl ) );
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      try
      {
         System.out.println( service.getLocation( new Point( 48.158, 11.575 ) ).toString(
            10, Location.Layer.COORDINATES ) );
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      try
      {
         System.out.println( service.getLocation( new Point( 34.175747, -85.657095 ) )
            .toString( 10, Location.Layer.COORDINATES ) );
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

//   private static GeonamesWebService instance;

   private Logger logger;


   public GeonamesWebService()
   {
      logger = Logger.getLogger( getClass() );
      try
      {
         timeoutMilliseconds =
            Preferences.userNodeForPackage( getClass() ).getInt(
               "geonames_timeout_milliseconds", timeoutMilliseconds );
         logger.info( "Using property value 'geonames_timeout_milliseconds' = "
                      + timeoutMilliseconds );
      }
      catch (Exception e)
      {
         logger
            .info( "Failed to get property value 'geonames_timeout_milliseconds' from preferences. Using default of "
                   + timeoutMilliseconds + " instead." );
      }
   }


//   public static GeonamesWebService getInstance()
//   {
//      if (instance == null)
//         instance = new GeonamesWebService();
//      return instance;
//   }
//

   public Position getPosition(GeneralLocation gl) throws IOException
   {
      Position p = getPosition( gl.getArea(), gl.getCountryCode() );
      if (p != null)
      {
         p.setAccuracy( Location.Layer.AREA.getDefaultError() );
         return p;
      }

      p = getPosition( gl.getCity(), gl.getCountryCode() );
      if (p != null)
      {
         p.setAccuracy( Location.Layer.CITY.getDefaultError() );
         return p;
      }

      p = getPosition( gl.getRegion(), gl.getCountryCode() );
      if (p != null)
      {
         p.setAccuracy( Location.Layer.REGION.getDefaultError() );
         return p;
      }

      p = getPosition( gl.getCountryCode().getEnglishCountryName(), gl.getCountryCode() );
      if (p != null)
      {
         p.setAccuracy( Location.Layer.COUNTRY.getDefaultError() );
         return p;
      }

      return null;
   }


   private Position getPosition(String name, CountryCode countryCode) throws IOException
   {
      if (name == null)
         return null;
      if (countryCode == null)
         return null;

      String url =
         "http://ws.geonames.org/search?q=" + name + "&maxRows=10&lang=en&style=full";
      String result = httpGet( url );
      for (Geoname g : parseGeonames( result ))
      {
         CountryCode gcc = CountryCode.getInstance( g.countryCode );
         if (g != null && g.countryName != null && gcc.equals( countryCode ))
         {
            Position p = new Position();
            p.setLatitude( g.lat );
            p.setLongitude( g.lng );
            return p;
         }
      }
      return null;
   }


   public GeneralLocation getGeneralLocation(Point coordinate) throws IOException
   {
      Location l = getLocation( coordinate );
      GeneralLocation gl = new GeneralLocation();
      gl.setArea( l.getArea() );
      gl.setCity( l.getCity() );
      gl.setRegion( l.getRegion() );
      gl.setCountryCode( l.getCountryCode() );

      return gl;
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.server.location.search.LocationLookupService#getLocationName(com.bcloud
    * .coords.GeodeticCoordinate)
    */
   public Location getLocation(Point p) throws IOException
   {
      long t0 = System.currentTimeMillis();

      logger.debug( "   GEO: getting location for " + p );
      Location l = new Location();

      // Query 1: get basic location hierarchy
      String urlString =
         "http://ws.geonames.org/extendedFindNearby?lat=" + p.getLatitude() + "&lng="
            + p.getLongitude();
      String xml = httpGet( urlString );

      if (xml.contains( "<address>" ))
      {

         String adm1 = getElementText( xml, "adminName1" );
         String adm2 = getElementText( xml, "adminName2" );
         String placename = getElementText( xml, "placename" );
         CountryCode countryCode =
            CountryCode.getInstance( getElementText( xml, "countryCode" ) );
         l.setRegion( adm1 );

         // use ADM2
         if (adm2 != null && !isAdm1PreferredRegionField( countryCode ))
            l.setRegion( adm2 );
         l.setCity( placename );
         l.setCountryCode( countryCode );
         logger.debug( "   GEO: country   : " + countryCode + " (countryCode)" );
         logger.debug( "   GEO: region    : " + adm1 + " (ADM1)" );
         logger.debug( "   GEO: region    : " + adm2 + " (ADM2)" );
         logger.debug( "   GEO: city      : " + placename + " (placename)" );

      }

      else
      {
         List<Geoname> geonames = parseGeonames( xml );
         int closestGeonameDistance = 10000000;
         Point closestGeonameCenter = null;
         Geoname closestGeoname = null;
         for (Geoname g : geonames)
         {
            if (g.fcode != null)
            {
               Point gCenter = new Point( g.lat, g.lng );
               int gDist = (int) p.getDistanceTo( gCenter );

               if (g.fcode.startsWith( "PC" ))
               {
                  l.setCountryCode( CountryCode.getInstance( g.countryCode ) );
                  logger.debug( "   GEO: country   : " + g.name + " (" + g.fcode
                                + ") center: " + gCenter + " " + gDist / 1000 + " km" );

                  // collect the closest
                  if (closestGeonameCenter == null || gDist < closestGeonameDistance)
                  {
                     closestGeoname = g;
                     closestGeonameCenter = gCenter;
                     closestGeonameDistance = gDist;
                  }

               }
               else if (g.fcode.equals( "ADM1" ))
               {
                  l.setRegion( g.name );
                  logger.debug( "   GEO: region    : " + g.name + " (ADM1) center: "
                                + gCenter + " " + gDist / 1000 + " km" );

                  // collect the closest
                  if (closestGeonameCenter == null || gDist < closestGeonameDistance)
                  {
                     closestGeoname = g;
                     closestGeonameCenter = gCenter;
                     closestGeonameDistance = gDist;
                  }
               }
               // overrides adm1
               else if (g.fcode.equals( "ADM2" ))
               {
                  l.setRegion( g.name );
                  logger
                     .debug( "   GEO: region    : " + g.name + " (ADM2) center: "
                             + gCenter + " " + gDist / 1000 + " km,  overwrites ADM1)" );

                  // collect the closest
                  if (closestGeonameCenter == null || gDist < closestGeonameDistance)
                  {
                     closestGeoname = g;
                     closestGeonameCenter = gCenter;
                     closestGeonameDistance = gDist;
                  }
               }
               else if (g.fcode.equals( "PPLX" ))
               {
                  // check distance and discard if "far" away
                  if (!gCenter.isDefault() && gDist > MAX_AREA_CENTER_DISTANCE_FROM_POINT)
                  {
                     logger.debug( "   GEO: area      : " + g.name + " (" + g.fcode
                                   + ") center: " + gCenter + " " + gDist / 1000
                                   + " km, more than "
                                   + MAX_AREA_CENTER_DISTANCE_FROM_POINT / 1000
                                   + "km away, discarded." );
                  }
                  else
                  {
                     l.setArea( g.name );
                     logger.debug( "   GEO: area      : " + g.name + " (" + g.fcode
                                   + ") center: " + gCenter + " " + gDist / 1000 + " km" );
                     // collect the closest
                     if (closestGeonameCenter == null || gDist < closestGeonameDistance)
                     {
                        closestGeoname = g;
                        closestGeonameCenter = gCenter;
                        closestGeonameDistance = gDist;
                     }
                  }
               }
               else if (g.fcode.startsWith( "PPL" ))
               {
                  l.setCity( g.name );
                  logger.debug( "   GEO: city      : " + g.name + " (" + g.fcode
                                + ") center: " + gCenter + " " + gDist / 1000 + " km" );

                  // collect the closest
                  if (closestGeonameCenter == null || gDist < closestGeonameDistance)
                  {
                     closestGeoname = g;
                     closestGeonameCenter = gCenter;
                     closestGeonameDistance = gDist;
                  }
               }
               else
               {
                  logger.debug( "   GEO: unused    : " + g.name + " (" + g.fcode
                                + ") center: " + gCenter + " " + gDist / 1000 + " km" );
               }
            }
         }
         if (closestGeoname != null)
         {
            l.setLatitude( closestGeonameCenter.getLatitude() );
            l.setLongitude( closestGeonameCenter.getLongitude() );
            l.setAccuracy( closestGeonameDistance );
            if (closestGeonameDistance < 1000)
            {
               logger.debug( "   GEO: Closest geonames entity: " + closestGeoname.name
                             + " at " + closestGeonameDistance + " m" );
            }
            else
            {
               logger.debug( "   GEO: Closest geonames entity: " + closestGeoname.name
                             + " at " + closestGeonameDistance / 1000 + " km" );

            }

         }

      }

      long t1 = System.currentTimeMillis();
      logger.debug( "   GEO: PERFORMANCE: lookup took " + ( t1 - t0 ) + " milliseconds" );

      return l;
   }


   /**
    * Checks if the provided country code is that of one of the countries where ADM1 is a
    * better map top region than ADM2
    * 
    * @param countryCode
    * @return
    */
   private boolean isAdm1PreferredRegionField(CountryCode countryCode)
   {
      boolean b = false;
      for (CountryCode cc : COUNTRY_CODES_WHERE_REGION_IS_ADM1)
      {
         if (countryCode.equals( cc ))
         {
            b = true;
         }
      }

      return b;
   }


   /**
    * Simple parsing of <element>Text</element>
    * 
    * @param xml
    *           source text
    * @param element
    *           name of element
    * @return content between opening <element> and closing </element> tags
    */
   private String getElementText(String xml, String element)
   {
      String openTag = "<" + element + ">";
      String closeTag = "</" + element + ">";
      int i1 = xml.indexOf( openTag ) + openTag.length();
      int i2 = xml.indexOf( closeTag );

      String value = null;
      if (i1 >= 0 && i2 > 0)
      {
         value = xml.substring( i1, i2 );
      }

      return value;
   }


   private String httpGet(String urlString) throws IOException
   {
      URL url;
      try
      {
         url = new URL( urlString );
      }
      catch (MalformedURLException e)
      {
         logger.error( "Failed to build url.", e );
         return null;
      }

      logger.debug( url.toString() );

      HttpURLConnection connection = null;
      InputStreamReader reader = null;
      String content = new String();

      char[] contentBuf = new char[1024 * 4];
      connection = (HttpURLConnection) url.openConnection();
      connection.setReadTimeout( timeoutMilliseconds );
      connection.setConnectTimeout( timeoutMilliseconds );

      // set the user agent property (no value). If this is not set,
      // some servers may block (HTTP error 403)...
      connection.setRequestProperty( "User-Agent", "" );

      // load content
      InputStream istream = (InputStream) connection.getContent();
      reader = new InputStreamReader( istream, "UTF-8" );
      logger.debug( "Reader encoding: " + reader.getEncoding() );
      int len = 0;
      do
      {
         len = reader.read( contentBuf );
         if (len > 0)
            content += new String( contentBuf, 0, len );
      }
      while (len > 0);

      if (reader != null)
         reader.close();
      connection.disconnect();

      // System.out.println( "host returned " + content.length() + " bytes" );

      return content;
   }


   private List<Geoname> parseGeonames(String xml)
   {
      Vector<Geoname> geonames = new Vector<Geoname>();

      int counter = 0;
      while (xml.contains( "</geoname>" ) && counter < 1000)
      {
         int i = xml.indexOf( "</geoname>" ) + "</geoname>".length();
         String s = xml.substring( 0, i );
         xml = xml.substring( i, xml.length() );
         Geoname g = parseGeoname( s );
         if (s != null)
            geonames.add( g );
         counter++;
      }

      if (counter == 1000)
         throw new RuntimeException( "Infinite loop detected" );

      return geonames;
   }


   private Geoname parseGeoname(String xml)
   {
      Geoname geoname = new Geoname();
      String err = "";
      try
      {
         geoname.countryCode = parseField( xml, "countryCode" );
      }
      catch (Exception e)
      {
         err += "Failed to parse countryCode. ";
      }
      try
      {
         geoname.countryName = parseField( xml, "countryName" );
      }
      catch (Exception e)
      {
         err += "Failed to parse countryName. ";
      }
      try
      {
         geoname.fcl = parseField( xml, "fcl" );
      }
      catch (Exception e)
      {
         err += "Failed to parse fcl. ";
      }
      try
      {
         geoname.fcode = parseField( xml, "fcode" );
      }
      catch (Exception e)
      {
         err += "Failed to parse fcode. ";
      }
      try
      {
         geoname.geonameId = Integer.parseInt( parseField( xml, "geonameId" ) );
      }
      catch (Exception e)
      {
         err += "Failed to parse geonameId. ";
      }
      try
      {
         geoname.lat = Double.parseDouble( parseField( xml, "lat" ) );
      }
      catch (Exception e)
      {
         err += "Failed to parse lat. ";
      }
      try
      {
         geoname.lng = Double.parseDouble( parseField( xml, "lng" ) );
      }
      catch (Exception e)
      {
         err += "Failed to parse lng. ";
      }

      try
      {
         geoname.name = parseField( xml, "name" );
      }
      catch (Exception e)
      {
         err += "Failed to parse name. ";
      }

      if (err.length() > 0)
         System.out.println( "Errors during geoname parsing: " + err );

      return geoname;
   }


   private String parseField(String xml, String fieldName)
   {
      String open = "<" + fieldName + ">";
      String close = "</" + fieldName + ">";
      if (!xml.contains( open ))
         return null;
      if (!xml.contains( close ))
         return null;
      String value =
         xml.substring( xml.indexOf( open ) + open.length(), xml.indexOf( close ) );

      // TODO proper de-htmlifization if needed, &amp; has been observed
      value = value.replace( "&amp;", "&" );

      return value;
   }


   /* (non-Javadoc)
    * @see com.buddycloud.location.ReverseGeocodingService#getName()
    */
   public String getName()
   {
      return "Geonames (Web)";
   }



}
