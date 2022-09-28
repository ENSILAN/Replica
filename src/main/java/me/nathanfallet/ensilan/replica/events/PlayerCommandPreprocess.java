package me.nathanfallet.ensilan.replica.events;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.nathanfallet.ensilan.core.models.AbstractGame.GameState;
import me.nathanfallet.ensilan.replica.Replica;
import me.nathanfallet.ensilan.replica.utils.Game;
import me.nathanfallet.ensilan.replica.utils.ReplicaPlayer;

public class PlayerCommandPreprocess implements Listener {

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		ReplicaPlayer zp = Replica.getInstance().getPlayer(e.getPlayer().getUniqueId());
		if (zp.getCurrentGame() != 0) {
			for (Game g : Replica.getInstance().getGames()) {
				if (g.getGameNumber() == zp.getCurrentGame() && g.getState().equals(GameState.IN_GAME)) {
					for (UUID uuid : g.getAllPlayers()) {
						if (e.getPlayer().getUniqueId().equals(uuid)) {
							if (!e.getMessage().equalsIgnoreCase("/replica leave")) {
								e.setCancelled(true);
								e.getPlayer().sendMessage(
										"§cVous ne pouvez utiliser que la commande &4/replica leave &cpendant une partie !");
							}
							return;
						}
					}
				}
			}
		}
	}

}
