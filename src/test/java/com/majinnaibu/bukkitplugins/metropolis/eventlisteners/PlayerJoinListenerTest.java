package com.majinnaibu.bukkitplugins.metropolis.eventlisteners;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.PlayerHome;
import com.sk89q.worldedit.math.BlockVector3;

class PlayerJoinListenerTest {
	@Test
	void tellsJoiningPlayerTheirHomeBounds() {
		MetropolisPlugin plugin = mock(MetropolisPlugin.class);
		Player player = mock(Player.class);
		PlayerJoinEvent event = mock(PlayerJoinEvent.class);
		when(event.getPlayer()).thenReturn(player);
		when(player.getName()).thenReturn("PlayerName");
		when(plugin.getPlayerHome(player)).thenReturn(new PlayerHome(
				UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
				"PlayerName",
				1,
				BlockVector3.at(1, 2, 3),
				BlockVector3.at(4, 5, 6)));

		new PlayerJoinListener(plugin).onPlayerJoin(event);

		verify(player).sendMessage(contains("home is between (1, 2, 3) and (4, 5, 6)"));
	}
}