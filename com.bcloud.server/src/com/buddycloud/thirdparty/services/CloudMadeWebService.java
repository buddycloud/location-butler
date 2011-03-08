
package com.buddycloud.thirdparty.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.buddycloud.common.GeneralLocation;
import com.buddycloud.geoid.Point;
import com.buddycloud.location.CountryCode;
import com.buddycloud.location.ReverseGeocodingService;

public class CloudMadeWebService implements ReverseGeocodingService
{

   private Logger logger;

   private static String findLocationURL =
      "http://geocoding.cloudmade.com/<enter your api key here>/geocoding/V2/find.js?object_type=%s&around=%s&distance=closest&return_geometry=false&return_location=true";


   public CloudMadeWebService()
   {
      logger = Logger.getLogger( getClass() );
   }


   private GeneralLocation parse(Point lookupPoint, String content)
   {
      GeneralLocation l = new GeneralLocation();
      
      if (content == null || content.length() < 3) {
         logger.error( "No JSON object found [" + content + "] " );
         return l;
      }

      JSONObject o = null;
      
      try {
         o = new JSONObject( new JSONTokener( content ) );
         JSONArray features = o.getJSONArray( "features" );

         double minDistRegion = Double.MAX_VALUE;
         double minDistCity = Double.MAX_VALUE;
         double minDistArea = Double.MAX_VALUE;
         double minDistCountry = Double.MAX_VALUE;
         double closestUnmappedDist = Double.MAX_VALUE;
         String closestUnmappedName = null;
         String closestUnmappedPlace = null;

         for (int i = 0; i < features.length(); i++) {
            // Next feature
         	JSONObject feature = features.getJSONObject( i );
            
            // Get result coordinates
            double dist = Double.MAX_VALUE;
            
            try {
               JSONObject centroid = feature.getJSONObject( "centroid" );
               JSONArray coordinates = centroid.getJSONArray( "coordinates" );
               Point featurePoint = new Point( coordinates.getDouble( 0 ), coordinates.getDouble( 1 ) );
               
               if (!featurePoint.isDefault()) {
                  dist = lookupPoint.getDistanceTo( featurePoint );
               }
            }
            catch (JSONException jsone) {
               logger.debug( "Failed to get feature coordinate: " + jsone.getMessage());
            }
            
            // Get result property data
            String name = null;
            String place = null;
            String area = null;
            String city = null;
            String region = null;
            String country = null;
            String isIn = null;
            String isInCountry = null;
            String isInCountryCode = null;

            try {
               JSONObject properties = feature.getJSONObject( "properties" );
               
               // Get name and place properties
               try {
                  name = properties.getString( "name" );
               }
               catch (Exception e) {
                  logger.error( e );
               }
               
               try {
                  place = properties.getString( "place" );
               }
               catch (Exception e) {
                  logger.error( e );
               }
               
               // Get "is_in" properties
               try {
                  isIn = properties.getString( "is_in" );
               }
               catch (Exception e) {}
               
               try {
                  city = properties.getString( "is_in:city" );
               }
               catch (Exception e) {}
              
               try {
                  region = properties.getString( "is_in:state" );
               }
               catch (Exception e) {}
               
               try {
                  isInCountry = properties.getString( "is_in:country" );
                  country = isInCountry;
               }
               catch (Exception e) {}
               
               try {
                  isInCountryCode = properties.getString( "is_in:country_code" );
               }
               catch (Exception e) {}
            }
            catch (JSONException jsone) {
               logger.debug( "Failed to get feature properties: " + jsone.getMessage());
            }
            
            // Get result location data
            try {
               JSONObject location = feature.getJSONObject( "location" );
               
               // Get city, county & country location data
               try {
                  area = location.getString( "suburb" );
               }
               catch (Exception e) {}
               
               try {
                  city = location.getString( "city" );
               }
               catch (Exception e) {}
               
               try {
                  region = location.getString( "county" );
               }
               catch (Exception e) {}
               
               try {
                  country = location.getString( "country" );
               }
               catch (Exception e) {}	
            }
            catch (JSONException jsone) {
               logger.debug( "Failed to get feature location: " + jsone.getMessage());
            }
            
            // Take name/place from properties if possible
            if (place != null && name != null && name.trim().length() > 0) {
               if (place.equals( "suburb" )) {
                  area = name;
               }
               else if (place.equals( "city" )) {
                  city = name;
               }
               else if (place.equals( "county" )) {
                  region = name;
               }
               else if (place.equals( "country" )) {
                  country = name;
               }
               else if (dist < closestUnmappedDist) {
                  closestUnmappedName = name;
                  closestUnmappedPlace = place;
                  closestUnmappedDist = dist;
               }
            }

            // get the county from one of three possible fields
            CountryCode cc = null;

            // get country code form location : {country}
            if(country != null) {
            	try {
            		cc = CountryCode.getInstance(country);
            	}
            	catch(Exception e1) {
            		logger.info("Unknown country from location : {country}: "+country);
            	}
            }
            else{
            	logger.debug("location : {country} is null");
            }

            // get country code form properties : {is_in:country}
				if (cc == null && isInCountry != null) {
					try {
						cc = CountryCode.getInstance(isInCountry);
					} 
					catch (Exception e2) {
						logger.info("Unknown country from properties : {is_in:country}: "	+ isInCountry);
					}
				} 
				else {
					logger.debug("properties : {is_in:country} is null");
				}
            
            // get country code form properties : {is_in:country_code}
            if(cc == null && isInCountryCode != null) {
            	try {
            		cc = CountryCode.getInstance(isInCountryCode);
            	}
            	catch(Exception e3) {
						logger.info("Unknown country from properties : {is_in:country_code}: "	+ isInCountry);
            	}
            }
         	else {
					logger.debug("properties : {is_in:country_code} is null");
         	}
         	
         	// No country could be found
            if(cc == null) {
            	country = null;
         		logger.warn("No country could be found, object = [" + content + "] ");
            }
            else {
            	country = cc.getEnglishCountryName();
            }
            
            
            logger.debug( "Feature : " + place + ": " + name + ", " + area + ", " + city
                          + ", " + region + ", " + country + " @ " + ( (int) dist )
                          + " m" );
            logger.debug( "Is in   : "+isIn);
            

            if (area != null && dist < minDistArea && dist < 2000) {
               logger.debug( "   Keeping area: " + area );
               l.setArea( area );
               minDistArea = dist;
               
               if (city != null) {
                  logger.debug( "   Keeping city: " + city );
                  l.setCity( city );
                  minDistCity = dist;
               }
               
               if (region != null) {
                  logger.debug( "   Keeping region: " + region );
                  l.setRegion( region );
                  minDistRegion = dist;
               }
               
               if (cc != null) {
                  logger.debug( "   Keeping country: " + country );
                  l.setCountryCode( cc );
                  minDistCountry = dist;
               }
            }

            if (city != null && dist < minDistCity && dist < 20000) {
               logger.debug( "   Keeping city: " + city );
               l.setCity( city );
               minDistCity = dist;
               
               if (region != null) {
                  logger.debug( "   Keeping region: " + region );
                  l.setRegion( region );
                  minDistRegion = dist;
               }
               
               if (country != null) {
                  logger.debug( "   Keeping country: " + country );
                  l.setCountryCode( CountryCode.getInstance( country ) );
                  minDistCountry = dist;
               }
            }

            if (region != null && dist < minDistRegion) {
               logger.debug( "   Keeping region: " + region );
               l.setRegion( region );
               minDistRegion = dist;
               
               if (country != null) {
                  logger.debug( "   Keeping country: " + country );
                  l.setCountryCode( CountryCode.getInstance( country ) );
                  minDistCountry = dist;
               }
            }

            if (country != null && dist < minDistCountry) {
               logger.debug( "   Keeping country: " + country );
               l.setCountryCode( CountryCode.getInstance( country ) );
               minDistCountry = dist;
            }
         }
         
         if (closestUnmappedName != null) {
            if(minDistCity > 6000 || l.getCity() == null) {
               String overwritten = l.getCity();
               l.setCity( closestUnmappedName );
               logger.info( "   Keeping '" + closestUnmappedName + "' of type '"
                            + closestUnmappedPlace + "' " + " @ "
                            + ( (int) closestUnmappedDist ) + " m, mapped to city, overwriting "+overwritten );
            }
            else if (closestUnmappedDist < minDistArea && closestUnmappedDist < 2000 && l.getCity() != null) {
               String overwritten = l.getArea();
               l.setArea( closestUnmappedName );
               logger.info( "   Keeping '" + closestUnmappedName + "' of type '"
                            + closestUnmappedPlace + "' " + " @ "
                            + ( (int) closestUnmappedDist ) + " m, mapped to area, overwriting "+overwritten );
            }      
            else {
               logger.info( "   Ignoring '" + closestUnmappedName + "' of type '"
                  + closestUnmappedPlace + "' " + " @ "
                  + ( (int) closestUnmappedDist ) + " m" );
               logger.debug( "closestArea: "+l.getArea() +"(@ "+minDistArea+"m)" );
               logger.debug( "closestCity: "+l.getCity() +"(@ "+minDistCity+"m)" );  
            }
         }
      }
      catch (Exception e1) {
         logger.error( "Failed to parse JSON object: " + e1.getMessage() + ", object = ["
                       + content + "] " );
      }

      return l;
   }


   private String getContent(String urlString) throws IOException
   {
      String content = new String();
      URL url;
      try
      {
         url = new URL( urlString );
      }
      catch (MalformedURLException e)
      {
         logger.error( "Malformed url: " + urlString );
         return null;
      }

      HttpURLConnection connection = null;
      InputStreamReader reader = null;

      long t0 = System.currentTimeMillis();

      char[] contentBuf = new char[1024 * 4];
      connection = (HttpURLConnection) url.openConnection();

      // set the user agent property (no value). If this is not set,
      // some servers may block (HTTP error 403)...
      connection.setRequestProperty( "User-Agent", "" );
      connection.setReadTimeout( 4000 );

      // load content
      InputStream istream = (InputStream) connection.getContent();
      reader = new InputStreamReader( istream );
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

      long t1 = System.currentTimeMillis();
      logger.debug( "   GEO: PERFORMANCE: lookup took " + ( t1 - t0 ) + " milliseconds" );

      return content;
   }


   public static void main(String[] args)
   {
      try
      {
         org.apache.log4j.BasicConfigurator.configure();
         CloudMadeWebService g = new CloudMadeWebService();

         Point[] points = new Point[]
         {
            new Point( 39.116,-84.507 ),
            new Point( 39.08,-84.51 ),
            new Point( 48.15636, 11.605079 ), 
            new Point( 51.272988,0.510876 ),
            new Point( 48.85645, 2.35243),
            new Point( 32.35313, 74.42622 )
            // new Point( 48.1595, 11.5860 ), new Point( -8.674164, 115.259474 )
            };

         for (Point p : points)
         {
            System.out.println( p + ": " + g.getGeneralLocation( p ) );
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.buddycloud.location.ReverseGeocodingService#getGeneralLocation(com.buddycloud
    * .geoid.Point)
    */
   public GeneralLocation getGeneralLocation(Point p) throws IOException
   {
      // the test script will frequently trigger this lookup, don't bother querying for it
      if (p != null && p.getLatitude() == 20 && p.getLongitude() == 20)
      {
         return new GeneralLocation();
      }

      String latlon = String.format( "%f,%f", p.getLatitude(), p.getLongitude() );
      String url;
      String content = null;
      GeneralLocation l = null;
      url = String.format( findLocationURL, "area", latlon );
      logger.debug( "Point: " + p );
      logger.debug( "Url: " + url );
      try
      {
         content = getContent( url );
         l = parse( p, content );
         logger.debug( "Result: " + l );
      }
      catch (IOException e)
      {
         logger.debug( "IOException: " + e.getMessage() + ", url = " + url );
      }

      return l;
   }


   /*
    * (non-Javadoc)
    * 
    * @see com.buddycloud.location.ReverseGeocodingService#getName()
    */
   public String getName()
   {
      return "CloudMade";
   }

}
