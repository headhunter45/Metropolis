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

import com.majinnaibu.bukkitplugins.metropolis.AvenueStairwayLayout.Flight;
import com.majinnaibu.bukkitplugins.metropolis.AvenueStairwayLayout.Step;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;

public final class AvenueStairwayBuilder {
  private AvenueStairwayBuilder() {}

  public static void build(World world, Flight flight, Material stairMaterial) {
    for (Step step : flight.stairBlocks()) {
      Block stairBlock = world.getBlockAt(step.x(), step.y(), step.z());
      stairBlock.setType(stairMaterial, false);
      BlockData blockData = stairBlock.getBlockData();
      if (blockData instanceof Stairs stairs) {
        stairs.setFacing(BlockFace.SOUTH);
        stairs.setHalf(Bisected.Half.BOTTOM);
        stairs.setShape(Stairs.Shape.STRAIGHT);
        stairBlock.setBlockData(stairs, false);
      }
    }

    for (Step backing : flight.invertedBackingBlocks()) {
      Block supportBlock = world.getBlockAt(backing.x(), backing.y(), backing.z());
      supportBlock.setType(stairMaterial, false);
      BlockData supportData = supportBlock.getBlockData();
      if (supportData instanceof Stairs stairs) {
        stairs.setFacing(BlockFace.NORTH);
        stairs.setHalf(Bisected.Half.TOP);
        stairs.setShape(Stairs.Shape.STRAIGHT);
        supportBlock.setBlockData(stairs, false);
      }
    }

    for (Step opening : flight.roadOpenings()) {
      world.getBlockAt(opening.x(), opening.y(), opening.z()).setType(Material.AIR, false);
    }
  }
}
