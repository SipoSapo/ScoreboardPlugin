package cat.tuplugin.uhc.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import java.util.*;

public class TeamManager {

    private final Scoreboard board;
    private final Map<UUID, UUID> equipsManuals = new HashMap<>();
    private final ChatColor[] colors = {
            ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
            ChatColor.AQUA, ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.DARK_AQUA,
            ChatColor.DARK_PURPLE, ChatColor.WHITE, ChatColor.DARK_RED, ChatColor.GRAY
    };

    public TeamManager() {
        this.board = Bukkit.getScoreboardManager().getMainScoreboard();
        netejarEquipsAntics();
    }
    private void netejarEquipsAntics() {
        for (Team team : board.getTeams()) {
            if (team.getName().startsWith("UHC_")) team.unregister();
        }
    }

    // Mètode per guardar la teva decisió
    public void definirEquipManual(Player p1, Player p2) {
        equipsManuals.put(p1.getUniqueId(), p2.getUniqueId());
        equipsManuals.put(p2.getUniqueId(), p1.getUniqueId());
    }

    public void definirEquipManual(Player p1) {
        equipsManuals.put(p1.getUniqueId(), p1.getUniqueId());
    }
    public void crearEquipsFinals() {
        netejarEquipsAntics(); // Ens assegurem que estigui net
        List<Player> senseEquip = new ArrayList<>(Bukkit.getOnlinePlayers());
        int indexColor = 0;

        // 1. Equips manuals
        Iterator<Player> it = senseEquip.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (equipsManuals.containsKey(p.getUniqueId())) {
                Player company = Bukkit.getPlayer(equipsManuals.get(p.getUniqueId()));
                if (company != null && senseEquip.contains(company)) {
                    ChatColor color = colors[indexColor % colors.length];
                    assignarAEquip("E" + (indexColor + 1), color, p, company);
                    indexColor++;

                    senseEquip.remove(p);
                    senseEquip.remove(company);
                    it = senseEquip.iterator();
                }
            }
        }

        // 2. Equips aleatoris
        Collections.shuffle(senseEquip);
        for (int i = 0; i < senseEquip.size(); i += 2) {
            ChatColor color = colors[indexColor % colors.length];
            if (i + 1 < senseEquip.size()) {
                assignarAEquip("E" + (indexColor + 1), color, senseEquip.get(i), senseEquip.get(i + 1));
            } else {
                assignarAEquip("E" + (indexColor + 1), color, senseEquip.get(i));
            }
            indexColor++;
        }
    }

    private void assignarAEquip(String nomId, ChatColor color, Player... jugadors) {
        Team team = board.registerNewTeam("UHC_" + nomId);
        team.setAllowFriendlyFire(true);
        team.setColor(color);
        team.setPrefix(color + "[" + nomId + "] ");

        for (Player p : jugadors) {
            team.addEntry(p.getName());
            // Canviar el color de la nametag sobre el jugador
            p.setDisplayName(color + p.getName() + ChatColor.RESET);
            p.setPlayerListName(color + p.getName() + ChatColor.RESET);
            // Força que es vegi el display name
            p.setCustomNameVisible(false);
            p.setCustomName(null);
            p.sendMessage("§fHas estat assignat a l'equip " + color + nomId);
        }
    }
    // Añade estos métodos dentro de la clase TeamManager.java

    // Elimina el equipo manual de un jugador
    public void eliminarEquipManual(Player p) {
        UUID pUUID = p.getUniqueId();
        if (equipsManuals.containsKey(pUUID)) {
            UUID partnerUUID = equipsManuals.get(pUUID);
            equipsManuals.remove(pUUID);
            equipsManuals.remove(partnerUUID);
        }
    }

    // Limpia todas las decisiones manuales
    public void resetearEquipsManuals() {
        equipsManuals.clear();
    }


}