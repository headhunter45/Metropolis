package com.majinnaibu.bukkit.plugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkit.plugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkit.plugins.metropolis.PlayerHome;

public class MetropolisHomeListCommand implements CommandExecutor {
	private MetropolisPlugin _plugin;
	
	public MetropolisHomeListCommand(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		for(PlayerHome cityBlock : _plugin.getCityBlocks()){
			sender.sendMessage(String.format("%s", cityBlock.toFriendlyString()));
		}
		return true;
	}

}
