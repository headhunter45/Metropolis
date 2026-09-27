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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class PlotReservationLookup {
  private PlotReservationLookup() {}

  public static List<Plot> forPlayer(Iterable<ProtectedRegion> regions, UUID playerId) {
    List<Plot> reservations = new ArrayList<>();
    for (ProtectedRegion region : regions) {
      if (!(region instanceof ProtectedCuboidRegion) || !isReservation(region)) {
        continue;
      }
      if (region.getOwners().getUniqueIds().contains(playerId)
          || region.getMembers().getUniqueIds().contains(playerId)) {
        reservations.add(Plot.get(region));
      }
    }
    reservations.sort(Comparator.comparing(Plot::getRegionName));
    return List.copyOf(reservations);
  }

  public static Plot findByName(List<Plot> reservations, String requestedName) {
    String prefixedName = requestedName.startsWith("r_") ? requestedName : "r_" + requestedName;
    for (Plot reservation : reservations) {
      if (reservation.getRegionName().equals(requestedName)
          || reservation.getRegionName().equals(prefixedName)) {
        return reservation;
      }
    }
    return null;
  }

  private static boolean isReservation(ProtectedRegion region) {
    String regionId = region.getId();
    if (regionId.startsWith("r_")) {
      return true;
    }
    if (regionId.startsWith("h_")
        || regionId.equalsIgnoreCase("City")
        || regionId.equalsIgnoreCase("Spawn")) {
      return false;
    }
    return region.getFlag(Flags.LAVA_FLOW) == StateFlag.State.DENY
        && region.getFlag(Flags.SNOW_FALL) == StateFlag.State.DENY;
  }
}
