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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class HomeNumberAllocator {
  private HomeNumberAllocator() {}

  public static int nextHomeNumber(List<Plot> plots) {
    Set<Integer> allocatedNumbers = new HashSet<>();
    for (Plot plot : plots) {
      if (plot instanceof PlayerHome home) {
        allocatedNumbers.add(home.getNumber());
      }
    }

    int nextHomeNumber = 1;
    while (allocatedNumbers.contains(nextHomeNumber)) {
      nextHomeNumber++;
    }
    return nextHomeNumber;
  }
}
