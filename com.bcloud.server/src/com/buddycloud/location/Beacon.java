/**
 * 
 */

package com.buddycloud.location;

import com.buddycloud.common.DatabaseObject;
import com.buddycloud.geoid.PositionedObject;

/**
 * 
 * Class representing a beacon (Cell, Wifi or Bluetooth)
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
 * limitations under the License.
 *
 *
 */ 

public class Beacon extends DatabaseObject implements PositionedObject
{

//   /**
//    * Range above which a cell is either movable or so big that it is of no use for
//    * positioning
//    */
//   public static final int RANGE_LIMIT_CELL = 10000;
//
//   /**
//    * Range above which a wifi access point is either movable or so big that it is of no
//    * use for positioning
//    */
//   public static final int RANGE_LIMIT_WIFI = 500;

   public enum Encoding {
      IEEE_802_1, OPEN, UNKNOWN, WEP, WPA, WPA_PSK, WPA2
   };

   /**
    * The source from which the position of this beacon was derived
    */
   public enum PositionSource {
      /**
       * Position derived general location, which in turn is derived from cell area code
       * or nearby beacons whose general locations have been set
       */
      GENERAL,

      /**
       * Position derived from the address / coordinates of places where this beacon has
       * been used in place mark pattern
       */
      PLACE,

      /** Position derived from GPS coordinates where this beacon has been observerd */
      GPS,

      /** Position obtained from google location service */
      GOOGLE

   };

   public enum Type {
      BLUETOOTH, CELL, WIFI, WIMAX
   };

   // /** The city-area where this beacon is located */
   // private String area;
   //
   // /** The city where this beacon is located */
   // private String city;

   /** The country where this beacon is located */
   private CountryCode countryCode;

   // /** The encoding of this beacon */
   // private Encoding encoding;

   /**
    * Flag specifying if this is a fixed beacon (true) or a movable one (false). Movable
    * beacons are typically ad-hoc hotspots or repeaters installed in trains etc
    */
   private boolean isFixed;

   /** The latitude of the beacon, 0.0 if not known */
   private double latitude;

   /** The longitude of the beacon, 0.0 if not known */
   private double longitude;

   /** The range of the beacon in meters */
   private double range;

   /** The mac addres of the beacon */
   private String mac;

   private PositionSource positionSource;

   /** The beacon type */
   private Type type;


   /**
    * Constructs a new beacon and sets it to fixed by default
    */
   public Beacon()
   {
      isFixed = true;
   }


   // /**
   // * @return the area
   // */
   // public String getArea()
   // {
   // return area;
   // }

   public int getLac()
   {
      if (type == Type.CELL)
      {
         String splits[] = mac.split( ":" );
         return Integer.parseInt( splits[2] );
      }
      else
      {
         throw new IllegalArgumentException(
            "Only Cell-type beacons have country codes..." );
      }
   }


   public int getCid()
   {
      if (type == Type.CELL)
      {
         String splits[] = mac.split( ":" );
         return Integer.parseInt( splits[3] );
      }
      else
      {
         throw new IllegalArgumentException(
            "Only Cell-type beacons have country codes..." );
      }
   }


   // /**
   // * @return the city
   // */
   // public String getCity()
   // {
   // return city;
   // }

   /**
    * @return the country
    */
   public CountryCode getCountryCode()
   {
      if (countryCode == null && type == Type.CELL)
      {
         countryCode = CountryCode.getInstanceFromMCC( getMcc() );

      }
      return countryCode;
   }


   public int getMcc()
   {
      if (type == Type.CELL)
      {
         String splits[] = mac.split( ":" );
         return Integer.parseInt( splits[0] );
      }
      else
      {
         throw new IllegalArgumentException(
            "Only Cell-type beacons have mobile country codes..." );
      }
   }


   // /**
   // * @return the encoding
   // */
   // public Encoding getEncoding()
   // {
   // return encoding;
   // }

   // public String getCountry() {
   // return country;
   // }
   //
   // public void setCountry(String country) {
   // this.country = country;
   // }
   //
   // public String getCity() {
   // return city;
   // }
   //
   // public void setCity(String city) {
   // this.city = city;
   // }
   //
   // public String getArea() {
   // return area;
   // }
   //
   // public void setArea(String area) {
   // this.area = area;
   // }
   //	
   // public GeneralLocation getGeneralLocation()
   // {
   // GeneralLocation gl = new GeneralLocation();
   // gl.setArea( area );
   // gl.setCity( city );
   // gl.setCountry( country );
   // return gl;
   // }

   /**
    * @return the latitude
    */
   public double getLatitude()
   {
      return latitude;
   }


   /**
    * @return the longitude
    */
   public double getLongitude()
   {
      return longitude;
   }


   /**
    * @return the mac
    */
   public String getMac()
   {
      return mac;
   }


   public int getMnc()
   {
      if (type == Type.CELL)
      {
         String splits[] = mac.split( ":" );
         return Integer.parseInt( splits[1] );
      }
      else
      {
         throw new IllegalArgumentException(
            "Only Cell-type beacons have country codes..." );
      }
   }


   /**
    * @return the positionSource
    */
   public PositionSource getPositionSource()
   {
      return positionSource;
   }


   /**
    * @return the type
    */
   public Type getType()
   {
      return type;
   }


   /**
    * The unique area code is MCC:MNC:LAC
    * 
    * @return
    */
   public String getMccMncLac()
   {
      if (type == Type.CELL)
      {
         return mac.substring( 0, mac.lastIndexOf( ":" ) );
      }
      else
      {
         throw new IllegalArgumentException(
            "Only Cell-type beacons have country codes..." );
      }
   }


   /**
    * @return the isFixed
    */
   public boolean isFixed()
   {
      if (!isFixed)
         return false;
//      if (type == Type.CELL && range > RANGE_LIMIT_CELL)
//         return false;
//      if (type == Type.WIFI && range > RANGE_LIMIT_WIFI)
//         return false;
      return true;
   }


   // /**
   // * @param area
   // * the area to set
   // */
   // public void setArea(String area)
   // {
   // this.area = area;
   // }
   //
   //
   // /**
   // * @param city
   // * the city to set
   // */
   // public void setCity(String city)
   // {
   // this.city = city;
   // }

   /**
    * @param country
    *           the country to set
    */
   public void setCountryCode(CountryCode countryCode)
   {
      this.countryCode = countryCode;
   }


   // /**
   // * @param encoding
   // * the encoding to set
   // */
   // public void setEncoding(Encoding encoding)
   // {
   // this.encoding = encoding;
   // }
   //

   /**
    * @param isFixed
    *           the isFixed to set
    */
   public void setFixed(boolean isFixed)
   {
      this.isFixed = isFixed;
   }


   /**
    * @param latitude
    *           the latitude to set
    */
   public void setLatitude(double latitude)
   {
      this.latitude = latitude;
   }


   /**
    * @param longitude
    *           the longitude to set
    */
   public void setLongitude(double longitude)
   {
      this.longitude = longitude;
   }


   /**
    * @param mac
    *           the mac to set
    */
   public void setMac(String mac)
   {
      this.mac = mac;
   }


   /**
    * @param positionSource
    *           the positionSource to set
    */
   public void setPositionSource(PositionSource positionSource)
   {
      this.positionSource = positionSource;
   }


   /**
    * @param type
    *           the type to set
    */
   public void setType(Type type)
   {
      this.type = type;
   }


   @Override
   public boolean equals(Object o)
   {
      if (o instanceof Beacon)
      {
         Beacon other = (Beacon) o;

         if (this.getId() != other.getId())
         {
            return false;
         }
         if (this.getLatitude() != other.getLatitude())
         {
            return false;
         }
         if (this.getLongitude() != other.getLongitude())
         {
            return false;
         }
         if (this.getRange() != other.getRange())
         {
            return false;
         }
         if (this.getType() == null && other.getType() != null)
         {
            return false;
         }
         if (this.getType() != other.getType())
         {
            return false;
         }

         if (this.getMac() == null && other.getMac() != null)
         {
            return false;
         }
         if (this.getMac() != null && !this.getMac().equals( other.getMac() ))
         {
            return false;
         }

         if (this.getCountryCode() == null && other.getCountryCode() != null)
         {
            return false;
         }
         // if (this.getCountry() != null && !this.getCountry().equals( other.getCountry()
         // ))
         // {
         // return false;
         // }
         //
         // if (this.getCity() == null && other.getCity() != null)
         // {
         // return false;
         // }
         // if (this.getCity() != null && !this.getCity().equals( other.getCity() ))
         // {
         // return false;
         // }
         //
         // if (this.getCity() == null && other.getCity() != null)
         // {
         // return false;
         // }
         // if (this.getCity() != null && !this.getCity().equals( other.getCity() ))
         // {
         // return false;
         // }
         //
         // if (this.getArea() == null && other.getArea() != null)
         // {
         // return false;
         // }
         // if (this.getArea() != null && !this.getArea().equals( other.getArea() ))
         // {
         // return false;
         // }
         //
         // if (this.getEncoding() == null && other.getEncoding() != null)
         // {
         // return false;
         // }
         // if (this.getEncoding() != null
         // && !this.getEncoding().equals( other.getEncoding() ))
         // {
         // return false;
         // }

         if (this.getPositionSource() == null && other.getPositionSource() != null)
         {
            return false;
         }
         if (this.getPositionSource() != null
             && !this.getPositionSource().equals( other.getPositionSource() ))
         {
            return false;
         }

         return true;

      }
      else
      {
         return false;
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return String.format( "%s %s", type.toString(), mac );
   }


   public String toLongString()
   {
      return String.format( "%s %s [ID %d] (%f, %f @%dm), src=%s, %s", type, mac,
         getId(), latitude, longitude, (int) range, positionSource, isFixed ? "fixed"
            : "movable" );
   }


   public static void main(String[] args)
   {
      Beacon b1 = new Beacon();
      b1.setMac( "00:18:84:1C:CC:ED" );
      b1.setType( Beacon.Type.WIFI );
      Beacon b2 = new Beacon();
      b2.setMac( "00:18:84:1C:CC:ED" );
      b2.setType( Beacon.Type.WIFI );
      // b1.setCity( "a" );
      // b2.setCity( "b" );

      System.out.println( "b1: " + b1.toLongString() );
      System.out.println( "b2: " + b2.toLongString() );
      System.out.println( "b1.equals(b2): " + b1.equals( b2 ) );

   }


   /**
    * @return the range
    */
   public double getRange()
   {
      return range;
   }


   /**
    * @param range
    *           the range to set
    */
   public void setRange(double range)
   {
      this.range = range;
   }

}
