package com.majinnaibu.bukkit.plugins.metropolis.eventlisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.majinnaibu.bukkit.plugins.metropolis.Cuboid;
import com.majinnaibu.bukkit.plugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkit.plugins.metropolis.PlayerHome;

public class PlayerJoinListener implements Listener {
	private MetropolisPlugin _plugin = null;
	
	public PlayerJoinListener(MetropolisPlugin plugin){
		_plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player == null){
			return;
		}
		
		PlayerHome home = _plugin.getPlayerHome(player);
		if(home==null){MetropolisPlugin.log.info("home is null");}
		else if(home.getCuboid() == null){MetropolisPlugin.log.info("home.getCuboid() is null");}
		else if(home.getCuboid().getVolume() == 0){MetropolisPlugin.log.info("home.getCuboid().getVolume() is 0");}
		
		if(home == null || home.getCuboid() == null || home.getCuboid().getVolume() == 0){
			MetropolisPlugin.log.info(String.format("Metropolis: Unable to get or create home for player %s", player.getName()));
		}else{
			Cuboid cuboid = home.getCuboid();
			player.sendMessage(String.format("Metropolis: Welcome %s your home is between (%d, %d, %d) and (%d, %d, %d)", player.getName(), cuboid.getMinX(), cuboid.getMinY(), cuboid.getMinZ(), cuboid.getMaxX(), cuboid.getMaxY(), cuboid.getMaxZ()));
		}
	}
}
