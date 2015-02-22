package com.hunnymustard.ssbl.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

public class Location {

	private Integer _id;
	private Double _lat, _lon;
	
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
