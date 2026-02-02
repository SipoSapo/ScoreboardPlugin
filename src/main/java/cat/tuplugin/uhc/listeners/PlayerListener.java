package cat.tuplugin.uhc.listeners;

import cat.tuplugin.uhc.UHCPluginGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements Listener {

    private final UHCPluginGame plugin;

    public PlayerListener(UHCPluginGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // 1. Mirem si la partida està activa
        if (!plugin.getGameManager().isPartidaActiva()) return;

        Player victim = event.getEntity();

        // 2. Fem que reaparegui automàticament (sense botó de Respawn)
        // Nota: Això requereix que el servidor estigui en Paper o Spigot recent
        victim.setHealth(20.0); // El curem per evitar el bucle de mort

        // 3. El passem a mode espectador
        victim.setGameMode(GameMode.SPECTATOR);

        // 4. Missatge de consol
        victim.sendMessage("§c§lHAS MORT! §fAra ets un espectador.");
        victim.sendMessage("§7Pots volar per veure la resta de la partida.");
    }
}