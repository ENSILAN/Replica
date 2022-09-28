package me.nathanfallet.ensilan.replica.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (p.getWorld().getName().equals("Replica") && p.getLocation().getY() > 0) {
				e.setCancelled(true);
			}
		}
	}

}
