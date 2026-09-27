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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class WorldGuardRegionOccupancy {
  private WorldGuardRegionOccupancy() {}

  public static List<Cuboid> boundsToAvoid(Collection<ProtectedRegion> regions) {
    List<Cuboid> bounds = new ArrayList<>();
    for (ProtectedRegion region : regions) {
      String id = region.getId();
      if (id.equalsIgnoreCase("city") || id.equalsIgnoreCase("__global__")) {
        continue;
      }
      bounds.add(new Cuboid(region.getMinimumPoint(), region.getMaximumPoint()));
    }
    return List.copyOf(bounds);
  }

  public static boolean overlapsAny(Cuboid candidate, Collection<Cuboid> occupiedBounds) {
    return occupiedBounds.stream().anyMatch(candidate::intersects);
  }
}
