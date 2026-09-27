package com.majinnaibu.bukkitplugins.metropolis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import com.sk89q.worldedit.math.BlockVector3;

class CuboidTest {
	@Test
	void convertsWorldEditBoundsToCuboidCoordinates() {
		Cuboid cuboid = new Cuboid(BlockVector3.at(1, 2, 3), BlockVector3.at(4, 5, 6));

		assertEquals(1, cuboid.getMinX());
		assertEquals(2, cuboid.getMinY());
		assertEquals(3, cuboid.getMinZ());
		assertEquals(4, cuboid.getMaxX());
		assertEquals(5, cuboid.getMaxY());
		assertEquals(6, cuboid.getMaxZ());
	}

	@Test
	void comparesVectorsByXThenZThenYAndHandlesNulls() {
		assertEquals(-1, Cuboid.compareBlockVectors(null, BlockVector3.ZERO));
		assertEquals(1, Cuboid.compareBlockVectors(BlockVector3.ZERO, null));
		assertEquals(0, Cuboid.compareBlockVectors(null, null));
		assertTrue(Cuboid.compareBlockVectors(BlockVector3.at(1, 0, 0), BlockVector3.at(2, 0, 0)) < 0);
		assertTrue(Cuboid.compareBlockVectors(BlockVector3.at(1, 0, 1), BlockVector3.at(1, 0, 2)) < 0);
		assertTrue(Cuboid.compareBlockVectors(BlockVector3.at(1, 1, 2), BlockVector3.at(1, 2, 2)) < 0);
	}

	@Test
	void treatsTouchingBoundsAsIntersecting() {
		Cuboid first = new Cuboid(0, 0, 0, 10, 10, 10);
		Cuboid touching = new Cuboid(10, 10, 10, 20, 20, 20);
		Cuboid separate = new Cuboid(11, 0, 0, 20, 10, 10);

		assertTrue(first.intersects(touching));
		assertFalse(first.intersects(separate));
	}

	@Test
	void containsPointsOnBoundsButRejectsPointsOutside() {
		Cuboid cuboid = new Cuboid(1, 2, 3, 4, 5, 6);

		assertTrue(cuboid.contains(new Location(null, 1, 2, 3)));
		assertTrue(cuboid.contains(new Location(null, 4, 5, 6)));
		assertFalse(cuboid.contains(new Location(null, 5, 5, 6)));
	}
}