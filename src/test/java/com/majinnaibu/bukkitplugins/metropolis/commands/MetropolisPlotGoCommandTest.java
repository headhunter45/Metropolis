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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.Plot;
import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class MetropolisPlotGoCommandTest {
  private static final UUID PLAYER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  @Test
  void teleportsPlayerToTheirOnlyReservation() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    Player player = player("Member", PLAYER_ID);
    Plot reservation = plot("r_camp");
    when(plugin.getReservationsForPlayer(PLAYER_ID)).thenReturn(List.of(reservation));
    MetropolisPlotGoCommand command = new MetropolisPlotGoCommand(plugin);

    assertTrue(command.onCommand(player, mock(Command.class), "plot-go", new String[0]));

    verify(plugin).teleportPlayerToPlot(player, reservation);
    verify(player).sendMessage(contains("reservation r_camp"));
  }

  @Test
  void asksForAReservationNameWhenPlayerBelongsToSeveral() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    Player player = player("Member", PLAYER_ID);
    when(plugin.getReservationsForPlayer(PLAYER_ID))
        .thenReturn(List.of(plot("r_camp"), plot("r_garden")));
    MetropolisPlotGoCommand command = new MetropolisPlotGoCommand(plugin);

    assertFalse(command.onCommand(player, mock(Command.class), "plot-go", new String[0]));

    verify(player).sendMessage(contains("r_camp, r_garden"));
    verify(plugin, never()).teleportPlayerToPlot(player, plot("r_camp"));
  }

  @Test
  void authorizedSenderCanTargetAnOnlineMember() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    CommandSender sender = mock(CommandSender.class);
    Player target = player("Member", PLAYER_ID);
    Plot reservation = plot("r_camp");
    when(sender.hasPermission("metropolis.plot.go")).thenReturn(true);
    when(plugin.getPlayer("Member")).thenReturn(target);
    when(plugin.getReservationsForPlayer(PLAYER_ID)).thenReturn(List.of(reservation));
    MetropolisPlotGoCommand command = new MetropolisPlotGoCommand(plugin);

    assertTrue(command.onCommand(sender, mock(Command.class), "plot-go", new String[] {"Member"}));

    verify(plugin).teleportPlayerToPlot(target, reservation);
    verify(target).sendMessage(contains("You were teleported"));
  }

  @Test
  void reportsNoMembershipAndUnsafeDestinations() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    Player player = player("Member", PLAYER_ID);
    when(plugin.getReservationsForPlayer(PLAYER_ID)).thenReturn(List.of());
    MetropolisPlotGoCommand command = new MetropolisPlotGoCommand(plugin);

    assertFalse(command.onCommand(player, mock(Command.class), "plot-go", new String[0]));
    verify(player).sendMessage(contains("not a member or owner"));

    Plot reservation = plot("r_camp");
    when(plugin.getReservationsForPlayer(PLAYER_ID)).thenReturn(List.of(reservation));
    when(plugin.teleportPlayerToPlot(player, reservation)).thenReturn("No safe teleport location");
    assertFalse(command.onCommand(player, mock(Command.class), "plot-go", new String[0]));
    verify(player).sendMessage(contains("No safe teleport location"));
  }

  private static Player player(String name, UUID id) {
    Player player = mock(Player.class);
    when(player.getName()).thenReturn(name);
    when(player.getUniqueId()).thenReturn(id);
    return player;
  }

  private static Plot plot(String name) {
    return new Plot(name, BlockVector3.ZERO, BlockVector3.at(5, 5, 5));
  }
}
