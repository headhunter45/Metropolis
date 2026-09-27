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
import com.majinnaibu.bukkitplugins.metropolis.Plot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MetropolisPlotGoCommand implements CommandExecutor {
  MetropolisPlugin _plugin;

  public MetropolisPlotGoCommand(MetropolisPlugin plugin) {
    _plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    Player player = null;
    Plot plot = null;

    if (sender instanceof Player) {
      player = (Player) sender;
    }

    if (args.length == 1 && player != null) {
      plot = _plugin.getPlot(args[0]);
    } else if (args.length >= 2) {
      player = _plugin.getPlayer(args[1]);
      plot = _plugin.getPlot(args[0]);
    } else {
      return false;
    }

    if (plot == null || player == null) {
      return false;
    }

    String errorMessage = _plugin.teleportPlayerToPlot(player, plot);
    if (errorMessage != null) {
      sender.sendMessage(errorMessage);
      return false;
    }

    return true;
  }
}
