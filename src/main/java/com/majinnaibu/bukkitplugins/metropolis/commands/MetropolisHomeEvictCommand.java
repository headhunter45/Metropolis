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

package com.majinnaibu.bukkitplugins.metropolis.commands;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MetropolisHomeEvictCommand implements CommandExecutor {
  MetropolisPlugin _plugin = null;

  public MetropolisHomeEvictCommand(MetropolisPlugin plugin) {
    _plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    // get the player and region
    String playerName = "";

    if (args.length == 0) {
      return false;
    }

    if (args.length >= 1) {
      playerName = args[0];
    }

    OfflinePlayer player = _plugin.getOfflinePlayer(playerName);
    if (player == null) {
      sender.sendMessage(
          String.format("The requested player {%s}does not appear to exist.", playerName));
      return false;
    }

    ProtectedRegion region =
        _plugin.getRegion(_plugin.getCurrentHomeRegionName(player.getUniqueId()));
    if (region == null) {
      sender.sendMessage(String.format("The player {%s} has no home to be evicted from."));
      return false;
    }

    // remove the player as owner and/or member of the region
    region.getMembers().removePlayer(player.getUniqueId());
    region.getOwners().removePlayer(player.getUniqueId());

    // if the region has no owners delete the region
    if (region.getMembers().size() == 0 && region.getOwners().size() == 0) {
      _plugin.removeRegion(region.getId());
    }

    _plugin.saveRegions();

    // ?optionally regen the region
    // _plugin.worldEdit.getCommand("regen").execute(_plugin.getServer().getConsoleSender(),
    // "regen", new String[]{});

    return true;
  }
}
