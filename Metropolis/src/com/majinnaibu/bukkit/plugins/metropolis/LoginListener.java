package com.majinnaibu.bukkit.plugins.metropolis;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginListener implements Listener {
	private MetropolisPlugin _plugin = null;
	
	public LoginListener(MetropolisPlugin plugin){
		_plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event){
		MetropolisPlugin.log.info("Metropolis: player login");
		Player player = event.getPlayer();
		if(player == null){
			return;
		}
		
		_plugin.getPlayerHome(player);
	}
}
