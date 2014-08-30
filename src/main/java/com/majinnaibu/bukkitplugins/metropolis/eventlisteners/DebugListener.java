package com.majinnaibu.bukkitplugins.metropolis.eventlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

public class DebugListener implements Listener {
	MetropolisPlugin _plugin;
	
	public DebugListener(MetropolisPlugin plugin){
		_plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onEvent(PlayerInteractEvent event){
		//event.
	}
}
