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

public class UserOverride {
  private String _username;
  private int _plotMultiplier;
  private int _maxPlots;

  public String getUsername() {
    return _username;
  }

  public int getPlotMultiplier() {
    return _plotMultiplier;
  }

  public int getMaxPlots() {
    return _maxPlots;
  }

  public UserOverride(String username, int plotMultiplier, int maxPlots) {
    _username = username;
    _plotMultiplier = plotMultiplier;
    _maxPlots = maxPlots;
  }
}
