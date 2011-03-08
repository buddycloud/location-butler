/**
 * 
 */

package com.buddycloud.common;

import java.util.Collection;
import java.util.Vector;

import com.buddycloud.geoid.Point;
import com.buddycloud.geoid.Position;
import com.buddycloud.location.CountryCode;

/**
 * A Location in short is the whereabouts of a user. It is layered as follows (in order of
 * ascending accuracy) - Country Accuracy Order ~100,000 m - City ~10,000 m - Area (part
 * of town) ~1,000 m - Place or Address (street + postal code) ~100 m - Coordinate
 * (latitude + longitude) ~10 m
 * 
 * Ideally location is a continuous stream that follows a user as he moves about.
 * Practically updates only need to be sent when one the value of one of the layers
 * change.
 * 
 * The location is also associated with a motion state: Moving or Stationary (or
 * "Restless" if inbetween
 * 
 * The location can be represented by a simple label that is drawn from place name, area,
 * city, country (whatever is available) and the motion state together.
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
public class Location
{

   public enum Layer {
      AREA(1000), CITY(10000), COORDINATES(50), COUNTRY(1000000), PLACE(100), REGION(
         100000), STREET(500);

      private final int defaultAccuracy;
      

      private Layer(int defaultAccuracy)
      {
         this.defaultAccuracy = defaultAccuracy;
      }


      public int getDefaultError()
      {
         return defaultAccuracy;
      }
   };

   public enum MotionState {
      MOVING, RESTLESS, STATIONARY
   };

   private double accuracy;

   private String area;

   private String city;

   private CountryCode countryCode;

   private long entryTime;

   private double latitude;

   private double longitude;

   private MotionState motionState;

   private int patternId;

   private int patternMatch;
   
   private int cellPatternQuality;

   private int placeId;

   private String placeName;

   private String postalCode;

   private String region;

   private String street;


   public Location()
   {
      this.motionState = MotionState.RESTLESS;
   }


   /**
    * @return the location accuracy in meters
    */
   public double getAccuracy()
   {

      if (accuracy <= 0)
         return this.getMostAccurateLayer().defaultAccuracy;
      return accuracy;
   }


   /**
    * @return the area
    */
   public String getArea()
   {

      return area;
   }


   /**
    * @return the city
    */
   public String getCity()
   {

      return city;
   }


   /**
    * @return the countryCode
    */
   public CountryCode getCountryCode()
   {

      return countryCode;
   }


   /**
    * @return the entryTime
    */
   public long getEntryTime()
   {
      return entryTime;
   }


   /**
    * @return the label
    */
   public String getLabel()
   {

      String label = new String();

      if (motionState != MotionState.MOVING && placeName != null
          && placeName.trim().length() > 0)
      {
         if (patternMatch >= LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH
             && motionState == MotionState.STATIONARY)
            label = placeName;
         else
            label = "Near " + placeName;
      }
      else
      {
         label = toString( 1, Layer.AREA );
         if (label != null && label.trim().length() > 0)
         {
            if (motionState == MotionState.STATIONARY)
            {
               label = "Somewhere new in " + label;
            }
            if (motionState == MotionState.MOVING)
            {
               label = "On the road in " + label;
            }
         }
      }

      return label;

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


   public Layer getMostAccurateLayer()
   {
      if (latitude != 0 || longitude != 0)
         return Layer.COORDINATES;
      else if (placeId > 0)
         return Layer.PLACE;
      else if (street != null)
         return Layer.STREET;
      else if (area != null)
         return Layer.AREA;
      else if (region != null)
         return Layer.REGION;
      else if (city != null)
         return Layer.CITY;
      else
         return Layer.COUNTRY;
   }


   /**
    * @return the state
    */
   public MotionState getMotionState()
   {

      return motionState;
   }


   /**
    * @return the placePatternId
    */
   public int getPatternId()
   {
      return patternId;
   }


   /**
    * @return the patternMatch
    */
   public int getPatternMatch()
   {
      return patternMatch;
   }


   /**
    * @return the placeId
    */
   public int getPlaceId()
   {

      return placeId;
   }


   /**
    * @return the placeName
    */
   public String getPlaceName()
   {

      return placeName;
   }


   /**
    * @return the postalCode
    */
   public String getPostalCode()
   {

      return postalCode;
   }


   /**
    * @return the region
    */
   public String getRegion()
   {

      return region;
   }


   /**
    * @return the street
    */
   public String getStreet()
   {

      return street;
   }


   /**
    * Convenience method for motion state info
    * 
    * @return true id motion state is MOVING
    */
   public boolean isMoving()
   {
      return motionState == MotionState.MOVING;
   }


   /**
    * Returns true if this location is at a known place
    * 
    * @return true if place id > 0 AND pattern match >= 80% AND motion state is stationary
    */
   public boolean isPlaceFix()
   {
      return placeId > 0 && patternMatch >= LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH
             && motionState == MotionState.STATIONARY;
   }


   /**
    * Returns true if location references a place but is not at the place
    * 
    * @return true if location is not a place fix
    */
   public boolean isPlaceNearby()
   {
      return placeId > 0 && patternMatch < LocationConstants.PLACE_FIX_MIN_PATTERN_MATCH
             && motionState != MotionState.STATIONARY;
   }


   /**
    * Convenience method for motion state info
    * 
    * @return true if motion state is RESTLESS
    */
   public boolean isRestless()
   {
      return motionState == MotionState.RESTLESS;
   }


   /**
    * Convenience method for motion state info
    * 
    * @return true if motion state is STATIONARY
    */
   public boolean isStationary()
   {
      return motionState == MotionState.STATIONARY;
   }


   /**
    * Specifies the location accuracy in meters
    * 
    * @param accuracy
    *           the accuracy to set
    */
   public void setAccuracy(double accuracy)
   {

      this.accuracy = accuracy;
   }


   /**
    * @param area
    *           the area to set
    */
   public void setArea(String area)
   {

      this.area = area;
   }


   /**
    * @param city
    *           the city to set
    */
   public void setCity(String city)
   {

      this.city = city;
   }


   /**
    * @param countryCode
    *           the countryCode to set
    */
   public void setCountryCode(CountryCode countryCode)
   {

      this.countryCode = countryCode;
   }


   /**
    * @param entryTime
    *           the entryTime to set
    */
   public void setEntryTime(long entryTime)
   {
      this.entryTime = entryTime;
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
    * @param state
    *           the state to set
    */
   public void setMotionState(MotionState state)
   {

      this.motionState = state;
   }


   /**
    * @param placePatternId
    *           the placePatternId to set
    */
   public void setPatternId(int patternId)
   {
      this.patternId = patternId;
   }


   /**
    * @param patterMatch
    *           the patternMatch to set
    */
   public void setPatternMatch(int patternMatch)
   {
      this.patternMatch = patternMatch;
   }


   public void setPlace(Place place)
   {
      if (place != null)
      {

         setPlaceId( place.getId() );
         setPlaceName( place.getName() );
         setCountryCode( place.getCountryCode() );
         setRegion( place.getRegion() );
         setCity( place.getCity() );
         setArea( place.getArea() );
         setStreet( place.getStreet() );
         setPostalCode( place.getPostalCode() );
         setLatitude( place.getLatitude() );
         setLongitude( place.getLongitude() );

      }
   }


   /**
    * @param placeId
    *           the placeId to set
    */
   public void setPlaceId(int placeId)
   {

      this.placeId = placeId;
   }


   /**
    * @param place
    *           the placeName to set
    */
   public void setPlaceName(String placeName)
   {

      this.placeName = placeName;
   }


   /**
    * @param postalCode
    *           the postalCode to set
    */
   public void setPostalCode(String postalCode)
   {

      this.postalCode = postalCode;
   }


   /**
    * @param street
    *           the street to set
    */
   public void setRegion(String region)
   {

      this.region = region;
   }


   /**
    * @param street
    *           the street to set
    */
   public void setStreet(String street)
   {

      this.street = street;
   }



   public String toString()
   {
      StringBuffer b = new StringBuffer();
      b.append( getLabel() );
      if(placeId>0){
         b.append( " (pid " );
         b.append( placeId );
         b.append( ")" );
      }
      if(area!=null){
         b.append(", ");
         b.append(area);
      }
      if(city!=null){
         b.append(", ");
         b.append(city);
      }
      if(region!=null){
         b.append(", ");
         b.append(region);
      }
      if(countryCode!=null){
         b.append(", ");
         b.append(countryCode.getEnglishCountryName());
      }
      
      b.append("[" + latitude + ", " + longitude + " @ " + accuracy + "m]");
      
      return b.toString();
   }


   public String toString(int layers, Layer detailLimit)
   {
      Collection<String> strings = toStrings( layers, detailLimit, false, false, false );
      String string = "";
      for (String s : strings)
      {
         if (string.length() > 0)
            string += ", ";
         string += s;
      }

      return string;
   }


   public String toString(int layers, Layer detailLimit, boolean addLabels,
      boolean includeNulls, boolean includeExtraInfo)
   {
      Collection<String> strings =
         toStrings( layers, detailLimit, addLabels, includeNulls, includeExtraInfo );
      String string = "";
      for (String s : strings)
      {
         if (s != null)
         {
            if (string.length() > 0)
               string += ", ";
            string += s;
         }
      }

      return string;
   }


   /**
    * Returns a string representation of the specified layer of this location, or null if
    * not set.
    * 
    * @param layer
    * @return
    */
   public String toString(Layer layer)
   {
      String s = null;
      if (layer == Layer.COORDINATES && ( latitude != 0 || longitude != 0 ))
      {
         s = "(" + latitude + ", " + longitude + " @ " + accuracy + "m)";
      }
      else if (layer == Layer.PLACE && placeName != null && placeName.trim().length() > 0)
      {
         s = placeName +"(ID "+placeId+")";
      }
      else if (layer == Layer.STREET && street != null && street.trim().length() > 0)
      {
         s = street;
      }
      else if (layer == Layer.AREA && area != null && area.trim().length() > 0)
      {
         s = area;
      }
      else if (layer == Layer.CITY && city != null && city.trim().length() > 0)
      {
         s = city;
      }
      else if (layer == Layer.REGION && region != null && region.trim().length() > 0)
      {
         s = region;
      }
      else if (layer == Layer.COUNTRY && countryCode != null)
      {
         s = countryCode.getEnglishCountryName();
      }
      return s;
   }


   public Collection<String> toStrings()
   {
      return toStrings( 10, Layer.COORDINATES, false, false, false );
   }


   /**
    * Returns collection of strings representing this location
    * 
    * @param layers
    *           The number of layers to include
    * @param detailLimit
    *           The smallest (most accurate) layer to include
    * @param addLabels
    *           Whether or not to add labels to each string (i.e. "AREA: Soho" rather than
    *           just "Soho")
    * @param includeNulls
    *           , whether or not to include parameters not set
    * @param includeExtraInfo
    *           , whether or not to include motion state, pattern id, pattern match
    * @return
    */
   public Collection<String> toStrings(int layers, Layer detailLimit, boolean addLabels,
      boolean includeNulls, boolean includeExtraInfo)
   {
      Vector<String> strings = new Vector<String>();

      Layer[] preferredOrder =
         {
            Layer.PLACE, Layer.STREET, Layer.AREA, Layer.CITY, Layer.REGION,
            Layer.COUNTRY, Layer.COORDINATES
         };

      for (Layer layer : preferredOrder)
      {
         String l = toString( layer );
         if (( l != null || includeNulls ) && strings.size() < layers
             && layer.defaultAccuracy >= detailLimit.defaultAccuracy)
         {
            if (addLabels)
            {
               strings.add( layer.toString() + ": " + l );
            }
            else
            {
               strings.add( l );
            }
         }
      }

      if (includeExtraInfo)
      {
         if (addLabels)
         {
            strings.add( "MOTION STATE: " + motionState );
            strings.add( "CELL PATTERN QUALITY: " + cellPatternQuality );
            strings.add( "PATTERN ID: " + patternId );
            strings.add( "PATTERN MATCH: " + patternMatch );
         }
         else
         {
            strings.add( "" + motionState );
            strings.add( "" + cellPatternQuality );
            strings.add( "" + patternId );
            strings.add( "" + patternMatch );
         }
      }
      
      // Note: Do not ad label to list of strings, calling getLabel() here will lead to an infinite recursion (learned the hard way)
      return strings;
   }


   /**
    * @return the latitude and longitude of this location as a point
    */
   public Point getPoint()
   {
      return new Point( latitude, longitude );
   }


   /**
    * @return the latitude and longitude and accuracy of this location as a position
    */
   public Position getPosition()
   {
      return new Position( latitude, longitude, accuracy );
   }


   
   /**
    * @return the cellPatternQuality
    */
   public int getCellPatternQuality()
   {
      return cellPatternQuality;
   }


   
   /**
    * @param cellPatternQuality the cellPatternQuality to set
    */
   public void setCellPatternQuality(int cellPatternQuality)
   {
      this.cellPatternQuality = cellPatternQuality;
   }


   /**
    * @return A General (area, city, region, country) representation of this location
    */
   public GeneralLocation getGeneralLocation()
   {
      GeneralLocation gl = new GeneralLocation();
      gl.setArea( area );
      gl.setCity( city );
      gl.setRegion( region );
      gl.setCountryCode( countryCode );
      return gl;
   }


}
