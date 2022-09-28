package me.nathanfallet.ensilan.replica.events;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.nathanfallet.ensilan.core.models.AbstractGame.GameState;
import me.nathanfallet.ensilan.replica.Replica;
import me.nathanfallet.ensilan.replica.utils.Game;
import me.nathanfallet.ensilan.replica.utils.ReplicaPlayer;

public class BlockBreak implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getWorld().getName().equals("Replica")) {
			ReplicaPlayer zp = Replica.getInstance().getPlayer(e.getPlayer().getUniqueId());
			if (zp != null && zp.isBuildmode()) {
				e.setCancelled(false);
				return;
			}
			if (e.getBlock().getLocation().getBlockY() != 64) {
				e.setCancelled(true);
				return;
			}
			if (e.getBlock().getLocation().getBlockZ() < 0 || e.getBlock().getLocation().getBlockZ() > 320) {
				e.setCancelled(true);
				return;
			}
			if (!e.getBlock().getType().toString().endsWith("_TERRACOTTA")) {
				e.setCancelled(true);
				return;
			}
			int z = e.getBlock().getChunk().getZ(), col = 0;
			while (z >= 2) {
				z -= 2;
				col++;
			}
			col++;
			for (Game g : Replica.getInstance().getGames()) {
				for (UUID c : g.getPlayers()) {
					if (e.getPlayer().getUniqueId().equals(c)) {
						if (g.getState().equals(GameState.IN_GAME)) {
							if (zp.getPlot() == col) {
								e.setCancelled(false);
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
						} else {
							e.setCancelled(true);
						}
						return;
					}
				}
			}
			e.setCancelled(true);
		}
	}

}
