package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.PlayerHome;

public class MetropolisHomeGoCommand implements CommandExecutor {
	MetropolisPlugin _plugin = null; 
	
	public MetropolisHomeGoCommand(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if(sender instanceof Player){
			player = (Player)sender;
		}else{
			sender.sendMessage("You must be a player");
			return false;
		}
		
		PlayerHome home = _plugin.getPlayerHome(player);
		
		Location loc = null;
		Location bedSpawn = player.getBedSpawnLocation();
		
		if(home.contains(bedSpawn)){
			loc = bedSpawn;
		}else {
			loc = home.getViableSpawnLocation(_plugin.getWorld());
		}
		
		if(loc != null){
			player.teleport(loc);
		}else{
			if(sender == player){
				sender.sendMessage("There is no valid spawn location in your home");
			}else{
				sender.sendMessage(String.format("There is no valid spawn location in %s's home", player.getName()));
			}
			return false;
		}
		
		return true;
	}
}
