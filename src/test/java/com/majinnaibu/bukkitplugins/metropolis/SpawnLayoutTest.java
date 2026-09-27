/*
This file is part of Metropolis.

Metropolis is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Metropolis is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Metropolis. If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>.
*/

package com.majinnaibu.bukkitplugins.metropolis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SpawnLayoutTest {
  @Test
  void defaultMultipliersPreserveTheOnePlotSpawnBounds() {
    Cuboid spawn = SpawnLayout.bounds(1, 1, 1, 32, 10, 32, 4, 4, 62);

    assertEqualsBounds(spawn, 2, 62, 2, 33, 71, 33);
  }

  @Test
  void appliesEachMultiplierToItsOwnAlignedGridDimension() {
    Cuboid spawn = SpawnLayout.bounds(2, 3, 4, 32, 10, 32, 4, 4, 62);

    assertEqualsBounds(spawn, 2, 62, 2, 69, 91, 141);
  }

  @Test
  void validatesTheEntireSpawnHeightAgainstWorldBounds() {
    Cuboid spawn = SpawnLayout.bounds(1, 3, 1, 32, 10, 32, 4, 4, 62);

    assertTrue(SpawnLayout.withinBuildHeight(spawn, -64, 320));
    assertFalse(SpawnLayout.withinBuildHeight(spawn, -64, 91));
  }

  @Test
  void alignsSpawnAxesToTheirIndependentRoadWidths() {
    Cuboid spawn = SpawnLayout.bounds(2, 1, 3, 32, 10, 32, 6, 2, 62);

    assertEqualsBounds(spawn, 3, 62, 1, 72, 71, 100);
  }

  @Test
  void rejectsNonPositiveSpawnMultipliers() {
    assertThrows(
        IllegalArgumentException.class, () -> SpawnLayout.bounds(0, 1, 1, 32, 10, 32, 4, 4, 62));
  }

  private static void assertEqualsBounds(
      Cuboid cuboid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    org.junit.jupiter.api.Assertions.assertEquals(minX, cuboid.minX);
    org.junit.jupiter.api.Assertions.assertEquals(minY, cuboid.minY);
    org.junit.jupiter.api.Assertions.assertEquals(minZ, cuboid.minZ);
    org.junit.jupiter.api.Assertions.assertEquals(maxX, cuboid.maxX);
    org.junit.jupiter.api.Assertions.assertEquals(maxY, cuboid.maxY);
    org.junit.jupiter.api.Assertions.assertEquals(maxZ, cuboid.maxZ);
  }
}
