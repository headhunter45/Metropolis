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
import com.majinnaibu.bukkitplugins.metropolis.PlotReservationLookup;

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
    if (args.length > 2) {
      sender.sendMessage(
          "Usage: /metropolis-plot-go [playerName] or /metropolis-plot-go <reservationName> [playerName]");
      return false;
    }

    Player player;
    String requestedReservation = null;
    if (args.length == 0) {
      if (!(sender instanceof Player senderPlayer)) {
        sender.sendMessage("Console must specify an online player name.");
        return false;
      }
      player = senderPlayer;
    } else if (args.length == 1) {
      Player namedPlayer = _plugin.getPlayer(args[0]);
      if (namedPlayer != null) {
        player = namedPlayer;
      } else if (sender instanceof Player senderPlayer) {
        player = senderPlayer;
        requestedReservation = args[0];
      } else {
        sender.sendMessage("No online player named " + args[0] + ".");
        return false;
      }
    } else {
      requestedReservation = args[0];
      player = _plugin.getPlayer(args[1]);
      if (player == null) {
        sender.sendMessage("No online player named " + args[1] + ".");
        return false;
      }
    }

    boolean canManagePlots = sender.hasPermission("metropolis.plot.go");
    if (player != sender && !canManagePlots) {
      sender.sendMessage("You do not have permission to teleport another player.");
      return false;
    }

    var reservations = _plugin.getReservationsForPlayer(player.getUniqueId());
    Plot plot;
    if (requestedReservation == null) {
      if (reservations.isEmpty()) {
        sender.sendMessage(
            player.getName() + " is not a member or owner of a Metropolis reservation.");
        return false;
      }
      if (reservations.size() > 1) {
        String names =
            reservations.stream()
                .map(Plot::getRegionName)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
        sender.sendMessage(
            player.getName()
                + " belongs to multiple reservations: "
                + names
                + ". Specify one with /metropolis-plot-go <reservationName> [playerName].");
        return false;
      }
      plot = reservations.get(0);
    } else {
      plot = PlotReservationLookup.findByName(reservations, requestedReservation);
      if (plot == null && canManagePlots) {
        plot = _plugin.getPlot(requestedReservation);
      }
      if (plot == null) {
        sender.sendMessage(
            player.getName()
                + " is not a member or owner of reservation "
                + requestedReservation
                + ".");
        return false;
      }
    }

    if (player != sender && !canManagePlots) {
      sender.sendMessage("You do not have permission to teleport another player.");
      return false;
    }

    String errorMessage = _plugin.teleportPlayerToPlot(player, plot);
    if (errorMessage != null) {
      sender.sendMessage(errorMessage);
      return false;
    }

    String destination = "reservation " + plot.getRegionName();
    sender.sendMessage("Teleported " + player.getName() + " to " + destination + ".");
    if (player != sender) {
      player.sendMessage("You were teleported to " + destination + ".");
    }
    return true;
  }
}
