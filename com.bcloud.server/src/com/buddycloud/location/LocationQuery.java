/**
 * 
 */

package com.buddycloud.location;

import java.util.Collection;
import java.util.Locale;
import java.util.Vector;

import com.buddycloud.common.DatabaseObject;

/**
 * Object representing a location query
 * 
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

public class LocationQuery extends DatabaseObject
{

   /**
    * The language in which to return results
    */
   private Locale locale;
   
   /**
    * Whether or not the location results should be published rather than returned to user
    */
   private boolean publish;

   /**
    * Radio beacons observed since last query Required: true
    */
   private Collection<Beacon> beacons;
   
//   /**
//    * The format in which query results are to be delivered Required: false Default:
//    * SIMPLE
//    */
//   private Format format;
   
   /**
    * The horizontal accuracy of latitude and longitude in meters. Required: false Unit:
    * Meters Range: 0 to Double.MAX
    */
   private double accuracy;

   /**
    * The latitude at which the query was assembled. Required: false Unit: Degrees Range:
    * -90 to 90
    */
   private double latitude;

   /**
    * The longitude at which the query was assembled. Required: false Unit: Degrees Range:
    * -180 to 180
    */
   private double longitude;
   
   /**
    * The altitude at which the query was assembled. Required: false Unit: Meters
    */
   private double altitude;

   /**
    * The datum of the latitude and longitude position. Required: false
    */
   private String datum;

   /**
    * The speed at which the query was assembled. Required: false Unit: Meters/second
    */
   private double speed;
   
   /**
    * The bearing relative to true north at which the query was assembled. Required: false Unit: Degrees
    */
   private double bearing;
   
   /**
    * The signal strengths with which each beacon was observed. Must be in same order and
    * equal number of items in collection.
    */
   private Collection<Integer> signalStrengths;

   /**
    * The time at which the query was assembled. Required: true Unit: milliseconds
    * Encoding: Unix epoch
    */
   private long time;


   /**
    * @return the accuracy
    */
   public double getAccuracy()
   {
      return accuracy;
   }


   /**
    * @return the altitude
    */
   public double getAltitude()
   {
      return altitude;
   }


//   /**
//    * @return the format
//    */
//   public Format getFormat()
//   {
//      return format;
//   }
//

   /**
    * @return the beacons
    */
   public Collection<Beacon> getBeacons()
   {
      return beacons;
   }


   /**
    * Get beacons of a specific type
    * 
    * @return the beacons
    */
   public Collection<Beacon> getBeacons(Beacon.Type type)
   {
      Vector<Beacon> v = new Vector<Beacon>();
      for (Beacon b : beacons)
      {
         if (b.getType() == type)
         {
            v.add( b );
         }
      }
      return v;
   }


   /**
    * @return the bearing
    */
   public double getBearing()
   {
      return bearing;
   }


   /**
    * @return the datum
    */
   public String getDatum()
   {
      return datum;
   }


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


//   /**
//    * @param format
//    *           the format to set
//    */
//   public void setFormat(Format format)
//   {
//      this.format = format;
//   }
//

   /**
    * @return the signalStrengths
    */
   public Collection<Integer> getSignalStrengths()
   {
      return signalStrengths;
   }


   /**
    * @return the speed
    */
   public double getSpeed()
   {
      return speed;
   }


   /**
    * @return the time
    */
   public long getTime()
   {
      return time;
   }


   /**
    * @return the publish
    */
   public boolean isPublish()
   {
      return publish;
   }


   public boolean isValid()
   {
      if (time < 1215400000000L)
         return false;
      if (beacons.size() > 0)
      {
         if (beacons.size() != signalStrengths.size())
            return false;
      }
      else if (latitude == 0 && longitude == 0)
      {
         return false;
      }

      return true;
   }


   
   /**
    * @param accuracy
    *           the accuracy to set
    */
   public void setAccuracy(double accuracy)
   {
      this.accuracy = accuracy;
   }


   
   /**
    * @param altitude the altitude to set
    */
   public void setAltitude(double altitude)
   {
      this.altitude = altitude;
   }


   
   /**
    * @param beacons
    *           the beacons to set
    */
   public void setBeacons(Collection<Beacon> beacons)
   {
      this.beacons = beacons;
   }


   
   /**
    * @param bearing the bearing to set
    */
   public void setBearing(double bearing)
   {
      this.bearing = bearing;
   }


   
   /**
    * @param datum the datum to set
    */
   public void setDatum(String datum)
   {
      this.datum = datum;
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
    * @param publish the publish to set
    */
   public void setPublish(boolean publish)
   {
      this.publish = publish;
   }


   
   /**
    * @param signalStrengths
    *           the signalStrengths to set
    */
   public void setSignalStrengths(Collection<Integer> signalStrengths)
   {
      this.signalStrengths = signalStrengths;
   }


   
   /**
    * @param speed the speed to set
    */
   public void setSpeed(double speed)
   {
      this.speed = speed;
   }


   
   /**
    * @param time
    *           the time to set
    */
   public void setTime(long time)
   {
      this.time = time;
   }


   
   /**
    * @return the locale
    */
   public Locale getLocale()
   {
      return locale;
   }


   
   /**
    * @param locale the locale to set
    */
   public void setLocale(Locale locale)
   {
      this.locale = locale;
   }

}
