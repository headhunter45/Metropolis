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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.junit.jupiter.api.Test;

class RoadBlockWriterTest {
  @Test
  void doesNotRewriteAnExistingRoadBlockOrItsClearance() {
    World world = mock(World.class);
    Block roadBlock = mock(Block.class);
    when(world.getBlockAt(4, 62, 8)).thenReturn(roadBlock);
    when(roadBlock.getType()).thenReturn(Material.COBBLESTONE);

    RoadBlockWriter.build(world, 4, 62, 8, Material.COBBLESTONE, Material.STONE, true, 4);

    verify(roadBlock, never()).setType(any(Material.class));
    verify(world, never()).getBlockAt(4, 63, 8);
  }

  @Test
  void fillsMissingRoadBlocksAndPreservesExistingStairTreads() {
    World world = mock(World.class);
    Block missingRoadBlock = mock(Block.class);
    Block stairBlock = mock(Block.class);
    when(world.getBlockAt(4, 62, 8)).thenReturn(missingRoadBlock);
    when(world.getBlockAt(5, 76, 12)).thenReturn(stairBlock);
    when(missingRoadBlock.getType()).thenReturn(Material.AIR);
    when(stairBlock.getType()).thenReturn(Material.COBBLESTONE_STAIRS);
    when(stairBlock.getBlockData()).thenReturn(mock(Stairs.class));

    RoadBlockWriter.build(world, 4, 62, 8, Material.COBBLESTONE, Material.STONE, false, 0);
    RoadBlockWriter.build(world, 5, 76, 12, Material.COBBLESTONE, Material.STONE, false, 4);

    verify(missingRoadBlock).setType(Material.COBBLESTONE);
    verify(stairBlock, never()).setType(any(Material.class));
  }
}
