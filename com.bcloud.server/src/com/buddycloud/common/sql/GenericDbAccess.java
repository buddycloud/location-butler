/**
 * 
 */

package com.buddycloud.common.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

/**
 * Base class for database interfaces
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
 */
public class GenericDbAccess
{

   protected Connection connection;

   protected String driverClass;

   protected String driverType;

   protected String host;

   protected Logger logger;

   protected String name;

   protected String pass;

   protected String port;

   protected String user;


   public GenericDbAccess()
   {
      this.logger = Logger.getLogger( getClass() );

      // load preferences if found
      Preferences prefs = Preferences.userNodeForPackage( getClass() );
      setHost( prefs.get( "database_host", "" ) );
      setPort( prefs.get( "database_port", "" ) );
      setName( prefs.get( "database_name", "" ) );
      setUser( prefs.get( "database_user", "" ) );
      setPass( prefs.get( "database_pass", "" ) );
      setType( prefs.get( "database_type", "" ) );
      
      validateSettings();
   }


   /**
    * Connect to database
    * 
    * @throws SQLException
    */
   public void connect() throws SQLException
   {
      if (connection != null && !connection.isClosed())
      {
         logger.debug( "Closing stale DB connections..." );
         connection.close();
      }

      String connectionString =
         "jdbc:" + driverType + "://" + host + ":" + port + "/" + name + "?user=" + user
            + "&password=" + pass
            + "&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";

      // connect to database
      logger.info( "Connecting..." );
      connection = DriverManager.getConnection( connectionString );
      logger.info( "Connected" );

   }


   /**
    * Disconnects from database
    * 
    * @throws SQLException
    */
   public void disconnect() throws SQLException
   {
      logger.info( "Disconnecting..." );
      if (connection != null)
      {
         if (!connection.isClosed())
            connection.close();

         connection = null;
      }
      logger.info( "Disconnected" );
   }


   /**
    * Specifies the database type to use. Supported: "mysql" or "postgresql".
    */
   public void setType(String type)
   {
      if (type.toLowerCase().contains( "mysql" ))
      {
         this.driverClass = "com.mysql.jdbc.Driver";
         this.driverType = "mysql";
      }
      else
      {
         this.driverClass = "org.postgresql.Driver";
         this.driverType = "postgresql";
      }
      logger.info( "type = " + driverType );
      logger.info( "driver = " + driverClass );
   }


   /**
    * Specifies the database host to connect to
    * 
    * @param host
    *           The host name
    */
   public void setHost(String host)
   {
      this.host = host;
      logger.info( "host = " + host );
   }


   /**
    * Specifies the name of the database to connect to
    * 
    * @param name
    *           The database name
    */
   public void setName(String name)
   {
      this.name = name;
      logger.info( "name = " + name );
   }


   /**
    * Specifies the password to connect to the database
    * 
    * @param pass
    *           The password
    */
   public void setPass(String pass)
   {
      this.pass = pass;
      logger.info( "pass = *********" );
   }


   /**
    * Specifies the port to connect to
    * 
    * @param port
    *           The port
    */
   public void setPort(String port)
   {
      this.port = port;
      logger.info( "port = " + port );
   }


   /**
    * Specifies the username to use when connecting
    * 
    * @param user
    *           The user name
    */
   public void setUser(String user)
   {
      this.user = user;
      logger.info( "user = " + user );
   }


   /**
    * Asserts that the database is properly connected
    * 
    * @throws SQLException
    */
   public void validateConnected() throws SQLException
   {
      if (connection == null)
      {
         logger.info( "Connection is NULL. Connecting." );
         connect();
         return;
      }
      else if (connection.isClosed())
      {
         logger.info( "Connection is closed. Reconnecting." );
         connect();
         return;
      }
      else
      {
         try
         {
            connection.getMetaData();
         }
         catch (Exception e)
         {
            logger.info( "Connection is dead: " + e );
            logger.info( "Reconnecting." );
            connect();
         }
      }
   }


   /**
    * Tests the settings by connecting and reconnecting to the database
    */
   public void validateSettings()
   {
      logger.info( "Validating settings by doing a connect / disconnect..." );

      // load DB driver
      try
      {
         // The newInstance() call is a work around for some
         // broken Java implementations

         Class.forName( driverClass ).newInstance();
         logger.info( "Driver " + driverClass + " loaded ok" );
      }
      catch (Exception e)
      {
         logger.error( "Failed to load DB driver.", e );
         throw new RuntimeException( e );
      }

      try
      {
         connect();
         disconnect();
         logger.info( "Settings are valid." );
      }
      catch (SQLException e)
      {
         logger.error( "Settings invalid.", e );
         throw new RuntimeException( "Settings invalid!" );
      }
   }

}
