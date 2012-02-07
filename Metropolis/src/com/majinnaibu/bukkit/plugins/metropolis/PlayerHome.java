package com.majinnaibu.bukkit.plugins.metropolis;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

@Entity()
@Table(name="met_home")
public class PlayerHome implements Comparable<PlayerHome>{
	@Id
	private int id;
	
	@NotNull
	private String playerName;
	
	@NotNull
	private Cuboid cuboid;

	public PlayerHome(String owner, BlockVector min, BlockVector max) {
		cuboid = new Cuboid(min, max);
		playerName = owner;
	}
	
	public PlayerHome() {
		// TODO Auto-generated constructor stub
	}
	
	public PlayerHome(ProtectedCuboidRegion region){
		if(region.getId().startsWith("h_") && region.getId().length() > 2){
			this.playerName = region.getId().substring(2);
		}else{
			this.playerName = region.getId();
		}
		
		this.cuboid = new Cuboid(region.getMinimumPoint(), region.getMaximumPoint());
	}

	public int getId(){return this.id;}
	public void setId(int id){this.id = id;}
	
	public String getPlayerName(){return this.playerName;}
	public void setPlayerName(String playerName){this.playerName = playerName;}
	
	public Cuboid getCuboid(){return this.cuboid;}
	public void setCuboid(Cuboid cuboid){this.cuboid = cuboid;}

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
}
