/**
 * 
 */
package com.buddycloud.geoid;


/**
 * Interface for positioned objects
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


public interface PositionedObject
{
   /**
    * @return The latitude of the object in degrees, range -90 to 90
    */
   public double getLatitude();

   /**
    * @return The longitude of the object in degrees, range -180 to 180
    */
   public double getLongitude();

//   /**
//    * @return The horizontal accuracy of the position in meters
//    */
//   public double getAccuracy();

}
