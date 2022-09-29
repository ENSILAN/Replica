package me.nathanfallet.ensilan.replica.events;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.nathanfallet.ensilan.core.models.AbstractGame.GameState;
import me.nathanfallet.ensilan.replica.Replica;
import me.nathanfallet.ensilan.replica.utils.Game;
import me.nathanfallet.ensilan.replica.utils.ReplicaPlayer;

public class BlockPlace implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getBlock().getWorld().getName().equals("Replica")) {
			ReplicaPlayer zp = Replica.getInstance().getPlayer(e.getPlayer().getUniqueId());
			int z = e.getBlock().getChunk().getZ(), col = 0;
			while (z >= 2) {
				z -= 2;
				col++;
			}
			col++;
			for (Game g : Replica.getInstance().getGames()) {
				for (UUID c : g.getPlayers()) {
					if (e.getPlayer().getUniqueId().equals(c)) {
						if (g.getState().equals(GameState.IN_GAME) && zp.getPlot() == col) {
							if (g.isCompletingPlot(col)) {
								g.breakPlot(col);
								e.getPlayer().getInventory().clear();
								e.getPlayer().updateInventory();
								e.getPlayer().setGameMode(GameMode.SPECTATOR);
								zp.setFinish(true);
								g.mainHandler();
							}
						} else {
							e.setCancelled(true);
						}
						return;
					}
				}
			}
			e.setCancelled(!zp.isBuildmode());
		}
	}

}
