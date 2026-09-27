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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class AvenueStairwayLayoutTest {
  @Test
  void centersStairsAcrossTheAvenueAndAlongThePlotLength() {
    Cuboid plot = new Cuboid(2, 62, 2, 33, 71, 33);

    List<AvenueStairwayLayout.Step> steps =
        AvenueStairwayLayout.centeredEastAvenue(plot, 4, 2, 62, 14);

    assertEquals(28, steps.size());
    assertEquals(new AvenueStairwayLayout.Step(35, 63, 12), steps.get(0));
    assertEquals(new AvenueStairwayLayout.Step(36, 63, 12), steps.get(1));
    assertEquals(new AvenueStairwayLayout.Step(35, 64, 13), steps.get(2));
    assertEquals(new AvenueStairwayLayout.Step(36, 76, 25), steps.get(27));
  }

  @Test
  void opensOnlyTheThreeUpperRoadBlocksBeforeTheTopStair() {
    Cuboid plot = new Cuboid(2, 62, 2, 33, 71, 33);

    List<AvenueStairwayLayout.Step> openings =
        AvenueStairwayLayout.upperRoadOpening(plot, 4, 2, 62, 14, 3);

    assertEquals(6, openings.size());
    assertEquals(new AvenueStairwayLayout.Step(35, 76, 22), openings.get(0));
    assertEquals(new AvenueStairwayLayout.Step(35, 76, 23), openings.get(1));
    assertEquals(new AvenueStairwayLayout.Step(35, 76, 24), openings.get(2));
    assertEquals(new AvenueStairwayLayout.Step(36, 76, 24), openings.get(5));
  }

  @Test
  void upperRoadFlightPreservesTheLowerRoadAndPlansInvertedBacking() {
    Cuboid plot = new Cuboid(2, 62, 2, 33, 71, 33);

    AvenueStairwayLayout.Flight flight = AvenueStairwayLayout.forUpperRoad(plot, 4, 2, 62, 14, 3);

    assertEquals(28, flight.stairBlocks().size());
    assertEquals(new AvenueStairwayLayout.Step(35, 63, 12), flight.stairBlocks().getFirst());
    assertEquals(new AvenueStairwayLayout.Step(36, 76, 25), flight.stairBlocks().getLast());
    assertEquals(26, flight.invertedBackingBlocks().size());
    assertEquals(
        new AvenueStairwayLayout.Step(35, 63, 13), flight.invertedBackingBlocks().getFirst());
    assertTrue(flight.stairBlocks().stream().allMatch(step -> step.y() > 62));
    assertTrue(flight.invertedBackingBlocks().stream().allMatch(step -> step.y() > 62));
    assertEquals(6, flight.roadOpenings().size());
    assertTrue(flight.roadOpenings().stream().allMatch(step -> step.y() == 76));
  }

  @Test
  void createsOneCenteredStairFlightPerLogicalPlotOnBothAvenueBorders() {
    Cuboid twoByTwoPlot = new Cuboid(2, 62, 2, 69, 71, 69);

    List<AvenueStairwayLayout.Flight> flights =
        AvenueStairwayLayout.forUpperRoadSegments(twoByTwoPlot, 32, 36, 0, 1, 4, 2, 62, 14, 3);

    assertEquals(4, flights.size());
    assertEquals(
        new AvenueStairwayLayout.Step(71, 63, 12), flights.get(0).stairBlocks().getFirst());
    assertEquals(
        new AvenueStairwayLayout.Step(-1, 63, 12), flights.get(1).stairBlocks().getFirst());
    assertEquals(
        new AvenueStairwayLayout.Step(71, 63, 48), flights.get(2).stairBlocks().getFirst());
    assertEquals(
        new AvenueStairwayLayout.Step(-1, 63, 48), flights.get(3).stairBlocks().getFirst());
    assertTrue(flights.stream().allMatch(flight -> flight.stairBlocks().getLast().y() == 76));
  }

  @Test
  void appliesStairCadenceToEachLogicalPlotSegment() {
    Cuboid twoByTwoPlot = new Cuboid(2, 62, 2, 69, 71, 69);

    List<AvenueStairwayLayout.Flight> flights =
        AvenueStairwayLayout.forUpperRoadSegments(twoByTwoPlot, 32, 36, 0, 2, 4, 2, 62, 14, 3);

    assertEquals(2, flights.size());
    assertEquals(
        new AvenueStairwayLayout.Step(71, 63, 48), flights.getFirst().stairBlocks().getFirst());
    assertEquals(
        new AvenueStairwayLayout.Step(-1, 63, 48), flights.getLast().stairBlocks().getFirst());
  }

  @Test
  void rejectsAStairRunThatCannotFitWithinThePlotLength() {
    Cuboid plot = new Cuboid(2, 62, 2, 33, 71, 13);

    assertTrue(AvenueStairwayLayout.centeredEastAvenue(plot, 4, 2, 62, 14).isEmpty());
  }

  @Test
  void rejectsStairsThatAreNotNarrowerThanTheAvenue() {
    Cuboid plot = new Cuboid(2, 62, 2, 33, 71, 33);

    assertTrue(AvenueStairwayLayout.centeredEastAvenue(plot, 4, 4, 62, 14).isEmpty());
  }
}
