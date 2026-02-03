package cat.tuplugin.uhc.listeners;

import cat.tuplugin.uhc.UHCPluginGame;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final UHCPluginGame plugin;
    private final Map<UUID, Location> deathLocations = new HashMap<>();

    public PlayerListener(UHCPluginGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getGameManager().isPartidaActiva()) return;

        Player victim = event.getEntity();

        String originalMessage = event.getDeathMessage();
        String customMessage = "§6§lUHC §8» §c" + originalMessage;
        event.setDeathMessage(customMessage);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
        }

        deathLocations.put(victim.getUniqueId(), victim.getLocation());
        victim.setHealth(20.0);
        victim.setGameMode(GameMode.SPECTATOR);

        victim.sendTitle("§4§l¡HAS MORT!", "§6✯ §7Visca Terra Lliure! §6✯", 5, 100, 5);
        victim.sendMessage("§c§lHAS MORT! §fAra ets un espectador.");
        victim.sendMessage("§7Pots volar per veure la resta de la partida.");
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        if (deathLocations.containsKey(player.getUniqueId())) {
            Location deathLoc = deathLocations.get(player.getUniqueId());
            event.setRespawnLocation(deathLoc);
            
            deathLocations.remove(player.getUniqueId());
            
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
}