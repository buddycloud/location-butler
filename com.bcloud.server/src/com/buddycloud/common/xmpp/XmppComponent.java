
package com.buddycloud.common.xmpp;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jabberstudio.jso.InfoQuery;
import org.jabberstudio.jso.JID;
import org.jabberstudio.jso.JSOImplementation;
import org.jabberstudio.jso.Packet;
import org.jabberstudio.jso.PacketError;
import org.jabberstudio.jso.Stream;
import org.jabberstudio.jso.StreamContext;
import org.jabberstudio.jso.StreamDataFactory;
import org.jabberstudio.jso.StreamException;
import org.jabberstudio.jso.event.PacketEvent;
import org.jabberstudio.jso.event.PacketListener;
import org.jabberstudio.jso.event.StreamStatusEvent;
import org.jabberstudio.jso.event.StreamStatusListener;
import org.jabberstudio.jso.io.src.ChannelStreamSource;
import org.jabberstudio.jso.util.DigestHash;
import org.jabberstudio.jso.util.PacketListenerRelay;
import org.jabberstudio.jso.util.PacketMonitor;
import org.jabberstudio.jso.util.Utilities;
import org.jabberstudio.jso.util.XPathListener;
import org.jabberstudio.jso.x.disco.DiscoInfoQuery;
import org.jabberstudio.jso.x.disco.DiscoItemsQuery;
import org.jabberstudio.jso.x.disco.DiscoUtilities;
import org.jabberstudio.jso.x.info.LastQuery;
import org.jabberstudio.jso.x.info.TimeQuery;
import org.jabberstudio.jso.x.info.VersionQuery;
import org.saxpath.SAXPathException;

/**
 * Base class for simple XMMP components. An XMPP component is a standalone program that
 * registers with an XMPP server and listens in on the packet stream. It can react to
 * packets and issue result packets in return.
 */
public abstract class XmppComponent implements PacketListener, StreamStatusListener
{

   private static JID jid;

   private static String host;

   /**
    * Whether or not this component is running. This will be set to <code>false</code> by
    * calling the <code>stop()</code> method.
    */
   protected boolean isRunning;

   /**
    * Whether or not incoming and outgoing packets shall be logged.
    */
   protected boolean isPacketEventLoggingEnabled;

   /**
    * The stream for communicating with the Jabber server
    */
   protected Stream jabberStream;

   /**
    * The logger logs everything that happens
    */
   protected Logger logger;

   /**
    * Queue for outgoing Jabber packets.
    */
   protected SynchronizedPacketQueue outQ;

   /**
    * A queue in which outging packet can be spooled. They will be sent as soon as
    * possible in the same order they where enqueued.
    */
   private OutQueueHandler outQH;

   private long period;

   private long startTime;

   private String xmppJID;

   private String xmppPass;

   private String xmppHost;

   private int xmppPort;


   /**
    * Creates a new instance. Component settings will be attempted drawn from
    * <code>Preferences.userNodeForPackage(getClass())</code>
    */
   public XmppComponent() throws IllegalArgumentException, IOException,
      UnknownHostException, StreamException
   {
      Preferences prefs = Preferences.userNodeForPackage( getClass() );
      init( prefs );

   }


   /**
    * Creates a new instance with settings from the proviced preferences node.
    */
   public XmppComponent(Preferences prefs) throws IllegalArgumentException, IOException,
      UnknownHostException, StreamException
   {

      init( prefs );

   }


   /**
    * Creates a new instance.
    */
   public void init(Preferences prefs) throws IllegalArgumentException, IOException,
      UnknownHostException, StreamException
   {

      isPacketEventLoggingEnabled = true;

      // initiate the Log4J logger
      logger = Logger.getLogger( getClass() );
      logger.info( getClass() + " constructor invoked." );

      this.period = 100;

      // Setup client JID, password, host, and port
      xmppJID = prefs.get( "component_jid", "" );
      xmppPass = prefs.get( "component_pass", "" );
      xmppHost = prefs.get( "component_host", "" );
      xmppPort = prefs.getInt( "component_port", 0 );

      // set the static properties
      XmppComponent.jid = new JID( xmppJID );
      XmppComponent.host = xmppHost;

      Logger unwantedLogger = Logger.getLogger( "net.outer_planes.jso.ComponentStream" );
      if (unwantedLogger != null)
      {
         unwantedLogger.info( "I will shut up now..." );
         unwantedLogger.setLevel( Level.ERROR );
      }

      unwantedLogger = Logger.getLogger( "org.jabberstudio.jso.util.PacketListenerRelay" );
      if (unwantedLogger != null)
      {
         unwantedLogger.info( "I will also shut up..." );
         unwantedLogger.setLevel( Level.ERROR );
      }

      // create out-queue
      outQ = new SynchronizedPacketQueue();

      boolean logPackets = prefs.getBoolean( "log_packets", false );
      setPacketEventLoggingEnabled( logPackets );

   }


   private void connectWithRetries()
   {

      // Setup class fields
      isRunning = true;

      while (( jabberStream == null || jabberStream.getCurrentStatus().isDisconnected() )
             && isRunning)
      {
         try
         {
            logger.debug( "***connecting***" );
            connectOnce();
            logger.debug( "***" + jabberStream.getCurrentStatus() + "***" );
         }
         catch (Exception e)
         {
            logger.error( "Failed to recconect (" + e.getMessage()
                          + "). Will try again soon..." );
         }
         if (jabberStream.getCurrentStatus().isDisconnected())
         {
            try
            {
               Thread.sleep( 5000 );
            }
            catch (InterruptedException e)
            {
            }
         }
      }
   }


   /**
    * Makes one single attempt to connect to the XMPP stream
    */
   private void connectOnce() throws StreamException, IOException
   {
      JSOImplementation jso = JSOImplementation.getInstance();

      // create stream if needed
      if (jabberStream == null)
      {
         jabberStream = jso.createStream( Utilities.COMPONENT_ACCEPT_NAMESPACE );
         jabberStream.addPacketListener( this );
         jabberStream.addStreamStatusListener( this );
         jabberStream.getInboundContext().setCharsetName( "UTF-8" );
         jabberStream.getOutboundContext().setCharsetName( "UTF-8" );
      }
      StreamDataFactory sdf = jabberStream.getDataFactory();

      // Validate client info
      JID client = sdf.createJID( xmppJID );
      if (Utilities.isValidString( client.getNode() )
          || Utilities.isValidString( client.getResource() ))
      {
         throw new IllegalArgumentException(
            "Client JID cannot include a node or resource" );
      }

      JID server = sdf.createJID( xmppHost );
      if (Utilities.isValidString( server.getNode() )
          || Utilities.isValidString( server.getResource() ))
      {
         throw new IllegalArgumentException( "Host JID cannot include a node or resource" );
      }

      // Connect and open stream
      logger.debug( "Connecting to " + server.toString() + ":" + xmppPort );
      jabberStream.connect( ChannelStreamSource
         .createSocket( server.toString(), xmppPort ) );
      logger.debug( "Connected" );
      jabberStream.open();
      logger.debug( "Connection Open" );

      // Get some stream info
      server = jabberStream.getInboundContext().getFrom();
      logger.debug( "Server        : " + server );
      String sessionID = jabberStream.getInboundContext().getID();
      logger.debug( "Session ID    :   " + sessionID );
      logger.debug( "Charset (in)  :   "
                    + jabberStream.getInboundContext().getCharsetName() );
      logger.debug( "Charset (out) :   "
                    + jabberStream.getOutboundContext().getCharsetName() );

      // Do handshake
      Packet handshake =
         sdf.createPacketNode( sdf.createNSI( "handshake", jabberStream
            .getDefaultNamespace() ) );
      handshake.addText( DigestHash.SHA1.hash( sessionID + xmppPass ) );

      handshake = PacketMonitor.sendAndWatch( jabberStream, handshake, 5000 );
      if (handshake == null)
      {
         logger.error( "Could not authenticate to server!" );
         throw new IllegalArgumentException( "Could not authenticate to server!" );
      }

   }


   public void execute() throws Exception
   {

      // Get the system process id
      String pid = ManagementFactory.getRuntimeMXBean().getName();
      if (pid.contains( "@" ))
      {
         pid = pid.substring( 0, pid.indexOf( "@" ) );
      }

      // connect to stream.
      // Note: This method will try to connect multiple times and
      // will not return until connected
      connectWithRetries();

      // setup handler for outgoing packets
      outQH = new OutQueueHandler( jabberStream, outQ );
      Thread outQHT = new Thread( outQH );
      outQHT.start();

      // Set start time
      startTime = new Date().getTime();
      
      // init event hub (if any)
      ComponentEventHandlerRegistry evHub = getEventHandlerRegistry();
      if(evHub!=null){
         evHub.init( outQ );
      }

      // init component-specific handlers. This MUST be don prior to the init of
      // the generic handlers, as one of the generic handlers will listen in on ALL
      // incoming info queries and automatically respond with a "feature-not-implemented"
      // error if the packet event is not handled by one of the component handlers
      int i = 0;
      for (PacketHandler handler : getPacketHandlers())
      {
         i++;
         handler.initialize( jabberStream, outQ );
         handler.start( getComponentName() + " PID " + pid + " t" + i );
      }

      // init generic handlers (service discovery and auto-error reply of un-handled
      // packets)
      initGenericHandlers();

      while (isRunning)
      {
         jabberStream.process();
         try
         {
            Thread.sleep( period );
         }
         catch (InterruptedException ie)
         {
            this.stop();
         }
      }

      logger.debug( "So that is it then, huh?" );

      stopHandlers();

      // close connections
      logger.debug( "Closing connections..." );
      jabberStream.close();
      jabberStream.disconnect();
      logger.debug( "Adios!" );
   }


   protected abstract ComponentEventHandlerRegistry getEventHandlerRegistry();


   protected abstract String getComponentDiscoIdentityType();


   protected abstract String getComponentName();


   protected abstract String getComponentVersion();


   protected Map<String, String> getSupportedNamespaces()
   {
      HashMap<String, String> nsMap = new HashMap<String, String>();

      // get name spaces from handler filters
      Collection<PacketHandler> phs = getPacketHandlers();
      if (phs != null)
      {
         for (PacketHandler ph : phs)
         {
            Collection<PacketFilter> filters = ph.getHandledPacketFilters();
            for (PacketFilter filter : filters)
            {
               if (filter != null && filter.getNamespaces() != null)
               {
                  for (String alias : filter.getNamespaces().keySet())
                  {
                     String namespace = filter.getNamespaces().get( alias );
                     if (nsMap.containsKey( alias ))
                     {
                        String check = nsMap.get( alias );
                        if (!check.equals( namespace ))
                        {
                           throw new RuntimeException(
                              "Multiple namespaces defined with the same alias: " + alias
                                 + " = " + namespace + " AND " + check );
                        }
                     }
                     else
                     {
                        nsMap.put( alias, namespace );
                     }
                  }
               }
            }
            // Collection<PacketFilter> filters = ph.getHandledQueries();
            // if (filters != null)
            // {
            // for (PacketFilter filter : filters)
            // {
            // if (filter.getChildElementNamespace() != null
            // && filter.getChildElementNamespaceAlias() != null)
            // {
            // if (ns.containsKey( filter.getChildElementNamespaceAlias() ))
            // {
            // String check = ns.get( filter.getChildElementNamespaceAlias() );
            // if (!filter.getChildElementNamespace().equals( check ))
            // {
            // throw new RuntimeException(
            // "Multiple namespaces defined with the same alias: "
            // + filter.getChildElementNamespaceAlias() + " = "
            // + filter.getChildElementNamespace() + " AND " + check );
            // }
            // }
            // else
            // {
            // ns.put( filter.getChildElementNamespaceAlias(), filter
            // .getChildElementNamespace() );
            // }
            // }
            // }
            // }
         }
      }
      return nsMap;
   }


   public void packetTransferred(PacketEvent evt)
   {
      if (isPacketEventLoggingEnabled)
      {
         PacketEvent.Type type = evt.getType();
         Packet data = evt.getData();
         logger.debug( type + "\n" + data.toString().replace( "><", ">\n<" ) );
      }
   }


   private void processDiscoInfo(InfoQuery in, InfoQuery out)
   {
      logger.debug( "Processing disco request from " + in.getFrom() );
      StreamDataFactory sdf = jabberStream.getDataFactory();
      DiscoInfoQuery info =
         (DiscoInfoQuery) sdf.createExtensionNode( DiscoInfoQuery.NAME,
            DiscoInfoQuery.class );

      info.setName( getComponentName() );
      info.addIdentity( "component", getComponentDiscoIdentityType() );
      info.addFeature( VersionQuery.NAMESPACE );
      info.addFeature( TimeQuery.NAMESPACE );
      info.addFeature( LastQuery.NAMESPACE );
      info.addFeature( DiscoUtilities.NAMESPACE );
      Map<String, String> ns = getSupportedNamespaces();
      Iterator<String> nsiter = ns.values().iterator();
      while (nsiter.hasNext())
      {
         info.addFeature( nsiter.next() );
      }

      out.add( info );
   }


   private void processDiscoItems(InfoQuery in, InfoQuery out)
   {
      StreamDataFactory sdf = jabberStream.getDataFactory();
      DiscoItemsQuery items =
         (DiscoItemsQuery) sdf.createExtensionNode( DiscoItemsQuery.NAME,
            DiscoItemsQuery.class );

      out.add( items );
   }


   private void processLast(InfoQuery in, InfoQuery out)
   {
      StreamDataFactory sdf = jabberStream.getDataFactory();
      LastQuery last =
         (LastQuery) sdf.createExtensionNode( LastQuery.NAME, LastQuery.class );
      long now = new Date().getTime();
      long elapsed = ( now - startTime ) / 1000;

      last.setSeconds( elapsed );

      out.add( last );
   }


   private void processTime(InfoQuery in, InfoQuery out)
   {
      StreamDataFactory sdf = jabberStream.getDataFactory();
      TimeQuery time =
         (TimeQuery) sdf.createElementNode( TimeQuery.NAME, TimeQuery.class );

      out.add( time );
   }


   private void processVersion(InfoQuery in, InfoQuery out)
   {
      StreamDataFactory sdf = jabberStream.getDataFactory();
      VersionQuery ver =
         (VersionQuery) sdf.createElementNode( VersionQuery.NAME, VersionQuery.class );

      // Setup Name
      ver.setName( getComponentName() );
      ver.setVersion( getComponentVersion() );
      ver.setOS( System.getProperty( "java.vm.name" ) + ", "
                 + System.getProperty( "java.vm.vendor" ) + ", "
                 + System.getProperty( "java.vm.version" ) );
      out.add( ver );
   }


   private void initGenericHandlers()
   {
      JSOImplementation jso = jabberStream.getJSO();
      PacketListenerRelay relay = new PacketListenerRelay();
      XPathListener watcher;

      // Define default name space mappings
      Map<String, String> ns = new TreeMap<String, String>();
      ns.put( "jabber", jabberStream.getDefaultNamespace() );
      ns.put( "time", "jabber:iq:time" );
      ns.put( "ver", "jabber:iq:version" );
      ns.put( "last", "jabber:iq:last" );
      ns.put( "dinfo", DiscoUtilities.INFO_NAMESPACE );
      ns.put( "ditems", DiscoUtilities.ITEMS_NAMESPACE );
      Map<String, String> supportedNamespaces = getSupportedNamespaces();
      if (supportedNamespaces != null)
         ns.putAll( supportedNamespaces );

      for (String alias : ns.keySet())
      {
         String namespace = ns.get( alias );
         logger.info( "Using namespace '" + alias + "' = " + namespace );
      }
      try
      {
         watcher = new XPathListener( jso, "jabber:iq/time:*" )
         {

            public void packetMatched(PacketEvent evt)
            {
               InfoQuery in = (InfoQuery) evt.getData();
               InfoQuery out = (InfoQuery) in.copy();

               // Prepare IQ
               out.reset();
               out.setID( in.getID() );
               out.setTo( in.getFrom() );
               out.setFrom( in.getTo() );
               out.setType( InfoQuery.RESULT );

               processTime( in, out );
               outQ.enque( out );
               evt.setHandled( true );
            }
         };
         watcher.setupNamespaces( ns );
         relay.addPacketListener( PacketEvent.RECEIVED, watcher );
      }
      catch (SAXPathException spe)
      {
      }
      try
      {
         watcher = new XPathListener( jso, "jabber:iq/ver:*" )
         {

            public void packetMatched(PacketEvent evt)
            {

               InfoQuery in = (InfoQuery) evt.getData();
               InfoQuery out = (InfoQuery) in.copy();

               // Prepare IQ
               out.reset();
               out.setID( in.getID() );
               out.setTo( in.getFrom() );
               out.setFrom( in.getTo() );
               out.setType( InfoQuery.RESULT );

               processVersion( in, out );
               outQ.enque( out );
               evt.setHandled( true );
            }
         };
         watcher.setupNamespaces( ns );
         relay.addPacketListener( PacketEvent.RECEIVED, watcher );
      }
      catch (SAXPathException spe)
      {
      }
      try
      {
         watcher = new XPathListener( jso, "jabber:iq/last:*" )
         {

            public void packetMatched(PacketEvent evt)
            {
               InfoQuery in = (InfoQuery) evt.getData();
               InfoQuery out = (InfoQuery) in.copy();

               // Prepare IQ
               out.reset();
               out.setID( in.getID() );
               out.setTo( in.getFrom() );
               out.setFrom( in.getTo() );
               out.setType( InfoQuery.RESULT );

               processLast( in, out );
               outQ.enque( out );
               evt.setHandled( true );
            }
         };
         watcher.setupNamespaces( ns );
         relay.addPacketListener( PacketEvent.RECEIVED, watcher );
      }
      catch (SAXPathException spe)
      {
      }
      try
      {
         watcher = new XPathListener( jso, "jabber:iq[@type='get']/dinfo:*", ns )
         {

            public void packetMatched(PacketEvent evt)
            {
               InfoQuery in = (InfoQuery) evt.getData();
               InfoQuery out = (InfoQuery) in.copy();

               // Prepare IQ
               out.reset();
               out.setID( in.getID() );
               out.setTo( in.getFrom() );
               out.setFrom( in.getTo() );
               out.setType( InfoQuery.RESULT );

               processDiscoInfo( in, out );
               outQ.enque( out );
               evt.setHandled( true );
            }
         };
         relay.addPacketListener( PacketEvent.RECEIVED, watcher );
      }
      catch (SAXPathException spe)
      {
      }
      try
      {
         watcher = new XPathListener( jso, "jabber:iq[@type='get']/ditems:*", ns )
         {

            public void packetMatched(PacketEvent evt)
            {
               InfoQuery in = (InfoQuery) evt.getData();
               InfoQuery out = (InfoQuery) in.copy();

               // Prepare IQ
               out.reset();
               out.setID( in.getID() );
               out.setTo( in.getFrom() );
               out.setFrom( in.getTo() );
               out.setType( InfoQuery.RESULT );

               processDiscoItems( in, out );
               outQ.enque( out );
               evt.setHandled( true );
            }
         };
         relay.addPacketListener( PacketEvent.RECEIVED, watcher );
      }
      catch (SAXPathException spe)
      {
      }

      relay.addPacketListener( PacketEvent.RECEIVED, new PacketListener()
      {

         public void packetTransferred(PacketEvent evt)
         {
            if (evt.getData() instanceof InfoQuery)
            {
               InfoQuery in = (InfoQuery) evt.getData();
               InfoQuery out = (InfoQuery) in.copy();
               StreamDataFactory sdf = evt.getContext().getDataFactory();
               Packet.Type type = in.getType();

               if (!evt.isHandled()
                   && ( ( type == InfoQuery.GET ) || ( type == InfoQuery.SET ) ))
               {
                  // Prepare IQ
                  logger.info( "Received unsupported package:" );
                  logger.info( in );
                  out.setID( in.getID() );
                  out.setTo( in.getFrom() );
                  out.setFrom( in.getTo() );
                  out.setError( sdf.createPacketError( PacketError.CANCEL,
                     "feature-not-implemented" ) );

                  outQ.enque( out );
               }
            }
         }
      } );
      jabberStream.addPacketListener( relay );

   }


   protected abstract Collection<PacketHandler> getPacketHandlers();


   public void statusChanged(StreamStatusEvent evt)
   {
      StreamContext ctx = evt.getContext();
      Stream.Status next = evt.getNextStatus();

      String s = next.toString();
      if (ctx.isOutbound())
         s = s.substring( 0, s.length() - 2 ) + "ing";

      logger.debug( "***" + s + "***" );

      if (s.equals( "disconnected" ) && isRunning)
      {
         connectWithRetries();
      }

      // while(s.equals("disconnected") && isRunning){
      // try {
      // logger.debug("***reconnecting***");
      // connectOnce();
      // s = jabberStream.getCurrentStatus().toString();
      // logger.debug("***"+s+"***");
      // } catch (Exception e) {
      // logger.error("Failed to recconect. Will try again soon...");
      // }
      // if(s.equals("disconnected")){
      // try {
      // Thread.sleep(5000);
      // } catch (InterruptedException e) {}
      // }
      // }

   }


   public void stop()
   {
      isRunning = false;
   }


   protected void stopHandlers()
   {

      // shutdown out queue handler
      outQH.stop();

      // stop custom query handlers
      Collection<PacketHandler> qhs = getPacketHandlers();
      if (qhs != null)
      {
         Iterator<PacketHandler> i = qhs.iterator();
         while (i.hasNext())
         {
            PacketHandler qh = i.next();
            qh.stop();
         }
      }
   }


   public void setPacketEventLoggingEnabled(boolean isPacketEventLoggingEnabled)
   {
      this.isPacketEventLoggingEnabled = isPacketEventLoggingEnabled;
   }


   /**
    * Public static handle to the component's JID to be used when building returns stanzas
    * where this is needed
    * 
    * @return The JID
    */
   public static JID getJID()
   {
      return jid;
   }


   /**
    * Returns the name of the host to which this component is connected (or will connect
    * to if not connected yet)
    * 
    * @return The host name
    */
   public static String getHostName()
   {
      return host;
   }

}
