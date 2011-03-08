/**
 * 
 */

package com.buddycloud.location;

import java.util.Vector;

import com.buddycloud.common.DatabaseObject;

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

public class BeaconPattern extends DatabaseObject
{

   public class BeaconPatternElement
   {

      public double avgSignalStrength;

      public Beacon beacon;

      public double timeFraction;
   }

   private Vector<BeaconPatternElement> elements;

   private long stableTime;


   private long totalTime;


   /**
    * Creates a empty pattern
    */
   public BeaconPattern()
   {
      elements = new Vector<BeaconPatternElement>();
   }


   public void addElement(Beacon beacon, double timeFraction, double avgSignalStrength)
   {
      BeaconPatternElement e = new BeaconPatternElement();
      e.avgSignalStrength = avgSignalStrength;
      e.beacon = beacon;
      e.timeFraction = timeFraction;
      elements.add( e );
   }


   /**
    * @return the elements
    */
   public Vector<BeaconPatternElement> getElements()
   {
      return elements;
   }


   // /**
   // * Creates a one-beacon pattern
   // * @param beacon The beacon
   // * @param signalStrength The observed signal strength
   // */
   // public BeaconPattern(Beacon beacon, int signalStrength){
   // elements = new Vector<BeaconPatternElement>();
   // addElement(beacon, 1.0, signalStrength);
   // }
   //
   public long getStableTime()
   {
      return stableTime;
   }


   public long getTotalTime()
   {
      return totalTime;
   }


   public Beacon.Type getBeaconType()
   {
      if (elements == null || elements.size() == 0)
         return null;
      else
         return elements.get( 0 ).beacon.getType();
   }


   /**
    * @param elements
    *           the elements to set
    */
   public void setElements(Vector<BeaconPatternElement> elements)
   {
      this.elements = elements;
   }

   public void setStableTime(long stableTime)
   {
      this.stableTime = stableTime;
   }


   public void setTotalTime(long totalTime)
   {
      this.totalTime = totalTime;
   }
}
