package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.Plot;

public class MetropolisHomeListCommand implements CommandExecutor {
	private MetropolisPlugin _plugin;
	
	public MetropolisHomeListCommand(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		for(Plot cityBlock : _plugin.getCityBlocks()){
			sender.sendMessage(String.format("%s", cityBlock.toFriendlyString()));
		}
		return true;
	}

}
