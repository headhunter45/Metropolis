package com.majinnaibu.bukkitplugins.metropolis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

final class CurrentHomesStore {
	private CurrentHomesStore() {}

	static Map<UUID, Integer> load(File file, Function<String, UUID> legacyOwnerResolver)
			throws IOException, InvalidConfigurationException {
		Map<UUID, Integer> homesByOwner = new HashMap<>();
		if (!file.exists()) {
			return homesByOwner;
		}

		YamlConfiguration yaml = new YamlConfiguration();
		yaml.load(file);
		for (String ownerKey : yaml.getKeys(false)) {
			UUID ownerId;
			try {
				ownerId = UUID.fromString(ownerKey);
			} catch (IllegalArgumentException ex) {
				ownerId = legacyOwnerResolver.apply(ownerKey);
			}
			int homeNumber = yaml.getInt(ownerKey, 0);
			if (ownerId != null && homeNumber > 0) {
				homesByOwner.put(ownerId, homeNumber);
			}
		}
		return homesByOwner;
	}

	static void save(File file, Map<UUID, Integer> homesByOwner) throws IOException {
		File parent = file.getParentFile();
		if (parent != null) {
			parent.mkdirs();
		}

		YamlConfiguration yaml = new YamlConfiguration();
		homesByOwner.forEach((ownerId, homeNumber) -> yaml.set(ownerId.toString(), homeNumber));
		yaml.save(file);
	}
}