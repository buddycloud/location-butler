/**
 * 
 */

package com.buddycloud.geoid;


/**
 * A geodetic rectangle defined by a southern and northern latitude, and western and
 * eastern longitude
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


public class Rectangle
{

   public Point southWest;

   public Point northEast;


   public Rectangle(Point southWest, Point northEast)
   {
      this.northEast = northEast;
      this.southWest = southWest;
   }


   public Rectangle(double latMin, double latMax, double lonMin, double lonMax)
   {
      southWest = new Point( latMin, lonMin );
      northEast = new Point( latMax, lonMax );
   }


   /**
    * Creates a new rectangle
    * 
    * @param center
    *           The center point of the rectangle
    * @param width
    *           The (east-west) width of the rectangle in meters measured through the center
    * @param height
    *           The (north-south) height of the rectangle in meters measured through the center
    */
   public Rectangle(Point center, double width, double height)
   {
      Point north = center.getPointAt( 0, height / 2 );
      Point south = center.getPointAt( 180, height / 2 );
      Point east = center.getPointAt( 90, width / 2 );
      Point west = center.getPointAt( -90, width / 2 );

      southWest = new Point( south.getLatitude(), west.getLongitude() );
      northEast = new Point( north.getLatitude(), east.getLongitude() );
   }


   public double getEasternmostLongitude()
   {
      return northEast.getLongitude();
   }


   public double getNorthernmostLatitude()
   {
      return northEast.getLatitude();
   }


   public double getWesternmostLongitude()
   {
      return southWest.getLongitude();
   }


   public double getSouthernmostLatitude()
   {
      return southWest.getLatitude();
   }


   public Point getCenter()
   {
      return new Point( ( northEast.getLatitude() + southWest.getLatitude() ) / 2,
         ( northEast.getLongitude() + southWest.getLongitude() ) / 2 );
   }
   
   /**
    * Returns the (east-west) width of the rectangle in meters as measured through the center
    * @return The width in meters
    */
   public double getWidth(){
      Point center = getCenter();
      Point east = new Point(center.getLatitude(), northEast.getLongitude());
      double halfDist = east.getDistanceTo( center );
      return 2+halfDist;
   }

   /**
    * Returns the (north-south) height of the rectangle in meters as measured through the center
    * @return The width in meters
    */
   public double getHeight(){
      Point center = getCenter();
      Point north = new Point(northEast.getLatitude(), center.getLongitude());
      double halfDist = north.getDistanceTo( center );
      return 2+halfDist;
   }

}
