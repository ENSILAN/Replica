package me.nathanfallet.ensilan.replica.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nathanfallet.ensilan.core.Core;
import me.nathanfallet.ensilan.replica.Replica;
import me.nathanfallet.ensilan.replica.utils.ReplicaPlayer;

public class Cmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Check that args are specified
		if (args.length != 0) {
			// Go to command
			if (args[0].equalsIgnoreCase("goto")) {
				// Permission check
				if (sender.hasPermission("replica.admin")) {
					// Convert to player
					if (sender instanceof Player) {
						// Teleport
						Player p = (Player) sender;
						p.sendMessage("§cVous avez été téléporté dans le monde du Replica !");
						p.teleport(new Location(Bukkit.getWorld("Replica"), 3.5, 65, 4.5));
					} else {
						// Error
						sender.sendMessage("§cVous devez être un joueur pour éxecuter cette commande !");
					}
				} else {
					// Error
					sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande !");
				}
			}
			// Build mode command
			else if (args[0].equalsIgnoreCase("buildmode")) {
				// Permission check
				if (sender.hasPermission("replica.admin")) {
					// Convert to player
					if (sender instanceof Player) {
						Player p = (Player) sender;
						ReplicaPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
						if (zp != null) {
							// Set buildmode
							zp.setBuildmode(!zp.isBuildmode());
							p.sendMessage("§cLe build mode à été " + (zp.isBuildmode() ? "activé" : "désactivé"));
						}
					} else {
						// Error
						sender.sendMessage("§cVous devez être un joueur pour éxecuter cette commande !");
					}
				} else {
					// Error
					sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande !");
				}
			}
			// Leave command
			else if (args[0].equalsIgnoreCase("leave")) {
				// Convert to player
				if (sender instanceof Player) {
					Player p = (Player) sender;
					ReplicaPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
					if (zp.getCurrentGame() != 0) {
						// Leave game
						zp.setCurrentGame(0);
						zp.setPlaying(false);
						zp.setFinish(false);
						zp.setPlot(0);
						p.teleport(Core.getInstance().getSpawn());
						p.setGameMode(GameMode.SURVIVAL);
						p.getInventory().clear();
						p.updateInventory();
					} else {
						// Error
						sender.sendMessage("§cVous ne faite partie d'aucune partie !");
					}
				} else {
					// Error
					sender.sendMessage("§cVous devez être un joueur pour éxecuter cette commande !");
				}
			}
			// Command not found, send help
			else {
				sendHelp(sender, label);
			}
		} else {
			sendHelp(sender, label);
		}
		return true;
	}

	public void sendHelp(CommandSender sender, String label) {
		if (sender.hasPermission("replica.admin")) {
			sender.sendMessage(
				"§6/" + label + " goto§f: Aller dans le monde du Replica\n" +
				"§6/" + label + " buildmode§f: Activer/désactiver le build mode\n"
			);
		}
		sender.sendMessage("§6/" + label + " leave§f: Quitter une partie");
	}

}
