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

import com.sk89q.worldedit.math.BlockVector3;

public final class PlotGridLayout {
  private final int plotSizeX;
  private final int plotSizeY;
  private final int plotSizeZ;
  private final int streetWidth;
  private final int avenueWidth;
  private final int offsetX;
  private final int offsetY;
  private final int offsetZ;
  private final int roadLevel;
  private final int gridSizeX;
  private final int gridSizeY;
  private final int gridSizeZ;

  public PlotGridLayout(
      int plotSizeX,
      int plotSizeY,
      int plotSizeZ,
      int streetWidth,
      int avenueWidth,
      int offsetX,
      int offsetY,
      int offsetZ,
      int roadLevel) {
    if (plotSizeX < 1 || plotSizeY < 1 || plotSizeZ < 1 || streetWidth < 0 || avenueWidth < 0) {
      throw new IllegalArgumentException(
          "Plot dimensions must be positive and road widths nonnegative");
    }

    this.plotSizeX = plotSizeX;
    this.plotSizeY = plotSizeY;
    this.plotSizeZ = plotSizeZ;
    this.streetWidth = streetWidth;
    this.avenueWidth = avenueWidth;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.offsetZ = offsetZ;
    this.roadLevel = roadLevel;
    gridSizeX = Math.addExact(plotSizeX, avenueWidth);
    gridSizeY = Math.addExact(plotSizeY, Math.max(streetWidth, avenueWidth));
    gridSizeZ = Math.addExact(plotSizeZ, streetWidth);
  }

  public BlockVector3 gridMin(int row, int col, int level) {
    return BlockVector3.at(
        offsetX + col * gridSizeX, levelStartY(level), offsetZ + row * gridSizeZ);
  }

  public BlockVector3 gridMax(int row, int col, int multiplierX, int multiplierZ, int level) {
    return BlockVector3.at(
        offsetX + (col + multiplierX) * gridSizeX - 1,
        levelStartY(level) + plotSizeY - 1,
        offsetZ + (row + multiplierZ) * gridSizeZ - 1);
  }

  public BlockVector3 plotMin(int row, int col, int level) {
    BlockVector3 gridMin = gridMin(row, col, level);
    return BlockVector3.at(
        gridMin.x() + avenueWidth / 2, gridMin.y(), gridMin.z() + streetWidth / 2);
  }

  public BlockVector3 plotMax(int row, int col, int multiplierX, int multiplierZ, int level) {
    BlockVector3 gridMax = gridMax(row, col, multiplierX, multiplierZ, level);
    return BlockVector3.at(
        gridMax.x() - (avenueWidth - avenueWidth / 2),
        gridMax.y(),
        gridMax.z() - (streetWidth - streetWidth / 2));
  }

  public int plotXIndexFromMin(Cuboid plot) {
    return Math.floorDiv(plot.minX - offsetX - avenueWidth / 2, gridSizeX);
  }

  public int plotZIndexFromMin(Cuboid plot) {
    return Math.floorDiv(plot.minZ - offsetZ - streetWidth / 2, gridSizeZ);
  }

  public int levelStartY(int level) {
    return roadLevel + offsetY + level * gridSizeY;
  }

  public int gridSizeX() {
    return gridSizeX;
  }

  public int gridSizeY() {
    return gridSizeY;
  }

  public int gridSizeZ() {
    return gridSizeZ;
  }
}
