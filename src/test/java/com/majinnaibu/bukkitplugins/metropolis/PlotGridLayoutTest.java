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

import com.sk89q.worldedit.math.BlockVector3;

import org.junit.jupiter.api.Test;

class PlotGridLayoutTest {
  @Test
  void zeroOffsetsPreserveTheExistingPlotGrid() {
    PlotGridLayout grid = new PlotGridLayout(32, 10, 32, 4, 4, 0, 0, 0, 62);

    assertEquals(BlockVector3.at(0, 62, 0), grid.gridMin(0, 0, 0));
    assertEquals(BlockVector3.at(35, 71, 35), grid.gridMax(0, 0, 1, 1, 0));
    assertEquals(BlockVector3.at(2, 62, 2), grid.plotMin(0, 0, 0));
    assertEquals(BlockVector3.at(33, 71, 33), grid.plotMax(0, 0, 1, 1, 0));
  }

  @Test
  void offsetsShiftGridAndPlotsOnAllAxesIncludingHigherLevels() {
    PlotGridLayout grid = new PlotGridLayout(32, 10, 32, 2, 6, -7, 3, 11, 62);

    assertEquals(BlockVector3.at(-45, 97, -23), grid.gridMin(-1, -1, 2));
    assertEquals(BlockVector3.at(30, 106, 44), grid.gridMax(-1, -1, 2, 2, 2));
    assertEquals(BlockVector3.at(-42, 97, -22), grid.plotMin(-1, -1, 2));
    assertEquals(BlockVector3.at(27, 106, 43), grid.plotMax(-1, -1, 2, 2, 2));
  }

  @Test
  void mapsNegativePlotBoundsBackToTheirLogicalIndices() {
    PlotGridLayout grid = new PlotGridLayout(32, 10, 32, 2, 6, -7, 3, 11, 62);
    Cuboid plot = new Cuboid(grid.plotMin(-1, -1, 0), grid.plotMax(-1, -1, 2, 2, 0));

    assertEquals(-1, grid.plotXIndexFromMin(plot));
    assertEquals(-1, grid.plotZIndexFromMin(plot));
  }
}
