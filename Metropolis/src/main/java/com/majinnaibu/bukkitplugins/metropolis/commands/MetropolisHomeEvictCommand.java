package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class MetropolisHomeEvictCommand implements CommandExecutor {
	MetropolisPlugin _plugin = null;

	public MetropolisHomeEvictCommand(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//get the player and region
		String playerName = "";
		
		if(args.length == 0){
			return false;
		}
		
		if(args.length >= 1){
			playerName = args[0];
		}
		
		OfflinePlayer player = _plugin.getServer().getOfflinePlayer(playerName);
		if(player == null){
			sender.sendMessage(String.format("The requested player {%s}does not appear to exist.", playerName));
			return false;
		}
		
		ProtectedRegion region = _plugin.getRegion(String.format("h_%s", player.getName()));
		if(region == null){
			sender.sendMessage(String.format("The player {%s} has no home to be evicted from."));
			return false;
		}
		
		//remove the player as owner and/or member of the region
		region.getMembers().removePlayer(playerName);
		region.getOwners().removePlayer(playerName);
		
		//if the region has no owners delete the region
		if(region.getMembers().size() == 0 && region.getOwners().size() == 0){
			_plugin.removeRegion(region.getId());
		}
		
		_plugin.saveRegions();
		
		//?optionally regen the region
		//_plugin.worldEdit.getCommand("regen").execute(_plugin.getServer().getConsoleSender(), "regen", new String[]{});
		
		return true;
	}

}
