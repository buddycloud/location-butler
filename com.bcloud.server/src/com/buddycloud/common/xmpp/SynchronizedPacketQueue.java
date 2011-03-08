
package com.buddycloud.common.xmpp;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.jabberstudio.jso.JSOImplementation;
import org.jabberstudio.jso.Packet;
import org.jabberstudio.jso.Stream;
import org.jabberstudio.jso.event.PacketEvent;
import org.jabberstudio.jso.util.XPathListener;
import org.saxpath.SAXPathException;

/**
 * This class provides synchronized methods for enquing and dequeing jabber packets such
 * that they can be processed in a different thread form the one that extracted them from
 * the stream. Also a SynchronizedPacketQueue can be attached directly to a stream for
 * automatic enqueing.
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


public class SynchronizedPacketQueue
{

   private List<Packet> q;

   private Logger logger;


   /**
    * Default constructor
    */
   public SynchronizedPacketQueue()
   {
      q = new LinkedList<Packet>();
      logger = Logger.getLogger( getClass() );
   }


   /**
    * Adds a new packet to the queue.
    * 
    * @param data
    *           The packet to be added
    */
   public synchronized void enque(Packet data)
   {
      if (data != null)
      {
         q.add( data );
      }
   }
   
   public synchronized int size(){
      return q.size();
   }


   /**
    * Extracts and removes a packet from the queue.
    * 
    * @return The enqued packet or null if the queue is empty.
    */
   public synchronized Packet deque()
   {
      return q.isEmpty() ? null : q.remove( 0 );
   }


   /**
    * Attaches this queue to a jabber stream, causing all incoming packets matching the
    * specified filter to be added to the queue.
    * 
    * @param stream
    *           The stream to attach to
    * @param packetFilter
    *           The filter for the packet to look for (e.g. iq, message, prescence)
    * @throws SAXPathException
    */
   public void attachToStream(Stream stream, PacketFilter filter) throws SAXPathException
   {

      JSOImplementation jso = stream.getJSO();
      TreeMap<String, String> ns = new TreeMap<String, String>();
      ns.put( "jabber", stream.getDefaultNamespace() );
      if (filter.getNamespaces() != null)
      {
         for (String alias : filter.getNamespaces().keySet())
         {
            String namespace = filter.getNamespaces().get( alias );
            ns.put( alias, namespace );
         }
      }
      XPathListener listener = new XPathListener( jso, filter.getXPath() )
      {

         public void packetMatched(PacketEvent evt)
         {
            enque( evt.getData() );
            evt.setHandled( true );
         }
      };
      logger.debug( "Adding XPath listener: " + filter.getXPath() );
      listener.setupNamespaces( ns );
      stream.addPacketListener( PacketEvent.RECEIVED, listener );

   }
}
