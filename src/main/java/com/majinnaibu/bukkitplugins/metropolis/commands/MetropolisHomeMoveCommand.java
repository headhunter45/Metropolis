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

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MetropolisHomeMoveCommand implements CommandExecutor {
  MetropolisPlugin _plugin;

  public MetropolisHomeMoveCommand(MetropolisPlugin plugin) {
    _plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length < 1 || args.length > 2) {
      sender.sendMessage("Usage: /metropolis-home-move <homeNumber> [playerName]");
      return false;
    }

    int homeNumber;
    try {
      homeNumber = Integer.parseInt(args[0]);
    } catch (NumberFormatException ex) {
      sender.sendMessage("Home number must be a positive integer.");
      return false;
    }
    if (homeNumber < 1) {
      sender.sendMessage("Home number must be a positive integer.");
      return false;
    }

    OfflinePlayer target;
    if (args.length == 1) {
      if (!(sender instanceof Player player)) {
        sender.sendMessage("Console must specify a player name.");
        return false;
      }
      target = player;
    } else {
      target = _plugin.getOfflinePlayer(args[1]);
      if (target == null) {
        sender.sendMessage("No known player named " + args[1] + ".");
        return false;
      }
    }

    if (!_plugin.homeExists(target.getUniqueId(), homeNumber)) {
      String targetName =
          target.getName() == null ? target.getUniqueId().toString() : target.getName();
      sender.sendMessage(targetName + " does not have home " + homeNumber + ".");
      return false;
    }

    _plugin.setHome(target.getUniqueId(), homeNumber);
    String targetName =
        target.getName() == null ? target.getUniqueId().toString() : target.getName();
    sender.sendMessage("Home " + homeNumber + " is now active for " + targetName + ".");
    if (target instanceof Player targetPlayer && targetPlayer != sender) {
      targetPlayer.sendMessage("Your active Metropolis home is now home " + homeNumber + ".");
    }

    return true;
  }
}
