package me.nathanfallet.ensilan.replica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.nathanfallet.ensilan.core.Core;
import me.nathanfallet.ensilan.core.interfaces.LeaderboardGenerator;
import me.nathanfallet.ensilan.core.interfaces.ScoreboardGenerator;
import me.nathanfallet.ensilan.core.models.EnsilanPlayer;
import me.nathanfallet.ensilan.replica.commands.Cmd;
import me.nathanfallet.ensilan.replica.events.BlockBreak;
import me.nathanfallet.ensilan.replica.events.BlockPlace;
import me.nathanfallet.ensilan.replica.events.EntityDamage;
import me.nathanfallet.ensilan.replica.events.PlayerCommandPreprocess;
import me.nathanfallet.ensilan.replica.events.PlayerJoin;
import me.nathanfallet.ensilan.replica.events.PlayerQuit;
import me.nathanfallet.ensilan.replica.events.PlayerRespawn;
import me.nathanfallet.ensilan.replica.utils.Game;
import me.nathanfallet.ensilan.replica.utils.Picture;
import me.nathanfallet.ensilan.replica.utils.ReplicaGenerator;
import me.nathanfallet.ensilan.replica.utils.ReplicaPlayer;

public class Replica extends JavaPlugin {

	public static final int DISTANCE = 5;
	public static final long SCORE = 10;
	public static final long MONEY = 10;
	private static Replica instance;

	public static Replica getInstance() {
		return instance;
	}

	private ArrayList<ReplicaPlayer> players = new ArrayList<ReplicaPlayer>();
	private ArrayList<Picture> pictures = new ArrayList<Picture>();
	private ArrayList<Game> games = new ArrayList<Game>();

	public ReplicaPlayer getPlayer(UUID uuid) {
		for (ReplicaPlayer current : players) {
			if (current.getUuid().equals(uuid)) {
				return current;
			}
		}
		return null;
	}

	public void initPlayer(Player p) {
		players.add(new ReplicaPlayer(p));
	}

	public void uninitPlayer(ReplicaPlayer p) {
		if (players.contains(p)) {
			players.remove(p);
		}
	}

	public Picture getRandomPicture() {
		Random r = new Random();
		return pictures.get(r.nextInt(pictures.size()));
	}

	public ArrayList<Game> getGames() {
		return games;
	}

	public void onEnable() {
		// Set current instance
		instance = this;

		// Check connection
		if (!initDatabase()) {
			return;
		}

		// Configuration stuff
		saveDefaultConfig();
		reloadConfig();

		// Load world
		WorldCreator w = new WorldCreator("Replica");
		w.type(WorldType.FLAT);
		w.generator(new ReplicaGenerator());
		w.createWorld();
		Bukkit.getWorld("Replica").setDifficulty(Difficulty.PEACEFUL);
		Bukkit.getWorld("Replica").setSpawnLocation(-1000, 0, 0);
		Bukkit.getWorld("Replica").setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		Bukkit.getWorld("Replica").setTime(0);

		// Init players
		for (Player p : Bukkit.getOnlinePlayers()) {
			initPlayer(p);
		}

		// Load games
		games.clear();
		int i = 1, ga = getConfig().getInt("games-amount");
		while (i <= ga) {
			games.add(new Game(i));
			i++;
		}
		if (games.size() < 1) {
			getLogger().severe("You have to add one game or more to use this plugin !");
			getLogger().severe("Vous devez au moins ajoutez une partie pour faire fonctionner le plugin !");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		for (Game g : games) {
			Core.getInstance().getGames().add(g);
			g.loadPlots();
		}

		// Load pictures
		pictures.clear();
		ConfigurationSection pf = getConfig().getConfigurationSection("pictures");
		if (pf != null) {
			for (String s : pf.getKeys(false)) {
				Picture p = new Picture(pf.getString(s + ".name"));
				String[] blocks = pf.getString(s + ".blocks").split(";");
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						p.setBlock(Integer.parseInt(blocks[y * 8 + x]), x, y);
					}
				}
				pictures.add(p);
			}
		}
		if (pictures.size() < 1) {
			getLogger().severe("You have to add one picture or more to use this plugin !");
			getLogger().severe("Vous devez au moins ajoutez une image pour faire fonctionner le plugin !");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Register events
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerQuit(), this);
		pm.registerEvents(new PlayerRespawn(), this);
		pm.registerEvents(new EntityDamage(), this);
		pm.registerEvents(new BlockPlace(), this);
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new PlayerCommandPreprocess(), this);

		// Register command
		getCommand("replica").setExecutor(new Cmd());

		// Initialize leaderboards
		Core.getInstance().getLeaderboardGenerators().put("replica_score", new LeaderboardGenerator() {
			@Override
			public List<String> getLines(int limit) {
				ArrayList<String> lines = new ArrayList<String>();

				try {
					// Fetch data to MySQL Database
					Statement state = Core.getInstance().getConnection().createStatement();
					ResultSet result = state.executeQuery(
							"SELECT name, replica_players.score FROM replica_players " +
									"INNER JOIN players ON replica_players.uuid = players.uuid " +
									"WHERE replica_players.score > 0 " +
									"ORDER BY replica_players.score DESC " +
									"LIMIT " + limit);

					// Set lines
					while (result.next()) {
						lines.add(
								result.getString("name") +
										ChatColor.GOLD + " - " + ChatColor.YELLOW +
										result.getInt("score") + " points");
					}
					result.close();
					state.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				return lines;
			}

			@Override
			public String getTitle() {
				return "Score au Replica";
			}
		});
		Core.getInstance().getLeaderboardGenerators().put("replica_victories", new LeaderboardGenerator() {
			@Override
			public List<String> getLines(int limit) {
				ArrayList<String> lines = new ArrayList<String>();

				try {
					// Fetch data to MySQL Database
					Statement state = Core.getInstance().getConnection().createStatement();
					ResultSet result = state.executeQuery(
							"SELECT name, replica_players.victories FROM replica_players " +
									"INNER JOIN players ON replica_players.uuid = players.uuid " +
									"WHERE replica_players.victories > 0 " +
									"ORDER BY replica_players.victories DESC " +
									"LIMIT " + limit);

					// Set lines
					while (result.next()) {
						lines.add(
								result.getString("name") +
										ChatColor.GOLD + " - " + ChatColor.YELLOW +
										result.getInt("victories") + " victoires");
					}
					result.close();
					state.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				return lines;
			}

			@Override
			public String getTitle() {
				return "Victoires au Replica";
			}
		});

		// Initialize scoreboard
		Core.getInstance().getScoreboardGenerators().add(new ScoreboardGenerator() {
			@Override
			public List<String> generateLines(Player player, EnsilanPlayer ep) {
				ArrayList<String> lines = new ArrayList<String>();
				ReplicaPlayer zp = getPlayer(player.getUniqueId());

				for (Game g : getGames()) {
					if (g.getGameNumber() == zp.getCurrentGame()) {
						lines.add("§c");
						lines.add("§c§lJoueurs");
						lines.add("§f" + g.getPlayers().size() + "/" + g.getMaxPlayers());
						lines.add("§d");
						lines.add("§d§lStatut :");
						lines.add("§f" + g.getGameDescription());
					}
				}

				return lines;
			}
		});
	}

	public void onDisable() {
		for (Game g : games) {
			for (UUID uuid : g.getAllPlayers()) {
				Player p = Bukkit.getPlayer(uuid);
				p.sendMessage("§cReload du serveur : la partie s'est arrêtée");
				p.teleport(Core.getInstance().getSpawn());
				p.setGameMode(GameMode.SURVIVAL);
				p.getInventory().clear();
				p.updateInventory();
			}
		}
		players.clear();
		games.clear();
	}

	// Initialize database
	private boolean initDatabase() {
		try {
			Statement create = Core.getInstance().getConnection().createStatement();
			create.executeUpdate("CREATE TABLE IF NOT EXISTS `replica_players` (" +
					"`uuid` varchar(255) NOT NULL," +
					"`score` bigint NOT NULL DEFAULT '0'," +
					"`victories` bigint NOT NULL DEFAULT '0'," +
					"PRIMARY KEY (`uuid`)" +
					")");
			create.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
