package com.majinnaibu.bukkitplugins.metropolis;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@Entity()
@Table(name="Metropolis_PlayerHome")
public class PlayerHome extends Plot{
	@NotNull
	private String playerName;
	public String getPlayerName(){return this.playerName;}
	public void setPlayerName(String playerName){this.playerName = playerName;}
		
	public PlayerHome(String owner, BlockVector min, BlockVector max) {
		super("h_" + owner, min, max);
		this.playerName = owner;
	}
	
	public PlayerHome() {
		this.playerName = "";
	}
	
	public PlayerHome(ProtectedRegion homeRegion){
		if(homeRegion instanceof ProtectedCuboidRegion){
			ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) homeRegion;
			if(cuboidRegion.getId().startsWith("h_") && cuboidRegion.getId().length() > 2){
				this.playerName = cuboidRegion.getId().substring(2);
			}else{
				this.playerName = cuboidRegion.getId();
			}
			
			setCuboid(new Cuboid(cuboidRegion.getMinimumPoint(), cuboidRegion.getMaximumPoint()));
		}else if(homeRegion instanceof ProtectedPolygonalRegion){
			ProtectedPolygonalRegion polygonalRegion = (ProtectedPolygonalRegion)homeRegion;
			if(polygonalRegion.getId().startsWith("h_") && polygonalRegion.getId().length() > 2){
				this.playerName = polygonalRegion.getId().substring(2);
			}else{
				this.playerName = polygonalRegion.getId();
			}
			
			setCuboid(new Cuboid(polygonalRegion.getMinimumPoint(), polygonalRegion.getMaximumPoint()));
		}
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof PlayerHome)){
			return super.equals(other);
		}
		
		PlayerHome otherPlayerHome = (PlayerHome)other;
		
		if(!this.playerName.equals(otherPlayerHome.playerName)){
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
}
