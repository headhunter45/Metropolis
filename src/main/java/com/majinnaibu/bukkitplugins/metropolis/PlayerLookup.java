package com.majinnaibu.bukkitplugins.metropolis;

import org.bukkit.entity.Player;

final class PlayerLookup {
	private PlayerLookup() {}

	static Player findOnline(String requestedName, Iterable<? extends Player> onlinePlayers) {
		if (requestedName == null) {
			return null;
		}
		for (Player player : onlinePlayers) {
			if (requestedName.equalsIgnoreCase(player.getName())) {
				return player;
			}
		}
		return null;
	}
}