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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class PlayerLookupTest {
  @Test
  void matchesOnlinePlayerNamesCaseInsensitivelyButNotByPrefix() {
    Player player = mock(Player.class);
    when(player.getName()).thenReturn("PlayerName");

    assertSame(player, PlayerLookup.findOnline("playername", List.of(player)));
    assertNull(PlayerLookup.findOnline("Player", List.of(player)));
  }
}
