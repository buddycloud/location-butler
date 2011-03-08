package com.buddycloud.thirdparty.services;

import java.io.IOException;
import java.util.Collection;

import com.buddycloud.common.Location;
import com.buddycloud.common.Place;
import com.buddycloud.geoid.Point;
import com.buddycloud.location.CountryCode;

/**
 * Common interface for third party location services. Since all third party services may not support all these methods, <code>null</code> is a valid return value.
 * TODO: 
 *  - Split this into separate interfaces for GeoCodingService and ReverseGeoCodingService
 *  - Add methods for getServiceName and needsCacheing
 *  - Let implementations recide in external project
 *  - Let location engine create instance reflectively using classByNamne with class name in preferences
 *  - Implement lookup result caching and use if needed by service
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



public interface ThirdPartyLocationService {
	
//	/**
//	 * Get the best known coordinates for the cell tower with the specified parameters
//	 * @param cid Cell ID
//	 * @param lac Area Code
//	 * @param mnc Network Code
//	 * @param mcc Country Code
//	 * @return The best known coordinate or null if nothing known
//	 * @throws IOException when network problems arise
//	 */
//	public GeodeticCoordinate getCoordinates(int cid, int lac, int mnc, int mcc) throws IOException;
	
	/**
	 * Get the best known coordinates for the given street address
	 * @param streetAndNumber Street name and street number
    * @param city city, town or village
    * @param postalCode postal code
	 * @param countryCode country code
	 * @return The best known coordinate or null if nothing known
	 * @throws IOException when network problems arise
	 */
	public Point getCoordinates(String streetAndNumber, String city, String postalCode, CountryCode countryCode) throws IOException;

	/**
	 * Get the location hierarchy (country, state, county, city, neighborhood, street) for the specified coordinate
	 * @param coordinates The coordinates
	 * @return The best known name or null if nothing known
	 * @throws IOException when network problems arise
	 */
	public Location getLocation(Point coordinates) throws IOException;
	
//	/**
//	 * Get the best known location name where the cell tower with the specified parameters is located
//	 * @param cid Cell ID
//	 * @param lac Area Code
//	 * @param mnc Network Code
//	 * @param mcc Country Code
//	 * @return The best known name or null if nothing known
//	 * @throws IOException when network problems arise
//	 */
//	public String getLocationName(int cid, int lac, int mnc, int mcc) throws IOException;
	
//	/**
//	 * Gets a list of locations with the equal or similar names to the specified name, nearby the cell tower with the specified parameters
//	 * @param name The name to search for
//	 * @param cid Cell ID
//	 * @param lac Area Code
//	 * @param mnc Network Code
//	 * @param mcc Country Code
//	 * @return A list of locations, or null if nothing known
//	 * @throws IOException when network problems arise
//	 */
//	public Collection<Place> getNearbyLocations(String name, int cid, int lac, int mnc, int mcc) throws IOException;
//	
	/**
	 * Gets a list of places with the equal or similar names to the specified name, nearby the specified address
	 * @param name The name to search for
	 * @param streetAndNumber Street name and street number
	 * @param localHints space separated list of local hints to help narrow the search (e.g. "Haidhausen 81667 München")
	 * @param country country name
	 * @return A list of locations, or null if nothing known
	 * @throws IOException when network problems arise
	 */
	public Collection<Place> findPlaces(String name, String localHints, String country) throws IOException;

	/**
	 * Gets a list of locations with the equal or similar names to the specified name, nearby the specified coordinate
	 * @param name The name to search for
	 * @param coordinates The coordinate
	 * @return A list of locations, or null if nothing known
	 * @throws IOException when network problems arise
	 */
	public Collection<Place> findPlaces(String name, Point coordinates) throws IOException;

}
