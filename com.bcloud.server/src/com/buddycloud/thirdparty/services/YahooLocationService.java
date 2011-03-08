
package com.buddycloud.thirdparty.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.buddycloud.common.Location;
import com.buddycloud.common.Place;
import com.buddycloud.geoid.Point;
import com.buddycloud.location.CountryCode;

public class YahooLocationService implements ThirdPartyLocationService
{

   private static final String TOKEN = "<enter your token here>";


   public static void main(String[] args)
   {
      YahooLocationService service = new YahooLocationService();
      ThirdPartyLocationServiceTest test = new ThirdPartyLocationServiceTest( service );
      test.execute();
   }

   private Logger logger;


   public YahooLocationService()
   {
      logger = Logger.getLogger( getClass() );
   }


   public Collection<Place> findPlaces(String name, Point coordinates)
      throws IOException
   {
      return null;
   }


   public Collection<Place> findPlaces(String name, String district, String country)
      throws IOException
   {
      return null;
   }


   public Point getCoordinates(String streetAndNumber, String city, String postalCode,
      CountryCode countryCode) throws IOException
   {
      return null;
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.services.ThirdPartyLocationService#getLocation(com.bcloud.
    * coords.GeodeticCoordinate)
    */
   public Location getLocation(Point coordinates) throws IOException
   {
      return null;
   }


   /*
    * (non-Javadoc)
    * 
    * @see com.bcloud.server.location.search.LocationLookupService#getLocationName(int,
    * int, int, int)
    */
   public String getLocationName(int cellId, int lac, int mnc, int mcc)
      throws IOException
   {

      // the service URL
      String urlString =
         "http://zonetag.research.yahooapis.com/services/rest/V1/cellLookup.php";

      // add parameters
      urlString += "?apptoken=" + TOKEN;
      urlString += "&cellid=" + cellId;
      urlString += "&lac=" + lac;
      urlString += "&mnc=" + mnc;
      urlString += "&mcc=" + mcc;
      urlString += "&results=10";
      urlString += "&compressed=true";

      URL url;
      try
      {
         url = new URL( urlString );
      }
      catch (MalformedURLException e)
      {
         logger.error( "Failed to build url. this should never happen...", e );
         return null;
      }

      HttpURLConnection connection = null;
      InputStreamReader reader = null;
      String content = new String();

      char[] contentBuf = new char[1024 * 4];
      connection = (HttpURLConnection) url.openConnection();

      // set the user agent property (no value). If this is not set,
      // some servers may block (HTTP error 403)...
      connection.setRequestProperty( "User-Agent", "" );

      // load content
      reader = new InputStreamReader( (InputStream) connection.getContent() );
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

      // parse and return
      String name = null;
      if (content.contains( "<City" ))
      {
         int i0 = content.indexOf( "<City" );
         int i1 = content.indexOf( ">", i0 ) + 1;
         int i2 = content.indexOf( "</City>" );
         name = content.substring( i1, i2 );
      }
      return name;
   }


   public Collection<Place> getNearbyLocations(String name, int cid, int lac, int mnc,
      int mcc) throws IOException
   {
      return null;
   }

}
