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

import java.util.List;
import java.util.UUID;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

import org.junit.jupiter.api.Test;

class PlotReservationLookupTest {
  private static final UUID PLAYER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @Test
  void findsReservationsWherePlayerIsOwnerOrMemberButExcludesHomeCityAndSpawn() {
    ProtectedCuboidRegion memberReservation = region("r_member");
    memberReservation.getMembers().addPlayer(PLAYER_ID);
    ProtectedCuboidRegion ownerReservation = region("private-yard");
    ownerReservation.getOwners().addPlayer(PLAYER_ID);
    ProtectedCuboidRegion home = region("h_1_" + PLAYER_ID);
    home.getMembers().addPlayer(PLAYER_ID);
    ProtectedCuboidRegion city = region("City");
    city.getMembers().addPlayer(PLAYER_ID);
    ProtectedCuboidRegion spawn = region("Spawn");
    spawn.getMembers().addPlayer(PLAYER_ID);
    ProtectedCuboidRegion unrelated = region("other-region", false);
    unrelated.getMembers().addPlayer(PLAYER_ID);

    List<Plot> reservations =
        PlotReservationLookup.forPlayer(
            List.of(memberReservation, ownerReservation, home, city, spawn, unrelated), PLAYER_ID);

    assertEquals(
        List.of("private-yard", "r_member"),
        reservations.stream().map(Plot::getRegionName).toList());
  }

  @Test
  void findsNamedReservationWithOrWithoutPrefix() {
    ProtectedCuboidRegion reservation = region("r_camp");
    reservation.getMembers().addPlayer(PLAYER_ID);
    List<Plot> reservations = PlotReservationLookup.forPlayer(List.of(reservation), PLAYER_ID);

    assertEquals("r_camp", PlotReservationLookup.findByName(reservations, "camp").getRegionName());
    assertEquals(
        "r_camp", PlotReservationLookup.findByName(reservations, "r_camp").getRegionName());
  }

  private static ProtectedCuboidRegion region(String id) {
    return region(id, true);
  }

  private static ProtectedCuboidRegion region(String id, boolean reservedFlags) {
    ProtectedCuboidRegion region =
        new ProtectedCuboidRegion(id, BlockVector3.ZERO, BlockVector3.at(5, 5, 5));
    if (reservedFlags) {
      region.setFlag(Flags.LAVA_FLOW, StateFlag.State.DENY);
      region.setFlag(Flags.SNOW_FALL, StateFlag.State.DENY);
    }
    return region;
  }
}
