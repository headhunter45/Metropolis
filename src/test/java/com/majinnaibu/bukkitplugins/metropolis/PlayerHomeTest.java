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

import java.util.UUID;

import com.sk89q.worldedit.math.BlockVector3;

import org.junit.jupiter.api.Test;

class PlayerHomeTest {
  @Test
  void buildsUuidBasedRegionIdentityAndPreservesDisplayName() {
    UUID playerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    PlayerHome home =
        new PlayerHome(
            playerId, "PlayerName", 2, BlockVector3.at(1, 2, 3), BlockVector3.at(4, 5, 6));

    assertEquals(playerId, home.getPlayerId());
    assertEquals("PlayerName", home.getPlayerName());
    assertEquals(2, home.getNumber());
    assertEquals("h_2_" + playerId, home.getRegionName());
  }
}
