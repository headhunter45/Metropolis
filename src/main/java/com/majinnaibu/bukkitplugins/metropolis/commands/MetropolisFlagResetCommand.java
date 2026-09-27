package com.majinnaibu.bukkitplugins.metropolis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
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
		cityRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
		cityRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
		cityRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
		cityRegion.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
		cityRegion.setFlag(Flags.ENDER_BUILD, StateFlag.State.DENY);
		cityRegion.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.DENY);
		cityRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
		cityRegion.setFlag(Flags.LAVA_FLOW, StateFlag.State.DENY);
		cityRegion.setFlag(Flags.SNOW_FALL, StateFlag.State.DENY);
		
		for(ProtectedRegion homeRegion: _plugin.regionManager.getRegions().values()){
			if(homeRegion.getId().startsWith("h_")){
				homeRegion.setFlag(Flags.PVP, StateFlag.State.DENY);
				homeRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.DENY);
				homeRegion.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
				homeRegion.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
				homeRegion.setFlag(Flags.ENDER_BUILD, StateFlag.State.DENY);
				homeRegion.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.DENY);
				homeRegion.setFlag(Flags.TNT, StateFlag.State.DENY);
				
			}
		}
		
		sender.sendMessage("Metropolis: flags have been reset");
		
		return true;
		
		

	}

}
