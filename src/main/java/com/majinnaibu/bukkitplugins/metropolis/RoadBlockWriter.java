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

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;

public final class RoadBlockWriter {
  private RoadBlockWriter() {}

  public static void build(
      World world,
      int x,
      int y,
      int z,
      Material roadMaterial,
      Material supportMaterial,
      boolean generateSupports,
      int clearSpaceAbove) {
    Block roadBlock = world.getBlockAt(x, y, z);
    if (roadBlock.getType() == roadMaterial || roadBlock.getBlockData() instanceof Stairs) {
      return;
    }

    roadBlock.setType(roadMaterial);
    if (generateSupports && (roadMaterial == Material.GRAVEL || roadMaterial == Material.SAND)) {
      Block blockUnder = world.getBlockAt(x, y - 1, z);
      if (!isSolid(blockUnder.getType())) {
        blockUnder.setType(supportMaterial);
      }
    }

    for (int clearance = 1; clearance <= clearSpaceAbove; clearance++) {
      Block blockAbove = world.getBlockAt(x, y + clearance, z);
      if (blockAbove.getType() != Material.AIR) {
        blockAbove.setType(Material.AIR);
      }
    }
  }

  private static boolean isSolid(Material material) {
    return material.isBlock()
        && material != Material.AIR
        && material != Material.WATER
        && material != Material.LAVA
        && material != Material.TORCH
        && material != Material.REDSTONE_TORCH;
  }
}
