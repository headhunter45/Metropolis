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