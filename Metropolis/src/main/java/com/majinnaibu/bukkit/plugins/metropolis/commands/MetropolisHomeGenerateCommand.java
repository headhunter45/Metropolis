package com.majinnaibu.bukkit.plugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkit.plugins.metropolis.MetropolisPlugin;

public class MetropolisHomeGenerateCommand implements CommandExecutor {

	private MetropolisPlugin _plugin;
	
	public MetropolisHomeGenerateCommand(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length < 1){
			return false;
		}
		
		_plugin.generateHome(args[0]);
		
		sender.sendMessage("[Metropolis] Home generated for " + args[0]);
		
		return true;
	}

}
