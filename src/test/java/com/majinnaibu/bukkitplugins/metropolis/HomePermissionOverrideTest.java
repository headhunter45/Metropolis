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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class HomePermissionOverrideTest {
  @Test
  void selectsTheHighestPriorityAndUsesLastEntryForTies() throws InvalidConfigurationException {
    YamlConfiguration config = new YamlConfiguration();
    config.loadFromString(
        """
        permissionOverrides:
          metropolis.plots.vip:
            priority: 10
            plotMultiplier: 2
            maxPlots: 2
          metropolis.plots.elite:
            priority: 20
            plotMultiplier: 3
            maxPlots: 5
          metropolis.plots.staff:
            priority: 20
            plotMultiplier: 4
            maxPlots: 8
        """);
    List<String> warnings = new ArrayList<>();

    List<HomePermissionOverride> overrides =
        HomePermissionOverride.load(
            config.getConfigurationSection("permissionOverrides"), warnings::add);
    HomePermissionOverride match =
        HomePermissionOverride.findMatch(
            overrides,
            Set.of("metropolis.plots.vip", "metropolis.plots.elite", "metropolis.plots.staff")
                ::contains);

    assertEquals(3, overrides.size());
    assertEquals("metropolis.plots.staff", match.getPermission());
    assertEquals(4, match.getPlotMultiplier());
    assertEquals(8, match.getMaxPlots());
    assertEquals(List.of(), warnings);
  }

  @Test
  void preservesDottedPermissionNamesAndSkipsInvalidEntries() throws InvalidConfigurationException {
    YamlConfiguration config = new YamlConfiguration();
    config.loadFromString(
        """
        permissionOverrides:
          metropolis.plots.vip:
            priority: 3
            plotMultiplier: 2
            maxPlots: 4
          metropolis.plots.invalid:
            plotMultiplier: 2
            maxPlots: 4
        """);
    List<String> warnings = new ArrayList<>();

    List<HomePermissionOverride> overrides =
        HomePermissionOverride.load(
            config.getConfigurationSection("permissionOverrides"), warnings::add);
    HomePermissionOverride match =
        HomePermissionOverride.findMatch(overrides, "metropolis.plots.vip"::equals);

    assertEquals(1, overrides.size());
    assertEquals("metropolis.plots.vip", overrides.get(0).getPermission());
    assertSame(overrides.get(0), match);
    assertNull(HomePermissionOverride.findMatch(overrides, ignored -> false));
    assertEquals(1, warnings.size());
  }

  @Test
  void resolvesUserOverrideBeforePermissionAndGlobalFallback()
      throws InvalidConfigurationException {
    YamlConfiguration config = new YamlConfiguration();
    config.set("permissionOverrides.metropolis.plots.vip.priority", 10);
    config.set("permissionOverrides.metropolis.plots.vip.plotMultiplier", 2);
    config.set("permissionOverrides.metropolis.plots.vip.maxPlots", 4);
    List<HomePermissionOverride> overrides =
        HomePermissionOverride.load(
            config.getConfigurationSection("permissionOverrides"), ignored -> {});
    UserOverride userOverride = new UserOverride("PlayerName", 3, 6);

    assertEquals(
        6,
        HomePermissionOverride.resolveMaxPlots(
            userOverride, overrides, "metropolis.plots.vip"::equals, 1));
    assertEquals(
        3,
        HomePermissionOverride.resolvePlotMultiplier(
            userOverride, overrides, "metropolis.plots.vip"::equals, 1));
    assertEquals(
        4,
        HomePermissionOverride.resolveMaxPlots(null, overrides, "metropolis.plots.vip"::equals, 1));
    assertEquals(
        2,
        HomePermissionOverride.resolvePlotMultiplier(
            null, overrides, "metropolis.plots.vip"::equals, 1));
    assertEquals(1, HomePermissionOverride.resolveMaxPlots(null, overrides, ignored -> false, 1));
    assertEquals(
        1, HomePermissionOverride.resolvePlotMultiplier(null, overrides, ignored -> false, 1));
  }
}
