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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.junit.jupiter.api.Test;

class WorldGuardRegionOccupancyTest {
  @Test
  void treatsArbitrarySavedRegionBoundsAsOccupiedButIgnoresCityAndGlobalRegions() {
    ProtectedRegion market =
        region("market", BlockVector3.at(40, 62, 40), BlockVector3.at(70, 90, 70));
    ProtectedRegion city =
        region("City", BlockVector3.at(-100, 0, -100), BlockVector3.at(100, 320, 100));
    ProtectedRegion global =
        region(
            "__global__",
            BlockVector3.at(-30_000_000, -64, -30_000_000),
            BlockVector3.at(30_000_000, 320, 30_000_000));

    List<Cuboid> occupied = WorldGuardRegionOccupancy.boundsToAvoid(List.of(market, city, global));

    assertEquals(1, occupied.size());
    assertTrue(WorldGuardRegionOccupancy.overlapsAny(new Cuboid(60, 70, 60, 80, 75, 80), occupied));
    assertFalse(WorldGuardRegionOccupancy.overlapsAny(new Cuboid(0, 62, 0, 31, 71, 31), occupied));
  }

  private static ProtectedRegion region(String id, BlockVector3 minimum, BlockVector3 maximum) {
    ProtectedRegion region = mock(ProtectedRegion.class);
    when(region.getId()).thenReturn(id);
    when(region.getMinimumPoint()).thenReturn(minimum);
    when(region.getMaximumPoint()).thenReturn(maximum);
    return region;
  }
}
