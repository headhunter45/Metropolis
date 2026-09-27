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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class MetropolisHomeMoveCommandTest {
  private static final UUID PLAYER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @Test
  void selectsTheSendersExistingHome() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    Player sender = mock(Player.class);
    when(sender.getUniqueId()).thenReturn(PLAYER_ID);
    when(sender.getName()).thenReturn("PlayerName");
    when(plugin.homeExists(PLAYER_ID, 2)).thenReturn(true);
    MetropolisHomeMoveCommand command = new MetropolisHomeMoveCommand(plugin);

    assertTrue(command.onCommand(sender, mock(Command.class), "move", new String[] {"2"}));

    verify(plugin).setHome(PLAYER_ID, 2);
    verify(sender).sendMessage(contains("Home 2 is now active for PlayerName"));
  }

  @Test
  void selectsAnOfflineTargetsExistingHome() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    CommandSender sender = mock(CommandSender.class);
    OfflinePlayer target = mock(OfflinePlayer.class);
    when(plugin.getOfflinePlayer("OfflineName")).thenReturn(target);
    when(target.getUniqueId()).thenReturn(PLAYER_ID);
    when(target.getName()).thenReturn("OfflineName");
    when(plugin.homeExists(PLAYER_ID, 3)).thenReturn(true);
    MetropolisHomeMoveCommand command = new MetropolisHomeMoveCommand(plugin);

    assertTrue(
        command.onCommand(sender, mock(Command.class), "move", new String[] {"3", "OfflineName"}));

    verify(plugin).setHome(PLAYER_ID, 3);
    verify(sender).sendMessage(contains("Home 3 is now active for OfflineName"));
  }

  @Test
  void rejectsInvalidNumbersMissingPlayersAndNonexistentHomes() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    Player sender = mock(Player.class);
    when(sender.getUniqueId()).thenReturn(PLAYER_ID);
    MetropolisHomeMoveCommand command = new MetropolisHomeMoveCommand(plugin);

    assertFalse(command.onCommand(sender, mock(Command.class), "move", new String[] {"0"}));
    assertFalse(command.onCommand(sender, mock(Command.class), "move", new String[] {"nope"}));
    assertFalse(
        command.onCommand(sender, mock(Command.class), "move", new String[] {"1", "unknown"}));
    when(plugin.getOfflinePlayer("unknown")).thenReturn(null);
    assertFalse(
        command.onCommand(sender, mock(Command.class), "move", new String[] {"1", "unknown"}));
    assertFalse(
        command.onCommand(sender, mock(Command.class), "move", new String[] {"1", "2", "extra"}));
    assertFalse(command.onCommand(sender, mock(Command.class), "move", new String[] {"1"}));

    verify(plugin, never()).setHome(PLAYER_ID, 0);
    verify(sender, times(2)).sendMessage(contains("positive integer"));
    verify(sender).sendMessage(contains("does not have home 1"));
    verify(sender).sendMessage(contains("Usage:"));
  }
}
