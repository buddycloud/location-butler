/**
 * 
 */
package com.buddycloud.common;

import org.jabberstudio.jso.JID;

import com.buddycloud.Constants;

/**
 * A Buddycloud or external XMPP user
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
public class LocationUser extends DatabaseObject {
	
	private JID jid;

   /**
    * @return the jid
    */
   public JID getJid()
   {
      return jid;
   }

   
   /**
    * @param jid the jid to set
    */
   public void setJid(JID jid)
   {
      this.jid = jid;
   }
   
   public String toString(){
      if(jid.getDomain().equals( Constants.XMPP_HOST_NAME )){
         return jid.getNode();
      }
      else{
         return String.format( "%s@%s", jid.getNode(), jid.getDomain());
      }
   }
	

}
