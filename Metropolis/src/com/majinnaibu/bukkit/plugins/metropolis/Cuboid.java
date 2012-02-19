package com.majinnaibu.bukkit.plugins.metropolis;

import java.util.logging.Logger;

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

	public Cuboid() {
		this.minX = 0;
		this.minY = 0;
		this.minZ = 0;
		this.maxX = 0;
		this.maxY = 0;
		this.maxZ = 0;
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

	public static int compareBlockVectors(BlockVector v1, BlockVector v2){
		Logger log = Logger.getLogger("Minecraft");
		
		if(v1 == null){
			if(v2 == null){
				log.info("in Cuboid.compareBlockVectors v1 and v2 are null");
				return 0;
			}else{
				log.info("in Cubiod.compareBlockVectors v1 is null");
				return -1;
			}
		}else if(v2 == null){
			log.info("in Cubiod.compareBlockVectors v2 is null");
			return 1;
		}
		log.info(String.format("v1.x: %d, v1.y: %d, v1.z: %d, v2.x: %d, v2.y: %d, v2.z: %d", v1.getBlockX(), v1.getBlockY(), v1.getBlockZ(), v2.getBlockX(), v2.getBlockY(), v2.getBlockZ()));
		if(v1.getBlockX() < v2.getBlockX()){
			return -1;
		}else if(v1.getBlockX() > v2.getBlockX()){
			return 1;
		}else if(v1.getBlockZ() < v2.getBlockZ()){
			return -1;
		}else if(v1.getBlockZ() > v2.getBlockZ()){
			return 1;
		}else if(v1.getBlockY() < v2.getBlockY()){
			return -1;
		}else if(v1.getBlockY() > v2.getBlockY()){
			return 1;
		}else{
			return 0;
		}
	}
	
	public static boolean isBlockLessThan(BlockVector v1, BlockVector v2) {
		return compareBlockVectors(v1, v2) < 0;
	}
	
	public Cuboid inset(int amount){
		return inset(amount, amount, amount);
	}
	
	public Cuboid inset(int x, int z){
		return inset(x, 0, z);
	}
	
	public Cuboid inset(int x, int y, int z){
		return new Cuboid(this.minX + x, this.minY, this.minZ + z, this.maxX - x, this.maxY, this.maxZ - z);
	}

	public int getVolume() {
		return (this.maxX - this.minX) * (this.maxY - this.minY) * (this.maxZ - this.minZ);
	}
	public int getMinX(){return minX;}
	public void setMinX(int minX){this.minX = minX;}
	public int getMinY(){return minY;}
	public void setMinY(int minY){this.minY = minY;}
	public int getMinZ(){return minZ;}
	public void setMinZ(int minZ){this.minZ = minZ;}
	public int getMaxX(){return maxX;}
	public void setMaxX(int maxX){this.maxX = maxX;}
	public int getMaxY(){return maxY;}
	public void setMaxY(int maxY){this.maxY = maxY;}
	public int getMaxZ(){return maxZ;}
	public void setMaxZ(int maxZ){this.maxZ = maxZ;}

	public Cuboid outset(int amount) {
		return outset(amount, amount, amount);
	}

	public Cuboid outset(int x, int z){
		return outset(x, 0, z);
	}
	
	private Cuboid outset(int x, int y, int z) {
		return new Cuboid(this.minX - x, this.minY - y, this.minZ - z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}
	
}
