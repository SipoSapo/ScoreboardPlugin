package cat.tuplugin.uhc.managers;

import cat.tuplugin.uhc.UHCPluginGame;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private final UHCPluginGame plugin;
    private final Map<Player, Player> parelles = new HashMap<>();
    private int segonsJugats = 0;
    private int segonsPacte = 3600; // 10 minuts
    private boolean pvpActiu = false;

    public GameManager(UHCPluginGame plugin) {
        this.plugin = plugin;
    }

    public void iniciarCompteEnrere(int segons) {
        new BukkitRunnable() {
            int i = segons;

            @Override
            public void run() {
                if (i <= 0) {
                    plugin.getTeamManager().crearEquipsFinals();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                        p.removePotionEffect(PotionEffectType.SLOW);
                        p.removePotionEffect(PotionEffectType.JUMP);
                        p.removePotionEffect(PotionEffectType.REGENERATION);
                        p.removePotionEffect(PotionEffectType.SATURATION);

                        p.sendTitle("§a§l¡GO!", "§6✯ §7Gora ETA! §6✯", 5, 20, 5);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                        plugin.getScoreboardManager().crearScoreboard(p);
                    }
                    assignarParellesAleatories();
                    iniciarCronometreJoc();

                    this.cancel();
                    return;
                }

                if (i == 5) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                        p.sendMessage("§6§lUHC §8» §fJa pots veure el teu voltant. Prepara't!");
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                    }
                }

                String color = (i <= 3) ? "§c" : "§e";
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(color + i, "§fPreparats...", 0, 21, 0);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                i--;
            }
        }.runTaskTimer(plugin, 40L, 20L);
    }
    private BukkitTask partidaTask;

    private void iniciarCronometreJoc() {
        this.partidaTask = new BukkitRunnable() {
            @Override
            public void run() {
                segonsJugats++;
                if (segonsPacte > 0) segonsPacte--;
                else if (!pvpActiu) {
                    pvpActiu = true;
                    Bukkit.broadcastMessage("§c§lUHC §8» §fEl pacte de cavallers ha acabat! El PvP està actiu.");
                    for (World w : Bukkit.getWorlds()) {
                        w.setPVP(true);
                    }
                }

                String tempsJoc = formatTemps(segonsJugats);
                String tempsPacte = (segonsPacte > 0) ? formatTemps(segonsPacte) : "§cFinalitzat";

                for (Player p : Bukkit.getOnlinePlayers()) {
                    plugin.getScoreboardManager().actualitzarScoreboard(p, tempsJoc, tempsPacte);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void acabarPartida() {

        if (partidaTask != null) {
            partidaTask.cancel();
            partidaTask = null;
        }

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(org.bukkit.GameRule.NATURAL_REGENERATION, true);
            world.getWorldBorder().setSize(30000000);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); // Sidebar buida
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.removePotionEffect(PotionEffectType.SLOW);
            p.removePotionEffect(PotionEffectType.JUMP);
            p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            p.removePotionEffect(PotionEffectType.REGENERATION);
            p.removePotionEffect(PotionEffectType.SATURATION);
        }

        pvpActiu = true;
        segonsJugats = 0;
        segonsPacte = 3600;
    }

    private void assignarParellesAleatories() {
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        for (int i = 0; i < players.length - 1; i += 2) {
            parelles.put(players[i], players[i+1]);
            parelles.put(players[i+1], players[i]);
        }
    }
    public boolean isPartidaActiva(){
        return partidaTask != null;
    }

    public Player getPartner(Player p) { return parelles.get(p); }
    public boolean isPvpActiu() { return pvpActiu; }

    private String formatTemps(int totalSegons) {
        int hores = totalSegons / 3600;
        int minuts = (totalSegons % 3600) / 60;
        int segons = totalSegons % 60;

        if (hores > 0) {
            return String.format("%02d:%02d:%02d", hores, minuts, segons);
        } else {
            return String.format("%02d:%02d", minuts, segons);
        }
    }

}