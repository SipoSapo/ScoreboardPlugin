package cat.tuplugin.uhc.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

public class GameListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        // Quan morin, els posem en espectador automàticament
        victim.setGameMode(GameMode.SPECTATOR);
        event.setDeathMessage("§c" + victim.getName() + " ha estat eliminat!");
    }
}