package com.eric.ssbl.donate.android.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * This entity represents a location. Locations contain a latitude and longitude
 * and represent the geographic location of a User, Event, etc. The Location class
 * also exposes methods for comparing the distances between different points.
 * 
 * @author ashwin
 * 
 * @see com.hunnymustard.ssbm.model.User
 * @see com.hunnymustard.ssbm.model.Event
 */
@JsonIdentityInfo(scope=Location.class, generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Location {

	private Integer _id;
	private Double _lat, _lon;
	
	public Location() {}
	
	public Location(Integer id, Double lat, Double lon) {
		_id = id;
		_lat = lat;
		_lon = lon;
	}

	public Integer getLocationId() {
		return _id;
	}
	
	public void setLocationId(Integer id) {
		_id = id;
	}

	public Double getLatitude() {
		return _lat;
	}
	
	public void setLatitude(Double lat) {
		_lat = lat;
	}

	public Double getLongitude() {
		return _lon;
	}
	
	public void setLongitude(Double lon) {
		_lon = lon;
	}
	
	/**
	 * Calculates the distance between this location and the specified location
	 * using the haversine formula. The result of this computation is the shortest
	 * distance over the earth's surface (ignoring hills, etc.).
	 * 
	 * @param oth
	 * @return
	 */
	public Double distance(Location oth) {
		double lat1 = Math.toRadians(this.getLatitude());
		double lat2 = Math.toRadians(oth.getLatitude());
		double lon1 = Math.toRadians(this.getLongitude());
		double lon2 = Math.toRadians(oth.getLongitude());
		
		// R = radius of the Earth = 3956 miles
		return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * 
				Math.cos(lat2) * Math.cos(lon2 - lon1)) * 3956;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(_lat)
			.append(_lon)
			.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Location)
			return new EqualsBuilder()
				.append(_lat, ((Location) obj).getLatitude())
				.append(_lon, ((Location) obj).getLongitude())
				.build();
		return false;
	}
}
