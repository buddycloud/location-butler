package com.buddycloud.common.xmpp;

import java.util.HashMap;
import java.util.Map;

/**
 * The properties of a custom packet that are needed to set up packet listeners and disco info
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

public class PacketFilter {

   private Map<String, String> namespaces;
//   private String namespaceAlias;
   private String xPath;
   
   /**
    * @return the namespaces
    */
   public Map<String, String> getNamespaces()
   {
      return namespaces;
   }

   /**
    * Adds a namespace used in the xpath of this filter
    * @param alias The namespace alias e.g "commands"
    * @param namespace The full namespace e.g. "http://jabber.org/protocol/commands"
    */
   public void addNamespace(String alias, String namespace)
   {
      if(namespaces==null) namespaces = new HashMap<String, String>();
      namespaces.put( alias, namespace );
   }
   
//   /**
//    * @return the namespaceAlias
//    */
//   public String getNamespaceAlias()
//   {
//      return namespaceAlias;
//   }
//   
//   /**
//    * @param namespaceAlias the namespaceAlias to set
//    */
//   public void setNamespaceAlias(String namespaceAlias)
//   {
//      this.namespaceAlias = namespaceAlias;
//   }
   
   /**
    * @return the xPath
    */
   public String getXPath()
   {
      return xPath;
   }
   
   /**
    * @param path the xPath to set
    */
   public void setXPath(String path)
   {
      xPath = path;
   }
}
