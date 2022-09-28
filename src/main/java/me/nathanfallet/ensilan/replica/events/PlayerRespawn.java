package me.nathanfallet.ensilan.replica.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.nathanfallet.ensilan.core.models.AbstractGame.GameState;
import me.nathanfallet.ensilan.replica.Replica;
import me.nathanfallet.ensilan.replica.utils.Game;
import me.nathanfallet.ensilan.replica.utils.ReplicaPlayer;

public class PlayerRespawn implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		ReplicaPlayer zp = Replica.getInstance().getPlayer(e.getPlayer().getUniqueId());
		if (zp.getCurrentGame() != 0) {
			Game g = null;
			for (Game g2 : Replica.getInstance().getGames()) {
				if (g2.getGameNumber() == zp.getCurrentGame()) {
					g = g2;
				}
			}
			if (g != null && g.getState().equals(GameState.IN_GAME)) {
				Location l = new Location(Bukkit.getWorld("Replica"),
						4 + Replica.DISTANCE * 16 * (zp.getCurrentGame() - 1), 65, (zp.getPlot() - 1) * 32 + 9);
				l.setYaw(-90);
				e.setRespawnLocation(l);
			}
		}
	}

}
