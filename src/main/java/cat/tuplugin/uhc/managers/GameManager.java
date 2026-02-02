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

    // AQUEST ÉS EL MÈTODE QUE ET SURTIA EN ROIG
    public void iniciarCompteEnrere(int segons) {
        new BukkitRunnable() {
            int i = segons;

            @Override
            public void run() {
                if (i <= 0) {
                    // QUAN ACABA EL COMPTE ENRERE, COMENCEM LA PARTIDA REAL
                    plugin.getTeamManager().crearEquipsFinals();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                        p.removePotionEffect(PotionEffectType.SLOW);
                        p.removePotionEffect(PotionEffectType.JUMP);

                        // Clear de l'inventari
                        p.getInventory().clear();
                        p.getEnderChest().clear();

                        p.sendTitle("§a§l¡GO!", "§fGora ETA!", 5, 20, 5);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                        // Creem la sidebar al moment de començar
                        plugin.getScoreboardManager().crearScoreboard(p);
                    }
                    assignarParellesAleatories();
                    iniciarCronometreJoc(); // Engeguem el temps de joc i pacte

                    this.cancel();
                    return;
                }
                if (i == 5) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                        p.sendMessage("§6§lUHC §8» §fJa pots veure el teu voltant. Prepareu-vos!");
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                    }
                }

                // Efectes visuals del compte enrere
                String color = (i <= 3) ? "§c" : "§e";
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle(color + i, "§fPreparats...", 0, 21, 0);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.closeInventory();
                }

                i--;
            }
        }.runTaskTimer(plugin, 40L, 20L);
    }
    private BukkitTask partidaTask;

    private void iniciarCronometreJoc() {
        new BukkitRunnable() {
            @Override
            public void run() {
                segonsJugats++;
                if (segonsPacte > 0) segonsPacte--;
                else if (!pvpActiu) {
                    pvpActiu = true;
                    Bukkit.broadcastMessage("§c§lUHC §fEl pacte de cavallers ha acabat!");
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
        // 1. Aturem el cronòmetre
        if (partidaTask != null) {
            partidaTask.cancel();
            partidaTask = null;
        }

        // 2. Tornem a posar les regles normals
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(org.bukkit.GameRule.NATURAL_REGENERATION, true);
            world.getWorldBorder().setSize(30000000); // Mida per defecte de Minecraft
        }

        // 3. Netegem sidebars i efectes de tothom
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); // Sidebar buida
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.removePotionEffect(PotionEffectType.SLOW);
            p.removePotionEffect(PotionEffectType.JUMP);
        }

        pvpActiu = true; // Per defecte el PvP està actiu en un món normal
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