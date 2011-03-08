/**
 * 
 */

package test.bcloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.buddycloud.utils.ValueSortableMap;

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
 * 
 */
public class GeoGrid
{

   private String[] labels;

   private double[] lats;

   private double[] lons;

   private String[][] grid;


   public GeoGrid(String[] labels, double[] lats, double[] lons)
   {
      this.labels = labels;
      this.lats = lats;
      this.lons = lons;
   }


   public void generate(int centerIndex, boolean compact)
   {

      // create 2 maps with label as key, one for delta lat to center, one for pseudo
      // distance to center and one from pseduo direction from center to item
      Map<String, Double> dirMap = new HashMap<String, Double>();
      ValueSortableMap<String, Double> distMap = new ValueSortableMap<String, Double>();

      for (int n = 0; n < labels.length; n++)
      {
         double dlat = lats[n] - lats[centerIndex];
         double dlon = lons[n] - lons[centerIndex];
         dirMap.put( labels[n], Math.atan2( dlat, dlon ) * 180 / Math.PI + 180 );
         distMap.put( labels[n], Math.sqrt( dlat * dlat + dlon * dlon ) );
      }

      distMap.sortByValue();

      // build a grid of concentric squares, i.e first contains 1 item, next the
      // surrounding 8, then the next 18 etc
      ArrayList<Collection<String>> shells = new ArrayList<Collection<String>>();

      // the core holds the center item only
      ArrayList<String> core = new ArrayList<String>();
      String centerLabel = labels[centerIndex];
      core.add( centerLabel );
      shells.add( core );

      // center layer is full, start new one
      ValueSortableMap<String, Double> currentShell =
         new ValueSortableMap<String, Double>();

      // insert the rest depending on delta lat/lon
      for (String key : labels)
      {

         if (!key.equals( centerLabel ))
         {

            if (!compact)
            {
               int shellCapacity = 4 * shells.size() + 4;
               double deltaDir = 360.0 / shellCapacity;
               double minDir = currentShell.size() * deltaDir - deltaDir / 2;
               double dir = dirMap.get( key );
               while (dir < minDir)
               {
                  // add to shell
                  currentShell.put( "[empty]", dir );
                  
                  minDir-=deltaDir;

                  // check if shell is full
                  if (currentShell.size() == 4 * ( shells.size() ) + 4)
                  {
                     System.out.println( "Shell " + shells.size() + " is full "
                                         + currentShell.size() );

                     // if so, sort by value (increasing direction relative to north)
                     currentShell.sortByValue();

                     // add layer to grid
                     shells.add( currentShell.keySet() );

                     // create new layer
                     currentShell = new ValueSortableMap<String, Double>();
                  }

               }

            }

            // add to shell
            currentShell.put( key, dirMap.get( key ) );

            // check if shell is full
            if (currentShell.size() == 4 * ( shells.size() ) + 4)
            {
               System.out.println( "Shell " + shells.size() + " is full "
                                   + currentShell.size() );

               // if so, sort by value (increasing direction relative to north)
               currentShell.sortByValue();

               // add layer to grid
               shells.add( currentShell.keySet() );

               // create new layer
               currentShell = new ValueSortableMap<String, Double>();
            }
         }
      }
      // add the last (unfinished) shell
      shells.add( currentShell.keySet() );

      // convert to array,
      int gridDim = 2 * ( shells.size() - 1 ) + 1;
      System.out.println( "Grid : dim " + gridDim );
      grid = new String[gridDim][gridDim];

      // add the center item
      grid[gridDim / 2][gridDim / 2] = centerLabel;

      // add all others
      for (int n = 1; n < shells.size(); n++)
      {
         Collection<String> shell = shells.get( n );
         int shellDim = 2 * n + 1;
         int minIndex = ( gridDim - shellDim ) / 2;
         int maxIndex = ( gridDim + shellDim ) / 2 - 1;
         int col = ( minIndex + maxIndex ) / 2;
         int row = gridDim / 2 - n;
         int currSide = 0;
         System.out.println( "Shell " + n + ": dim " + shellDim + ", indices range "
                             + minIndex + "-" + maxIndex );
         for (String label : shell)
         {

            System.out.println( "[" + row + "," + col + "] = " + label + " (dist="
                                + distMap.get( label ) + ", dir=" + dirMap.get( label ) );
            grid[row][col] = label;

            // match along top edge to the right
            if (currSide == 0 && col < maxIndex)
            {
               col++;
               if (col == maxIndex)
                  currSide++;
            }
            // then down the right edge
            else if (currSide == 1 && row < maxIndex)
            {
               row++;
               if (row == maxIndex)
                  currSide++;
            }
            // back along the bottom edge
            else if (currSide == 2 && col > minIndex)
            {
               col--;
               if (col == minIndex)
                  currSide++;
            }
            else if (currSide == 3 && row > minIndex)
            {
               row--;
               if (row == minIndex)
                  currSide++;
            }

         }

      }
   }


   public String toString()
   {
      String s = "";
      for (int row = 0; row < grid.length; row++)
      {
         for (int col = 0; col < grid.length; col++)
         {
            s += grid[row][col] + '\t';
         }
         s += '\n';
      }
      return s;
   }


   public static void main(String[] args)
   {
      int size = 15;
      String[] labels = new String[size];
      double[] lat = new double[size];
      double[] lon = new double[size];
      for (int i = 0; i < size; i++)
      {
         labels[i] = "" + i;
         lat[i] = Math.random() * 180 - 90;
         lon[i] = Math.random() * 360 - 180;
      }

      GeoGrid g = new GeoGrid( labels, lat, lon );

      int centerIndex = (int) ( Math.random() * size );
      System.out.println( "Center index: " + centerIndex + " (" + labels[centerIndex]
                          + ")" );
      g.generate( centerIndex, false );
      System.out.println( g );
   }
}
