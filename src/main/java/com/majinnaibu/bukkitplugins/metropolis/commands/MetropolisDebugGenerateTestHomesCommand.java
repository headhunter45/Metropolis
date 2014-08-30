package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

public class MetropolisDebugGenerateTestHomesCommand implements CommandExecutor {

	private MetropolisPlugin _plugin;

	public MetropolisDebugGenerateTestHomesCommand(MetropolisPlugin plugin) {
		_plugin = plugin;
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try{
			int numHomes = Integer.parseInt(args[0]);
			for(int i=1; i<= numHomes; i++){
				_plugin.generateHome(Bukkit.getOfflinePlayer(String.format("test%d", i)));
			}
		
			return true;
		}catch(NumberFormatException ex){
			return false;
		}
	}

}
