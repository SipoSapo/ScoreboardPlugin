package cat.tuplugin.uhc;

import cat.tuplugin.uhc.commands.UHCCommand;
import cat.tuplugin.uhc.managers.GameManager;
import cat.tuplugin.uhc.managers.ScatterManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class UHCPlugin extends JavaPlugin {

    private GameManager gameManager;
    private ScatterManager scatterManager;

    @Override
    public void onEnable() {
        // Inicialitzem els Managers
        this.scatterManager = new ScatterManager();
        this.gameManager = new GameManager(this);

        // Registrem la comanda i li passem "this" (el plugin)
        if (getCommand("uhc") != null) {
            getCommand("uhc").setExecutor(new UHCCommand(this));
        }

        getLogger().info("UHC Plugin activat correctament!");
    }

    // Getters per poder accedir als managers des d'altres classes
    public GameManager getGameManager() { return gameManager; }
    public ScatterManager getScatterManager() { return scatterManager; }
}