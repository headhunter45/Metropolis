package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

public class MetropolisHomeMoveCommand implements CommandExecutor {
	MetropolisPlugin _plugin;

	public MetropolisHomeMoveCommand(MetropolisPlugin plugin) {
		_plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		int homeNumber = 0;
		
		if(sender instanceof Player){
			player = (Player) sender;
		}
		
		
		if(args.length == 1 && player != null){
			try{
				homeNumber = Integer.parseInt(args[0]);
			}catch(NumberFormatException ex){
				return false;
			}
		}else if(args.length >= 2){
			try{
				homeNumber = Integer.parseInt(args[0]);
			}catch(NumberFormatException ex){
				return false;
			}
		}else{
			return false;			
		}

		if(homeNumber <= 0){
			return false;
		}
		
		String errorMessage = _plugin.setCurrentHome(player, homeNumber);
		if(errorMessage != null){
			sender.sendMessage(errorMessage);
			return false;
		}
		
		return true;
	}
}
