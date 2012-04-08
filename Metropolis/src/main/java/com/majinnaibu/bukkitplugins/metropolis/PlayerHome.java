package com.majinnaibu.bukkitplugins.metropolis;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@Entity()
@Table(name="Metropolis_PlayerHome")
public class PlayerHome extends Plot{
	@NotNull
	private String playerName;
	public String getPlayerName(){return this.playerName;}
	public void setPlayerName(String playerName){this.playerName = playerName;}
	
	private int number;
		
	public PlayerHome(String owner, BlockVector min, BlockVector max) {
		super("h_" + owner, min, max);
		this.playerName = owner;
	}
	
	public PlayerHome() {
		this.playerName = "";
	}
	
	public PlayerHome(ProtectedRegion homeRegion){
		try{
			String rname = homeRegion.getId();
			
			if(rname.startsWith("h_")){
				int secondUnderscore = rname.indexOf('_', 2);
				if(secondUnderscore > 2){
					try{
						this.number = Integer.parseInt(rname.substring(2, secondUnderscore));
						this.playerName = rname.substring(secondUnderscore+1);
					}catch(Exception ex){
						this.number = 0;
					}
				}else{
					this.number = 0;
					this.playerName = rname.substring(2);
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
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(int number){
		this.number = number;
	}
}
