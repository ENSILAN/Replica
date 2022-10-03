package me.nathanfallet.ensilan.replica.utils;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nathanfallet.ensilan.core.Core;
import me.nathanfallet.ensilan.core.models.AbstractGame;
import me.nathanfallet.ensilan.core.models.EnsilanPlayer;
import me.nathanfallet.ensilan.replica.Replica;

public class Game extends AbstractGame {

	// Initializer

	public Game(int gameNumber) {
		super(gameNumber);
	}

	// Methods

	// Countdown before the start of the game. Zero to disable
	@Override
    public int getCountdown() {
		return 30;
	}

    // Number of players required for the game to start
	@Override
    public int getMinPlayers() {
		return 2;
	}

    // Max number of players in the game
	@Override
    public int getMaxPlayers() {
		return 10;
	}

    // Name of the game
	@Override
    public String getGameName() {
		return "Replica";
	}
    
    // Handle the start process of the game
	@Override
    public void start() {
		for (UUID uuid : getPlayers()) {
			ReplicaPlayer zp = Replica.getInstance().getPlayer(uuid);
			zp.setPlaying(true);
		}
		state = GameState.IN_GAME;
		loadDraw();
	}

    // Handle the stop process of the game
	@Override
    public void stop() {
		if (state.equals(GameState.IN_GAME)) {
			state = GameState.FINISHED;
			Player p = Bukkit.getPlayer(getPlayers().get(0));
			if (p != null) {
				Bukkit.broadcastMessage("§e" + p.getName() + "§7 a gagné la partie de Replica !");
				p.getInventory().clear();
				p.updateInventory();
				p.setGameMode(GameMode.SPECTATOR);
				p.sendMessage("§aTu as gagné la partie !");

				EnsilanPlayer ep = Core.getInstance().getPlayer(p.getUniqueId());
				ReplicaPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
				ep.setVictories(ep.getVictories() + 1);
				ep.setScore(ep.getScore() + Replica.SCORE);
				ep.setMoney(ep.getMoney() + Replica.MONEY);
				zp.setVictories(zp.getVictories() + 1);
				zp.setScore(zp.getScore() + Replica.SCORE);
			}
			currentCountValue = 0;
			loadPlots();
			Bukkit.getScheduler().scheduleSyncDelayedTask(Replica.getInstance(), new Runnable() {
				@Override
				public void run() {
					for (UUID uuid : getAllPlayers()) {
						Player player = Bukkit.getPlayer(uuid);
						ReplicaPlayer zp = Replica.getInstance().getPlayer(uuid);
						zp.setCurrentGame(0);
						zp.setPlaying(false);
						zp.setFinish(false);
						zp.setPlot(0);
						player.teleport(Core.getInstance().getSpawn());
						player.setGameMode(GameMode.SURVIVAL);
						player.getInventory().clear();
						player.updateInventory();
					}
					state = GameState.WAITING;
				}
			}, 100);
		}
	}

    // Called every second
	@Override
    public void mainHandler() {
		int number = getPlayers().size();
		int current = 0;
		UUID no = null;
		for (UUID uuid : getPlayers()) {
			ReplicaPlayer zp = Replica.getInstance().getPlayer(uuid);
			if (zp.isFinish()) {
				current++;
			} else {
				no = uuid;
			}
		}
		if (number == 0 || number == 1) {
			stop();
		} else if (current >= number - 1) {
			if (no != null) {
				Player nop = Bukkit.getPlayer(no);
				ReplicaPlayer zp = Replica.getInstance().getPlayer(no);
				zp.setPlaying(false);
				zp.setFinish(false);
				zp.setPlot(0);
				for (UUID uuid : getAllPlayers()) {
					Player p = Bukkit.getPlayer(uuid);
					p.sendMessage("§e" + nop.getName() + "§7 a perdu !");
				}
				nop.getInventory().clear();
				nop.updateInventory();
				nop.setGameMode(GameMode.SPECTATOR);
				nop.sendMessage("§7Vous avez perdu !");
			}
			if (number == 2) {
				stop();
			} else {
				loadDraw();
			}
		}
	}

    // Get players participating in the game (excluding those who lost)
	@Override
	public ArrayList<UUID> getPlayers() {
		ArrayList<UUID> result = new ArrayList<UUID>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			ReplicaPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
			if (zp.getCurrentGame() == getGameNumber()
					&& ((!state.equals(GameState.IN_GAME) && !state.equals(GameState.FINISHED)) || zp.isPlaying())
					&& !zp.isBuildmode()) {
				result.add(p.getUniqueId());
			}
		}
		return result;
	}

    // Get all players of the game, even those who lost
	@Override
	public ArrayList<UUID> getAllPlayers() {
		ArrayList<UUID> result = new ArrayList<UUID>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			ReplicaPlayer zp = Replica.getInstance().getPlayer(p.getUniqueId());
			if (zp.getCurrentGame() == getGameNumber() && !zp.isBuildmode()) {
				result.add(p.getUniqueId());
			}
		}
		return result;
	}

    // Make a player join this game
	@Override
    public void join(Player player, EnsilanPlayer ep) {
		// Make player part of this game
		Replica.getInstance().getPlayer(player.getUniqueId()).setCurrentGame(getGameNumber());
	}

	public Material makeClay(int color) {
		switch (color) {
			case 0:
				return Material.WHITE_TERRACOTTA;
			case 1:
				return Material.ORANGE_TERRACOTTA;
			case 2:
				return Material.MAGENTA_TERRACOTTA;
			case 3:
				return Material.LIGHT_BLUE_TERRACOTTA;
			case 4:
				return Material.YELLOW_TERRACOTTA;
			case 5:
				return Material.LIME_TERRACOTTA;
			case 6:
				return Material.PINK_TERRACOTTA;
			case 7:
				return Material.GRAY_TERRACOTTA;
			case 8:
				return Material.LIGHT_GRAY_TERRACOTTA;
			case 9:
				return Material.CYAN_TERRACOTTA;
			case 10:
				return Material.PURPLE_TERRACOTTA;
			case 11:
				return Material.BLUE_TERRACOTTA;
			case 12:
				return Material.BROWN_TERRACOTTA;
			case 13:
				return Material.GREEN_TERRACOTTA;
			case 14:
				return Material.RED_TERRACOTTA;
			case 15:
				return Material.BLACK_TERRACOTTA;
		}
		return null;
	}

	public void loadPlots() {
		for (int i = 0; i < 20; i++) {
			for (int x = 5; x < 13; x++) {
				for (int z = 5; z < 13; z++) {
					new Location(Bukkit.getWorld("Replica"), x + Replica.DISTANCE * 16 * (gameNumber - 1), 64, z + i * 32)
							.getBlock().setType(Material.AIR);
				}
			}
			for (int y = 0; y < 8; y++) {
				for (int z = 5; z < 13; z++) {
					new Location(Bukkit.getWorld("Replica"), 14 + Replica.DISTANCE * 16 * (gameNumber - 1), 66 + y, z + i * 32)
							.getBlock().setType(Material.AIR);
				}
			}
		}
	}

	public void breakPlot(int col) {
		col--;
		for (int x = 5; x < 13; x++) {
			for (int z = 5; z < 13; z++) {
				new Location(Bukkit.getWorld("Replica"), x + Replica.DISTANCE * 16 * (gameNumber - 1), 64, z + col * 32)
						.getBlock().setType(Material.AIR);
			}
		}
	}

	public void drawPlot(int col, Picture p) {
		col--;
		for (int y = 0; y < 8; y++) {
			for (int z = 5; z < 13; z++) {
				Block b = new Location(Bukkit.getWorld("Replica"), 14 + Replica.DISTANCE * 16 * (gameNumber - 1), 66 + (7 - y),
						z + col * 32).getBlock();
				b.setType(makeClay(p.getBlock(z - 5, y)));
			}
		}
	}

	public boolean isCompletingPlot(int col) {
		col--;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location b = new Location(Bukkit.getWorld("Replica"), 14 + Replica.DISTANCE * 16 * (gameNumber - 1),
						66 + (7 - y), (7 - x) + col * 32 + 5);
				Location b2 = new Location(Bukkit.getWorld("Replica"), 5 + (7 - y) + Replica.DISTANCE * 16 * (gameNumber - 1),
						64, (7 - x) + col * 32 + 5);
				if (!b.getBlock().getType().equals(b2.getBlock().getType())) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean containsColor(int col, int color) {
		col--;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location b = new Location(Bukkit.getWorld("Replica"), 14 + Replica.DISTANCE * 16 * (gameNumber - 1),
						66 + (7 - y), (7 - x) + col * 32 + 5);
				if (b.getBlock().getType().equals(makeClay(color))) {
					return true;
				}
			}
		}
		return false;
	}

	public void loadDraw() {
		Picture p = Replica.getInstance().getRandomPicture();
		loadPlots();
		draw(p, getPlayers().size());
		int plot = 1;
		for (UUID uuid : getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			ReplicaPlayer zp = Replica.getInstance().getPlayer(uuid);
			Location l = new Location(Bukkit.getWorld("Replica"), 4 + Replica.DISTANCE * 16 * (gameNumber - 1), 65,
					(plot - 1) * 32 + 9);
			l.setYaw(-90);
			player.teleport(l);
			player.setGameMode(GameMode.SURVIVAL);
			zp.setPlaying(true);
			zp.setPlot(plot);
			zp.setFinish(false);
			player.getInventory().clear();
			player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
			for (int i = 0; i < 16; i++) {
				if (containsColor(plot, i)) {
					player.getInventory().addItem(new ItemStack(makeClay(i), 64));
				}
			}
			player.updateInventory();
			player.sendMessage("§6Image : " + p.getName());
			plot++;
		}
	}

	public void draw(Picture p, int limit) {
		for (int i = 1; i <= limit; i++) {
			drawPlot(i, p);
		}
	}

}
