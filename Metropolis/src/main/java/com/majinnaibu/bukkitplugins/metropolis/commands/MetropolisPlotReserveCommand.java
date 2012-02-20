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
		int minX = 0;
		int minY = 0;
		int minZ = 0;
		int maxX = 0;
		int maxY = 0;
		int maxZ = 0;
		Cuboid cuboid = new Cuboid();
		
		if(sender instanceof Player && args.length == 1){
			Selection selection = _plugin.worldEdit.getSelection((Player) sender);
			cuboid = new Cuboid(selection);
		}else if(args.length == 6){
			try{
				minX = Integer.parseInt(args[1]);
				minY = Integer.parseInt(args[2]);
				minZ = Integer.parseInt(args[3]);
				maxX = Integer.parseInt(args[4]);
				maxY = Integer.parseInt(args[5]);
				maxZ = Integer.parseInt(args[6]);
			}catch(NumberFormatException ex){
				return false;
			}
			cuboid = new Cuboid(minX, minY, minZ, maxX, maxY, maxZ);
		}else if(args.length == 4){
			try{
				minX = Integer.parseInt(args[1]);
				minY = 0;
				minZ = Integer.parseInt(args[2]);
				maxX = Integer.parseInt(args[3]);
				maxY = _plugin.getWorld().getMaxHeight();
				maxZ = Integer.parseInt(args[4]);
			}catch(NumberFormatException ex){
				return false;
			}
			cuboid = new Cuboid(minX, minY, minZ, maxX, maxY, maxZ);
		}else{
			return false;
		}
		
		String regionName = args[0];
		
		_plugin.reserveCuboid(regionName, cuboid);
		
		return true;
	}

}
