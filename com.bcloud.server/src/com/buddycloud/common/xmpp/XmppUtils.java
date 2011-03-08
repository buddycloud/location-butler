package com.buddycloud.common.xmpp;

import org.jabberstudio.jso.InfoQuery;
import org.jabberstudio.jso.JID;
import org.jabberstudio.jso.Message;
import org.jabberstudio.jso.NSI;
import org.jabberstudio.jso.Packet;
import org.jabberstudio.jso.PacketError;
import org.jabberstudio.jso.StreamDataFactory;
import org.jabberstudio.jso.StreamElement;

import com.buddycloud.Constants;

public class XmppUtils {

   private static final JID PUBSUB_SERVER_JID =
      new JID( "broadcaster." + Constants.XMPP_HOST_NAME );
   
	public static Packet createBadRequestError(Packet p, String message){
		Packet error = (Packet)p.copy();
		error.setID(p.getID());
		error.setTo(p.getFrom());
		error.setFrom(p.getTo());
		error.setType(Packet.ERROR);
		PacketError e = p.getDataFactory().createPacketError(PacketError.MODIFY, PacketError.BAD_REQUEST_CONDITION);
		if(message!=null)e.addText(message);
		error.setError(e);
		return error;
	}

	public static Packet createItemNotFoundError(Packet p, String message){
		Packet error = (Packet)p.copy();
		error.setID(p.getID());
		error.setTo(p.getFrom());
		error.setFrom(p.getTo());
		error.setType(Packet.ERROR);
		PacketError e = p.getDataFactory().createPacketError(PacketError.CANCEL, PacketError.ITEM_NOT_FOUND_CONDITION);
		if(message!=null)e.addText(message);
		error.setError(e);
		return error;
	}

	public static Message createMessage(StreamDataFactory sdf, JID sender, JID recipient, String messageText){
		
	    Message msg = (Message)sdf.createPacketNode(
	    		sdf.createNSI("message", null),
	    		Message.class);
	    msg.setBody(messageText);
	    msg.setTo(recipient);
	    msg.setFrom(sender);
	    msg.setType(Message.CHAT);
	    return msg;
		
	}
	
//	public static Presence createMucPresence(StreamDataFactory sdf, JID sender, JID group, String nickname){
//	    Presence p = (Presence)sdf.createPacketNode(
//	    		sdf.createNSI("presence", null),
//	    		Presence.class);
//	    
//	    p.setTo(new JID(group+"/"+nickname));
//	    p.setFrom(sender);
//	    p.addElement("x", "http://jabber.org/protocol/muc");
//	    
//	    return p;
//	}
//
//	public static Presence createMucAbsence(StreamDataFactory sdf, JID sender, JID group, String nickname){
//	    Presence p = (Presence)sdf.createPacketNode(
//	    		sdf.createNSI("presence", null),
//	    		Presence.class);
//	    
//	    p.setTo(new JID(group+"/"+nickname));
//	    p.setFrom(sender);
//	    p.setType(Presence.UNAVAILABLE);
//	    
//	    return p;
//	}

	/**
	 * @deprecated Use pubsub instead of muc
	 */
   public static Message createMucMessage(StreamDataFactory sdf, JID sender, JID recipient, String messageText){
      
      Message msg = (Message)sdf.createPacketNode(
           sdf.createNSI("message", null),
           Message.class);
      msg.setBody(messageText);
      msg.setTo(recipient);
      msg.setFrom(sender);
      msg.setType(Message.GROUPCHAT);
      return msg;
     
  }

   public static InfoQuery createPubsubMessage(StreamDataFactory sdf, String channelNode, JID senderJid, String senderName, String messageText){
      
      InfoQuery iq =
         (InfoQuery) sdf.createElementNode( sdf.createNSI( "iq", null ), InfoQuery.class );
      iq.setFrom (senderJid );
      iq.setTo( PUBSUB_SERVER_JID );
      iq.setType( InfoQuery.SET );
      iq.setID( "publish:"+System.currentTimeMillis() );
      StreamElement pubsub =
         iq.addElement( new NSI( "pubsub", "http://jabber.org/protocol/pubsub" ) );
      StreamElement publish = pubsub.addElement( "publish" );
      publish.setAttributeValue( "node", channelNode );
      StreamElement item = publish.addElement( "item" );
      StreamElement entry = item.addElement( "entry", "http://www.w3.org/2005/Atom" );
      StreamElement content = entry.addElement( "content" );
      content.setAttributeValue( "type", "html" );
      content.addText( messageText );
      
      StreamElement author = entry.addElement( "author" );
      StreamElement name = author.addElement( "name" );
      name.addText( senderName );
      StreamElement jid = author.addElement( "jid" );
      jid.addText( senderJid.toBareJID().toString() );

      StreamElement published = entry.addElement( "published" );
      published.addText( ""+System.currentTimeMillis() );
      
      return iq;
     
  }

	public static Packet createNotAuthorizedError(Packet p, String message){
		Packet error = (Packet)p.copy();
		error.setID(p.getID());
		error.setTo(p.getFrom());
		error.setFrom(p.getTo());
		error.setType(Packet.ERROR);
		PacketError e = p.getDataFactory().createPacketError(PacketError.AUTH, PacketError.NOT_AUTHORIZED_CONDITION);
		if(message!=null)e.addText(message);
		error.setError(e);
		return error;
	}

	public static InfoQuery createSimpleResult(InfoQuery iq){
		InfoQuery result = (InfoQuery)iq.copy();
		result.clearElements();
		result.setID(iq.getID());
		result.setTo(iq.getFrom());
		result.setFrom(iq.getTo());
		result.setType(InfoQuery.RESULT);
		return result;
	}

	public static Packet createUnhandledExceptionError(Packet p, String message){
		Packet error = (Packet) p.copy();
		error.setID(p.getID());
		error.setTo(p.getFrom());
		error.setFrom(p.getTo());
		error.setType(Packet.ERROR);
		PacketError e = p.getDataFactory().createPacketError(PacketError.WAIT, PacketError.INTERNAL_SERVER_ERROR_CONDITION);
		if(message!=null)e.addText(message);
		error.setError(e);
		return error;
	}

   /**
    * Creates a copy of the provided info query with type set to result and to and from swapped
    * @param iq
    * @return
    */
   public static Packet createCopyResult(InfoQuery iq)
   {
      InfoQuery copy = (InfoQuery)iq.copy();
      copy.setType( InfoQuery.RESULT );
      copy.setTo( iq.getFrom() );
      copy.setFrom( iq.getTo() );
      return copy;
   }

}
