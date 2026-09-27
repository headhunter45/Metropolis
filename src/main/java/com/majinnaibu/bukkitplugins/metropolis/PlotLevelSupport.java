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

public final class PlotLevelSupport {
  private PlotLevelSupport() {}

  public static boolean hasCompleteSupport(
      int row, int col, int multiplier, int targetLevel, GeneratedCellLookup generatedCellLookup) {
    if (multiplier < 1 || targetLevel < 0) {
      return false;
    }

    for (int supportLevel = 0; supportLevel < targetLevel; supportLevel++) {
      for (int rowOffset = 0; rowOffset < multiplier; rowOffset++) {
        for (int colOffset = 0; colOffset < multiplier; colOffset++) {
          if (!generatedCellLookup.isGenerated(row + rowOffset, col + colOffset, supportLevel)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  @FunctionalInterface
  public interface GeneratedCellLookup {
    boolean isGenerated(int row, int col, int level);
  }
}
