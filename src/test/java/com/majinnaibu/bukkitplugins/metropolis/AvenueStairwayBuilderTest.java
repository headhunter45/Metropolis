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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.majinnaibu.bukkitplugins.metropolis.AvenueStairwayLayout.Flight;
import com.majinnaibu.bukkitplugins.metropolis.AvenueStairwayLayout.Step;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.junit.jupiter.api.Test;

class AvenueStairwayBuilderTest {
  @Test
  void buildsStreetStairsFacingEastWithWestFacingInvertedBacking() {
    Cuboid plot = new Cuboid(2, 62, 2, 33, 71, 33);
    Flight flight =
        AvenueStairwayLayout.forUpperStreetSegments(plot, 32, 36, 0, 1, 4, 2, 62, 14, 3).getFirst();
    World world = mock(World.class);
    Map<Step, Block> blocks = new HashMap<>();
    Map<Step, Stairs> blockData = new HashMap<>();
    when(world.getBlockAt(anyInt(), anyInt(), anyInt()))
        .thenAnswer(
            invocation -> {
              Step position =
                  new Step(
                      invocation.getArgument(0),
                      invocation.getArgument(1),
                      invocation.getArgument(2));
              return blocks.computeIfAbsent(
                  position,
                  ignored -> {
                    Block block = mock(Block.class);
                    Stairs stairs = mock(Stairs.class);
                    when(block.getBlockData()).thenReturn(stairs);
                    blockData.put(position, stairs);
                    return block;
                  });
            });

    AvenueStairwayBuilder.build(world, flight, Material.COBBLESTONE_STAIRS);

    verify(blockData.get(flight.stairBlocks().getFirst())).setFacing(BlockFace.EAST);
    verify(blockData.get(flight.invertedBackingBlocks().getFirst())).setFacing(BlockFace.WEST);
  }

  @Test
  void buildsAscendingAndInvertedStairsAndClearsOnlyThePlannedOpening() {
    Cuboid plot = new Cuboid(2, 62, 2, 33, 71, 33);
    Flight flight = AvenueStairwayLayout.forUpperRoad(plot, 4, 2, 62, 14, 3);
    World world = mock(World.class);
    Map<Step, Block> blocks = new HashMap<>();
    Map<Step, Stairs> blockData = new HashMap<>();
    when(world.getBlockAt(anyInt(), anyInt(), anyInt()))
        .thenAnswer(
            invocation -> {
              Step position =
                  new Step(
                      invocation.getArgument(0),
                      invocation.getArgument(1),
                      invocation.getArgument(2));
              return blocks.computeIfAbsent(
                  position,
                  ignored -> {
                    Block block = mock(Block.class);
                    Stairs stairs = mock(Stairs.class);
                    when(block.getBlockData()).thenReturn(stairs);
                    blockData.put(position, stairs);
                    return block;
                  });
            });

    AvenueStairwayBuilder.build(world, flight, Material.COBBLESTONE_STAIRS);

    for (Step stair : flight.stairBlocks()) {
      verify(blocks.get(stair)).setType(Material.COBBLESTONE_STAIRS, false);
    }
    for (Step backing : flight.invertedBackingBlocks()) {
      verify(blocks.get(backing)).setType(Material.COBBLESTONE_STAIRS, false);
    }
    for (Step opening : flight.roadOpenings()) {
      verify(blocks.get(opening)).setType(Material.AIR, false);
    }

    Step firstStair = flight.stairBlocks().getFirst();
    Stairs firstStairData = blockData.get(firstStair);
    assertNotNull(firstStairData);
    verify(firstStairData).setFacing(BlockFace.SOUTH);
    verify(firstStairData).setHalf(Bisected.Half.BOTTOM);

    Step firstInvertedBacking = flight.invertedBackingBlocks().getFirst();
    Stairs invertedBackingData = blockData.get(firstInvertedBacking);
    assertNotNull(invertedBackingData);
    verify(invertedBackingData).setFacing(BlockFace.NORTH);
    verify(invertedBackingData).setHalf(Bisected.Half.TOP);

    verify(world, never()).getBlockAt(anyInt(), eq(62), anyInt());
  }
}
