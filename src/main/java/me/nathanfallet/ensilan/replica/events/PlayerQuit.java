package me.nathanfallet.ensilan.replica.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.nathanfallet.ensilan.replica.Replica;
import me.nathanfallet.ensilan.replica.utils.ReplicaPlayer;

public class PlayerQuit implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		ReplicaPlayer zp = Replica.getInstance().getPlayer(e.getPlayer().getUniqueId());
		if (zp != null) {
			Replica.getInstance().uninitPlayer(zp);
		}
	}

}
