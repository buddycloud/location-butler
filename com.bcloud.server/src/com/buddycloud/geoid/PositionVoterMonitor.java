/**
 * 
 */

package com.buddycloud.geoid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

/**
 * A voter monitor for separating similar positions from dissimilar ones in a list with
 * several similar ones and a few dissimilar ones.
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

public class PositionVoterMonitor<K extends PositionedObject>
{

   private Collection<K> items;

   private Collection<K> winners;

   private Collection<K> loosers;

   private double groupRadius;


   /**
    * Creates a new PositionVoterMonitor on the supplied set of positioned items
    * 
    * @param items
    *           The positioned items to vote on
    * @param groupRadius
    *           the radius to use when collecting the items into groups and selecting the
    *           most populous group
    */
   public PositionVoterMonitor(Collection<K> items, double groupRadius)
   {

      this.items = items;
      this.groupRadius = groupRadius;

   }


   /**
    * @return The set of winners of the vote
    */
   public Collection<K> getWinners()
   {
      if (winners == null)
      {
         vote();
      }
      return winners;
   }


   /**
    * @return The set of winners of the vote
    */
   public Collection<K> getLoosers()
   {
      if (loosers == null)
      {
         vote();
      }
      return loosers;
   }


   /**
    * Performs the vote
    */
   protected void vote()
   {
      Point bestGroup = null;
      int highscore = 0;

      // split the set up in groups of overlapping and non-overlapping items
      HashMap<Point, Collection<K>> groups = new HashMap<Point, Collection<K>>();
      for (K p : items)
      {

         Point pos = new Point( p.getLatitude(), p.getLongitude() );

         boolean foundFit = false;
         for (Point key : groups.keySet())
         {

            // check if center of item is inside group
            if (key.getDistanceTo( pos ) <= groupRadius)
            {

               foundFit = true;

               // // grow group if not completely contained
               // double maxDist = key.getMaxDistanceTo( pos );
               // if (maxDist > key.getAccuracy())
               // {
               // double dir = key.getDirectionTo( pos );
               // double dist = maxDist - key.getAccuracy();
               // System.out.println( "Expanding group " + key + " by " + dist + "m" );
               // key.setAccuracy( maxDist );
               // key.shift( dir, dist );
               // }

               // add item to group
               groups.get( key ).add( p );
               System.out.println( "Added " + p + " to group " + key );

               // check if this is now the most populous group
               if (groups.get( key ).size() > highscore)
               {
                  bestGroup = key;
                  highscore = groups.get( key ).size();
               }
            }
         }
         // if no fitting group found, create new group
         if (!foundFit)
         {
            Collection<K> firstGroup = new ArrayList<K>();
            firstGroup.add( p );
            groups.put( pos, firstGroup );
            System.out.println( "Created group " + pos + " with entry " + p );

            // if this was the first group to be created, set high score
            if (highscore == 0)
            {
               bestGroup = pos;
               highscore = 1;
            }
         }
      }

      // call the winners
      winners = groups.get( bestGroup );

      // collect the loosers
      loosers = new ArrayList<K>();

      for (Point key : groups.keySet())
      {
         if (key != bestGroup)
         {
            loosers.addAll( groups.get( key ) );
         }
      }

   }


   public static void main(String[] args)
   {
      Collection<Position> testItems = new ArrayList<Position>();
      testItems.add( new Position( 66.000, 33.000, 1000 ) );
      testItems.add( new Position( 66.030, 33.020, 5000 ) );
      testItems.add( new Position( 66.020, 33.030, 4000 ) );
      testItems.add( new Position( 66.002, 33.001, 6000 ) );
      testItems.add( new Position( 66.004, 33.050, 5000 ) );
      testItems.add( new Position( 66.030, 33.700, 3000 ) );
      testItems.add( new Position( 66.060, 33.009, 2000 ) );

      PositionVoterMonitor<Position> vm =
         new PositionVoterMonitor<Position>( testItems, 5000 );
      for (PositionedObject p : vm.getWinners())
      {
         System.out.println( "Winner: " + p );
      }
      for (PositionedObject p : vm.getLoosers())
      {
         System.out.println( "Loosers: " + p );
      }
   }

}
