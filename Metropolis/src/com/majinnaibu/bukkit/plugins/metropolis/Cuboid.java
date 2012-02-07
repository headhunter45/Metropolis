package com.majinnaibu.bukkit.plugins.metropolis;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.sk89q.worldedit.BlockVector;

@Entity
public class Cuboid implements Comparable<Cuboid> {
	@Id
	private int id;
	
	public int minX;
	public int minY;
	public int minZ;
	
	public int maxX;
	public int maxY;
	public int maxZ;
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public Cuboid(int minX, int minY, int minZ, int maxX, int maxY, int maxZ){
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public Cuboid(BlockVector min, BlockVector max) {
		this.minX = min.getBlockX();
		this.minY = min.getBlockY();
		this.minZ = min.getBlockZ();
		this.maxX = max.getBlockX();
		this.maxY = max.getBlockY();
		this.maxZ = max.getBlockZ();
	}

	public BlockVector getMin(){
		return new BlockVector(minX, minY, minZ);
	}
	
	public BlockVector getMax(){
		return new BlockVector(maxX, maxY, maxZ);
	}

	@Override
	public int compareTo(Cuboid o) {
		BlockVector min = getMin();
		BlockVector otherMin = o.getMin();
		
		if(min.getBlockX() < otherMin.getBlockX()){
			return -1;
		}else if(min.getBlockX() > otherMin.getBlockX()){
			return 1;
		}else if(min.getBlockZ() < otherMin.getBlockZ()){
			return -1;
		}else if(min.getBlockZ() > otherMin.getBlockZ()){
			return 1;
		}else if(min.getBlockY() < otherMin.getBlockY()){
			return -1;
		}else if(min.getBlockY() > otherMin.getBlockY()){
			return 1;
		}else{
			return 0;
		}
	}

	public static int compareBlockVectors(BlockVector min, BlockVector otherMin){
		if(min.getBlockX() < otherMin.getBlockX()){
			return -1;
		}else if(min.getBlockX() > otherMin.getBlockX()){
			return 1;
		}else if(min.getBlockZ() < otherMin.getBlockZ()){
			return -1;
		}else if(min.getBlockZ() > otherMin.getBlockZ()){
			return 1;
		}else if(min.getBlockY() < otherMin.getBlockY()){
			return -1;
		}else if(min.getBlockY() > otherMin.getBlockY()){
			return 1;
		}else{
			return 0;
		}
	}
	
	public static boolean isBlockLessThan(BlockVector v1, BlockVector v2) {
		return compareBlockVectors(v1, v2) < 0;
	}
	
	public Cuboid inset(int x, int z){
		return inset(x, 0, z);
	}
	
	public Cuboid inset(int x, int y, int z){
		return new Cuboid(this.minX + x, this.minY, this.minZ + z, this.maxX - x, this.maxY, this.maxZ - z);
	}
}
