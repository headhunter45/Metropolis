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

public final class AvenueStairwayLayout {
  private AvenueStairwayLayout() {}

  public static List<Step> centeredEastAvenue(
      Cuboid plot, int roadWidth, int stairWidth, int lowerY, int rise) {
    int plotLength = plot.maxZ - plot.minZ + 1;
    if (roadWidth < 1
        || stairWidth < 1
        || stairWidth >= roadWidth
        || rise < 1
        || rise + 2 > plotLength) {
      return List.of();
    }

    int firstX = plot.maxX + 1 + (roadWidth - stairWidth) / 2;
    int firstZ = plot.minZ + (plotLength - rise) / 2 + 1;
    List<Step> steps = new ArrayList<>(stairWidth * rise);
    for (int riseIndex = 0; riseIndex < rise; riseIndex++) {
      for (int widthIndex = 0; widthIndex < stairWidth; widthIndex++) {
        steps.add(new Step(firstX + widthIndex, lowerY + riseIndex + 1, firstZ + riseIndex));
      }
    }
    return List.copyOf(steps);
  }

  public static List<Step> upperRoadOpening(
      Cuboid plot, int roadWidth, int stairWidth, int lowerY, int rise, int openingLength) {
    List<Step> stairs = centeredEastAvenue(plot, roadWidth, stairWidth, lowerY, rise);
    if (stairs.isEmpty() || openingLength < 1) {
      return List.of();
    }

    Step topStair = stairs.get(stairs.size() - 1);
    int firstX = stairs.getFirst().x();
    int upperRoadY = lowerY + rise;
    List<Step> opening = new ArrayList<>(stairWidth * openingLength);
    for (int widthIndex = 0; widthIndex < stairWidth; widthIndex++) {
      for (int lengthIndex = openingLength; lengthIndex > 0; lengthIndex--) {
        opening.add(new Step(firstX + widthIndex, upperRoadY, topStair.z() - lengthIndex));
      }
    }
    return List.copyOf(opening);
  }

  public static Flight forUpperRoad(
      Cuboid plot, int roadWidth, int stairWidth, int lowerY, int rise, int openingLength) {
    List<Step> stairs = centeredEastAvenue(plot, roadWidth, stairWidth, lowerY, rise);
    if (stairs.isEmpty()) {
      return new Flight(List.of(), List.of(), List.of());
    }

    List<Step> invertedBackings = new ArrayList<>();
    for (Step stair : stairs) {
      if (stair.y() > lowerY + 1) {
        invertedBackings.add(new Step(stair.x(), stair.y() - 1, stair.z()));
      }
    }

    return new Flight(
        stairs,
        invertedBackings,
        upperRoadOpening(plot, roadWidth, stairWidth, lowerY, rise, openingLength));
  }

  public record Step(int x, int y, int z) {}

  public record Flight(
      List<Step> stairBlocks, List<Step> invertedBackingBlocks, List<Step> roadOpenings) {
    public Flight {
      stairBlocks = List.copyOf(stairBlocks);
      invertedBackingBlocks = List.copyOf(invertedBackingBlocks);
      roadOpenings = List.copyOf(roadOpenings);
    }
  }
}
