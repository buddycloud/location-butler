
package com.buddycloud.thirdparty.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.buddycloud.common.GeneralLocation;
import com.buddycloud.common.Location;
import com.buddycloud.common.Place;
import com.buddycloud.geoid.Point;
import com.buddycloud.geoid.Position;
import com.buddycloud.location.CountryCode;
import com.buddycloud.location.ReverseGeocodingService;

public class GoogleMapsWebService implements ReverseGeocodingService
{

   private Logger logger;

   private static String findPlacesURL =
      "http://maps.google.com/maps?q=%s&near=%s&mrt=yp&output=kml&ie=UTF8";

   private static String findLocationURL =
      "http://maps.google.com/maps/geo?q=%s&output=xml&oe=utf8&key=<your api key goes here>&sensor=false";

//   private static GoogleLocationService instance;


   public GoogleMapsWebService()
   {
      logger = Logger.getLogger( getClass() );
   }


   public Collection<Place> findPlaces(String name, Point p) throws IOException
   {
      String near = p.getLatitude() + ", " + p.getLongitude();

      logger.info( "Searching for " + name + ", near " + near );

      String kml = findPlacesKML( name, near );

      // parse and return
      Collection<Place> results = parseKml( kml, null );
      logger.info( results.size() + " results found" );
      return results;
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.server.location.search.LocationLookupService#getNearbyLocations(java.
    * lang.String, java.lang.String, java.lang.String)
    */
   public Collection<Place> findPlaces(String name, String localHints, String country)
      throws IOException
   {

      String near = country;
      if (localHints != null && localHints.length() > 0)
      {
         near = localHints + " " + near;
      }

      logger.info( "Searching for " + name + ", near " + near );

      String kml = findPlacesKML( name, near );

      // parse and return
      Collection<Place> results = parseKml( kml, country );
      logger.info( results.size() + " results found" );
      return results;

   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.server.location.search.LocationLookupService#getCoordinates(com.bcloud
    * .server.location.data.Beacon)
    */
   public Point getCoordinates(int cid, int lac, int mnc, int mcc) throws IOException
   {

      // Create a connection to some 'hidden' Google-API
      String urlString = "http://www.google.com/glm/mmap";

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

      HttpURLConnection connection = null;
      connection = (HttpURLConnection) url.openConnection();

      // set the user agent property (no value). If this is not set,
      // some servers may block (HTTP error 403)...
      connection.setRequestProperty( "User-Agent", "" );

      // set do-output
      connection.setDoOutput( true );

      // write request (very proprietary format!!!)
      DataOutputStream os = new DataOutputStream( connection.getOutputStream() );
      os.writeShort( 21 );
      os.writeLong( 0 );
      os.writeUTF( "fr" );
      os.writeUTF( "Sony_Ericsson-K750" );
      os.writeUTF( "1.3.1" );
      os.writeUTF( "Web" );
      os.writeByte( 27 );

      os.writeInt( 0 );
      os.writeInt( 0 );
      os.writeInt( 3 );
      os.writeUTF( "" );
      os.writeInt( cid ); // CELL-ID
      os.writeInt( lac ); // LAC
      os.writeInt( 0 );
      os.writeInt( 0 );
      os.writeInt( 0 );
      os.writeInt( 0 );
      os.flush();

      // load content
      Point p = new Point();
      DataInputStream dis = new DataInputStream( connection.getInputStream() );
      dis.readShort();
      dis.readByte();
      int code = dis.readInt();
      if (code == 0)
      {
         int lat = dis.readInt();
         int lon = dis.readInt();
         p.setLatitude( (double) lat / 1000000D );
         p.setLongitude( (double) lon / 1000000D );
      }
      else
      {
         // System.out.println("code="+code);
      }

      if (os != null)
         os.close();
      if (dis != null)
         dis.close();
      connection.disconnect();

      return p;

   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.server.location.search.LocationLookupService#getCoordinates(java.lang
    * .String, java.lang.String, java.lang.String)
    */
   public Point getCoordinates(String streetAndNumber, String city, String postalCode,
      CountryCode countryCode) throws IOException
   {
      String cityAndPostal = "";
      if (city != null && city.trim().length() > 0)
      {
         cityAndPostal += city;
      }
      if (postalCode != null && postalCode.trim().length() > 0)
      {
         if (cityAndPostal.length() > 0)
         {
            cityAndPostal += " ";
         }
         cityAndPostal += postalCode;
      }

      String address = "";
      if (streetAndNumber != null && streetAndNumber.trim().length() > 0)
      {
         address += streetAndNumber;
      }
      if (cityAndPostal != null && cityAndPostal.trim().length() > 0)
      {
         if (address.length() > 0)
            address += ", ";
         address += cityAndPostal;
      }
      if (cityAndPostal != null && cityAndPostal.trim().length() > 0)
      {
         if (address.length() > 0)
            address += ", ";
         address += cityAndPostal;
      }
      if (countryCode != null)
      {
         if (address.length() > 0)
            address += ", ";
         address += countryCode.getEnglishCountryName();
      }

      address = URLEncoder.encode( address, "UTF-8" );
      String urlString = "http://maps.google.com/maps?q=" + address + "&output=kml";

      URL url;
      try
      {
         url = new URL( urlString );
      }
      catch (MalformedURLException e)
      {
         logger.error( "Failed to build url. Please check input parameters ("
                       + streetAndNumber + ", " + cityAndPostal + ", " + countryCode
                       + ")", e );
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
      connection.disconnect();

      // parse and return
      return parsePlacemark( content );
   }


   /*
    * (non-Javadoc)
    * 
    * @see
    * com.bcloud.thirdparty.services.ThirdPartyLocationService#getLocation(com.bcloud.
    * coords.GeodeticCoordinate)
    */
   public Location getLocation(Point p) throws IOException
   {

      String latlon = String.format( "%f,%f", p.getLatitude(), p.getLongitude() );
      String url = String.format( findLocationURL, latlon );
      //System.out.println( url );
      String content = getContent( url );
      Location l = getFirstPlacemarkAsLocation(content);

      return l;
   }
   
   private Location getFirstPlacemarkAsLocation(String kml){
      String cc = getFirstElementText( kml, "<CountryNameCode>" );
      String region1 = getFirstElementText( kml, "<AdministrativeAreaName>" );
      String region2 = getFirstElementText( kml, "<SubAdministrativeAreaName>" );
      String city = getFirstElementText( kml, "<LocalityName>" );
      String area = getFirstElementText( kml, "<DependentLocalityName>" );
      String street = getFirstElementText( kml, "<ThoroughfareName>" );
      String postal = getFirstElementText( kml, "<PostalCodeNumber>" );
      String coords = getFirstElementText( kml, "<coordinates>" );

      Location l = new Location();
      l.setCountryCode( CountryCode.getInstance( cc ) );
      l.setRegion( region1 );
      l.setCity( city );
      l.setArea( area );
      l.setStreet( street );
      l.setPostalCode( postal );
      try
      {
         l.setLatitude( Double.parseDouble( coords.split( "," )[1] ) );
         l.setLongitude( Double.parseDouble( coords.split( "," )[0] ) );
      }
      catch (Exception e)
      {

      }

      // England, Scotland, Wales and North Ireland is boring. use sub-adm name instead
      if (l.getCountryCode() == CountryCode.GB && region2 != null)
      {
         l.setRegion( region2 );
      }
      
      return l;
   }


   private String getFirstElementText(String xml, String element)
   {
      int i1 = xml.indexOf( element );
      i1 += element.length();
      int i2 = xml.indexOf( "<", i1 );
      String text = null;
      if (i1 > element.length() && i2 > i1)
      {
         text = xml.substring( i1, i2 );
      }
      return text;
   }


   private String getContent(String urlString) throws IOException
   {
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
      String content = new String();

      long t0 = System.currentTimeMillis();

      char[] contentBuf = new char[1024 * 4];
      connection = (HttpURLConnection) url.openConnection();

      // set the user agent property (no value). If this is not set,
      // some servers may block (HTTP error 403)...
      connection.setRequestProperty( "User-Agent", "" );

      // load content
      InputStream istream = (InputStream) connection.getContent();
      reader = new InputStreamReader( istream, "UTF-8" );
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
      logger.debug( "   GEO: PERFORMANCE: google maps lookup took " + ( t1 - t0 ) + " milliseconds" );

      return content;
   }


   private String findPlacesKML(String name, String near) throws IOException
   {

      String nameParam = URLEncoder.encode( name, "UTF-8" );
      String nearParam = URLEncoder.encode( near, "UTF-8" );

      String urlString = String.format( findPlacesURL, nameParam, nearParam );
      return getContent( urlString );
      //
      // URL url;
      // try
      // {
      // url = new URL( urlString );
      // }
      // catch (MalformedURLException e)
      // {
      // logger.error( "Failed to build url. Please check input parameters (" + name
      // + ", " + near + ")", e );
      // return null;
      // }
      //
      // logger.debug( url.toString() );
      //
      // HttpURLConnection connection = null;
      // InputStreamReader reader = null;
      // String content = new String();
      //
      // char[] contentBuf = new char[1024 * 4];
      // connection = (HttpURLConnection) url.openConnection();
      //
      // // set the user agent property (no value). If this is not set,
      // // some servers may block (HTTP error 403)...
      // connection.setRequestProperty( "User-Agent", "" );
      //
      // // load content
      // InputStream istream = (InputStream) connection.getContent();
      // reader = new InputStreamReader( istream, "UTF-8" );
      // logger.debug( "Reader encoding: " + reader.getEncoding() );
      // int len = 0;
      // do
      // {
      // len = reader.read( contentBuf );
      // if (len > 0)
      // content += new String( contentBuf, 0, len );
      // }
      // while (len > 0);
      //
      // if (reader != null)
      // reader.close();
      // connection.disconnect();
      //
      // logger.debug( "host returned " + content.length() + " bytes" );
      //
      // return content;
   }


   private String htmlDecode(String in)
   {
      // ignore all tags
      // TODO check for ISO LAtin-1 code (e.g. &#225; etc)
      String out = in.replaceAll( "<.*?>", "" );
      out = out.replace( "&lt;", "<" );
      out = out.replace( "&gt;", ">" );
      out = out.replace( "&amp;", "&" );
      out = out.replace( "&quot;", "\"" );
      out = out.replace( "&agrave;", "" );
      out = out.replace( "&Agrave;", "" );
      out = out.replace( "&acirc;", "" );
      out = out.replace( "&Acirc;", "" );
      out = out.replace( "&auml;", "" );
      out = out.replace( "&Auml;", "" );
      out = out.replace( "&aring;", "" );
      out = out.replace( "&Aring;", "" );
      out = out.replace( "&aelig;", "" );
      out = out.replace( "&AElig;", "" );
      out = out.replace( "&ccedil;", "" );
      out = out.replace( "&Ccedil;", "" );
      out = out.replace( "&eacute;", "" );
      out = out.replace( "&Eacute;", "" );
      out = out.replace( "&egrave;", "" );
      out = out.replace( "&Egrave;", "" );
      out = out.replace( "&ecirc;", "" );
      out = out.replace( "&Ecirc;", "" );
      out = out.replace( "&euml;", "" );
      out = out.replace( "&Euml;", "" );
      out = out.replace( "&iuml;", "" );
      out = out.replace( "&Iuml;", "" );
      out = out.replace( "&ocirc;", "" );
      out = out.replace( "&Ocirc;", "" );
      out = out.replace( "&ouml;", "" );
      out = out.replace( "&Ouml;", "" );
      out = out.replace( "&oslash;", "" );
      out = out.replace( "&Oslash;", "" );
      out = out.replace( "&szlig;", "" );
      out = out.replace( "&ugrave;", "" );
      out = out.replace( "&Ugrave;", "" );
      out = out.replace( "&ucirc;", "" );
      out = out.replace( "&Ucirc;", "" );
      out = out.replace( "&uuml;", "" );
      out = out.replace( "&Uuml;", "" );
      out = out.replace( "&reg;", "ܮ" );
      out = out.replace( "&copy;", "" );
      out = out.replace( "&euro;", "" );
      out = out.replace( "&nbsp;", " " );
      out = out.replace( "&#180;", "" );
      out = out.replace( "&#96;", "`" );
      out = out.replace( "&#39;", "'" );

      return out;
   }


   private Collection<Place> parseKml(String kml, String country)
   {
      Vector<Place> results = new Vector<Place>();
      if (!kml.contains( "<Placemark>" ))
         return results;
      kml = kml.substring( kml.indexOf( "<Placemark>" ) );
      kml = kml.substring( 0, kml.lastIndexOf( "</Placemark>" ) );

      String[] placemarks = kml.split( "</Placemark>" );
      for (int i = 0; i < placemarks.length; i++)
      {
         if (placemarks[i].startsWith( "<Placemark>" ))
         {
            try
            {
               results.add( parsePlacemark( placemarks[i], country ) );
            }
            catch (Exception e)
            {
               logger.error( "Failed to parse placemark: " + e.getMessage() + " "
                             + placemarks[i] );
            }
         }
      }

      return results;

   }


   private Point parsePlacemark(String placemark)
   {

      Point g = new Point();
      try
      {
         String latitude =
            placemark.substring( placemark.indexOf( "<latitude>" )
                                 + "<latitude>".length(), placemark
               .indexOf( "</latitude>" ) );
         g.setLatitude( Double.parseDouble( latitude ) );
      }
      catch (Exception e)
      {
      }
      ;
      try
      {
         String longitude =
            placemark.substring( placemark.indexOf( "<longitude>" )
                                 + "<longitude>".length(), placemark
               .indexOf( "</longitude>" ) );
         g.setLongitude( Double.parseDouble( longitude ) );
      }
      catch (Exception e)
      {
      }

      return g;
   }


   private Place parsePlacemark(String placemark, String country)
   {

      String name = "N/A";
      String address = "N/A&lt;br/&gt;N/A";
      String coords = "0,0";

      try
      {
         name =
            placemark.substring( placemark.indexOf( "<name>" ) + "<name>".length(),
               placemark.indexOf( "</name>" ) );

         // remove links if any
         logger.info( "name " + name );
      }
      catch (Exception e)
      {
      }

      try
      {
         coords =
            placemark.substring( placemark.indexOf( "<coordinates>" )
                                 + "<coordinates>".length(), placemark
               .indexOf( "</coordinates>" ) );
      }
      catch (Exception e)
      {
      }

      try
      {
         address =
            placemark.substring( placemark.indexOf( "<address>" ) + "<address>".length(),
               placemark.indexOf( "</address>" ) );
      }
      catch (Exception e)
      {
      }

      String[] latLon = coords.split( "," );
      String[] addressArray = address.split( "&lt;br/&gt;" );
      String street = addressArray[0];
      String cityAndPostal = addressArray[1];
      String lat = latLon[1];
      String lon = latLon[0];
      Place result = new Place();

      // remove html character encoding
      name = htmlDecode( name );
      street = htmlDecode( street );
      cityAndPostal = htmlDecode( cityAndPostal );

      // remove html links
      if (name.contains( "<a href" ))
      {
         logger.info( "Removing link from place name '" + name + "'" );
         int i1 = name.indexOf( ">" );
         int i2 = name.indexOf( "</a" );
         if (i1 > 0 && i2 > 0)
         {
            i1 += 1;
            name = name.substring( i1, i2 );
            logger.info( "Link-removed name: '" + name + "'" );
         }
      }

      // if address is encoded in name, split
      if (name.contains( ", " ) && street.equals( "N/A" ))
      {
         logger.info( "Name looks like complete address, splitting: '" + name + "'" );
         String[] addressParts = name.split( ", " );
         if (addressParts.length > 0)
         {
            name = addressParts[0];
         }
         if (addressParts.length > 1)
         {
            street = addressParts[1];
         }
         if (addressParts.length > 2)
         {
            cityAndPostal = addressParts[2];
         }
      }

      // use blanks if no info available
      if (street.equals( "N/A" ))
         street = "";
      if (cityAndPostal.equals( "N/A" ))
         cityAndPostal = "";

      // strip country off the cityAndPostal field if present
      if (country != null && cityAndPostal.endsWith( country ))
      {
         cityAndPostal =
            cityAndPostal.substring( 0, cityAndPostal.length() - country.length() );
      }
      while (cityAndPostal.length() > 1
             && ( cityAndPostal.endsWith( " " ) || cityAndPostal.endsWith( "," )
                  || cityAndPostal.endsWith( ";" ) || cityAndPostal.endsWith( "-" ) ))
      {
         cityAndPostal = cityAndPostal.substring( 0, cityAndPostal.length() - 1 );
      }

      logger.debug( name + " | " + street + " | " + cityAndPostal + " | " + country
                    + " | (" + lat + ", " + lon + ")" );

      String[] cityAndPostalArray = new String[]
      {
         "", ""
      };

      if (cityAndPostal != null && cityAndPostal.length() > 0)
      {
         splitCityAndPostalCode( cityAndPostal, cityAndPostalArray );
      }

      result.setName( name );
      result.setStreet( street );
      result.setCity( cityAndPostalArray[0] );
      result.setPostalCode( cityAndPostalArray[1] );
      result.setCountryCode( CountryCode.getInstance( country ) );
      result.setLatitude( Double.parseDouble( lat ) );
      result.setLongitude( Double.parseDouble( lon ) );
      return result;
   }


   /**
    * Splits the provided city and postal code composite into city and postal code parts.
    * It works under the assumption that the postal code contains numbers and/or only
    * uppercase letters while the city name does not.
    * 
    * @param composite
    *           The city + postal code composite (in any order)
    * @param cityAndPostal
    *           A string array of length 2 where the results wil be stored. City at index
    *           0 and postal code at index 1
    * @return Returns true if the city was in front of the postal code in the input,
    *         otherwise false
    */
   public boolean splitCityAndPostalCode(String composite, String[] cityAndPostal)
   {

      if (composite == null)
         throw new IllegalArgumentException( "City / Postal composite cannot be null" );

      if (composite.length() == 0)
         throw new IllegalArgumentException(
            "City / Postal composite cannot be zero length" );

      if (cityAndPostal == null)
         throw new IllegalArgumentException( "City / Postal array cannot be null" );

      if (cityAndPostal.length != 2)
         throw new IllegalArgumentException( "City / Postal array must be of length 2" );

      // make sure all parts of composite is space-separated
      composite = composite.replace( ",", " " );
      composite = composite.replace( "  ", " " );

      // split into parts
      String[] splits = composite.split( " " );

      // separate parts with numbers
      Vector<String> cityParts = new Vector<String>();
      Vector<String> postalParts = new Vector<String>();
      for (String s : splits)
      {
         if (s.matches( "(?=\\w*\\d)\\w*" ))
         {
            postalParts.add( s );
         }
         else if (s.toUpperCase().equals( s ))
         {
            postalParts.add( s );
         }
         else
         {
            cityParts.add( s );
         }
      }

      // reassemble as city and postal code
      String city = "";
      for (String s : cityParts)
      {
         if (city.length() > 0)
            city += " ";
         city += s;
      }

      String postal = "";
      for (String s : postalParts)
      {
         if (postal.length() > 0)
            postal += " ";
         postal += s;
      }

      cityAndPostal[0] = city;
      cityAndPostal[1] = postal;

      // return display order
      return cityParts.size() > 0 && composite.startsWith( cityParts.elementAt( 0 ) );
   }


   public static void main(String[] args)
   {
      try
      {
         org.apache.log4j.BasicConfigurator.configure();
         GoogleMapsWebService g = new GoogleMapsWebService();
         GeneralLocation gl = new GeneralLocation();
         gl.setCountryCode( CountryCode.IN );
         System.out.println(gl+ " -> "+g.getPosition( gl ));
         if(true)return;

         gl.setCountryCode( CountryCode.DE );
         gl.setRegion( "Bavaria" );
         gl.setCity( "Munich" );
         gl.setArea( "Haidhausen" );
         
         System.out.println(gl+": "+g.getPosition( gl ));

         Point p = new Point( 41.08, -73.9 );
         for (String s : g.getLocation( p ).toStrings( 8, Location.Layer.COORDINATES,
            true, false, false ))
         {
            System.out.println( s );
         }
         

         // strange polish place serach with embedded links in place name
         // String name = "Shiva";
         // String localHints = "Sródmiescie Pradnik Czerwony Województwo Malopolskie";
         // String country = "Poland";
         // Collection<Place> results = g.findPlaces( name, localHints, country );
         // for (Place p : results)
         // {
         // System.out.println( p.getName() + ", " + p.getStreet() + ", " + p.getCity() );
         // }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }


//   /**
//    * @return
//    */
//   public static GoogleLocationService getInstance()
//   {
//      if(instance == null){
//         instance = new GoogleLocationService();
//      }
//      return instance;
//   }


   /**
    * @param gl
    * @return
    */
   public Position getPosition(GeneralLocation gl) throws IOException
   {
      String query = URLEncoder.encode( gl.toString(), "UTF-8" );
      String urlString = String.format(findLocationURL, query);
      //System.out.println(urlString);
      String content = getContent(urlString);
      Location l = getFirstPlacemarkAsLocation(content);
      return l.getPosition();
   }


   /* (non-Javadoc)
    * @see com.buddycloud.location.ReverseGeocodingService#getGeneralLocation(com.buddycloud.geoid.Point)
    */
   public GeneralLocation getGeneralLocation(Point p) throws IOException
   {
      return getLocation( p ).getGeneralLocation();
   }


   /* (non-Javadoc)
    * @see com.buddycloud.location.ReverseGeocodingService#getName()
    */
   public String getName()
   {
      return "Google Maps";
   }
}
