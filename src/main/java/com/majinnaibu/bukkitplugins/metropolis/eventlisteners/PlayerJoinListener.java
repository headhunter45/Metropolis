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

package com.majinnaibu.bukkitplugins.metropolis.eventlisteners;

import com.majinnaibu.bukkitplugins.metropolis.Cuboid;
import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.PlayerHome;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
  private MetropolisPlugin _plugin = null;

  public PlayerJoinListener(MetropolisPlugin plugin) {
    _plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (player == null) {
      return;
    }

    PlayerHome home = _plugin.getPlayerHome(player);
    if (home == null) {
      _plugin.getLogger().info("home is null");
    } else if (home.getCuboid() == null) {
      _plugin.getLogger().info("home.getCuboid() is null");
    } else if (home.getCuboid().getVolume() == 0) {
      _plugin.getLogger().info("home.getCuboid().getVolume() is 0");
    }

    if (home == null || home.getCuboid() == null || home.getCuboid().getVolume() == 0) {
      _plugin
          .getLogger()
          .info(
              String.format(
                  "Metropolis: Unable to get or create home for player %s", player.getName()));
    } else {
      Cuboid cuboid = home.getCuboid();
      player.sendMessage(
          String.format(
              "Metropolis: Welcome %s your home is between (%d, %d, %d) and (%d, %d, %d)",
              player.getName(),
              cuboid.getMinX(),
              cuboid.getMinY(),
              cuboid.getMinZ(),
              cuboid.getMaxX(),
              cuboid.getMaxY(),
              cuboid.getMaxZ()));
    }
  }
}
