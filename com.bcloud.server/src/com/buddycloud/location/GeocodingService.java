/**
 * 
 */
package com.buddycloud.location;

import java.io.IOException;

import com.buddycloud.common.GeneralLocation;
import com.buddycloud.geoid.Position;


/**
 * Interface defining the required features of the geocoding services with which the buddycloud location butler can work
 * 
 * In addition to the methods declared here, implementation MUST provide a default (no-args) constructor, as this may be invoked reflectively.
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
 */ 


public interface GeocodingService
{
  
   /**
    * @return Returns the service name
    */
   public String getName();
   
   /**
    * Returns the position corresponding to the specified hierarchical location names (area, city, region and country code)
    * 
    * @param gl The location names for which geocoding is to be performed
    * @throws If the lookup failed due to IO problems (network of file system)
    * @return The 4-tier hierarchical location names at the given point
    */
   public Position getPosition(GeneralLocation gl) throws IOException;

}
