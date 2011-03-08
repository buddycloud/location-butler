package com.buddycloud.geoid;



/**
 * A point on the earth's surface defined by a latitude and a longitude.
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

public class Point
{

    /**
     * Latitude.
     */
    private double latitude;

    /**
     * Longitude.
     */
    private double longitude;

    /**
     * Constructs a new position initialized at the equator and the
     * Greenwich meridian (0,0).
     */
    public Point()
    {
        this(0.0, 0.0);
    }

    /**
     * Constructs a new point.
     * 
     * @param latitude
     *            The initial latitude.
     * @param longitude
     *            The initial longitude.
     */
    public Point(double latitude, double longitude)
    {
        setLatitude(latitude);
        setLongitude(longitude);
    }
    
    /**
     * Sets the latitude of this point
     * @param latitude The new latitude value in degrees range -90 to 90.
     */
    public void setLatitude(double latitude){
       if(latitude<-90) throw new IllegalArgumentException("Latitude cannot be less than -90 degrees. Was "+latitude);
       if(latitude >90) throw new IllegalArgumentException("Latitude cannot be more than 90 degrees. Was "+latitude);
       if(Double.isInfinite(latitude)) throw new IllegalArgumentException("Latitude cannot be infinite.");
       if(Double.isNaN(latitude)) throw new IllegalArgumentException("Latitude must be a number.");
       this.latitude = latitude;
    }

    /**
     * Sets the longitude of this point
     * @param longitude The new longitude value in degrees range -180 to 180.
     */
    public void setLongitude(double longitude){
       if(longitude<-180) throw new IllegalArgumentException("Longitude cannot be less than -180 degrees. Was "+longitude);
       if(longitude >180) throw new IllegalArgumentException("Longitude cannot be more than 180 degrees. Was "+longitude);
       if(Double.isInfinite(longitude)) throw new IllegalArgumentException("Longitude cannot be infinite.");
       if(Double.isNaN(longitude)) throw new IllegalArgumentException("Longitude must be a number.");
       this.longitude = longitude;
    }
    
    /**
     * @return the latitude of this point in degrees range -90 to 90.
     */
    public double getLatitude(){
       return latitude;
    }

    /**
     * @return the longitude of this point in degrees range -180 to 180.
     */
    public double getLongitude(){
       return longitude;
    }

    /**
     * Constructs a copy of this point.
     * 
     */
    public Point copy()
    {
    	return new Point(latitude, longitude);
    }

    /**
     * @return Returns a String representation of this coordinate on the form
     *         (lat,long).
     */
    public String toString()
    {
        return "(" + latitude + "," + longitude + ")";
    }
    
    /**
     * Returns the distance form this point to the other. The distance is calculated along a great circle segment defined by this, the other coordinate and the earths center.
     * @param other The other coordinate.
     * @return The great circle distance in meters.
     */
    public double getDistanceTo(Point other){
    	
    	// convert to radians
    	double lat1 = this.latitude*Math.PI/180.0;
    	double lon1 = this.longitude*Math.PI/180.0;
    	double lat2 = other.latitude*Math.PI/180.0;
    	double lon2 = other.longitude*Math.PI/180.0;

    	// Calculate the great circle distance in radiance using the
    	// Haversine formula (better than cosine rule for small
    	// distances)
    	double dLat = lat2 - lat1;
    	double dLon = lon2 - lon1;    	

    	double M = Math.sin(0.5*dLat)*Math.sin(0.5*dLat) +
    	Math.sin(0.5*dLon)*Math.sin(0.5*dLon) * Math.cos(lat1) * Math.cos(lat2);

    	double arcDist = 2.0 * Math.atan2(Math.sqrt(M), Math.sqrt(1.0-M));
    	
     	return arcDist*EarthConstants.QUADRATIC_MEAN_RADIUS;
    }    	

    /**
     * Returns the direction form this point to the other, measured at this coordinate. The direction is calculated along a great circle segment defined by this, the other coordinate and the earths center.
     * Reference: http://mathforum.org/library/drmath/view/55417.html
     * @param other The other coordinate.
     * @return The direction in degrees.
     */
    
    public double getDirectionTo(Point other){
    	
    	
    	double lat1 = this.latitude*Math.PI/180.0;
    	double lon1 = this.longitude*Math.PI/180.0;
    	double lat2 = other.latitude*Math.PI/180.0;
    	double lon2 = other.longitude*Math.PI/180.0;
    	
    	double y = Math.sin(lon2-lon1)*Math.cos(lat2);
    	double x = Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1);
    	
    	double dirRad = Math.atan2(y, x);
    	
    	return 180*dirRad/Math.PI;
    }
    
    
    public Point getPointAt(double direction, double distance){
    	
    	// convert to radians
    	double lat1 = latitude*Math.PI/180.0;
    	double lon1 = longitude*Math.PI/180.0;
    	double dir = direction*Math.PI/180.0;
    	
    	// non-dimensionalize distance with respect to earth radius
    	double arcLength = distance / EarthConstants.QUADRATIC_MEAN_RADIUS;
    	
    	if(arcLength == 0.0){
    		return new Point(latitude, longitude);
    	}

    	// calculate latitude of point B
    	double lat2 =
    		Math.asin(
    				Math.sin(lat1)*Math.cos(arcLength) +
    				Math.cos(lat1)*Math.sin(arcLength)*Math.cos(dir)
    		);

    	double lon2;

    	// check if one of the end points is on a pole
    	if(Math.abs(lat1) == Math.PI/2){
    		lon2 = dir;
    	}
    	else if(Math.abs(lat2) == Math.PI/2){
    		lon2 = lon1;
    	}
    	else{
    		double y =
    			Math.sin(dir)*Math.sin(arcLength)/Math.cos(lat2);

    		double x =
    			(Math.cos(arcLength) - Math.sin(lat1)*Math.sin(lat2)) /
    			(Math.cos(lat1)*Math.cos(lat2));

    		double dlon = Math.atan2(y, x);

    		lon2 = lon1 + dlon;
    	}

    	return new Point(lat2*180.0/Math.PI, lon2*180/Math.PI);

    }
    
    /**
     * Shifts this point the given distance in the given direction
     * @param direction The direction in degrees range -180 to 180)
     * @param distance The distance in meters
     */
    public void shift(double direction, double distance){
       if(direction<-180 || direction >180)throw new IllegalArgumentException("Direction must be in the range -180 deg to 180 deg. Was "+direction);
       if(distance<0) throw new IllegalArgumentException("Distance must be positive. Was "+distance);
       if(distance>0){
          Point p = getPointAt(direction, distance);
          setLatitude( p.getLatitude() );
          setLongitude( p.getLongitude() );
       }
       
    }

    
    /**
     * Returns true if the supplied object is an instance of GeodeticCoordinate with identical latitude an longitude to this.
     */
    public boolean equals(Object o){
        if(o instanceof Point){
            Point g = (Point)o;
            if(g.latitude == latitude && g.longitude == longitude)
                return true;
            
        }
        return false;
    }
    
    /**
     * Returns true if this point has the default value of (0.0,0.0)
     * @return Returns true if default, otherwise false.
     */
    public boolean isDefault(){
        return (latitude == 0.0 && longitude==0.0);
    }
    
}
