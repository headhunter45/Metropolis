package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
		String targetUUIDString = null;

		OfflinePlayer targetPlayer = null;
		
		if(args.length == 0){
			return false;
		}
		
		if(args.length >= 1){
			targetUUIDString = args[0];
			targetPlayer = _plugin.getOfflinePlayer(targetUUIDString);
		}
		
		if(targetPlayer == null){
			sender.sendMessage(String.format("The requested player {%s}does not appear to exist.", targetUUIDString));
			return false;
		}
		
		ProtectedRegion region = _plugin.getRegion(String.format("h_%s", targetPlayer.getUniqueId().toString()));
		if(region == null){
			sender.sendMessage(String.format("The player {%s} has no home to be evicted from."));
			return false;
		}
		
		//remove the player as owner and/or member of the region
		region.getMembers().removePlayer(targetPlayer.getName());//playerName);
		region.getOwners().removePlayer(targetPlayer.getName());
		
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
