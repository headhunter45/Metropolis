package com.majinnaibu.bukkitplugins.metropolis.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

class MetropolisHomeAcquireTest {
	@Test
	void assignsAPlotWhenThePlayerIsBelowTheirLimit() {
		MetropolisPlugin plugin = mock(MetropolisPlugin.class);
		Player player = mock(Player.class);
		UUID playerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		when(player.getUniqueId()).thenReturn(playerId);
		when(plugin.getNumPlots(playerId)).thenReturn(0);
		when(plugin.getMaxPlots(playerId)).thenReturn(1);
		MetropolisHomeAcquire executor = new MetropolisHomeAcquire(plugin);

		assertTrue(executor.onCommand(player, mock(Command.class), "acquire", new String[0]));

		verify(plugin).assignPlot(player);
	}

	@Test
	void doesNotAssignAPlotAfterThePlayerReachesTheirLimit() {
		MetropolisPlugin plugin = mock(MetropolisPlugin.class);
		Player player = mock(Player.class);
		UUID playerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		when(player.getUniqueId()).thenReturn(playerId);
		when(plugin.getNumPlots(playerId)).thenReturn(1);
		when(plugin.getMaxPlots(playerId)).thenReturn(1);
		MetropolisHomeAcquire executor = new MetropolisHomeAcquire(plugin);

		assertFalse(executor.onCommand(player, mock(Command.class), "acquire", new String[0]));

		verify(plugin, never()).assignPlot(player);
	}
}