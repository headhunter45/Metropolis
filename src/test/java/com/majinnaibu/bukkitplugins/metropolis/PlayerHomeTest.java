package com.majinnaibu.bukkitplugins.metropolis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.sk89q.worldedit.math.BlockVector3;

class PlayerHomeTest {
	@Test
	void buildsUuidBasedRegionIdentityAndPreservesDisplayName() {
		UUID playerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		PlayerHome home = new PlayerHome(
				playerId,
				"PlayerName",
				2,
				BlockVector3.at(1, 2, 3),
				BlockVector3.at(4, 5, 6));

		assertEquals(playerId, home.getPlayerId());
		assertEquals("PlayerName", home.getPlayerName());
		assertEquals(2, home.getNumber());
		assertEquals("h_2_" + playerId, home.getRegionName());
	}
}