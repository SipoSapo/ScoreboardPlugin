package cat.tuplugin.uhc.listeners;

import cat.tuplugin.uhc.UHCPluginGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class GameListener implements Listener {

    private final UHCPluginGame plugin;

    public GameListener(UHCPluginGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        victim.setGameMode(GameMode.SPECTATOR);
        event.setDeathMessage("§c§lUHC §8» §f" + victim.getName() + " ha estat eliminat!");
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        // Comprovem si un jugador n'ataca un altre
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();

            // Si el pvp NO està actiu (Pacte de Cavallers), cancel·lem el dany
            if (!plugin.getGameManager().isPvpActiu()) {
                event.setCancelled(true);
                damager.sendMessage("§c§lUHC §8» §fNo pots atacar fins que acabi el pacte!");
            }
        }
    }
}