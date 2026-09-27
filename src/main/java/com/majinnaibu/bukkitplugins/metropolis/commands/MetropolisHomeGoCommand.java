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
import com.majinnaibu.bukkitplugins.metropolis.PlayerHome;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MetropolisHomeGoCommand implements CommandExecutor {
  MetropolisPlugin _plugin = null;

  public MetropolisHomeGoCommand(MetropolisPlugin plugin) {
    _plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;

      if (args.length >= 1) {
        if (player.hasPermission("")) {
          player = _plugin.getPlayer(args[0]);

          if (player == null) {
            sender.sendMessage(String.format("Unable to find player %s", args[0]));
            return false;
          }
        } else {
          sender.sendMessage("Permission denied");
          return false;
        }
      }
    } else {
      if (args.length >= 1) {
        player = _plugin.getPlayer(args[0]);

        if (player == null) {
          sender.sendMessage(String.format("Unable to find player %s", args[0]));
          return false;
        }
      } else {
        sender.sendMessage("You must be a player");
        return false;
      }
    }

    PlayerHome home = _plugin.getPlayerHome(player);

    Location loc = null;
    Location bedSpawn = player.getBedSpawnLocation();

    if (home.contains(bedSpawn)) {
      loc = bedSpawn;
    } else {
      loc = home.getViableSpawnLocation(_plugin.getWorld());
    }

    if (loc != null) {
      player.teleport(loc);
    } else {
      if (sender == player) {
        sender.sendMessage("There is no valid spawn location in your home");
      } else {
        sender.sendMessage(
            String.format("There is no valid spawn location in %s's home", player.getName()));
      }
      return false;
    }

    return true;
  }
}
