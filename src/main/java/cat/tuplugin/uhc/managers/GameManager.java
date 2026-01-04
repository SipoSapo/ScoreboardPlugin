package cat.tuplugin.uhc.managers;

import cat.tuplugin.uhc.UHCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {

    private final UHCPlugin plugin;

    public GameManager(UHCPlugin plugin) {
        this.plugin = plugin;
    }

    public void iniciarCompteEnrere(int segons) {
        new BukkitRunnable() {
            int i = segons;

            @Override
            public void run() {
                if (i <= 0) {
                    // Quan el compte enrere arriba a 0
                    Bukkit.broadcastMessage("§6§lUHC §f¡COMENCEU!");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                        p.removePotionEffect(PotionEffectType.SLOW);
                        p.sendTitle("§a§l¡Endavant!", "§fBona sort a tothom", 5, 20, 5);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    }
                    this.cancel(); // Atura el temporitzador
                    return;
                }

                // Mentre el compte enrere està actiu
                String color = (i <= 3) ? "§c" : "§e"; // Vermell si queda poc
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(color + i, "§fPreparats...", 0, 21, 0);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                i--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks = 1 segon
    }
}