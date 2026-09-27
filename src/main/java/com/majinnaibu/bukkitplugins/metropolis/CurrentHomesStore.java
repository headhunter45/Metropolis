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
