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

import org.junit.jupiter.api.Test;

class PlotWorldBorderBoundsTest {
  @Test
  void searchBoundFollowsTheWorldBorderInsteadOfAnArbitraryRingCount() {
    int maximumRing = PlotWorldBorderBounds.maximumSearchRing(60_000_000, 0, 0, 0, 0, 36, 36, 1);

    assertTrue(maximumRing > 256);
  }

  @Test
  void accountsForAnOffsetBorderCenterAndMultiplier() {
    int maximumRing = PlotWorldBorderBounds.maximumSearchRing(512, 100, -100, 64, -32, 36, 34, 2);

    assertTrue(maximumRing > 10);
  }

  @Test
  void requiresTheCompleteGridFootprintInsideTheBorder() {
    assertTrue(PlotWorldBorderBounds.contains(new Cuboid(-9, 0, -9, 9, 10, 9), 20, 0, 0));
    assertFalse(PlotWorldBorderBounds.contains(new Cuboid(-11, 0, -9, 9, 10, 9), 20, 0, 0));
  }
}
