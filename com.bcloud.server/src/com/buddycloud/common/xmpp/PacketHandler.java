
package com.buddycloud.common.xmpp;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jabberstudio.jso.JID;
import org.jabberstudio.jso.Packet;
import org.jabberstudio.jso.Stream;
import org.jabberstudio.jso.StreamElement;
import org.saxpath.SAXPathException;

import com.buddycloud.Constants;

/**
 * Base class for packet queue handlers
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
 *
 */ 

public abstract class PacketHandler implements Runnable
{

   private static final long SLEEP_TIME_MILLSECONDS = 100;

   protected Logger logger;

   protected Stream stream;

   protected SynchronizedPacketQueue inQ;

   protected SynchronizedPacketQueue outQ;

   private boolean stopped;

   private boolean logPerformance;

   private Thread thread;

   public PacketHandler()
   {

      inQ = new SynchronizedPacketQueue();
      stopped = true;
      logPerformance = true;
      logger = Logger.getLogger( getClass() );

   }


   /**
    * Turns on/off performance logging (how many milliseconds to process each package(
    * 
    * @param b
    */
   public void enablePerformanceLogging(boolean b)
   {
      logPerformance = b;
   }


   /**
    * Initializes the handler for a new stream (for instance by reconnects)
    * 
    * @param stream
    *           The stream
    */
   public void initialize(Stream stream, SynchronizedPacketQueue outQ)
   {
      this.stream = stream;
      this.outQ = outQ;

      // attach in-queue to stream using the defined filters for this handler
      Collection<PacketFilter> filters = getHandledPacketFilters();
      if (filters != null)
      {
         for (PacketFilter f : filters)
         {
            try
            {
               inQ.attachToStream( stream, f );
            }
            catch (SAXPathException e)
            {
               logger.error( "Faild to attach queue to stream with filter " + f, e );
            }
         }
      }

   }


   public void start(String threadName)
   {
      if (!stopped)
         return;
      thread = new Thread( this );
      thread.setName( threadName );
      thread.start();
   }


   public void stop()
   {
      stopped = true;
      thread = null;
   }


   public void run()
   {

      stopped = false;
      Packet p;
      
//      long tLastLoadCheck = System.currentTimeMillis();
//      int cyclesSinceLoadCheck = 0;

      while (!stopped)
      {
         while (( p = inQ.deque() ) != null)
         {
            long tp0 = System.currentTimeMillis();

            Packet reply = null;

            try
            {
               reply = handlePacket( p );
            }
            catch (Exception e)
            {
               reply = XmppUtils.createUnhandledExceptionError( p, e.getMessage() );
               logger.error( "Unhandled exception: ", e );
            }

            // enqueue reply
            if (reply != null)
               outQ.enque( reply );
            long tp1 = System.currentTimeMillis();
            int qsize = inQ.size();
            if (logPerformance)
               logger.info( "PERFORMANCE: Packet processed in " + ( tp1 - tp0 )
                            + " milliseconds. Remaining packets in queue: " + qsize );

         }
         try
         {
            Thread.sleep( SLEEP_TIME_MILLSECONDS );
         }
         catch (InterruptedException ie)
         {
            logger.info( "Thread Interrupted." );
         }
         
//         cyclesSinceLoadCheck++;
//         
//         long t = System.currentTimeMillis();
//         long dt = t-tLastLoadCheck; 
//         if(dt>5000){
//            long wt = dt - cyclesSinceLoadCheck*SLEEP_TIME_MILLSECONDS;
//            double load = (double)wt/dt;
//            logger.info( String.format( "LOAD: %.1f%%", load*100 ));
//            tLastLoadCheck = t;
//            cyclesSinceLoadCheck = 0;
//         }
      }

      handleStop();

      logger.debug( "Stopped." );
   }


   /**
    * Called when a packet is extracted from the queue
    * 
    * @param p
    *           The packet that must be handled
    * @return An optional reply package (null if not needed)
    */
   protected abstract Packet handlePacket(Packet p);


   /**
    * Called when the handler have been stopped. Close special resources (e.g. database
    * connections, files etc) here
    */
   protected abstract void handleStop();
   

   /**
    * returns a collection of filters for the types of packets handled by this handler.
    * 
    * @return The filters
    */
   public abstract Collection<PacketFilter> getHandledPacketFilters();


   protected boolean isBuddycloudUser(JID jid)
   {
      return jid.getDomain().toLowerCase().equals( Constants.XMPP_HOST_NAME );
   }


   protected boolean isBuddycloudConference(JID jid)
   {
      return jid.getDomain().toLowerCase().equals( Constants.CHANNELS_HOST_NAME );
   }


   protected boolean isExternalConference(JID jid)
   {
      return !isBuddycloudConference( jid )
             && jid.getDomain().toLowerCase().contains( "conference" );
   }


   protected boolean isExternalUser(JID jid)
   {
      return !isBuddycloudUser( jid ) && !isBuddycloudConference( jid )
             && !isExternalConference( jid );
   }


   protected boolean isUser(JID jid)
   {
      return isBuddycloudUser( jid ) || isExternalUser( jid )
             && !isExternalConference( jid );
   }


   protected boolean isConference(JID jid)
   {
      return isBuddycloudConference( jid ) || isExternalConference( jid )
             && !isExternalConference( jid );
   }
   
   /**
    * A utility for getting the text of a child element
    * @param parent
    * @param elementName
    * @return
    */
   protected String getChildElementText(StreamElement parent, String elementName)
   {
      if(parent==null) return null;
      StreamElement e = parent.getFirstElement( elementName );
      if (e != null)
         return e.normalizeTrimText();
      else
         return null;
   }

   protected void addChildElementTextIfNotNull(StreamElement parent, String elementName,
      Object value)
   {
      if (value != null && value.toString().length() > 0)
      {
         StreamElement e = parent.addElement( elementName );
         e.addText( value.toString() );
      }
   }

}
