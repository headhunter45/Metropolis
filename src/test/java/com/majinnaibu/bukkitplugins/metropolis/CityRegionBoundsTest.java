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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class CityRegionBoundsTest {
  @Test
  void expandsExistingCityToIncludeElevatedHomesAndSpawnWithoutShrinking() {
    Cuboid existingCity = new Cuboid(-20, 62, -20, 20, 71, 20);
    Cuboid requestedCity = new Cuboid(-40, 62, -40, 40, 71, 40);
    Cuboid elevatedHome = new Cuboid(10, 104, 10, 41, 113, 41);
    Cuboid spawn = new Cuboid(2, 62, 2, 69, 81, 69);

    Cuboid expanded =
        CityRegionBounds.expandToContain(existingCity, requestedCity, List.of(elevatedHome, spawn));

    assertEquals(-40, expanded.minX);
    assertEquals(62, expanded.minY);
    assertEquals(-40, expanded.minZ);
    assertEquals(69, expanded.maxX);
    assertEquals(113, expanded.maxY);
    assertEquals(69, expanded.maxZ);
  }
}
