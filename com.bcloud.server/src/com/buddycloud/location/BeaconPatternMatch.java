/**
 * 
 */
package com.buddycloud.location;

import com.buddycloud.common.Place;



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
 *Licensed as Apache v2.0
 * 
 *
 */


public class BeaconPatternMatch
{

   private Place place;
   private int patternId;
   private int patternMatch;
   private Beacon.Type beaconType;

   /**
    * @param place
    */
   public void setPlace(Place place)
   {
      this.place = place;
      
   }

   /**
    * @param id
    */
   public void setPatternId(int patternId)
   {
      this.patternId = patternId;
      
   }

   /**
    * @param i
    */
   public void setPatternMatch(int patternMatch)
   {
      this.patternMatch = patternMatch;
      
   }

   
   /**
    * @return the place
    */
   public Place getPlace()
   {
      return place;
   }

   
   /**
    * @return the patternId
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
    * @return the beaconType
    */
   public Beacon.Type getBeaconType()
   {
      return beaconType;
   }

   
   /**
    * @param beaconType the beaconType to set
    */
   public void setBeaconType(Beacon.Type beaconType)
   {
      this.beaconType = beaconType;
   }

}
