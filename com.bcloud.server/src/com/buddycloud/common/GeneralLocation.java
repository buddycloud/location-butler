
package com.buddycloud.common;

import com.buddycloud.location.CountryCode;

/**
 * A general location is country and/or city and/or area within city
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
public class GeneralLocation
{

   private String area;

   private String city;

   private String region;

   private CountryCode countryCode;


   public boolean equals(Object o)
   {
      if (o instanceof GeneralLocation)
      {
         GeneralLocation other = (GeneralLocation) o;
         if (this.countryCode == null && other.countryCode != null)
            return false;
         if (this.region == null && other.region != null)
            return false;
         if (this.city == null && other.city != null)
            return false;
         if (this.area == null && other.area != null)
            return false;
         if (this.countryCode != null && this.countryCode != other.countryCode )
            return false;
         if (this.region != null && !this.region.equals( other.region ))
            return false;
         if (this.city != null && !this.city.equals( other.city ))
            return false;
         if (this.area != null && !this.area.equals( other.area ))
            return false;
         return true;
      }
      else
      {
         return false;
      }

   }


   public String getArea()
   {
      return area;
   }


   public String getCity()
   {
      return city;
   }


   public CountryCode getCountryCode()
   {
      return countryCode;
   }


   public String getRegion()
   {
      return region;
   }


   @Override
   public int hashCode()
   {
      return toString().hashCode();
   }


   public void setArea(String area)
   {
      this.area = area;
   }


   public void setCity(String city)
   {
      this.city = city;
   }


   public void setCountryCode(CountryCode countryCode)
   {
      this.countryCode = countryCode;
   }


   public void setRegion(String region)
   {
      this.region = region;
   }


   public String toShortString()
   {
      if (area != null && area.length() > 0)
         return area;
      else if (city != null && city.length() > 0)
         return city;
      else if (region != null && region.length() > 0)
         return region;
      else if (countryCode != null)
         return countryCode.getEnglishCountryName();
      else
         return "";
   }


   public String toString()
   {
      String s = "";
      if (area != null)
         s += area;
      if (s.length() > 0 && city != null && city.length() > 0)
         s += ", ";
      if (city != null && city.length() > 0)
         s += city;
      if (s.length() > 0 && region != null && region.length() > 0)
         s += ", ";
      if (region != null && region.length() > 0)
         s += region;
      if (s.length() > 0 && countryCode != null)
         s += ", ";
      if (countryCode != null)
         s += countryCode.getEnglishCountryName();

      return s;
   }

}
