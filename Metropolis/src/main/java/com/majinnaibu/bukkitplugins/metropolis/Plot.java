package com.majinnaibu.bukkitplugins.metropolis;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.avaje.ebean.validation.NotNull;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@Entity()
@Table(name="Metropolis_Plot")
public class Plot implements Comparable<Plot>{
	@Id
	private int _id;
	public int getId(){return _id;}
	public void setId(int id){_id = id;}
	
	@NotNull
	private Cuboid _cuboid;
	public Cuboid getCuboid(){return _cuboid;}
	public void setCuboid(Cuboid cuboid){_cuboid = cuboid;}

	@NotNull
	private String _regionName;
	public String getRegionName(){return _regionName;}
	public void setRegionName(String regionName){_regionName = regionName;}
	
	public Plot(String regionName, BlockVector min, BlockVector max){
		_cuboid = new Cuboid(min, max);
		_regionName = regionName;
	}
	
	public Plot(ProtectedCuboidRegion cuboid){
		_cuboid = new Cuboid(cuboid.getMinimumPoint(), cuboid.getMaximumPoint());
		_regionName = cuboid.getId();
	}
	
	public Plot(){
		_cuboid = new Cuboid();
		_regionName = "";
	}

	public BlockVector getPlotMin(int roadWidth) {
		return new BlockVector(_cuboid.minX - roadWidth/2, _cuboid.minY, _cuboid.minZ - roadWidth/2);
	}
	
	public int getRow(int roadWidth, int plotSizeZ){
		BlockVector min = getPlotMin(roadWidth);
		return min.getBlockZ() / plotSizeZ;
	}
	
	public int getCol(int roadWidth, int plotSizeX){
		return getPlotMin(roadWidth).getBlockX() / plotSizeX;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Plot)){
			return super.equals(other);
		}
		
		Plot otherPlayerHome = (Plot)other;
		
		if(!getCuboid().equals(otherPlayerHome.getCuboid())){
			return false;
		}
		
		return true;
	}

	@Override
	public int compareTo(Plot another) {
		return getCuboid().compareTo(another.getCuboid());
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("{PlayerHome }"));
		
		return sb.toString();
	}
	
	public static Plot get(ProtectedRegion region){
		if(region instanceof ProtectedCuboidRegion){
			return new Plot((ProtectedCuboidRegion) region);
		}else{
			return null;
		}
	}
	
	public String toFriendlyString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Metropolis Reserved Plot {min: (%d, %d, %d) max: (%d, %d, %d)}", getCuboid().getMinX(), getCuboid().getMinY(), getCuboid().getMinZ(), getCuboid().getMaxX(), getCuboid().getMaxY(), getCuboid().getMaxZ()));
		
		return sb.toString();
	}
	
	public boolean contains(Location bedSpawn) {
		return _cuboid.contains(bedSpawn);
	}
	
	public Location getViableSpawnLocation(World world) {
		Cuboid cuboid = getCuboid();
		for(int y=cuboid.maxY-1; y>= cuboid.minY; y--){
			for(int x=cuboid.minX; x<= cuboid.maxX; x++){
				for(int z=cuboid.minZ; z<= cuboid.maxZ; z++){
					Block block = world.getBlockAt(x, y, z);
					Block blockAbove = world.getBlockAt(x, y+1, z);
					Block blockUnder = world.getBlockAt(x, y-1, z);
					if(block.getType() == Material.AIR & blockAbove.getType() == Material.AIR && blockUnder.getType() != Material.AIR){
						return new Location(world, x, y, z);
					}
				}
			}
		}
		
		return null;
	}
}
