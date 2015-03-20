package com.hunnymustard.ssbl.model;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Locatable> {

	private Location _origin;
	
	public DistanceComparator(Location origin) {
		_origin = origin;
	}
	
	@Override
	public int compare(Locatable o1, Locatable o2) {
		return (int)(_origin.distance(o1.getLocation()) - _origin.distance(o2.getLocation()));
	}
}
