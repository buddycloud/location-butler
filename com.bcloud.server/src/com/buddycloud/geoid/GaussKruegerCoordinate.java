package com.buddycloud.geoid;


/**
 * Gauss-Kr�ger coordinates, including conversion to and from geodetic coordinates.
 * 
 * gaus ->   geo from http://www.gsmsite.de/viagkoordinaten.htm
 * geo  -> gauss from http://www.florian-brede.de/post/2007/08/Conversion-of-the-Gauss-Krueger-notation-into-latitudelongitude.aspx
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

public class GaussKruegerCoordinate {

	/** I have no idea what this is */
	private static int DEGREES = 5;
	
	/** The x-coordinate, distance north equator in meters */
	public double y;
	
	/** The y-coordinate, distance east of the 0-meridian in meters */
	public double x; // east

	/**
	 * Creates a new set of gauss-kr�ger coordinates initialized to the provided coordinates.
	 * @param x The x-coordinate, distance north equator in meters
	 * @param y The y-coordinate, distance east of the 0-meridian
	 */
	public GaussKruegerCoordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new set of guss-kr�ger coordinates initialized from the provided set of geodetic coordinates
	 * @param geo
	 */
	public GaussKruegerCoordinate(Point geo) {
		double rho = 180 / Math.PI;
		double brDezimal, laDezimal, rm, e2, c, bf, g, co, g2, g1, t, dl, fa, sy;
		sy = 3;
		e2 = 0.0067192188;
		c = 6398786.849;
		brDezimal = geo.getLatitude();
		laDezimal = geo.getLongitude();
		bf = brDezimal / rho;
		g = 111120.61962 * brDezimal - 15988.63853 * Math.sin(2 * bf)
				+ 16.72995 * Math.sin(4 * bf) - 0.02178 * Math.sin(6 * bf)
				+ 0.00003 * Math.sin(8 * bf);
		co = Math.cos(bf);
		g2 = e2 * (co * co);
		g1 = c / Math.sqrt(1 + g2);
		t = Math.tan(bf);
		dl = laDezimal - sy * 3;
		fa = co * dl / rho;
		y = g + fa * fa * t * g1 / 2 + fa * fa * fa * fa * t * g1
				* (5 - t * t + 9 * g2) / 24;
		rm = fa * g1 + fa * fa * fa * g1 * (1 - t * t + g2) / 6 + fa * fa * fa
				* fa * fa * g1 * (5 - 18 * t * t * t * t * t * t) / 120;
		x = rm + sy * 1000000 + 500000;
		
		x/=10;
		y/=10;
	}

	
	/**
	 * A test...
	 * @param args not needed
	 */
	public static void main(String[] args){
		GaussKruegerCoordinate gk = new GaussKruegerCoordinate(369230,534220);
		Point geo = gk.toGeodetic();
		System.out.println("(369230,534220)->"+geo+", should be (48.189586, 11.586773)");
		if(Math.abs(geo.getLatitude()-48.189586)>0.000001) System.out.println("Latitude failed");
		if(Math.abs(geo.getLongitude()-11.586773)>0.000001)  System.out.println("Longitude failed");
		geo.setLatitude(48.189586);
		geo.setLongitude(11.586773);
		gk = new GaussKruegerCoordinate(geo);
		System.out.println(geo+"->"+"("+gk.x+","+gk.y+")"+", should be (369230,534220)");
		if(Math.abs(gk.x-369230)>0.01) System.out.println("X failed");
		if(Math.abs(gk.y-534220)>0.01)  System.out.println("Y failed");
	}
	
	/**
	 * Returns the geodetic coordinates corresponding to this gauss-kr�ger coordinate set.
	 * @return The geodetic coordinates
	 */
	public Point toGeodetic(){
		String xString = "" + x;
		String yString = "" + y;
		char rechtswert = xString.charAt(0);
		double lon0 = 0;
		if (rechtswert == '2')
			lon0 = 6;
		if (rechtswert == '3')
			lon0 = 9;
		if (rechtswert == '4')
			lon0 = 12;
		if (rechtswert == '5')
			lon0 = 15;

		double[] ENh = new double[3];
		double[] BLh = new double[3];

		// remove first digit of x-coordinate
		xString = xString.substring(1, xString.length());

		ENh[0] = Double.parseDouble(xString) * 10;
		ENh[1] = Double.parseDouble(yString) * 10;

		int m0 = 1;

		int ell = 1;

		double a = getEarthRadius(ell, 0);
		double b = getEarthRadius(ell, 1);
		double c = a * a / b;
		double e22 = (a * a - b * b) / b / b;

		double lon0rad = toRad(lon0);
		BLh = konfToGeog(ENh, lon0rad, m0, c, e22);
		
		Point geo = new Point();
		geo.setLatitude(BLh[0]*180/Math.PI);
		geo.setLongitude(BLh[1]*180/Math.PI);
		
		return geo;
	}

	/**
	 * Returns the earth radius
	 * @param nr earth model to use???
	 * @param type maximum or minimum radius?
	 * @return the earth radiues
	 */
	private double getEarthRadius(int nr, int type) {
		double radius = 0;
		if (nr == 0) {
			if (type == 0) {
				radius = 6378137.000;
			}
			if (type == 1) {
				radius = 6356752.314;
			}
		}
		if (nr == 1) {
			if (type == 0) {
				radius = 6377397.155;
			}
			if (type == 1) {
				radius = 6356078.962;
			}
		}
		if (nr == 2) {
			if (type == 0) {
				radius = 6378388.000;
			}
			if (type == 1) {
				radius = 6356911.946;
			}
		}
		if (nr == 3) {
			if (type == 0) {
				radius = 6378245.000;
			}
			if (type == 1) {
				radius = 6356863.019;
			}
		}
		return radius;
	}

	/**
	 * I have no idea what this method does...
	 * @param ENh No idea
	 * @param L0 Possibly reference longitude
	 * @param m0 no idea
	 * @param c some earth-constant?
	 * @param e22 some earth-constant?
	 * @return A vector of some numbers...
	 */
	private double[] konfToGeog(double[] ENh, double L0, double m0, double c, double e22) {
		double dy = (ENh[0] - 500000.0) / m0;
		double bf = bf(ENh[1], m0, c, e22);
		double[] q = getKoeffG(c, e22, bf);
		double[] arg = new double[5];
		for (int i = 0; i < 5; i++) {
			arg[i] = Math.pow(dy, i + 1);
		}
		double[] geog = mult(q, arg, 2, 5);
		geog[0] += bf;
		geog[1] += L0;
		return geog;
	}
	
	/**
	 * I have no idea what this does...
	 * @param N No idea
	 * @param m0 No idea
	 * @param c some earth-constant?
	 * @param e22 some earth-constant?
	 * @return A number
	 */
	private double bf(double N, double m0, double c, double e22) { // Breite Fusspunkt
		double a = c;
		double e2 = Math.sqrt(e22);
		for (int i = 1; i <= DEGREES + 2; i++) {
			double ab = Math.pow(e2 / 2.0, 2 * i) * binom(-1.5, i, 1);
			a += c * ab * binom(2 * i, i, 0);
		}

		double[] q = getKoeffB();
		double[] arg = new double[4];
		for (int i = 0; i < 4; i++) {
			arg[i] = Math.pow(e22, i + 1);
		}
		double[] b = mult(q, arg, 3, 4);
		double b0 = N / m0 / a;
		double bf = b0;

		for (int i = 1; i <= 3; i++) {
			bf += b[i - 1] * Math.sin(2 * i * b0);
		}
		return bf;
	}	
	
	/**
	 * I have no idea what this does...
	 * Optimization tip: Make static
	 * @return A vector of numbers
	 */
	private double[] getKoeffB() {
		double[] b = new double[3*4];
		b[getIndex(0, 0, 4)] = 3.0 / 8.0;
		b[getIndex(0, 1, 4)] = -3.0 / 16.0;
		b[getIndex(0, 2, 4)] = 213.0 / 2048.0;
		b[getIndex(0, 3, 4)] = -255.0 / 4096.0;
		b[getIndex(1, 1, 4)] = 21.0 / 256.0;
		b[getIndex(1, 2, 4)] = -b[getIndex(1, 1, 4)];
		b[getIndex(1, 3, 4)] = 533.0 / 8192.0;
		b[getIndex(2, 2, 4)] = 151.0 / 6144.0;
		b[getIndex(2, 3, 4)] = -453.0 / 12288.0;
		return b;
	}
	
	/**
	 * I have no idea what this does....
	 * @param c some earth-constant?
	 * @param e22 some earth-constant?
	 * @param bf no idea
	 * @return a vector of numbers
	 */
	private double[] getKoeffG(double c, double e22, double bf) {
		double co = Math.cos(bf);
		double ta = Math.tan(bf);
		double ta2 = ta * ta;
		double v = Math.sqrt(1.0 + e22 * co * co);
		double r1 = v / c;
		double r12 = r1 * r1;
		double[] g = new double[2*5];
		g[getIndex(1, 0, 5)] = r1 * co * (1.0 + ta2);
		g[getIndex(0, 1, 5)] = -v * v * r12 * ta / 2.0;
		g[getIndex(1, 2, 5)] = -r12 * r1 * co * (1.0 + ta2) * (v * v + 2.0 * ta2) / 6.0;
		g[getIndex(0, 3, 5)] = -r12 * r12 * ta * (1.0 - 6.0 * v * v - 3.0 * (3.0 - 2.0 * v * v) * ta2) / 24.0;
		g[getIndex(1, 4, 5)] = r12 * r12 * r1 * co * (1.0 + ta2)	* (5.0 + 28.0 * ta2 + 24.0 * ta2 * ta2) / 120.0;
		return g;
	}
	
	/**
	 * Returns the index in a one-dimensional representation of a two-dimmensional matrix
	 * @param row the matrix row
	 * @param column the matrix column
	 * @param columns number of columns in matrix
	 * @return The equivalent 1D index
	 */
	private int getIndex(int row, int column, int columns) {
		return row*columns+column;
	}
	
	/**
	 * Returns the faculty of the specified number: n!
	 * @param n a number
	 * @return the faculty of n
	 */
	private int faculty(double n) {
		int f = 1;
		if (n > 0) {
			for (int i = 1; i <= n; i++) {
				f *= i;
			}
		}
		return f;

	}

	/**
	 * Calculates some binomial...?
	 * @param o a number
	 * @param u another number
	 * @param type some type....
	 * @return A binominal value..?
	 */
	private double binom(double o, double u, int type) {
		double bi = 1;
		if (type == 0) {
			double diff = o - u;
			if (diff > 0) {
				bi = faculty(o) / faculty(u) / faculty(diff);
			}
		} else {
			bi = o;
			if (u > 0) {
				for (int i = 1; i < u; i++) {
					bi *= o - i;
				}
			}
			bi /= faculty(u);
		}
		return bi;
	}
	
	/**
	 * Multiplies the provided matrix with the provided vector
	 * @param matrix The matrix (one-dimensional representation in array of size m*n)
	 * @param vektor the vector
	 * @param n the number of rows in matrix 
	 * @param m the number of columns in matrix and elements in vector
	 * @return the product vector (size n)
	 */
	private double[] mult(double[] matrix, double[] vektor, int n, int m) {
		   double[] vek = new double[n];
		   for (int i=0; i<n; i++) {
		      for (int j=0; j<m; j++) {
		         vek[i] += matrix[getIndex(i,j,m)]*vektor[j];
		      }
		   }
		   return vek;
	}

	private double toRad(double deg) {
		   return Math.PI*deg/180;
	}
}