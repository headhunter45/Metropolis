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
      int roadWidth,
      int roadLevel) {
    if (sizeX < 1 || sizeY < 1 || sizeZ < 1 || roadWidth < 0) {
      throw new IllegalArgumentException(
          "Spawn multipliers must be positive and road width nonnegative");
    }

    int gridSizeX = Math.addExact(plotSizeX, roadWidth);
    int gridSizeZ = Math.addExact(plotSizeZ, roadWidth);
    int inset = roadWidth / 2;
    int maxInset = roadWidth - inset;
    int maxX = Math.subtractExact(Math.multiplyExact(sizeX, gridSizeX) - 1, maxInset);
    int maxY = Math.addExact(roadLevel, Math.multiplyExact(sizeY, plotSizeY) - 1);
    int maxZ = Math.subtractExact(Math.multiplyExact(sizeZ, gridSizeZ) - 1, maxInset);

    return new Cuboid(inset, roadLevel, inset, maxX, maxY, maxZ);
  }

  public static boolean withinBuildHeight(Cuboid cuboid, int minHeight, int maxHeight) {
    return cuboid.minY >= minHeight && cuboid.maxY < maxHeight;
  }
}
