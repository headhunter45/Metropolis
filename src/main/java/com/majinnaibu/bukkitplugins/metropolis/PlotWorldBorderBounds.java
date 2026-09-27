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

public final class PlotWorldBorderBounds {
  private PlotWorldBorderBounds() {}

  public static int maximumSearchRing(
      double borderSize,
      double borderCenterX,
      double borderCenterZ,
      int offsetX,
      int offsetZ,
      int gridSizeX,
      int gridSizeZ,
      int plotMultiplier) {
    if (borderSize <= 0 || gridSizeX <= 0 || gridSizeZ <= 0 || plotMultiplier < 1) {
      return 0;
    }

    double halfBorder = borderSize / 2;
    double farthestX = halfBorder + Math.abs(borderCenterX - offsetX);
    double farthestZ = halfBorder + Math.abs(borderCenterZ - offsetZ);
    double cellRadius = Math.max(farthestX / gridSizeX, farthestZ / gridSizeZ);
    long bound = (long) Math.ceil(cellRadius) + plotMultiplier + 1L;
    return (int) Math.min(bound, Integer.MAX_VALUE);
  }

  public static boolean contains(Cuboid bounds, double borderSize, double centerX, double centerZ) {
    if (borderSize <= 0) {
      return false;
    }

    double halfBorder = borderSize / 2;
    double borderMinX = centerX - halfBorder;
    double borderMaxX = centerX + halfBorder;
    double borderMinZ = centerZ - halfBorder;
    double borderMaxZ = centerZ + halfBorder;
    return bounds.minX + 0.5 >= borderMinX
        && bounds.maxX + 0.5 <= borderMaxX
        && bounds.minZ + 0.5 >= borderMinZ
        && bounds.maxZ + 0.5 <= borderMaxZ;
  }
}
