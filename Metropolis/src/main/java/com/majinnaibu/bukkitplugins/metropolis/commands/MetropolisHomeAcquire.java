package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

public class MetropolisHomeAcquire implements CommandExecutor {
	MetropolisPlugin _plugin;
	
	public MetropolisHomeAcquire(MetropolisPlugin plugin){
		_plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("This command can only be used by players");
			return false;
		}
		
		Player player = (Player) sender;
		
		if(_plugin.getNumPlots(player.getName()) >= _plugin.getMaxPlots(player.getName())){
			sender.sendMessage("You cannot have any more plots");
			return false;
		}
		
		_plugin.assignPlot(player);
		return true;
	}

}
