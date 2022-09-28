package me.nathanfallet.ensilan.replica.utils;

import java.util.UUID;

import org.bukkit.entity.Player;

public class ReplicaPlayer {

	private UUID uuid;
	private int currentGame;
	private boolean buildmode;
	private boolean playing;
	private boolean finish;
	private int plot;

	public ReplicaPlayer(Player p) {
		uuid = p.getUniqueId();
		setCurrentGame(0);
		setBuildmode(false);
		setPlaying(false);
		setFinish(false);
		setPlot(0);
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(int currentGame) {
		this.currentGame = currentGame;
	}

	public boolean isBuildmode() {
		return buildmode;
	}

	public void setBuildmode(boolean buildmode) {
		this.buildmode = buildmode;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public int getPlot() {
		return plot;
	}

	public void setPlot(int plot) {
		this.plot = plot;
	}

}
