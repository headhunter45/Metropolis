package com.majinnaibu.bukkitplugins.metropolis.commands;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

public class MetropolisDebugMatchMaterialCommand implements CommandExecutor {

	private MetropolisPlugin _plugin;
	
	public MetropolisDebugMatchMaterialCommand(MetropolisPlugin plugin) {
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			sender.sendMessage(String.format("Material.getMaterial(\"%s\") returns \"%s\"", String.join(" ", args), Material.matchMaterial(args[0])));
			return true;
		} catch(Exception ex) {
			_plugin.getLogger().log(Level.ALL, "Error", ex);
			return false;
		}
	}
}
