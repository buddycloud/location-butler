/**
 * 
 */

package com.buddycloud.location.xmpp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.buddycloud.common.Location;
import com.buddycloud.common.LocationUser;
import com.buddycloud.common.xmpp.SynchronizedPacketQueue;


/**
 * Third-party application adapter for facebook. Pushes data to a PHP-based facebook app
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

public class FacebookLocationEventHandler implements LocationEventHandler
{

   private Logger logger;


   public FacebookLocationEventHandler()
   {
      logger = Logger.getLogger( getClass() );
   }


   /*
    * (non-Javadoc)
    * 
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#getAppName()
    */
   public String getAppName()
   {
      return "Facebook";
   }


   /* (non-Javadoc)
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userCurrentLocationChanged(com.bcloud.server.community.User, java.lang.String)
    */
   public void userCurrentLocationChanged(LocationUser user, Location l)
   {
      try
      {
         String urlString =
            "http://facebook.buddycloud.com/userupdate.php?action=setcurr&user="
               + user;
         pushUpdate( urlString );
      }
      catch (Exception e)
      {
         logger.error( "Failed to notify current location change for user " + user + ": "
                       + e.getMessage() );
      }
   }


   /* (non-Javadoc)
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userNextLocationChanged(com.bcloud.server.community.User, java.lang.String)
    */
   public void userNextLocationChanged(LocationUser user, Location location)
   {
      try
      {
         String urlString =
            "http://facebook.buddycloud.com/userupdate.php?action=setnext&user="
               + user;
         pushUpdate( urlString );
      }
      catch (Exception e)
      {
         logger.error( "Failed to notify next location changed for user " + user + ": "
                       + e.getMessage() );
      }
   }


   /* (non-Javadoc)
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userDefinedNewPlace(com.bcloud.server.community.User, java.lang.String)
    */
   public void userDefinedNewPlace(LocationUser user, Location location)
   {
      try
      {
         String urlString =
            "http://facebook.buddycloud.com/userupdate.php?action=newplace&user="
               + user;
         pushUpdate( urlString );
      }
      catch (Exception e)
      {
         logger.error( "Failed to notify new place defined for user " + user + ": "
                       + e.getMessage() );
      }
   }


   /* (non-Javadoc)
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userEnteredPlace(com.bcloud.server.community.User, java.lang.String)
    */
   public void userEnteredPlace(LocationUser user, Location location)
   {
      try
      {
         String urlString =
            "http://facebook.buddycloud.com/userupdate.php?action=enteredplace&user="
               + user;
         pushUpdate( urlString );
      }
      catch (Exception e)
      {
         logger.error( "Failed to notify place entered for user " + user + ": "
                       + e.getMessage() );
      }
   }


   /* (non-Javadoc)
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userLeftPlace(com.bcloud.server.community.User, java.lang.String)
    */
   public void userLeftPlace(LocationUser user, Location location)
   {
   }


   /* (non-Javadoc)
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#userPreviousLocationChanged(com.bcloud.server.community.User, java.lang.String)
    */
   public void userPreviousLocationChanged(LocationUser user, Location l)
   {
   }


   /* (non-Javadoc)
    * @see com.bcloud.thirdparty.apps.ThirdPartyApplicationAdapter#usersMet(com.bcloud.server.community.User, com.bcloud.server.community.User, java.lang.String)
    */
   public void usersMet(LocationUser user, LocationUser otherUser, Location location)
   {
      try
      {
         String urlString =
            "http://facebook.buddycloud.com/userupdate.php?action=metwith&user="
               + user + "&with=" + otherUser;
         pushUpdate( urlString );
      }
      catch (Exception e)
      {
         logger.error( "Failed to notify rendezvous of users " + user + " and "
                       + otherUser + ": " + e.getMessage() );
      }
   }



   private void pushUpdate(String urlString) throws IOException
   {

      URL url;
      try
      {
         url = new URL( urlString );
      }
      catch (MalformedURLException e)
      {
         throw new IOException( e.getMessage() );
      }

      HttpURLConnection connection = null;
      InputStreamReader reader = null;
      String content = new String();

      char[] contentBuf = new char[1024 * 4];
      connection = (HttpURLConnection) url.openConnection();

      // set the user agent property (no value). If this is not set,
      // some servers may block (HTTP error 403)...
      connection.setRequestProperty( "User-Agent", "" );

      // load content
      reader = new InputStreamReader( (InputStream) connection.getContent() );
      int len = 0;
      do
      {
         len = reader.read( contentBuf );
         if (len > 0)
            content += new String( contentBuf, 0, len );
      }
      while (len > 0);

      if (reader != null)
         reader.close();

   }


   /* (non-Javadoc)
    * @see com.buddycloud.thirdparty.apps.LocationEventHandler#init(com.buddycloud.common.xmpp.SynchronizedPacketQueue)
    */
   public void init(SynchronizedPacketQueue outQ)
   {
   }

}
