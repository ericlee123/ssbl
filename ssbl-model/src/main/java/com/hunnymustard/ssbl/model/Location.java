package com.hunnymustard.ssbl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="locations")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Location {

	private Integer _id;
	private Double _lat, _lon;
	
	public Location() {}
	
	public Location(Integer id, Double lat, Double lon) {
		_id = id;
		_lat = lat;
		_lon = lon;
	}
	
	@Id
	@GenericGenerator(name="gen",strategy="increment")
	@GeneratedValue(generator="gen")
	@Column(name="location_id", unique=true, nullable=false)
	public Integer getLocationId() {
		return _id;
	}
	
	public void setLocationId(Integer id) {
		_id = id;
	}
	
	@Column(name="latitude", nullable=false)
	public Double getLatitude() {
		return _lat;
	}
	
	public void setLatitude(Double lat) {
		_lat = lat;
	}
	
	@Column(name="longitude", nullable=false)
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
		double dlat = lat2 - lat1;
		double dlon = lon2 - lon1;
		
		double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon/2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a) );
		
		// R = radius of the earth in meters = 6373000m
		return 6373000 * c;
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
