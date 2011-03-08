/**
 * 
 */

package com.buddycloud.thirdparty.services;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import com.buddycloud.common.GeneralLocation;
import com.buddycloud.geoid.Point;
import com.buddycloud.geonames.GeoNames;
import com.buddycloud.geonames.GeoNamesException;
import com.buddycloud.geonames.GeoUnit;
import com.buddycloud.location.CountryCode;
import com.buddycloud.location.ReverseGeocodingService;

/**
 * A simple wrapper for the local geonames lookup to put results in the local format
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
public class GeonamesLocalService implements ReverseGeocodingService
{

   private static final String GEONAMES_LOCAL_RMI_NAME = "geonames";

//   private static GeonamesLocalService instance;

   private Logger logger;

   private GeoNames geonames;


//   public static GeonamesLocalService getInstance()
//   {
//      if (instance == null)
//      {
//         instance = new GeonamesLocalService();
//      }
//      return instance;
//   }


   public GeonamesLocalService()
   {
      logger = Logger.getLogger( getClass() );

      String host = "cave.buddycloud.com";
      int port = 12345;
      try
      {
         host =
            Preferences.userNodeForPackage( getClass() )
               .get( "geonames_local_host", host );
         logger.info( "Using property value 'geonames_local_host' = " + host );
      }
      catch (Exception e)
      {
         logger
            .info( "Failed to get property value 'geonames_local_host' from preferences. Using default of "
                   + host + " instead." );
      }
      try
      {
         port =
            Preferences.userNodeForPackage( getClass() ).getInt( "geonames_local_port",
               port );
         logger.info( "Using property value 'geonames_local_port' = " + port );
      }
      catch (Exception e)
      {
         logger
            .info( "Failed to get property value 'geonames_local_host' from preferences. Using default of "
                   + port + " instead." );
      }
      try
      {
         geonames = findServer( host, port, GEONAMES_LOCAL_RMI_NAME );
      }
      catch (Exception e)
      {
         logger.error( "Failed to find local geoname server: " + e.getMessage() );
      }
   }




   private GeoNames findServer(String host, int port, String rmiName)
      throws GeoNamesException
   {
      try
      {
         Registry registry = LocateRegistry.getRegistry( host, port );
         GeoNames geoNames = (GeoNames) registry.lookup( rmiName );
         return geoNames;
      }
      catch (NotBoundException ex)
      {
         String err =
            String.format( "No GeoNames server object registered "
                           + "under name '%s' on server '%s:%d'. Please make sure the "
                           + "server is running.", rmiName, host, port );
         // logger.error( err, ex );
         throw new GeoNamesException( err, ex );
      }
      catch (RemoteException ex2)
      {
         String err =
            String.format(
               "Issue while trying to look up "
                  + "GeoNames server object under name '%s' on server "
                  + "'%s:%d'. Errmsg: %s. Please make sure the server is running.",
               rmiName, host, port, ex2.getMessage() );
         // logger.error( err, ex2 );
         throw new GeoNamesException( err, ex2 );
      }
   }


   /* (non-Javadoc)
    * @see com.buddycloud.location.ReverseGeocodingService#getGeneralLocation(com.buddycloud.geoid.Point)
    */
   public GeneralLocation getGeneralLocation(Point p) throws IOException
   {
      if (geonames == null)
      {
         throw new RemoteException( "Not connected to server" );
      }
      GeoUnit u = geonames.findLocation( p.getLatitude(), p.getLongitude() );
      GeneralLocation l = new GeneralLocation();
      int closestGeonameDistance = 10000000;
//      Point closestGeonameCenter = null;
      GeoUnit closestGeoname = null;
      while (u != null)
      {

         Point uCenter = new Point( u.lat(), u.lon() );
         int uDist = (int) p.getDistanceTo( uCenter );
         String uDistString;
         if(uDist>1000){
            uDistString = (int)(uDist/1000)+" km";
         }
         else{
            uDistString = (int)uDist+" m";
         }

         // keep closest
         if (uDist < closestGeonameDistance)
         {
            closestGeonameDistance = uDist;
//            closestGeonameCenter = uCenter;
            closestGeoname = u;
         }

         // set layers
         if (u.getType() == GeoUnit.Type.NEIGHBORHOOD)
         {
            logger.debug( String.format( "   GEO: Area: %s  (%s) center: %s  %s", u
               .getName(), u.getType(), uCenter, uDistString ) );
            l.setArea( u.getName() );
         }
         else if (u.getType() == GeoUnit.Type.CITY)
         {
            logger.debug( String.format( "   GEO: City: %s  (%s) center: %s  %s", u
               .getName(), u.getType(), uCenter, uDistString ) );
            l.setCity( u.getName() );
         }
         else if (u.getType() == GeoUnit.Type.ADMIN2)
         {
            logger.debug( String.format( "   GEO: Region: %s  (%s) center: %s  %s", u
               .getName(), u.getType(), uCenter, uDistString ) );
            l.setRegion( u.getName() );
         }
         else if (u.getType() == GeoUnit.Type.ADMIN1)
         {
            logger.debug( String.format( "   GEO: Region: %s  (%s) center: %s  %s", u
               .getName(), u.getType(), uCenter, uDistString ) );
            l.setRegion( u.getName() );
         }
         else if (u.getType() == GeoUnit.Type.COUNTRY)
         {
            logger.debug( String.format( "   GEO: Country: %s  (%s) center: %s  %s", u
               .getName(), u.getType(), uCenter, uDistString ) );
            l.setCountryCode( CountryCode.getInstance( u.getName() ) );
         }
         else{
            logger.debug( String.format( "   GEO: Unused: %s  (%s) center: %s  %s", u
               .getName(), u.getType(), uCenter, uDistString ) );
         }
         u = u.getParent();
      }

      if (closestGeoname != null)
      {
//         l.setLatitude( closestGeonameCenter.getLatitude() );
//         l.setLongitude( closestGeonameCenter.getLongitude() );
//         l.setAccuracy( closestGeonameDistance );
         if (closestGeonameDistance < 1000)
         {
            logger.debug( "   GEO: Closest geonames entity: " + closestGeoname.getName()
                          + " at " + closestGeonameDistance + " m" );
         }
         else
         {
            logger.debug( "   GEO: Closest geonames entity: " + closestGeoname.getName()
                          + " at " + closestGeonameDistance / 1000 + " km" );

         }

      }
      return l;
   }


   /* (non-Javadoc)
    * @see com.buddycloud.location.ReverseGeocodingService#getName()
    */
   public String getName()
   {
      return "Geonames (Local)";
   }

}
