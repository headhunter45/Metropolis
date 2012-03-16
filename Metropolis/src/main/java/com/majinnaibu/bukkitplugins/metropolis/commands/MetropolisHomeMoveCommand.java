package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

public class MetropolisHomeMoveCommand implements CommandExecutor {
	MetropolisPlugin _plugin;

	public MetropolisHomeMoveCommand(MetropolisPlugin plugin) {
		_plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		OfflinePlayer player = null;
		int newHomeNumber = 0;
		
		if(sender instanceof OfflinePlayer){
			player = (OfflinePlayer)sender;
		}
		
		if(args.length == 1){
			try{
				newHomeNumber = Integer.parseInt(args[0]);
			}catch(NumberFormatException ex){
				return false;
			}
		}else if(args.length >= 2){
			try{
				newHomeNumber = Integer.parseInt(args[0]);
			}catch(NumberFormatException ex){
				return false;
			}
			
			player = _plugin.getPlayer(args[1]);
		}else{
			return false;
		}
		
		if(player == null || !_plugin.homeExists(player.getName(), newHomeNumber)){
			return false;
		}
		
		_plugin.setHome(player.getName(), newHomeNumber);
				
		return true;
	}

}
