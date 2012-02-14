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
		Player player = event.getPlayer();
		if(player == null){
			return;
		}
		
		PlayerHome home = _plugin.getPlayerHome(player);
		if(home == null || home.getCuboid() == null || home.getCuboid().getVolume() == 0){
			MetropolisPlugin.log.info(String.format("Metropolis: Unable to get or create home for player %s", player.getName()));
		}
		
		Cuboid cuboid = home.getCuboid();
		player.sendMessage(String.format("Metropolis: Welcome %s your home is between (%d, %d, %d) and (%d, %d, %d)", player.getName(), cuboid.getMinX(), cuboid.getMinY(), cuboid.getMinZ(), cuboid.getMaxX(), cuboid.getMaxY(), cuboid.getMaxZ()));
	}
}
