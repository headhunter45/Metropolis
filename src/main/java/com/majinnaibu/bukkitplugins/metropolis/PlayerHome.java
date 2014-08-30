package com.majinnaibu.bukkitplugins.metropolis;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.bukkit.OfflinePlayer;

import com.avaje.ebean.validation.NotNull;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@Entity()
@Table(name="Metropolis_PlayerHome")
public class PlayerHome extends Plot{
	@NotNull
	private UUID playerUUID = null;
	public UUID getPlayerUUID(){return this.playerUUID;}
	public void setPlayerUUID(UUID playerUUID){this.playerUUID = playerUUID;}
	
	private int number;
		
	public PlayerHome(UUID owner, BlockVector min, BlockVector max) {
		super("h_1_" + owner, min, max);
		this.playerUUID = owner;
	}
	
	public PlayerHome(OfflinePlayer owner, BlockVector min, BlockVector max) {
		super("h_1_" + owner.getUniqueId(), min, max);
		this.playerUUID = owner.getUniqueId();
	}
	
	public PlayerHome() {
		this.playerUUID = null;
	}
	
	public PlayerHome(ProtectedRegion homeRegion){
		this.playerUUID = null;
		this.number = 0;
		
		try{
			String rname = homeRegion.getId();
			
			if(rname.startsWith("h_")){
				int secondUnderscore = rname.indexOf('_', 2);
				if(secondUnderscore > 2){
					try{
						this.number = Integer.parseInt(rname.substring(2, secondUnderscore));
						this.playerUUID = UUID.fromString(rname.substring(secondUnderscore+1));
					}catch(Exception ex){
						this.number = 0;
					}
				}else{
					this.number = 0;
				}
				
				setCuboid(new Cuboid(homeRegion.getMinimumPoint(), homeRegion.getMaximumPoint()));
			}
			else{
				throw new RuntimeException("Method not implemented.");
			}
		}catch(Exception ex){
			throw new RuntimeException("Method not implemented.", ex);
		}
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof PlayerHome)){
			return super.equals(other);
		}
		
		PlayerHome otherPlayerHome = (PlayerHome)other;
		
		if(!this.playerUUID.equals(otherPlayerHome.playerUUID)){
			return false;
		}
		
		if(!getCuboid().equals(otherPlayerHome.getCuboid())){
			return false;
		}
		
		return true;
	}

	public String toFriendlyString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Metropolis Home {Owner: %s min: (%d, %d, %d) max: (%d, %d, %d)}", getPlayerUUID(), getCuboid().getMinX(), getCuboid().getMinY(), getCuboid().getMinZ(), getCuboid().getMaxX(), getCuboid().getMaxY(), getCuboid().getMaxZ()));
		
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
