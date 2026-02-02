package cat.tuplugin.uhc;

import cat.tuplugin.uhc.commands.UHCCommand;
import cat.tuplugin.uhc.managers.*;
import cat.tuplugin.uhc.listeners.GameListener;
import org.bukkit.plugin.java.JavaPlugin;
import cat.tuplugin.uhc.listeners.PlayerListener;

public final class UHCPluginGame extends JavaPlugin {

    private GameManager gameManager;
    private ScatterManager scatterManager;
    private ScoreboardManager scoreboardManager;
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        this.scatterManager = new ScatterManager();
        this.scoreboardManager = new ScoreboardManager(this);
        this.teamManager = new TeamManager();

        getCommand("uhc").setExecutor(new UHCCommand(this));
        getServer().getPluginManager().registerEvents(new GameListener(this), this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("UhcPlugin activat!");
    }

    public GameManager getGameManager() { return gameManager; }
    public ScatterManager getScatterManager() { return scatterManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public TeamManager getTeamManager() { return teamManager; }
}