/**
 * 
 */

package com.buddycloud.geoid;


/**
 * A position on the surface of the earth that can not be pinpointed to an exact point but
 * which is likely to be within a circle whose senter is at (latitude, longitude) and
 * whose radius is defined by the accuracy field (TODO: definition of likely: in 99.9% of
 * cases?)
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

public class Position extends Point implements PositionedObject
{

   private double accuracy;


   public Position()
   {
      super();
      accuracy = 0.0;
   }


   public Position(double latitude, double longitude)
   {
      super( latitude, longitude );
      accuracy = 0.0;
   }


   public Position(double latitude, double longitude, double accuracy)
   {
      super( latitude, longitude );
      setAccuracy( accuracy );
   }
   
   public boolean contains(Point other){
      double d = this.getDistanceTo( other );
      return d < accuracy;
   }


   public void setAccuracy(double accuracy)
   {
      if (accuracy < 0)
         throw new IllegalArgumentException( "Accuracy must be positive. Was " + accuracy );
      this.accuracy = accuracy;
   }


   public double getAccuracy()
   {
      return accuracy;
   }
   
   /**
    * If the provided point lies outside this position and its accuracy latitude and longitude and accuracy are modified as little as possible such that the provided point lies inside. 
    * @param p the point that will lie inside center+radius after this method returns
    */
   public void addPoint(Point p){
      double dist = getDistanceTo( p );
      if (dist > accuracy)
      {

         // shift center by half the excess distance
         double shiftDir = getDirectionTo( p );
         double shiftDist = ( dist - accuracy ) / 2.0;
         shift( shiftDir, shiftDist );

         // expand accuracy by same amount
         accuracy += shiftDir;

      }
   }


   /**
    * Returns the distance from the center of this to the center of the other plus the sum
    * of both accuracies
    */
   public double getMaxDistanceTo(Position other)
   {
      double d = getDistanceTo( other );
      d += other.accuracy;
      d += this.accuracy;

      return d;
   }


   /* (non-Javadoc)
    * @see com.bcloud.geo.Point#equals(java.lang.Object)
    */
   public boolean equals(Object o)
   {
      if (o instanceof Position)
      {
         Position other = (Position) o;
         return this.getLatitude() == other.getLatitude()
                && this.getLongitude() == other.getLongitude()
                && this.getAccuracy() == other.getAccuracy();
      }
      return false;
   }
   
   public String toString(){
      return super.toString()+" @ "+accuracy+"m";
   }
}
