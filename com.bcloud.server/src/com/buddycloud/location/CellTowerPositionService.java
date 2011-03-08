/**
 * 
 */
package com.buddycloud.location;

import java.io.IOException;

import com.buddycloud.geoid.Position;


/**
 * Interface to a source of cell tower positions
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


public interface CellTowerPositionService
{
  
   /**
    * @return Returns the service name
    */
   public String getName();
   
   /**
    * Returns the position corresponding to the specified cell tower
    * 
    * @param mcc The cell tower mobile country code
    * @param mnc The cell tower mobile network code
    * @param lac The cell tower local area code
    * @param cid The cell tower ID
    * @throws If the lookup failed due to IO problems (network of file system)
    * @return The position of the cell tower with range encoded in the accuracy field
    */
   public Position getPosition(int mcc, int mnc, int lac, int cid) throws IOException;

}
