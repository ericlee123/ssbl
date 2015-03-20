package com.hunnymustard.ssbl.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class LocationTest {

	@Test
	public void testDistance() {
		Location l1 = new Location(1, 38.898556, -77.037852);	// 1600 Pennsylvania Ave NW, Washington, DC
		Location l2 = new Location(2, 38.897147, -77.043934);	// 1922 F St NW, Washington, DC
		
		assertEquals(0.340, l1.distance(l2), 0.001);
		assertEquals(0.340, l2.distance(l1), 0.001);
	}
}
