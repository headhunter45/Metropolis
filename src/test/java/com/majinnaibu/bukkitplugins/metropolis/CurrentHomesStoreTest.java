package com.majinnaibu.bukkitplugins.metropolis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CurrentHomesStoreTest {
	@TempDir
	File temporaryDirectory;

	@Test
	void roundTripsHomesByUuid() throws Exception {
		File file = new File(temporaryDirectory, "currentHomes.yml");
		UUID playerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		Map<UUID, Integer> expected = Map.of(playerId, 3);

		CurrentHomesStore.save(file, expected);

		assertEquals(expected, CurrentHomesStore.load(file, ignored -> null));
	}

	@Test
	void resolvesLegacyPlayerNameKeys() throws Exception {
		File file = new File(temporaryDirectory, "currentHomes.yml");
		UUID playerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		YamlConfiguration yaml = new YamlConfiguration();
		yaml.set("OldPlayerName", 2);
		yaml.save(file);

		assertEquals(Map.of(playerId, 2), CurrentHomesStore.load(file, name -> {
			assertEquals("OldPlayerName", name);
			return playerId;
		}));
	}
}