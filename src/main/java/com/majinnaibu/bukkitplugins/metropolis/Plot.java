/*
This file is part of Metropolis.

Metropolis is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Metropolis is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Metropolis. If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>.
*/

package com.majinnaibu.bukkitplugins.metropolis;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Plot implements Comparable<Plot> {
  private int _id;

  public int getId() {
    return _id;
  }

  public void setId(int id) {
    _id = id;
  }

  private Cuboid _cuboid;

  public Cuboid getCuboid() {
    return _cuboid;
  }

  public void setCuboid(Cuboid cuboid) {
    _cuboid = cuboid;
  }

  private String _regionName;

  public String getRegionName() {
    return _regionName;
  }

  public void setRegionName(String regionName) {
    _regionName = regionName;
  }

  public Plot(String regionName, BlockVector3 min, BlockVector3 max) {
    _cuboid = new Cuboid(min, max);
    _regionName = regionName;
  }

  public Plot(ProtectedCuboidRegion cuboid) {
    _cuboid = new Cuboid(cuboid.getMinimumPoint(), cuboid.getMaximumPoint());
    _regionName = cuboid.getId();
  }

  public Plot() {
    _cuboid = new Cuboid();
    _regionName = "";
  }

  public BlockVector3 getPlotMin(int roadWidth) {
    return BlockVector3.at(
        _cuboid.minX - roadWidth / 2, _cuboid.minY, _cuboid.minZ - roadWidth / 2);
  }

  public int getRow(int roadWidth, int plotSizeZ) {
    BlockVector3 min = getPlotMin(roadWidth);
    return min.z() / plotSizeZ;
  }

  public int getCol(int roadWidth, int plotSizeX) {
    return getPlotMin(roadWidth).x() / plotSizeX;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Plot)) {
      return super.equals(other);
    }

    Plot otherPlayerHome = (Plot) other;

    if (!getCuboid().equals(otherPlayerHome.getCuboid())) {
      return false;
    }

    return true;
  }

  @Override
  public int compareTo(Plot another) {
    return getCuboid().compareTo(another.getCuboid());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(String.format("{PlayerHome }"));

    return sb.toString();
  }

  public static Plot get(ProtectedRegion region) {
    if (region instanceof ProtectedCuboidRegion) {
      return new Plot((ProtectedCuboidRegion) region);
    } else {
      return null;
    }
  }

  public String toFriendlyString() {
    StringBuilder sb = new StringBuilder();

    sb.append(
        String.format(
            "Metropolis Reserved Plot {min: (%d, %d, %d) max: (%d, %d, %d)}",
            getCuboid().getMinX(),
            getCuboid().getMinY(),
            getCuboid().getMinZ(),
            getCuboid().getMaxX(),
            getCuboid().getMaxY(),
            getCuboid().getMaxZ()));

    return sb.toString();
  }

  public boolean contains(Location bedSpawn) {
    return _cuboid.contains(bedSpawn);
  }

  public Location getViableSpawnLocation(World world) {
    Cuboid cuboid = getCuboid();
    for (int y = cuboid.maxY - 1; y >= cuboid.minY; y--) {
      for (int x = cuboid.minX; x <= cuboid.maxX; x++) {
        for (int z = cuboid.minZ; z <= cuboid.maxZ; z++) {
          Block block = world.getBlockAt(x, y, z);
          Block blockAbove = world.getBlockAt(x, y + 1, z);
          Block blockUnder = world.getBlockAt(x, y - 1, z);
          if (block.getType() == Material.AIR & blockAbove.getType() == Material.AIR
              && blockUnder.getType() != Material.AIR) {
            return new Location(world, x, y, z);
          }
        }
      }
    }

    return null;
  }
}
