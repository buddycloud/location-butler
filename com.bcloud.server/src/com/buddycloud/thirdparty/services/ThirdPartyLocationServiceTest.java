
package com.buddycloud.thirdparty.services;

import java.io.IOException;
import java.util.Collection;

import com.buddycloud.common.Location;
import com.buddycloud.common.Place;
import com.buddycloud.geoid.Point;
import com.buddycloud.location.CountryCode;

public class ThirdPartyLocationServiceTest
{

   private ThirdPartyLocationService service;


   public ThirdPartyLocationServiceTest(ThirdPartyLocationService service)
   {
      this.service = service;
   }


   public void execute()
   {
      // testGetCoordinatesFromCellInfo();
      testGetCoordinatesFromAddress();
      // testGetLocationNameFromCellInfo();
      testGetLocationFromCoordinates();
      testGetNearbyLocationsFromAddress();
      // testGetNearbyLocationsFromCellInfo();
      testGetNearbyLocationsFromCoordinates();

   }


   // public void testGetCoordinatesFromCellInfo(){
   // int cid = 30649;
   // int lac = 32119;
   // int mnc = 3;
   // int mcc = 262;
   //		
   // System.out.print("Test: getCoordinates("+cid+", "+lac+", "+mnc+", "+mcc+") = ");
   // GeodeticCoordinate coord = null;
   // try {
   // coord = service.getCoordinates(cid,lac, mnc, mcc);
   // System.out.println(coord);
   // } catch (IOException e) {
   // System.out.println(e);
   // }
   // }

   public void testGetCoordinatesFromAddress()
   {
      String street = "Innere Wiener Str. 19";
      String city = "München";
      String pc = "81667";
      CountryCode cc = CountryCode.DE;
      Point coord = null;
      System.out.print( "Test: getCoordinates(" + street + ", " + city + " " + pc + ","
                        + cc + ") = " );
      try
      {
         coord = service.getCoordinates( street, city, pc, cc );
         System.out.println( coord );
      }
      catch (IOException e)
      {
         System.out.println( e );
      }
   }


   // public void testGetLocationNameFromCellInfo(){
   // // munich
   // int cid = 30649;
   // int lac = 32119;
   // int mnc = 3;
   // int mcc = 262;
   //		
   // // london 1
   // lac= 1208;
   // cid=135605364;
   // mcc=234;
   // mnc=20;
   //
   // // london 2
   // lac= 201;
   // cid=48627380;
   // mcc=234;
   // mnc=15;
   // System.out.print("Test: getLocationName("+cid+", "+lac+", "+mnc+", "+mcc+") = ");
   // try {
   // String name = service.getLocationName(cid, lac, mnc, mcc);
   // System.out.println(name);
   // } catch (IOException e) {
   // System.out.println(e);
   // }
   // }

   public void testGetLocationFromCoordinates()
   {
      Point coord = new Point( 48.13, 11.605 );
      System.out.println( "Test: getLocationName" + coord + " = " );
      try
      {
         Location l = service.getLocation( coord );
         System.out.println( l.getCountryCode().getEnglishCountryName() );
         System.out.println( l.getCity() );
         System.out.println( l.getArea() );
         System.out.println( l.getStreet() );
         System.out.println( l.getPostalCode() );
      }
      catch (IOException e)
      {
         System.out.println( e );
      }
   }


   public void testGetNearbyLocationsFromAddress()
   {
      String name = "Hofbraukeller";
      String district = "Mnchen";
      String country = "Germany";
      Collection<Place> locs;
      System.out.println( "Test: getNearbyLocations(" + name + ", " + district + ","
                          + country + ") = {" );
      try
      {
         locs = service.findPlaces( name, district, country );
         if (locs == null)
         {
            System.out.println( "   null" );
         }
         else
         {
            for (Place l : locs)
            {
               System.out.println( "   " + l.getName() + ", " + l.getStreet() + ", "
                                   + l.getCity() + ", " + l.getPostalCode() + ", "
                                   + l.getCountryCode().getEnglishCountryName() + " ("
                                   + l.getLatitude() + ", " + l.getLongitude() + ")" );
            }
         }
      }
      catch (IOException e)
      {
         System.out.println( e );
      }
      System.out.println( "}" );

   }


   public void testGetNearbyLocationsFromCoordinates()
   {
      Point coord = new Point( 48.130844, 11.607181 );
      Collection<Place> locs;
      System.out.println( "Test: getNearbyLocations" + coord + " = {" );
      try
      {
         locs = service.findPlaces( "Lidl", coord );
         if (locs == null)
         {
            System.out.println( "   null" );
         }
         else
         {
            for (Place l : locs)
            {
               System.out.println( "   " + l.getName() + ", " + l.getStreet() + ", "
                                   + l.getCity() + ", " + l.getPostalCode() + ", "
                                   + l.getCountryCode().getEnglishCountryName() + " ("
                                   + l.getLatitude() + ", " + l.getLongitude() + ")" );
            }
         }
      }
      catch (IOException e)
      {
         System.out.println( e );
      }
      System.out.println( "}" );

   }

   // public void testGetNearbyLocationsFromCellInfo(){
   // int cid = 30649;
   // int lac = 32119;
   // int mnc = 3;
   // int mcc = 262;
   // System.out.println("Test: getNearbyLocations("+cid+", "+lac+", "+mnc+", "+mcc+") = ");
   // Collection<Place> locs;
   // try {
   // locs = service.getNearbyLocations("Lidl", cid,lac,mnc,mcc);
   // if(locs==null){
   // System.out.println("   null");
   // }
   // else{
   // for(Place l : locs){
   // System.out.println("   "+l.getName()+", "+l.getStreet()+", "+l.getCityAndPostalCode()+", "+l.getCountry()+" ("+l.getLatitude()+", "+l.getLongitude()+")");
   // }
   // }
   // } catch (IOException e) {
   // System.out.println(e);
   // }
   // System.out.println("}");
   // }
}
