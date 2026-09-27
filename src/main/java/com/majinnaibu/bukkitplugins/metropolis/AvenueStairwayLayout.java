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
    return stairBlocks(plot, roadWidth, stairWidth, lowerY, rise, Side.EAST);
  }

  public static List<Flight> forUpperRoadSegments(
      Cuboid plot,
      int plotSizeZ,
      int gridSizeZ,
      int firstBlockIndex,
      int everyNBlocks,
      int avenueWidth,
      int stairWidth,
      int lowerY,
      int rise,
      int openingLength) {
    if (plotSizeZ < 1 || gridSizeZ < plotSizeZ || everyNBlocks < 1) {
      return List.of();
    }

    List<Flight> flights = new ArrayList<>();
    int segmentIndex = 0;
    for (int segmentStart = plot.minZ;
        segmentStart <= plot.maxZ;
        segmentStart += gridSizeZ, segmentIndex++) {
      int avenueBlockIndex = firstBlockIndex + segmentIndex;
      if (Math.floorMod(avenueBlockIndex + 1, everyNBlocks) != 0) {
        continue;
      }

      Cuboid segment =
          new Cuboid(
              plot.minX,
              plot.minY,
              segmentStart,
              plot.maxX,
              plot.maxY,
              Math.min(segmentStart + plotSizeZ - 1, plot.maxZ));
      for (Side side : List.of(Side.EAST, Side.WEST)) {
        List<Step> stairs = stairBlocks(segment, avenueWidth, stairWidth, lowerY, rise, side);
        if (stairs.isEmpty()) {
          continue;
        }
        List<Step> backings = invertedBackings(stairs, lowerY);
        List<Step> openings =
            upperRoadOpening(stairs, avenueWidth, stairWidth, lowerY, rise, openingLength, side);
        flights.add(new Flight(side, stairs, backings, openings));
      }
    }
    return List.copyOf(flights);
  }

  public static List<Flight> forUpperStreetSegments(
      Cuboid plot,
      int plotSizeX,
      int gridSizeX,
      int firstBlockIndex,
      int everyNBlocks,
      int streetWidth,
      int stairWidth,
      int lowerY,
      int rise,
      int openingLength) {
    if (plotSizeX < 1 || gridSizeX < plotSizeX || everyNBlocks < 1) {
      return List.of();
    }

    List<Flight> flights = new ArrayList<>();
    int segmentIndex = 0;
    for (int segmentStart = plot.minX;
        segmentStart <= plot.maxX;
        segmentStart += gridSizeX, segmentIndex++) {
      if (Math.floorMod(firstBlockIndex + segmentIndex + 1, everyNBlocks) != 0) {
        continue;
      }

      Cuboid segment =
          new Cuboid(
              segmentStart,
              plot.minY,
              plot.minZ,
              Math.min(segmentStart + plotSizeX - 1, plot.maxX),
              plot.maxY,
              plot.maxZ);
      for (Side side : List.of(Side.NORTH, Side.SOUTH)) {
        List<Step> stairs = stairBlocks(segment, streetWidth, stairWidth, lowerY, rise, side);
        if (stairs.isEmpty()) {
          continue;
        }
        flights.add(
            new Flight(
                side,
                stairs,
                invertedBackings(stairs, lowerY),
                upperRoadOpening(
                    stairs, streetWidth, stairWidth, lowerY, rise, openingLength, side)));
      }
    }
    return List.copyOf(flights);
  }

  public static List<Step> upperRoadOpening(
      Cuboid plot, int roadWidth, int stairWidth, int lowerY, int rise, int openingLength) {
    List<Step> stairs = centeredEastAvenue(plot, roadWidth, stairWidth, lowerY, rise);
    return upperRoadOpening(stairs, roadWidth, stairWidth, lowerY, rise, openingLength, Side.EAST);
  }

  private static List<Step> upperRoadOpening(
      List<Step> stairs,
      int roadWidth,
      int stairWidth,
      int lowerY,
      int rise,
      int openingLength,
      Side side) {
    if (stairs.isEmpty() || openingLength < 1) {
      return List.of();
    }

    Step topStair = stairs.get(stairs.size() - 1);
    Step firstStair = stairs.getFirst();
    int upperRoadY = lowerY + rise;
    List<Step> opening = new ArrayList<>(stairWidth * openingLength);
    for (int widthIndex = 0; widthIndex < stairWidth; widthIndex++) {
      for (int lengthIndex = openingLength; lengthIndex > 0; lengthIndex--) {
        opening.add(
            side.runsAlongX()
                ? new Step(topStair.x() - lengthIndex, upperRoadY, firstStair.z() + widthIndex)
                : new Step(firstStair.x() + widthIndex, upperRoadY, topStair.z() - lengthIndex));
      }
    }
    return List.copyOf(opening);
  }

  public static Flight forUpperRoad(
      Cuboid plot, int roadWidth, int stairWidth, int lowerY, int rise, int openingLength) {
    List<Step> stairs = centeredEastAvenue(plot, roadWidth, stairWidth, lowerY, rise);
    if (stairs.isEmpty()) {
      return new Flight(Side.EAST, List.of(), List.of(), List.of());
    }

    return new Flight(
        Side.EAST,
        stairs,
        invertedBackings(stairs, lowerY),
        upperRoadOpening(stairs, roadWidth, stairWidth, lowerY, rise, openingLength, Side.EAST));
  }

  private static List<Step> invertedBackings(List<Step> stairs, int lowerY) {
    List<Step> invertedBackings = new ArrayList<>();
    for (Step stair : stairs) {
      if (stair.y() > lowerY + 1) {
        invertedBackings.add(new Step(stair.x(), stair.y() - 1, stair.z()));
      }
    }
    return List.copyOf(invertedBackings);
  }

  private static List<Step> stairBlocks(
      Cuboid plot, int roadWidth, int stairWidth, int lowerY, int rise, Side side) {
    int plotLength = side.runsAlongX() ? plot.maxX - plot.minX + 1 : plot.maxZ - plot.minZ + 1;
    if (roadWidth < 1
        || stairWidth < 1
        || stairWidth >= roadWidth
        || rise < 1
        || rise + 2 > plotLength) {
      return List.of();
    }

    int firstRun = (side.runsAlongX() ? plot.minX : plot.minZ) + (plotLength - rise) / 2 + 1;
    int firstCross =
        switch (side) {
          case EAST -> plot.maxX + 1 + (roadWidth - stairWidth) / 2;
          case WEST -> plot.minX - roadWidth + (roadWidth - stairWidth) / 2;
          case NORTH -> plot.minZ - roadWidth + (roadWidth - stairWidth) / 2;
          case SOUTH -> plot.maxZ + 1 + (roadWidth - stairWidth) / 2;
        };
    List<Step> steps = new ArrayList<>(stairWidth * rise);
    for (int riseIndex = 0; riseIndex < rise; riseIndex++) {
      for (int widthIndex = 0; widthIndex < stairWidth; widthIndex++) {
        steps.add(
            side.runsAlongX()
                ? new Step(firstRun + riseIndex, lowerY + riseIndex + 1, firstCross + widthIndex)
                : new Step(firstCross + widthIndex, lowerY + riseIndex + 1, firstRun + riseIndex));
      }
    }
    return List.copyOf(steps);
  }

  public enum Side {
    EAST,
    WEST,
    NORTH,
    SOUTH;

    public boolean runsAlongX() {
      return this == NORTH || this == SOUTH;
    }
  }

  public record Step(int x, int y, int z) {}

  public record Flight(
      Side side,
      List<Step> stairBlocks,
      List<Step> invertedBackingBlocks,
      List<Step> roadOpenings) {
    public Flight {
      stairBlocks = List.copyOf(stairBlocks);
      invertedBackingBlocks = List.copyOf(invertedBackingBlocks);
      roadOpenings = List.copyOf(roadOpenings);
    }
  }
}
