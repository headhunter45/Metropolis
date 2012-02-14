package com.majinnaibu.bukkit.plugins.metropolis;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@Entity()
@Table(name="met_home")
public class PlayerHome implements Comparable<PlayerHome>{
	@Id
	private int id;
	
	@NotNull
	private String playerName;
	
	@NotNull
	private Cuboid cuboid;

	@NotNull
	private String regionName;
	
	public PlayerHome(String owner, BlockVector min, BlockVector max) {
		this.cuboid = new Cuboid(min, max);
		this.playerName = owner;
		this.regionName = "h_" + owner;
	}
	
	public PlayerHome() {
		this.cuboid = new Cuboid();
		this.playerName = "";
		this.regionName = "";
	}
	
	public PlayerHome(ProtectedRegion homeRegion){
		if(homeRegion instanceof ProtectedCuboidRegion){
			ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) homeRegion;
			if(cuboidRegion.getId().startsWith("h_") && cuboidRegion.getId().length() > 2){
				this.playerName = cuboidRegion.getId().substring(2);
			}else{
				this.playerName = cuboidRegion.getId();
			}
			
			this.cuboid = new Cuboid(cuboidRegion.getMinimumPoint(), cuboidRegion.getMaximumPoint());
		}else if(homeRegion instanceof ProtectedPolygonalRegion){
			ProtectedPolygonalRegion polygonalRegion = (ProtectedPolygonalRegion)homeRegion;
			if(polygonalRegion.getId().startsWith("h_") && polygonalRegion.getId().length() > 2){
				this.playerName = polygonalRegion.getId().substring(2);
			}else{
				this.playerName = polygonalRegion.getId();
			}
			
			this.cuboid = new Cuboid(polygonalRegion.getMinimumPoint(), polygonalRegion.getMaximumPoint());
		}
	}

	public int getId(){return this.id;}
	public void setId(int id){this.id = id;}
	
	public String getPlayerName(){return this.playerName;}
	public void setPlayerName(String playerName){this.playerName = playerName;}
	
	public Cuboid getCuboid(){return this.cuboid;}
	public void setCuboid(Cuboid cuboid){this.cuboid = cuboid;}

	public String getRegionName(){return this.regionName;}
	public void setRegionName(String regionName){this.regionName = regionName;}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof PlayerHome)){
			return super.equals(other);
		}
		
		PlayerHome otherPlayerHome = (PlayerHome)other;
		
		if(!this.playerName.equals(otherPlayerHome.playerName)){
			return false;
		}
		
		if(!this.cuboid.equals(otherPlayerHome.cuboid)){
			return false;
		}
		
		return true;
	}

	@Override
	public int compareTo(PlayerHome another) {
		return cuboid.compareTo(another.cuboid);
	}

	public BlockVector getPlotMin(int roadWidth) {
		return new BlockVector(this.cuboid.minX - roadWidth/2, this.cuboid.minY, this.cuboid.minZ - roadWidth/2);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("{PlayerHome "));
		
		return sb.toString();
	}
}
