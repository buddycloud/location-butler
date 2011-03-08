/**
 * 
 */
package com.buddycloud.common;

/**
 * Base class for all bcloud data onjects
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
public class DatabaseObject {
	
	/**
	 * Object identifier, unique for its class
	 * TODO: Is a int big enough? If we ever have more than 2,147,483,648 objects of a class it isn't
	 */
	private int id;
	
	/**
	 * Returns the identifier of this object
	 * @return The object identifier
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Sets the identifier of this object. The identifier must be unique 
	 * for all objects of the same class. Note that no test of uniqueness
	 * is performed here.
	 * @param id The object identifier
	 */
	public void setId(int id){
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		if(o instanceof DatabaseObject && o.getClass().equals(getClass())){
			DatabaseObject other = (DatabaseObject)o;
			return other.getId()==this.getId();
		}
		else{
			return false;
		}
	}

}
