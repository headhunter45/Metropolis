package com.majinnaibu.bukkitplugins.metropolis;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.avaje.ebean.validation.NotNull;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@Entity()
@Table(name="Metropolis_PlayerHome")
public class PlayerHome extends Plot{
	private UUID playerId;
	@NotNull
	private String playerName;
	public UUID getPlayerId(){return this.playerId;}
	public String getPlayerName(){return this.playerName;}
	public void setPlayerName(String playerName){this.playerName = playerName;}
	
	private int number;
		
	public PlayerHome(UUID ownerId, String ownerName, int homeNumber, BlockVector min, BlockVector max) {
		super(String.format("h_%d_%s", homeNumber, ownerId), min, max);
		this.playerId = ownerId;
		this.playerName = ownerName;
		this.number = homeNumber;
	}
	
	public PlayerHome() {
		this.playerId = null;
		this.playerName = "";
	}
	
	public PlayerHome(ProtectedRegion homeRegion){
		String regionName = homeRegion.getId();
		if (!regionName.startsWith("h_")) {
			throw new IllegalArgumentException("Not a Metropolis home region: " + regionName);
		}
		String regionOwner = regionName.substring(2);
		int separator = regionOwner.indexOf('_');
		if (separator > 0) {
			try {
				this.number = Integer.parseInt(regionOwner.substring(0, separator));
				regionOwner = regionOwner.substring(separator + 1);
			} catch (NumberFormatException ex) {
				this.number = 1;
			}
		} else {
			this.number = 1;
		}
		try {
			this.playerId = UUID.fromString(regionOwner);
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
			this.playerName = offlinePlayer.getName() == null ? regionOwner : offlinePlayer.getName();
		} catch (IllegalArgumentException ex) {
			Set<UUID> ownerIds = homeRegion.getOwners().getUniqueIds();
			this.playerId = ownerIds.isEmpty()
					? Bukkit.getOfflinePlayer(regionOwner).getUniqueId()
					: ownerIds.iterator().next();
			this.playerName = regionOwner;
		}
		setCuboid(new Cuboid(homeRegion.getMinimumPoint(), homeRegion.getMaximumPoint()));
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof PlayerHome)){
			return super.equals(other);
		}
		
		PlayerHome otherPlayerHome = (PlayerHome)other;
		
		if(!java.util.Objects.equals(this.playerId, otherPlayerHome.playerId)){
			return false;
		}
		
		if(!getCuboid().equals(otherPlayerHome.getCuboid())){
			return false;
		}
		
		return true;
	}

	public String toFriendlyString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Metropolis Home {Owner: %s min: (%d, %d, %d) max: (%d, %d, %d)}", getPlayerName(), getCuboid().getMinX(), getCuboid().getMinY(), getCuboid().getMinZ(), getCuboid().getMaxX(), getCuboid().getMaxY(), getCuboid().getMaxZ()));
		
		return sb.toString();
	}
	
	public static PlayerHome get(ProtectedRegion homeRegion){
		if(homeRegion instanceof ProtectedCuboidRegion){
			return new PlayerHome((ProtectedCuboidRegion) homeRegion);
		}else{
			return null;
		}
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(int number){
		this.number = number;
	}
}
