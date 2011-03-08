/**
 * 
 */
package com.buddycloud.common;


/**
 * Constants used multiple places
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

public class LocationConstants
{

   /**
    * The minimum pattern match needed before someone is at a place, rather than just near it
    */
   public static final int PLACE_FIX_MIN_PATTERN_MATCH = 80;
   public static final int NEARBY_FADEOUT_TIME_DAYS = 7;
   public static final int MOTION_STATE_MOVING_CONFIDENCE_LIMIT = -10;

   public static final int MOTION_STATE_STATIONARY_CONFIDENCE_LIMIT = 80;


}
