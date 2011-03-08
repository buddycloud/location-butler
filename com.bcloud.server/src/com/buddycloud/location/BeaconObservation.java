/**
 * 
 */
package com.buddycloud.location;

import java.util.Vector;


/**
 * A beacon observation represents a number of beacon sightings within a certain time interval.
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

public class BeaconObservation implements Comparable<BeaconObservation> {
	
	/** The observed beacon */
	private Beacon beacon;
	
	/** The last time the beacon was observed, measured in milliseconds since start of unix epoch */
	private long lastSeen;

	/** The first time the beacon was observed, measured in milliseconds since start of unix epoch */
	private long firstSeen;
	
	/** Total time of observation */
	private long timeObserved;

	/** The signal strength for each sighting */
	private Vector<Integer> signalStrengths;
	
	//private int numMergedSightings;
	
	public BeaconObservation(Beacon beacon){
		this.beacon = beacon;
		this.firstSeen = Long.MAX_VALUE;
		this.lastSeen = Long.MIN_VALUE;
		signalStrengths = new Vector<Integer>();
		this.timeObserved = 0;
		//this.numMergedSightings = 0;
	}
	
	/**
	 * Adds a sighting to this observation
	 * @param time The time of the sighting, measured in milliseconds since start of unix epoch
	 * @param signalStrength the signal strength
	 */
	public void addSighting(long startTime, long endTime, int signalStrength){
		if(startTime<firstSeen) firstSeen = startTime;
		if(endTime>lastSeen) lastSeen = endTime;
		
		signalStrengths.add(signalStrength);
		this.timeObserved += (endTime-startTime);
		
//		if(signalStrengths.size()==1){
//			numMergedSightings = 1;
//		}
//		else if(startTime!=lastSeen && signalStrength!=signalStrengths.get(signalStrengths.size()-1)){
//			numMergedSightings++;
//		}
	}
	
//	/**
//	 * Merges this beacon observation with another observation of the same beacon
//	 * @param other The other beacon observation
//	 * @throws IllegalArgumentException if the other observation is of another beacon.
//	 */
//	public void merge(BeaconObservation other){
//		if(!other.getBeacon().equals(beacon)){
//			throw new IllegalArgumentException("Cannot merge observations of two different beacons.");
//		}
//		if(other.getFirstSeen()<firstSeen) firstSeen = other.getFirstSeen();
//		if(other.getLastSeen()>lastSeen) lastSeen = other.getLastSeen();
//		double tmp = avgSignalStrength*numberOfSightings;
//		tmp += other.getAvgSignalStrength()*other.getNumberOfSightings();
//		numberOfSightings += other.getNumberOfSightings();
//		avgSignalStrength = tmp/numberOfSightings;
//	}
//
	/**
	 * @return the beacon
	 */
	public Beacon getBeacon() {
		return beacon;
	}

	/**
	 * @return the numberOfSightings
	 */
	public int getNumberOfSightings() {
		return signalStrengths.size();
	}

	/**
	 * @return the lastSeen
	 */
	public long getLastSeen() {
		return lastSeen;
	}

	/**
	 * @return the firstSeen
	 */
	public long getFirstSeen() {
		return firstSeen;
	}

	/**
	 * @return the avgSignalStrength
	 */
	public double getSignalStrengthMean() {
		double d = 0.0;
		for(int i=0; i<signalStrengths.size(); i++){
			d += signalStrengths.get(i);
		}
		return d/signalStrengths.size();
	}

	/**
	 * @return the avgSignalStrength
	 */
	public double getSignalStrengthStddev() {
		double d = 0.0;
		double mean = getSignalStrengthMean();
		for(int i=0; i<signalStrengths.size(); i++){
			double dev = (signalStrengths.get(i)-mean);
			d += (dev*dev);
		}
		return Math.sqrt(d/signalStrengths.size());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BeaconObservation){
			BeaconObservation other = (BeaconObservation)obj;
			if( //other.getBeacon().equals(beacon) &&
				other.getNumberOfSightings()    == this.getNumberOfSightings()    &&
				other.getSignalStrengthMean()   == this.getSignalStrengthMean()   &&
				other.getSignalStrengthStddev() == this.getSignalStrengthStddev() &&
				other.getFirstSeen()            == this.getFirstSeen()            &&
				other.getLastSeen()             == this.getLastSeen()             &&
				other.getTimeObserved()         == this.getTimeObserved()) return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		long now = System.currentTimeMillis();
		String ssm = toString(getSignalStrengthMean());
		String sss = toString(getSignalStrengthStddev());
		String time = toString(timeObserved);
		String ago = toString(now-lastSeen);
    	return 
    		getNumberOfSightings() + " x cid " + 
    		beacon.getMac() + " @ -" +
    		ssm + "+-" + sss + " dBM, seen " +
    		time + ". Last seen " + ago + " ago.";
	}
	
	private String toString(double d){
		return ""+((double)Math.round(d*10.0)/10.0);
	}
	
	private String toString(long relTimeMsec){
		boolean neg = false;
		if(relTimeMsec<0){
			neg=true;
			relTimeMsec = -relTimeMsec;
		}
		
		long hr = relTimeMsec/(1000*3600);
		relTimeMsec -= hr*1000*3600;
		long min = relTimeMsec/(1000*60);
		relTimeMsec -= min*1000*60;
		long sec = relTimeMsec/1000;
		String s = "";
		if(hr>0) s+= hr + ":";
		if(min<10) s+="0";
		s+=min+":";
		if(sec<10)s+="0";
		s+=sec;
		
		if(neg)  s = "-"+s;
		return s;
	}

	/**
	 * @return the timeObserved
	 */
	public long getTimeObserved() {
		return timeObserved;
	}

	/**
	 * @param timeObserved the timeObserved to set
	 */
	public void setTimeObserved(long timeObserved) {
		this.timeObserved = timeObserved;
	}

	/**
	 * Compares this BeaconObservation to another (for use in sorting).
	 * This method will return a negative value the beacon of this observation was last seen longer ago than that of o
	 */
	public int compareTo(BeaconObservation other) {
		if(other.getLastSeen()==this.getLastSeen()){
			return (int)(this.getFirstSeen() - other.getFirstSeen());
		}
		else{
			return (int)(this.getLastSeen() - other.getLastSeen());
		}
		
	}
	
	

}
