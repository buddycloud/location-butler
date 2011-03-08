/**
 * 
 */

package com.buddycloud.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

/**
 * A key-value map whose entries can be sorted by value
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


public class ValueSortableMap<K, V extends Comparable<V>>
{

   public enum ReplacePolicy {
      KEEP_SMALLEST, KEEP_LARGEST
   }

   private class KeyValuePair<T, S extends Comparable<S>> implements
      Comparable<KeyValuePair<T, S>>
   {

      public T key;

      public S value;


      public KeyValuePair(T key, S value)
      {
         this.key = key;
         this.value = value;
      }


      @SuppressWarnings("unchecked")
      public boolean equals(Object o)
      {
         if (o instanceof KeyValuePair)
         {
            KeyValuePair other = (KeyValuePair) o;
            return this.key.equals( other.key );
         }
         else
            return false;
      }


      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      public int compareTo(KeyValuePair<T, S> other)
      {
         return this.value.compareTo( other.value );
      }
   }

   private Vector<KeyValuePair<K, V>> elements;

   private ReplacePolicy replacePolicy;


   public ValueSortableMap()
   {
      elements = new Vector<KeyValuePair<K, V>>();
      replacePolicy = ReplacePolicy.KEEP_LARGEST;
   }


   /**
    * Specifies the policy to use when values are put several times for the same key. if
    * KEEP_SMALLEST the smallest value of the existing and the new value, as determined by
    * the <code>Comparable.compare()</code>, is kept. If KEEP_LARGEST the largest value
    * is kept. Default is KEEP_LARGEST
    */
   public void setReplacePolicy(ReplacePolicy replacePolicy)
   {
      this.replacePolicy = replacePolicy;
   }


   /**
    * Adds the value <code>value</code> to the map, associated with the key
    * <code>key</code>. If a value has already been specified for this key, the value
    * will bek kept or replaced as determined by the replace policy.
    * 
    * @see SortableValueMap.setReplacePolicy(ReplacePolicy)
    */
   public void put(K key, V value)
   {
      if (containsKey( key ))
      {
         V oldValue = get( key );
         if (replacePolicy == ReplacePolicy.KEEP_LARGEST)
         {
            if (value.compareTo( oldValue ) > 0)
            {
               getKeyValuePair( key ).value = value;
            }
         }
         else
         {
            if (value.compareTo( oldValue ) < 0)
            {
               getKeyValuePair( key ).value = value;
            }
         }
      }
      else
      {
         elements.add( new KeyValuePair<K, V>( key, value ) );
      }
   }


   public Collection<K> keySet()
   {
      Vector<K> keys = new Vector<K>();
      for (KeyValuePair<K, V> e : elements)
      {
         keys.add( e.key );
      }
      return keys;
   }


   /**
    * Adds all values in the map <code>map</code> to this map, associated with their
    * respective keys. If a value has already been specified for a key, the value will bek
    * kept or replaced as determined by the replace policy.
    * 
    * @see SortableValueMap.setReplacePolicy(ReplacePolicy)
    */
   public void putAll(Map<K, V> map)
   {
      if (map == null)
         return;
      for (K key : map.keySet())
      {
         put( key, map.get( key ) );
      }
   }


   public int size()
   {
      return elements.size();
   }


   private KeyValuePair<K, V> getKeyValuePair(K key)
   {
      int i = elements.indexOf( new KeyValuePair<K, V>( key, null ) );
      return elements.get( i );
   }


   public V get(K key)
   {
      return getKeyValuePair( key ).value;
   }


   public boolean containsKey(K key)
   {
      return elements.contains( new KeyValuePair<K, V>( key, null ) );
   }


   public void sortByValue()
   {
      Collections.sort( elements );
   }


   public static void main(String[] args)
   {
      ValueSortableMap<String, Integer> highscore =
         new ValueSortableMap<String, Integer>();

      ValueSortableMap<String, Integer> lowscore =
         new ValueSortableMap<String, Integer>();

      highscore.setReplacePolicy( ReplacePolicy.KEEP_LARGEST );
      lowscore.setReplacePolicy( ReplacePolicy.KEEP_SMALLEST );

      lowscore.put( "bob", 200 );
      highscore.put( "bob", 200 );
      System.out.println( "bob, 200" );

      lowscore.put( "alice", 350 );
      highscore.put( "alice", 350 );
      System.out.println( "alice, 350" );

      lowscore.put( "bob", 150 );
      highscore.put( "bob", 150 );
      System.out.println( "bob, 150" );

      lowscore.put( "eve", 200 );
      highscore.put( "eve", 200 );
      System.out.println( "eve, 200" );

      lowscore.put( "bob", 370 );
      highscore.put( "bob", 370 );
      System.out.println( "bob, 370" );

      lowscore.put( "eve", 240 );
      highscore.put( "eve", 240 );
      System.out.println( "eve, 240" );

      lowscore.put( "alice", 410 );
      highscore.put( "alice", 410 );
      System.out.println( "alice, 410" );

      highscore.sortByValue();
      lowscore.sortByValue();

      System.out.println();
      System.out.println( "Highscore:" );
      for (String name : highscore.keySet())
      {
         int score = highscore.get( name );
         System.out.println( name + ": " + score );
      }
      System.out.println();
      System.out.println( "Lowscore:" );
      for (String name : lowscore.keySet())
      {
         int score = lowscore.get( name );
         System.out.println( name + ": " + score );
      }
   }
}
