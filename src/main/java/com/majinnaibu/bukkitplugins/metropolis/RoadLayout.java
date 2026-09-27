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
import java.util.List;

public final class RoadLayout {
  private RoadLayout() {}

  public static List<BlockPosition> surroundingRoad(Cuboid plot, int streetWidth, int avenueWidth) {
    if (streetWidth < 0 || avenueWidth < 0 || streetWidth + avenueWidth == 0) {
      return List.of();
    }

    List<BlockPosition> blocks = new ArrayList<>();
    addRectangle(
        blocks, plot.minX, plot.maxX, plot.minZ - streetWidth, plot.minZ - 1, RoadType.STREET);
    addRectangle(
        blocks, plot.minX, plot.maxX, plot.maxZ + 1, plot.maxZ + streetWidth, RoadType.STREET);
    addRectangle(
        blocks, plot.minX - avenueWidth, plot.minX - 1, plot.minZ, plot.maxZ, RoadType.AVENUE);
    addRectangle(
        blocks, plot.maxX + 1, plot.maxX + avenueWidth, plot.minZ, plot.maxZ, RoadType.AVENUE);
    if (streetWidth > 0 && avenueWidth > 0) {
      addRectangle(
          blocks,
          plot.minX - avenueWidth,
          plot.minX - 1,
          plot.minZ - streetWidth,
          plot.minZ - 1,
          RoadType.INTERSECTION);
      addRectangle(
          blocks,
          plot.maxX + 1,
          plot.maxX + avenueWidth,
          plot.minZ - streetWidth,
          plot.minZ - 1,
          RoadType.INTERSECTION);
      addRectangle(
          blocks,
          plot.minX - avenueWidth,
          plot.minX - 1,
          plot.maxZ + 1,
          plot.maxZ + streetWidth,
          RoadType.INTERSECTION);
      addRectangle(
          blocks,
          plot.maxX + 1,
          plot.maxX + avenueWidth,
          plot.maxZ + 1,
          plot.maxZ + streetWidth,
          RoadType.INTERSECTION);
    }
    return List.copyOf(blocks);
  }

  private static void addRectangle(
      List<BlockPosition> blocks, int minX, int maxX, int minZ, int maxZ, RoadType roadType) {
    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        blocks.add(new BlockPosition(x, z, roadType));
      }
    }
  }

  public enum RoadType {
    STREET,
    AVENUE,
    INTERSECTION
  }

  public record BlockPosition(int x, int z, RoadType roadType) {}
}
