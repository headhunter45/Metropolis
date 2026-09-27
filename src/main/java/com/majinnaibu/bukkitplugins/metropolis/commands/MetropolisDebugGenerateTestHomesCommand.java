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

package com.majinnaibu.bukkitplugins.metropolis.commands;

import java.util.logging.Level;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;
import com.majinnaibu.bukkitplugins.metropolis.PlayerHome;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

public class MetropolisDebugGenerateTestHomesCommand implements CommandExecutor {
  private static final long TICKS_BETWEEN_HOMES = 2L;

  private final MetropolisPlugin _plugin;
  private BukkitTask _activeTask;

  public MetropolisDebugGenerateTestHomesCommand(MetropolisPlugin plugin) {
    _plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length != 1) {
      sender.sendMessage("Usage: /metropolis-debug-generatetesthomes <count>");
      return false;
    }

    int numHomes;
    try {
      numHomes = Integer.parseInt(args[0]);
    } catch (NumberFormatException ex) {
      sender.sendMessage("Usage: /metropolis-debug-generatetesthomes <count>");
      return false;
    }

    if (numHomes < 1) {
      sender.sendMessage("Usage: /metropolis-debug-generatetesthomes <count>");
      return false;
    }

    if (_activeTask != null && !_activeTask.isCancelled()) {
      sender.sendMessage("[Metropolis] Test-home generation is already in progress.");
      return false;
    }

    TestHomeBatch batch = new TestHomeBatch(sender, numHomes);
    _activeTask =
        _plugin.getServer().getScheduler().runTaskTimer(_plugin, batch, 1L, TICKS_BETWEEN_HOMES);
    sender.sendMessage(
        "[Metropolis] Queued "
            + numHomes
            + " test homes; generating one every "
            + TICKS_BETWEEN_HOMES
            + " ticks.");
    return true;
  }

  private final class TestHomeBatch implements Runnable {
    private final CommandSender sender;
    private final int requestedHomes;
    private int generatedHomes;
    private int nextTestNumber = 1;
    private boolean finished;

    private TestHomeBatch(CommandSender sender, int requestedHomes) {
      this.sender = sender;
      this.requestedHomes = requestedHomes;
    }

    @Override
    public void run() {
      if (finished) {
        return;
      }

      int testNumber = nextTestNumber++;
      String testName = "Test" + testNumber;
      try {
        OfflinePlayer testPlayer = _plugin.getServer().getOfflinePlayer(testName);
        if (_plugin.getNumPlots(testPlayer.getUniqueId()) > 0) {
          return;
        }

        PlayerHome home = _plugin.generateHome(testPlayer, 1);
        if (home == null) {
          sender.sendMessage(
              "[Metropolis] Could not generate "
                  + testName
                  + "; completed "
                  + generatedHomes
                  + " of "
                  + requestedHomes
                  + " test homes.");
          finish();
          return;
        }

        generatedHomes++;
        if (generatedHomes == requestedHomes) {
          sender.sendMessage("[Metropolis] Generated " + generatedHomes + " test homes.");
          finish();
        }
      } catch (RuntimeException exception) {
        _plugin
            .getLogger()
            .log(Level.SEVERE, "Failed to generate test home " + testName, exception);
        sender.sendMessage(
            "[Metropolis] Test-home generation failed after " + generatedHomes + " homes.");
        finish();
      }
    }

    private void finish() {
      finished = true;
      if (_activeTask != null) {
        _activeTask.cancel();
        _activeTask = null;
      }
    }
  }
}
