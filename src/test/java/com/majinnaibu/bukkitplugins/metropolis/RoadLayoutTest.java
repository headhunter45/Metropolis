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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

class RoadLayoutTest {
  private static final Cuboid PLOT = new Cuboid(2, 62, 2, 33, 71, 33);

  @Test
  void buildsFullRoadWidthAndNonOverlappingCornersAtOnce() {
    List<RoadLayout.BlockPosition> road = RoadLayout.surroundingRoad(PLOT, 4);
    HashSet<RoadLayout.BlockPosition> uniqueBlocks = new HashSet<>(road);

    assertEquals(576, road.size());
    assertEquals(road.size(), uniqueBlocks.size());
    assertTrue(road.contains(new RoadLayout.BlockPosition(34, 2)));
    assertTrue(road.contains(new RoadLayout.BlockPosition(37, 33)));
    assertTrue(road.contains(new RoadLayout.BlockPosition(2, 34)));
    assertTrue(road.contains(new RoadLayout.BlockPosition(33, 37)));
  }

  @Test
  void returnsNoRoadForZeroWidth() {
    assertTrue(RoadLayout.surroundingRoad(PLOT, 0).isEmpty());
  }
}
