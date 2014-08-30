package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

public class MetropolisCommand implements CommandExecutor {
	private MetropolisPlugin _plugin;
	
	public MetropolisCommand(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		sender.sendMessage("Metropolis: version "+ _plugin.pdf.getVersion());
		
		return true;
	}

}
