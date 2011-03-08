/**
 * 
 */

package com.buddycloud.thirdparty.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.buddycloud.geoid.Position;
import com.buddycloud.location.Beacon;
import com.buddycloud.location.CellTowerPositionService;

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


public class GoogleGearsCellTowerPositionService implements CellTowerPositionService
{

   private Logger logger = Logger.getLogger( GoogleGearsCellTowerPositionService.class );


//   public static Location getLocation(String wifiMac) throws JSONException, IOException
//   {
//      Beacon b = new Beacon();
//      b.setMac( wifiMac );
//      b.setType( Beacon.Type.WIFI );
//      return getLocation( b );
//
//   }


   public Position getPosition(int mcc, int mnc, int lac, int cid)
      throws IOException
   {
      Beacon b = new Beacon();
      b.setMac( mcc + ":" + mnc + ":" + lac + ":" + cid );
      b.setType( Beacon.Type.CELL );
      return getPosition( b );
   }


   public Position getPosition(Beacon b) throws IOException
   {

      long t0 = System.currentTimeMillis();
      
      JSONObject jsonResponse;
      try{
         jsonResponse = get( b );
      }
      catch(Exception e){
         logger.error( "Failed to parse JSON result: "+e.getMessage() );
         return null;
      }

      JSONObject jsonLocation = jsonResponse.optJSONObject( "location" );

      // format to native object
      Position p = new Position();
      try
      {
         p.setLatitude( jsonLocation.getDouble( "latitude" ) );
      }
      catch (Exception e)
      {
      }

      try
      {
         p.setLongitude( jsonLocation.getDouble( "longitude" ) );
      }
      catch (Exception e)
      {
      }

      // documentation and example disagrees on accuracy vs vertical_accuracy
      // http://code.google.com/p/gears/wiki/GeolocationAPI
      try
      {
         p.setAccuracy( jsonLocation.getDouble( "vertical_accuracy" ) );
      }
      catch (Exception e)
      {
      }

      // documentation and example disagrees on accuracy vs vertical_accuracy
      // http://code.google.com/p/gears/wiki/GeolocationAPI
      try
      {
         p.setAccuracy( jsonLocation.getDouble( "accuracy" ) );
      }
      catch (Exception e)
      {
      }
      
      logger.info( "Position of beacon with ID "+b.getId()+" and MAC "+b.getMac()+": "+p );

      long t1 = System.currentTimeMillis();
      logger.debug( "   GEO: PERFORMANCE: lookup took " + ( t1 - t0 ) + " milliseconds" );

      return p;

   }


//   public static Location getLocation(Beacon b) throws JSONException, IOException
//   {
//
//      long t0 = System.currentTimeMillis();
//
//      JSONObject jsonResponse = get( b );
//
//      JSONObject jsonLocation = jsonResponse.optJSONObject( "location" );
//
//      logger.debug( "GEO: beacon  : " + b.getMac() + " (" + b.getType() + ")" );
//
//      // format to native object
//      Location l = new Location();
//      try
//      {
//         l.setLatitude( jsonLocation.getDouble( "latitude" ) );
//         logger.debug( "GEO: latitude  : " + jsonLocation.getDouble( "latitude" ) );
//      }
//      catch (Exception e)
//      {
//      }
//
//      try
//      {
//         l.setLongitude( jsonLocation.getDouble( "longitude" ) );
//         logger.debug( "GEO: longitude : " + jsonLocation.getDouble( "longitude" ) );
//      }
//      catch (Exception e)
//      {
//      }
//
//      try
//      {
//         l.setAccuracy( (int) jsonLocation.getDouble( "horizontal_accuracy" ) );
//         logger.debug( "GEO: accuracy  : "
//                       + jsonLocation.getDouble( "horizontal_accuracy" )
//                       + " (horizontal)" );
//      }
//      catch (Exception e)
//      {
//      }
//
//      try
//      {
//         l.setAccuracy( (int) jsonLocation.getDouble( "accuracy" ) );
//         logger.debug( "GEO: accuracy  : " + jsonLocation.getDouble( "accuracy" ) );
//      }
//      catch (Exception e)
//      {
//      }
//
//      try
//      {
//         JSONObject jsonAddress = jsonLocation.optJSONObject( "address" );
//         try
//         {
//            l.setCountryCode( CountryCode
//               .getInstance( jsonAddress.getString( "country" ) ) );
//            logger.debug( "GEO: country   : " + jsonAddress.getString( "country" ) );
//         }
//         catch (Exception e)
//         {
//         }
//
//         try
//         {
//            l.setRegion( jsonAddress.getString( "region" ) );
//            logger.debug( "GEO: region    : " + jsonAddress.getString( "region" ) );
//         }
//         catch (Exception e)
//         {
//         }
//
//         try
//         {
//            l.setCity( jsonAddress.getString( "city" ) );
//            logger.debug( "GEO: city      : " + jsonAddress.getString( "city" ) );
//         }
//         catch (Exception e)
//         {
//         }
//
//         try
//         {
//            l.setStreet( jsonAddress.getString( "street" ) );
//            logger.debug( "GEO: street    : " + jsonAddress.getString( "street" ) );
//         }
//         catch (Exception e)
//         {
//         }
//
//         try
//         {
//            l.setPostalCode( jsonAddress.getString( "postal_code" ) );
//            logger.debug( "GEO: postalcode: " + jsonAddress.getString( "postal_code" ) );
//         }
//         catch (Exception e)
//         {
//         }
//      }
//      catch (Exception e)
//      {
//      }
//
//      long t1 = System.currentTimeMillis();
//      logger.debug( "   GEO: PERFORMANCE: lookup took " + ( t1 - t0 ) + " milliseconds" );
//
//      return l;
//
//   }


   private JSONObject get(Beacon b) throws JSONException, IOException
   {

      JSONObject o = new JSONObject();

      {
         o.put( "version", "1.1.0" );
         o.put( "host", "buddycloud.com" );
         // o.put( "host", "maps.google.com" );
         // o.put( "access_token", "2,k7j3G6LaL6u_lafw,4iXOeOpTh1glSXe" );
         if (b.getType() == Beacon.Type.CELL)
         {
            o.put( "home_mobile_country_code", b.getMcc() );
            o.put( "home_mobile_network_code", b.getMnc() );
            // o.put( "radio_type", "gsm" );
            // o.put( "carrier", "Vodafone" );
         }
         o.put( "request_address", true );
         o.put( "address_language", "en_GB" );

         if (b.getType() == Beacon.Type.CELL)
         {
            JSONArray cells = new JSONArray();
            JSONObject cell = new JSONObject();
            cell.put( "cell_id", b.getCid() );
            cell.put( "location_area_code", b.getLac() );
            cell.put( "mobile_country_code", b.getMcc() );
            cell.put( "mobile_network_code", b.getMnc() );
            cell.put( "age", 0 );
            // cell.put( "signal_strength", -60 );
            // cell.put( "timing_advance", 5555 );
            cells.put( cell );
            o.put( "cell_towers", cells );
         }
         if (b.getType() == Beacon.Type.WIFI)
         {
            JSONArray wifis = new JSONArray();
            JSONObject wifi = new JSONObject();
            wifi.put( "mac_address", "01-23-45-67-89-ab" );
            wifi.put( "signal_strength", 8 );
            wifi.put( "age", 0 );
            wifis.put( wifi );
            o.put( "wifi_towers", wifis );
         }
      }

      String requestContent = o.toString();

      // System.out.println( requestContent );

      URL url = null;
      try
      {
         url = new URL( "http://www.google.com/loc/json" );
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
         return null;
      }

      HttpURLConnection connection = null;
      InputStreamReader reader = null;
      String responseContent = new String();

      char[] contentBuf = new char[1024 * 4];
      connection = (HttpURLConnection) url.openConnection();

      // set the user agent property (no value). If this is not set,
      // some servers may block (HTTP error 403)...
      connection.setRequestProperty( "User-Agent", "" );
      connection.setRequestMethod( "POST" );
      connection.setRequestProperty( "Content-Type", "application/json" );
      connection.setRequestProperty( "Content-Length", requestContent.length() + "" );
      connection.setDoOutput( true );

      // postrequest
      OutputStream out = connection.getOutputStream();
      out.write( requestContent.getBytes() );
      out.close();

      // load content
      reader = new InputStreamReader( (InputStream) connection.getContent() );
      int len = 0;
      do
      {
         len = reader.read( contentBuf );
         if (len > 0)
            responseContent += new String( contentBuf, 0, len );
      }
      while (len > 0);

      if (reader != null)
         reader.close();
      connection.disconnect();

      // parse to JSON object
      JSONObject jsonResponse = new JSONObject( responseContent );

      return jsonResponse;
   }


   public static void main(String[] args)
   {
      org.apache.log4j.BasicConfigurator.configure();
      try
      {
         // getLocation(262,07,51521,34190334);
         // getLocation(262,07,51521,34170327);
         //getLocation( 262, 07, 51047, 50880 );
         // getLocation(262,07,51521,34171136);
         //getLocation( "00:18:4D:BD:BC:02" );
         Beacon cell = new Beacon();
         GoogleGearsCellTowerPositionService g = new GoogleGearsCellTowerPositionService();
         cell.setMac( "310:260:5199:41323:" );
         cell.setType( Beacon.Type.CELL );
         System.out.println(g.getPosition( cell ));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }


   /* (non-Javadoc)
    * @see com.buddycloud.location.CellTowerPositionService#getName()
    */
   public String getName()
   {
      return "Google Gears";
   }
}
