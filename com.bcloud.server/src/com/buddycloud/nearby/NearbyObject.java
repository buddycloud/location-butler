/**
 * 
 */
package com.buddycloud.nearby;

import com.buddycloud.common.GeneralLocation;



/**
 * An encapsulation of an object in a relative distance context
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
 */ 


public class NearbyObject<T>
{
   private T object;
   private GeneralLocation location;
   private int distance;
   
   public String toString(){
      String distanceString;
      if(distance>=1000) distanceString = " ("+distance/1000+"km) ";
      else distanceString = " ("+distance+"m) ";
      if(location!=null){
         return object+": "+location.toString()+distanceString;
      }
      else{
         return object+":"+distanceString;
      }
   }
   
   /**
    * @return the object
    */
   public T getObject()
   {
      return object;
   }
   
   /**
    * @param object the object to set
    */
   public void setObject(T object)
   {
      this.object = object;
   }
   
   /**
    * @return the distance
    */
   public int getDistance()
   {
      return distance;
   }
   
   /**
    * @param distance the distance to set
    */
   public void setDistance(int distance)
   {
      this.distance = distance;
   }

   
   /**
    * @return the location
    */
   public GeneralLocation getLocation()
   {
      return location;
   }

   
   /**
    * @param location the location to set
    */
   public void setLocation(GeneralLocation location)
   {
      this.location = location;
   }

}
