package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

public class MetropolisHomeEvictCommand implements CommandExecutor {
	MetropolisPlugin _plugin = null;

	public MetropolisHomeEvictCommand(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// TODO Auto-generated method stub
		return false;
	}

}
