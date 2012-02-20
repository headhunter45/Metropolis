package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.majinnaibu.bukkitplugins.metropolis.Cuboid;
import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class MetropolisPlotReserveCommand implements CommandExecutor {
	MetropolisPlugin _plugin;

	public MetropolisPlotReserveCommand(MetropolisPlugin metropolisPlugin) {
		_plugin = metropolisPlugin;
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Cuboid cuboid = new Cuboid();
		if(sender instanceof Player && args.length == 1){
			Selection selection = _plugin.worldEdit.getSelection((Player) sender);
			cuboid = new Cuboid(selection);
		}else if(args.length == 6){
			int minX = Integer.parseInt(args[1]);
			int minY = Integer.parseInt(args[2]);
			int minZ = Integer.parseInt(args[3]);
			int maxX = Integer.parseInt(args[4]);
			int maxY = Integer.parseInt(args[5]);
			int maxZ = Integer.parseInt(args[6]);
			cuboid = new Cuboid(minX, minY, minZ, maxX, maxY, maxZ);
		}else if(args.length == 4){
			int minX = Integer.parseInt(args[1]);
			int minY = 0;
			int minZ = Integer.parseInt(args[2]);
			int maxX = Integer.parseInt(args[3]);
			int maxY = _plugin.getWorld().getMaxHeight();
			int maxZ = Integer.parseInt(args[4]);
			cuboid = new Cuboid(minX, minY, minZ, maxX, maxY, maxZ);
		}else{
			return false;
		}
		
		String regionName = args[0];
		
		_plugin.reserveCuboid(regionName, cuboid);
		
		return true;
	}

}
