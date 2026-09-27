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

public class MetropolisHomeMoveCommand implements CommandExecutor {
  MetropolisPlugin _plugin;

  public MetropolisHomeMoveCommand(MetropolisPlugin plugin) {
    _plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    OfflinePlayer player = null;
    int newHomeNumber = 0;

    if (sender instanceof OfflinePlayer) {
      player = (OfflinePlayer) sender;
    }

    if (args.length == 1) {
      try {
        newHomeNumber = Integer.parseInt(args[0]);
      } catch (NumberFormatException ex) {
        return false;
      }
    } else if (args.length >= 2) {
      try {
        newHomeNumber = Integer.parseInt(args[0]);
      } catch (NumberFormatException ex) {
        return false;
      }

      player = _plugin.getOfflinePlayer(args[1]);
    } else {
      return false;
    }

    if (player == null || !_plugin.homeExists(player.getUniqueId(), newHomeNumber)) {
      return false;
    }

    _plugin.setHome(player.getUniqueId(), newHomeNumber);

    return true;
  }
}
