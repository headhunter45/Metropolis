package com.majinnaibu.bukkitplugins.metropolis.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.majinnaibu.bukkitplugins.metropolis.Cuboid;
import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

class MetropolisPlotReserveCommandTest {
	@Test
	void reservesCoordinatesFromAllSixArguments() {
		MetropolisPlugin plugin = mock(MetropolisPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		MetropolisPlotReserveCommand executor = new MetropolisPlotReserveCommand(plugin);

		assertTrue(executor.onCommand(sender, command, "reserve", new String[] {"north", "1", "2", "3", "4", "5", "6"}));

		ArgumentCaptor<Cuboid> cuboid = ArgumentCaptor.forClass(Cuboid.class);
		verify(plugin).reserveCuboid(eq("north"), cuboid.capture());
		assertEquals(1, cuboid.getValue().getMinX());
		assertEquals(2, cuboid.getValue().getMinY());
		assertEquals(3, cuboid.getValue().getMinZ());
		assertEquals(4, cuboid.getValue().getMaxX());
		assertEquals(5, cuboid.getValue().getMaxY());
		assertEquals(6, cuboid.getValue().getMaxZ());
	}

	@Test
	void rejectsMalformedCoordinateArgumentsWithoutCallingPlugin() {
		MetropolisPlugin plugin = mock(MetropolisPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		MetropolisPlotReserveCommand executor = new MetropolisPlotReserveCommand(plugin);

		assertFalse(executor.onCommand(sender, command, "reserve", new String[] {"north", "1", "two", "3", "4", "5", "6"}));
		verifyNoInteractions(plugin);
	}
}