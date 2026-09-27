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

package com.majinnaibu.bukkitplugins.metropolis.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.PlayerHome;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class MetropolisHomeAcquireTest {
  @Test
  void assignsAPlotWhenThePlayerIsBelowTheirLimit() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    Player player = mock(Player.class);
    UUID playerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    when(player.getUniqueId()).thenReturn(playerId);
    when(plugin.getNumPlots(playerId)).thenReturn(0);
    when(plugin.getMaxPlots(player)).thenReturn(1);
    when(plugin.acquireHome(player)).thenReturn(mock(PlayerHome.class));
    MetropolisHomeAcquire executor = new MetropolisHomeAcquire(plugin);

    assertTrue(executor.onCommand(player, mock(Command.class), "acquire", new String[0]));

    verify(plugin).acquireHome(player);
  }

  @Test
  void doesNotAssignAPlotAfterThePlayerReachesTheirLimit() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    Player player = mock(Player.class);
    UUID playerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    when(player.getUniqueId()).thenReturn(playerId);
    when(plugin.getNumPlots(playerId)).thenReturn(1);
    when(plugin.getMaxPlots(player)).thenReturn(1);
    MetropolisHomeAcquire executor = new MetropolisHomeAcquire(plugin);

    assertFalse(executor.onCommand(player, mock(Command.class), "acquire", new String[0]));

    verify(plugin, never()).acquireHome(player);
  }

  @Test
  void supportsDisablingAutomaticInitialHomeAllocation() {
    assertEquals(1, MetropolisPlugin.normalizeInitialHomeCount(1));
    assertTrue(MetropolisPlugin.shouldAutoGenerateInitialHome(1));

    int zero = MetropolisPlugin.normalizeInitialHomeCount(0);
    assertEquals(0, zero);
    assertFalse(MetropolisPlugin.shouldAutoGenerateInitialHome(zero));
  }

  @Test
  void keepsLevelCountsWithinTheConfiguredAndWorldBounds() {
    assertEquals(1, MetropolisPlugin.normalizeMaxLevels(0));
    assertEquals(4, MetropolisPlugin.normalizeMaxLevels(4));
    assertEquals(3, MetropolisPlugin.clampMaxLevels(10, 3));
    assertEquals(1, MetropolisPlugin.clampMaxLevels(2, 1));
    assertEquals(1, MetropolisPlugin.clampMaxLevels(0, 0));
  }
}
