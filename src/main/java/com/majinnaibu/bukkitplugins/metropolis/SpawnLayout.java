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

public final class SpawnLayout {
  private SpawnLayout() {}

  public static Cuboid bounds(
      int sizeX,
      int sizeY,
      int sizeZ,
      int plotSizeX,
      int plotSizeY,
      int plotSizeZ,
      int avenueWidth,
      int streetWidth,
      int roadLevel,
      int offsetX,
      int offsetY,
      int offsetZ) {
    if (sizeX < 1 || sizeY < 1 || sizeZ < 1 || avenueWidth < 0 || streetWidth < 0) {
      throw new IllegalArgumentException(
          "Spawn multipliers must be positive and road widths nonnegative");
    }

    int gridSizeX = Math.addExact(plotSizeX, avenueWidth);
    int gridSizeZ = Math.addExact(plotSizeZ, streetWidth);
    int minX = avenueWidth / 2;
    int minZ = streetWidth / 2;
    int maxInsetX = avenueWidth - minX;
    int maxInsetZ = streetWidth - minZ;
    int maxX = Math.subtractExact(Math.multiplyExact(sizeX, gridSizeX) - 1, maxInsetX);
    int baseY = Math.addExact(roadLevel, offsetY);
    int maxY = Math.addExact(baseY, Math.multiplyExact(sizeY, plotSizeY) - 1);
    int maxZ = Math.subtractExact(Math.multiplyExact(sizeZ, gridSizeZ) - 1, maxInsetZ);

    return new Cuboid(offsetX + minX, baseY, offsetZ + minZ, offsetX + maxX, maxY, offsetZ + maxZ);
  }

  public static boolean withinBuildHeight(Cuboid cuboid, int minHeight, int maxHeight) {
    return cuboid.minY >= minHeight && cuboid.maxY < maxHeight;
  }
}
