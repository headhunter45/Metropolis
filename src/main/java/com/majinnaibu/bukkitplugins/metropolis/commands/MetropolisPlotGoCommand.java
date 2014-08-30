package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.Plot;

public class MetropolisPlotGoCommand implements CommandExecutor {
	MetropolisPlugin _plugin;

	public MetropolisPlotGoCommand(MetropolisPlugin plugin) {
		_plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		Plot plot = null;
		
		if(sender instanceof Player){
			player = (Player) sender;
		}
		
		if(args.length == 1 && player != null){
			plot = _plugin.getPlot(args[0]);
		}else if(args.length >= 2){
			player = _plugin.getPlayer(args[1]);
			plot = _plugin.getPlot(args[0]);
		}else{
			return false;
		}
		
		if(plot == null || player == null){
			return false;
		}
		
		String errorMessage = _plugin.teleportPlayerToPlot(player, plot);
		if(errorMessage != null){
			sender.sendMessage(errorMessage);
			return false;
		}
		
		return true;
	}

}
