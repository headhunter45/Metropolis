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

import com.majinnaibu.bukkitplugins.metropolis.Cuboid;
import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MetropolisPlotReserveCommand implements CommandExecutor {
  MetropolisPlugin _plugin;

  public MetropolisPlotReserveCommand(MetropolisPlugin metropolisPlugin) {
    _plugin = metropolisPlugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    int minX = 0;
    int minY = 0;
    int minZ = 0;
    int maxX = 0;
    int maxY = 0;
    int maxZ = 0;
    Cuboid cuboid = new Cuboid();

    if (sender instanceof Player && args.length == 1) {
      try {
        Region selection =
            _plugin
                .worldEdit
                .getSession((Player) sender)
                .getSelection(BukkitAdapter.adapt(_plugin.getWorld()));
        cuboid = new Cuboid(selection);
      } catch (IncompleteRegionException ex) {
        sender.sendMessage("Select a WorldEdit region first.");
        return false;
      }
    } else if (args.length == 7) {
      try {
        minX = Integer.parseInt(args[1]);
        minY = Integer.parseInt(args[2]);
        minZ = Integer.parseInt(args[3]);
        maxX = Integer.parseInt(args[4]);
        maxY = Integer.parseInt(args[5]);
        maxZ = Integer.parseInt(args[6]);
      } catch (NumberFormatException ex) {
        return false;
      }
      cuboid = new Cuboid(minX, minY, minZ, maxX, maxY, maxZ);
    } else if (args.length == 5) {
      try {
        minX = Integer.parseInt(args[1]);
        minY = 0;
        minZ = Integer.parseInt(args[2]);
        maxX = Integer.parseInt(args[3]);
        maxY = _plugin.getWorld().getMaxHeight();
        maxZ = Integer.parseInt(args[4]);
      } catch (NumberFormatException ex) {
        return false;
      }
      cuboid = new Cuboid(minX, minY, minZ, maxX, maxY, maxZ);
    } else {
      return false;
    }

    String regionName = args[0];

    _plugin.reserveCuboid(regionName, cuboid);
    sender.sendMessage("Reserved plot region " + regionName + ".");

    return true;
  }
}
