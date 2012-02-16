package com.majinnaibu.bukkit.plugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkit.plugins.metropolis.MetropolisPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class MetropolisFlagResetCommand implements CommandExecutor {
	private MetropolisPlugin _plugin;

	public MetropolisFlagResetCommand(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		ProtectedRegion cityRegion = _plugin.regionManager.getRegion("City");
		cityRegion.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
		cityRegion.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.DENY);
		cityRegion.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
		cityRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
		cityRegion.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
		cityRegion.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
		cityRegion.setFlag(DefaultFlag.TNT, StateFlag.State.DENY);
		cityRegion.setFlag(DefaultFlag.LAVA_FLOW, StateFlag.State.DENY);
		cityRegion.setFlag(DefaultFlag.SNOW_FALL, StateFlag.State.DENY);
		
		for(ProtectedRegion homeRegion: _plugin.regionManager.getRegions().values()){
			if(homeRegion.getId().startsWith("h_")){
				homeRegion.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
				homeRegion.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.DENY);
				homeRegion.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
				homeRegion.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
				homeRegion.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
				homeRegion.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
				homeRegion.setFlag(DefaultFlag.TNT, StateFlag.State.DENY);
				
			}
		}
		
		sender.sendMessage("Metropolis: flags have been reset");
		
		return true;
		
		

	}

}
