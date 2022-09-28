package me.nathanfallet.ensilan.replica.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.nathanfallet.ensilan.replica.Replica;

public class PlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Replica.getInstance().initPlayer(e.getPlayer());
	}

}
