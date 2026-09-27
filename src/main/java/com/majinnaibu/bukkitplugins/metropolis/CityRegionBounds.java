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

public final class CityRegionBounds {
  private CityRegionBounds() {}

  public static Cuboid expandToContain(
      Cuboid existing, Cuboid requested, Iterable<Cuboid> occupiedRegions) {
    int minX = Math.min(existing.minX, requested.minX);
    int minY = Math.min(existing.minY, requested.minY);
    int minZ = Math.min(existing.minZ, requested.minZ);
    int maxX = Math.max(existing.maxX, requested.maxX);
    int maxY = Math.max(existing.maxY, requested.maxY);
    int maxZ = Math.max(existing.maxZ, requested.maxZ);

    for (Cuboid occupied : occupiedRegions) {
      minX = Math.min(minX, occupied.minX);
      minY = Math.min(minY, occupied.minY);
      minZ = Math.min(minZ, occupied.minZ);
      maxX = Math.max(maxX, occupied.maxX);
      maxY = Math.max(maxY, occupied.maxY);
      maxZ = Math.max(maxZ, occupied.maxZ);
    }

    return new Cuboid(minX, minY, minZ, maxX, maxY, maxZ);
  }
}
