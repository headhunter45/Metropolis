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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.PlayerHome;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Test;

class MetropolisDebugGenerateTestHomesCommandTest {
  @Test
  void repeatedRunsContinueWithTheNextUnusedTestName() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    Server server = mock(Server.class);
    BukkitScheduler scheduler = mock(BukkitScheduler.class);
    CommandSender sender = mock(CommandSender.class);
    Set<UUID> existingHomeOwners = new HashSet<>();
    List<String> generatedNames = new ArrayList<>();
    List<Runnable> scheduledBatches = new ArrayList<>();

    when(plugin.getServer()).thenReturn(server);
    when(server.getScheduler()).thenReturn(scheduler);
    when(scheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(1L), eq(2L)))
        .thenAnswer(
            invocation -> {
              scheduledBatches.add(invocation.getArgument(1));
              return mock(BukkitTask.class);
            });
    when(server.getOfflinePlayer(anyString()))
        .thenAnswer(
            invocation -> {
              String name = invocation.getArgument(0);
              OfflinePlayer player = mock(OfflinePlayer.class);
              UUID playerId = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
              when(player.getName()).thenReturn(name);
              when(player.getUniqueId()).thenReturn(playerId);
              return player;
            });
    when(plugin.getNumPlots(any(UUID.class)))
        .thenAnswer(invocation -> existingHomeOwners.contains(invocation.getArgument(0)) ? 1 : 0);
    when(plugin.generateHome(any(OfflinePlayer.class), eq(1)))
        .thenAnswer(
            invocation -> {
              OfflinePlayer player = invocation.getArgument(0);
              generatedNames.add(player.getName());
              existingHomeOwners.add(player.getUniqueId());
              return mock(PlayerHome.class);
            });

    MetropolisDebugGenerateTestHomesCommand executor =
        new MetropolisDebugGenerateTestHomesCommand(plugin);

    assertTrue(executor.onCommand(sender, mock(Command.class), "testhomes", new String[] {"10"}));
    assertTrue(generatedNames.isEmpty());
    assertEquals(1, scheduledBatches.size());
    assertFalse(executor.onCommand(sender, mock(Command.class), "testhomes", new String[] {"10"}));

    scheduledBatches.getFirst().run();
    assertEquals(List.of("Test1"), generatedNames);
    for (int tick = 1; tick < 10; tick++) {
      scheduledBatches.getFirst().run();
    }
    assertEquals(10, generatedNames.size());

    assertTrue(executor.onCommand(sender, mock(Command.class), "testhomes", new String[] {"10"}));
    assertEquals(2, scheduledBatches.size());
    for (int tick = 0; tick < 20; tick++) {
      scheduledBatches.get(1).run();
    }

    List<String> expectedNames = new ArrayList<>();
    for (int testNumber = 1; testNumber <= 20; testNumber++) {
      expectedNames.add("Test" + testNumber);
    }
    assertEquals(expectedNames, generatedNames);
  }

  @Test
  void rejectsNonPositiveCountsWithoutGeneratingHomes() {
    MetropolisPlugin plugin = mock(MetropolisPlugin.class);
    MetropolisDebugGenerateTestHomesCommand executor =
        new MetropolisDebugGenerateTestHomesCommand(plugin);

    assertFalse(
        executor.onCommand(
            mock(CommandSender.class), mock(Command.class), "testhomes", new String[] {"0"}));

    verify(plugin, never()).generateHome(any(OfflinePlayer.class), eq(1));
  }
}
