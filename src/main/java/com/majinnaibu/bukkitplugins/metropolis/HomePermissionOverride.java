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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.configuration.ConfigurationSection;

public final class HomePermissionOverride {
  private final String _permission;
  private final int _priority;
  private final int _plotMultiplier;
  private final int _maxPlots;

  private HomePermissionOverride(
      String permission, int priority, int plotMultiplier, int maxPlots) {
    _permission = permission;
    _priority = priority;
    _plotMultiplier = plotMultiplier;
    _maxPlots = maxPlots;
  }

  public String getPermission() {
    return _permission;
  }

  public int getPriority() {
    return _priority;
  }

  public int getPlotMultiplier() {
    return _plotMultiplier;
  }

  public int getMaxPlots() {
    return _maxPlots;
  }

  public static List<HomePermissionOverride> load(
      ConfigurationSection section, Consumer<String> warningLogger) {
    if (section == null) {
      return List.of();
    }

    List<HomePermissionOverride> overrides = new ArrayList<>();
    collect(section, "", overrides, warningLogger);
    return List.copyOf(overrides);
  }

  private static void collect(
      ConfigurationSection section,
      String permissionPrefix,
      List<HomePermissionOverride> overrides,
      Consumer<String> warningLogger) {
    for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
      String permission =
          permissionPrefix.isEmpty() ? entry.getKey() : permissionPrefix + "." + entry.getKey();
      Map<?, ?> values = getValues(entry.getValue());
      if (values != null && !hasRuleValues(values)) {
        collect((ConfigurationSection) entry.getValue(), permission, overrides, warningLogger);
        continue;
      }

      if (permission.isBlank()
          || values == null
          || !(values.get("priority") instanceof Number priority)
          || !(values.get("plotMultiplier") instanceof Number plotMultiplier)
          || !(values.get("maxPlots") instanceof Number maxPlots)
          || priority.intValue() != priority.doubleValue()
          || plotMultiplier.intValue() != plotMultiplier.doubleValue()
          || maxPlots.intValue() != maxPlots.doubleValue()
          || plotMultiplier.intValue() < 1
          || maxPlots.intValue() < 0) {
        warningLogger.accept("Ignoring invalid permissionOverrides entry '" + permission + "'.");
        continue;
      }

      overrides.add(
          new HomePermissionOverride(
              permission, priority.intValue(), plotMultiplier.intValue(), maxPlots.intValue()));
    }
  }

  private static boolean hasRuleValues(Map<?, ?> values) {
    return values.containsKey("priority")
        || values.containsKey("plotMultiplier")
        || values.containsKey("maxPlots");
  }

  public static HomePermissionOverride findMatch(
      List<HomePermissionOverride> overrides, Predicate<String> hasPermission) {
    HomePermissionOverride match = null;
    for (HomePermissionOverride override : overrides) {
      if (hasPermission.test(override.getPermission())
          && (match == null || override.getPriority() >= match.getPriority())) {
        match = override;
      }
    }
    return match;
  }

  public static int resolveMaxPlots(
      UserOverride userOverride,
      List<HomePermissionOverride> overrides,
      Predicate<String> hasPermission,
      int fallback) {
    if (userOverride != null) {
      return userOverride.getMaxPlots();
    }
    HomePermissionOverride match = findMatch(overrides, hasPermission);
    return match == null ? fallback : match.getMaxPlots();
  }

  public static int resolvePlotMultiplier(
      UserOverride userOverride,
      List<HomePermissionOverride> overrides,
      Predicate<String> hasPermission,
      int fallback) {
    if (userOverride != null) {
      return userOverride.getPlotMultiplier();
    }
    HomePermissionOverride match = findMatch(overrides, hasPermission);
    return match == null ? fallback : match.getPlotMultiplier();
  }

  private static Map<?, ?> getValues(Object value) {
    if (value instanceof ConfigurationSection section) {
      return section.getValues(false);
    }
    if (value instanceof Map<?, ?> map) {
      return map;
    }
    return null;
  }
}
