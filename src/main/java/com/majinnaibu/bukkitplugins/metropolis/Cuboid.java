package com.majinnaibu.bukkitplugins.metropolis;


import org.bukkit.Location;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class Cuboid implements Comparable<Cuboid> {
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
	
	public Cuboid(BlockVector3 min, BlockVector3 max) {
		this.minX = min.x();
		this.minY = min.y();
		this.minZ = min.z();
		this.maxX = max.x();
		this.maxY = max.y();
		this.maxZ = max.z();
	}

	public Cuboid() {
		this.minX = 0;
		this.minY = 0;
		this.minZ = 0;
		this.maxX = 0;
		this.maxY = 0;
		this.maxZ = 0;
	}

	public Cuboid(Region selection) {
		this.minX = selection.getMinimumPoint().x();
		this.minY = selection.getMinimumPoint().y();
		this.minZ = selection.getMinimumPoint().z();
		this.maxX = selection.getMaximumPoint().x();
		this.maxY = selection.getMaximumPoint().y();
		this.maxZ = selection.getMaximumPoint().z();
	}

	public BlockVector3 getMin(){
		return BlockVector3.at(minX, minY, minZ);
	}
	
	public BlockVector3 getMax(){
		return BlockVector3.at(maxX, maxY, maxZ);
	}

	@Override
	public int compareTo(Cuboid o) {
		BlockVector3 min = getMin();
		BlockVector3 otherMin = o.getMin();
		
		if(min.x() < otherMin.x()){
			return -1;
		}else if(min.x() > otherMin.x()){
			return 1;
		}else if(min.z() < otherMin.z()){
			return -1;
		}else if(min.z() > otherMin.z()){
			return 1;
		}else if(min.y() < otherMin.y()){
			return -1;
		}else if(min.y() > otherMin.y()){
			return 1;
		}else{
			return 0;
		}
	}

	public static int compareBlockVectors(BlockVector3 v1, BlockVector3 v2){
		if(v1 == null){
			if(v2 == null){
				return 0;
			}else{
				return -1;
			}
		}else if(v2 == null){
			return 1;
		}
		if(v1.x() < v2.x()){
			return -1;
		}else if(v1.x() > v2.x()){
			return 1;
		}else if(v1.z() < v2.z()){
			return -1;
		}else if(v1.z() > v2.z()){
			return 1;
		}else if(v1.y() < v2.y()){
			return -1;
		}else if(v1.y() > v2.y()){
			return 1;
		}else{
			return 0;
		}
	}
	
	public static boolean isBlockLessThan(BlockVector3 v1, BlockVector3 v2) {
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
	
	public Cuboid outset(int x, int y, int z) {
		return new Cuboid(this.minX - x, this.minY - y, this.minZ - z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}
	
	public int getCenterX(){
		return (this.minX + this.maxX) /2;
	}
	
	public int getCenterY(){
		return (this.minY + this.maxY)/2;
	}
	
	public int getCenterZ(){
		return (this.minZ + this.maxZ)/2;
	}

	public boolean intersects(Cuboid other) {
		if (this.maxX >= other.minX && this.minX <= other.maxX)
        {
            if (this.maxZ >= other.minZ && this.minZ <= other.maxZ)
            {
                if (this.maxY >= other.minY && this.minY <= other.maxY)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
	}

	public boolean contains(Location bedSpawn) {
		if(this.minX > bedSpawn.getBlockX() || this.maxX < bedSpawn.getBlockX()){
			return false;
		}else if(this.minZ > bedSpawn.getBlockZ() || this.maxZ < bedSpawn.getBlockZ()){
			return false;
		}else if(this.minY > bedSpawn.getBlockY() || this.maxY < bedSpawn.getBlockY()){
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return String.format("{Cuboid minX=%d, minY=%d, minZ=%d, maxX=%d, maxY=%d, maxZ=%d}", minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	
}
