package com.buddycloud.geoid;

/**
 * This class provides some essential constants related to the geometry of the
 * earth.
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

public class EarthConstants
{

    /**
     * The length of the earth semi major axis (equatorial radius) in meters
     * Reference: http://en.wikipedia.org/wiki/Earth_radius
     */
    public static final double SEMI_MAJOR_AXIS = 6378135.0;

    /**
     * Semi major axis, private shorthand notation.
     */
    private static final double a = SEMI_MAJOR_AXIS;

    /**
     * The length of the earth semi minor axis (ploar radius) in meters
     * Reference: http://en.wikipedia.org/wiki/Earth_radius
     */
    public static final double SEMI_MINOR_AXIS = 6356750.0;

    /**
     * Semi minor axis, private shorthand notation.
     */
    private static final double b = SEMI_MINOR_AXIS;

    /**
     * The ellipsoidal quadratic mean radius in meters Reference:
     * http://en.wikipedia.org/wiki/Earth_radius
     */
    public static final double QUADRATIC_MEAN_RADIUS = Math.sqrt((3 * a * a + b
        * b) / 4);

    /**
     * The eccentricity of the earth. Reference:
     * http://en.wikipedia.org/wiki/Eccentricity_(mathematics)
     */
    public static final double ECCENTRICITY = Math.sqrt(1 - a / b);
}
