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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

class PlotLevelSupportTest {
  @Test
  void rejectsUpperPlotWhenAnyLogicalCellBelowIsMissing() {
    Set<String> generatedCells = Set.of("0:0:0", "0:1:0", "1:0:0");

    assertFalse(
        PlotLevelSupport.hasCompleteSupport(
            0, 0, 2, 1, (row, col, level) -> generatedCells.contains(key(row, col, level))));
  }

  @Test
  void requiresEveryLogicalCellAtEveryLowerLevel() {
    Set<String> generatedCells =
        Set.of("0:0:0", "0:1:0", "1:0:0", "1:1:0", "0:0:1", "0:1:1", "1:0:1", "1:1:1");

    assertTrue(
        PlotLevelSupport.hasCompleteSupport(
            0, 0, 2, 2, (row, col, level) -> generatedCells.contains(key(row, col, level))));
  }

  @Test
  void baseLevelDoesNotRequireLowerSupport() {
    assertTrue(PlotLevelSupport.hasCompleteSupport(3, 4, 2, 0, (row, col, level) -> false));
  }

  private static String key(int row, int col, int level) {
    return row + ":" + col + ":" + level;
  }
}
