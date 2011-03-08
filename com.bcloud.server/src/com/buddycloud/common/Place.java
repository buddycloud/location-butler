/**
 * 
 */

package com.buddycloud.common;

import com.buddycloud.geoid.Point;
import com.buddycloud.location.CountryCode;

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

public class Place extends DatabaseObject
{

   private double accuracy;

   private String area;

   private String city;

   private CountryCode countryCode;

   private String description;

   private boolean isPublic;

   private double latitude;

   private double longitude;

   private String name;

   private int ownerId;

   private String postalCode;

   private String region;

   private String siteUrl;

   private String street;

   private String wikiUrl;
   
   private int revision;


   /**
    * @return the accuracy
    */
   public double getAccuracy()
   {
      return accuracy;
   }


   public String getArea()
   {
      return area;
   }


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


   public String getDescription()
   {
      return description;
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


   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }


   /**
    * @return the owner
    */
   public int getOwnerId()
   {
      return ownerId;
   }


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


   public String getSiteUrl()
   {
      return siteUrl;
   }


   public String getStreet()
   {
      return street;
   }


   public String getWikiUrl()
   {
      return wikiUrl;
   }


   public boolean hasSamePositionAs(Place other)
   {
      if (this.latitude == 0.0 || other.latitude == 0.0)
         return false;
      if (this.longitude == 0.0 || other.longitude == 0.0)
         return false;
      Point a = new Point( this.latitude, this.longitude );
      Point b = new Point( other.latitude, other.longitude );
      return a.getDistanceTo( b ) < 5.0;
   }


   /**
    * @return the isPublic
    */
   public boolean isPublic()
   {
      return isPublic;
   }


   /**
    * @param accuracy the accuracy to set
    */
   public void setAccuracy(double accuracy)
   {
      this.accuracy = accuracy;
   }


   public void setArea(String area)
   {
      this.area = area;
   }


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


   public void setDescription(String description)
   {
      this.description = description;
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
    * @param name
    *           the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }


   /**
    * @param owner
    *           the owner to set
    */
   public void setOwnerId(int ownerId)
   {
      this.ownerId = ownerId;
   }

   public void setPostalCode(String postalCode)
   {
      this.postalCode = postalCode;
   }


   /**
    * @param isPublic
    *           the isPublic to set
    */
   public void setPublic(boolean isPublic)
   {
      this.isPublic = isPublic;
   }


   // public boolean hasSameAddressAs(Place other) {
   // return this.getAddress().equals( other.getAddress() );
   // }
   //   
   // public String getAddress(){
   // String address = "";
   // if(street!=null && street.trim().length()==0) address += street;
   // if(postalCode!=null && postalCode.trim().length()==0){
   // if(address.length()>0 ) address += ", ";
   // address += postalCode;
   // }
   // if(city!=null && city.trim().length()==0){
   // if(address.length()>0 ) address += ", ";
   // address += city;
   // }
   // if(country!=null && country.trim().length()==0){
   // if(address.length()>0 ) address += ", ";
   // address += country;
   // }
   //      
   // return address;
   // }

   /**
    * @param region
    *           the region to set
    */
   public void setRegion(String region)
   {
      this.region = region;
   }


   public void setSiteUrl(String siteUrl)
   {
      this.siteUrl = siteUrl;
   }


   public void setStreet(String street)
   {
      this.street = street;
   }


   
   public void setWikiUrl(String wikiUrl)
   {
      this.wikiUrl = wikiUrl;
   }


   
   /**
    * Fills in any unset fields with values from the provided location
    * 
    * @param other
    *           The location to grab information from
    */
   public void updateUnsetFields(Place other)
   {
      if (this.latitude == 0)
         this.latitude = other.latitude;
      if (this.longitude == 0)
         this.longitude = other.longitude;
      if (this.ownerId == 0)
         this.ownerId = other.ownerId;
      if (this.accuracy == 0)
         this.accuracy = other.accuracy;
      if (this.countryCode == null)
         this.countryCode = other.countryCode;
      if (this.name == null || this.name.length() == 0)
         this.name = other.name;
      if (this.description == null || this.description.length() == 0)
         this.description = other.description;
      if (this.street == null || this.street.length() == 0)
         this.street = other.street;
      if (this.area == null || this.area.length() == 0)
         this.area = other.area;
      if (this.city == null || this.city.length() == 0)
         this.city = other.city;
      if (this.postalCode == null || this.postalCode.length() == 0)
         this.postalCode = other.postalCode;
      if (this.region == null || this.region.length() == 0)
         this.region = other.region;
      if (this.siteUrl == null || this.siteUrl.length() == 0)
         this.siteUrl = other.siteUrl;
      if (this.wikiUrl == null || this.wikiUrl.length() == 0)
         this.wikiUrl = other.wikiUrl;

   }
   
   public String toString(){
      return name + "(ID "+getId()+")";
   }


   
   /**
    * @return the revision
    */
   public int getRevision()
   {
      return revision;
   }


   
   /**
    * @param revision the revision to set
    */
   public void setRevision(int revision)
   {
      this.revision = revision;
   }


   /**
    * @return A General (area, city, region, country) representation of this place
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
