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
        // 1. Mirem si la partida està activa
        if (!plugin.getGameManager().isPartidaActiva()) return;

        Player victim = event.getEntity();

        for (Player p : Bukkit.getOnlinePlayers()) {
            // Reproduïm el so del Wither (se sent a tot el món)
            // El 1.0f final és el 'pitch' (com de greu o agut és)
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);

            // Si vols un so encara més "èpic", pots afegir el del llampec:
            // p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.8f);
        }
        // Guardem la localització de mort
        deathLocations.put(victim.getUniqueId(), victim.getLocation());

        // 2. Fem que reaparegui automàticament (sense botó de Respawn)
        // Nota: Això requereix que el servidor estigui en Paper o Spigot recent
        victim.setHealth(20.0); // El curem per evitar el bucle de mort

        // 3. El passem a mode espectador
        victim.setGameMode(GameMode.SPECTATOR);

        // 4. Missatge de consol
        victim.sendTitle("§a§l¡HAS MORT!", "§f✯Visca Terra Lliure!✯", 5, 20, 5);
        victim.sendMessage("§c§lHAS MORT! §fAra ets un espectador.");
        victim.sendMessage("§7Pots volar per veure la resta de la partida.");
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        // Si tenim la seva localització de mort guardada, el teleportem allà
        if (deathLocations.containsKey(player.getUniqueId())) {
            Location deathLoc = deathLocations.get(player.getUniqueId());
            event.setRespawnLocation(deathLoc);
            
            // Netegem la localització guardada
            deathLocations.remove(player.getUniqueId());
            
            // Assegurem que segueixi en mode espectador
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
}