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

package com.majinnaibu.bukkitplugins.metropolis.eventlisteners;

import com.majinnaibu.bukkitplugins.metropolis.MetropolisPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class DebugListener implements Listener {
  MetropolisPlugin _plugin;

  public DebugListener(MetropolisPlugin plugin) {
    _plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEvent(PlayerInteractEvent event) {
    // event.
  }
}
