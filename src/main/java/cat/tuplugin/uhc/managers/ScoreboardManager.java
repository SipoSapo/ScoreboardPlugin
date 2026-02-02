package cat.tuplugin.uhc.managers;

import cat.tuplugin.uhc.UHCPluginGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Criteria;

public class ScoreboardManager {

    private final UHCPluginGame plugin;

    public ScoreboardManager(UHCPluginGame plugin) {
        this.plugin = plugin;
    }

    public void crearScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("uhc_sidebar", Criteria.DUMMY, "§6§lULTRA CAF CORE");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setRenderType(RenderType.INTEGER);

        player.setScoreboard(board);
    }

    public void actualitzarScoreboard(Player player, String tempsJoc, String tempsPacte) {
        // Comprovem si el jugador és vàlid
        if (player == null || !player.isOnline()) return;

        Scoreboard board = player.getScoreboard();
        if (board == null) {
            crearScoreboard(player);
            board = player.getScoreboard();
        }

        Objective obj = board.getObjective("uhc_sidebar");

        // Si l'objectiu no existeix, el creem
        if (obj == null) {
            obj = board.registerNewObjective("uhc_sidebar", Criteria.DUMMY, "§6§lULTRA CAF CORE");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        // Netegem les línies antigues per actualitzar (mètode simple)
        for (String entry : board.getEntries()) {
            board.resetScores(entry);
        }
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
        String nomCompany = "Cap";
        String vidaCompany = "N/A";

        if (team != null) {
            for (String entry : team.getEntries()) {
                if (!entry.equals(player.getName())) { // Si no sóc jo, és el meu company
                    Player partner = Bukkit.getPlayer(entry);
                    nomCompany = entry;
                    if (partner != null) {
                        vidaCompany = "§c" + (int) partner.getHealth() + " PV";
                    } else if (!player.isOnline()) {
                        vidaCompany = "§7DESCONNECTAT";
                    } else {
                        vidaCompany = "§7MORT";
                    }
                }
            }
        }


        obj.getScore("§7----------------").setScore(10);
        obj.getScore("§fTemps: §e" + tempsJoc).setScore(9);
        obj.getScore("§fPacte: §b" + tempsPacte).setScore(8);
        obj.getScore(" ").setScore(7);
        obj.getScore("§fCompany: §a" + nomCompany).setScore(6);
        obj.getScore("§fVida: " + vidaCompany).setScore(5);
        obj.getScore("  ").setScore(4);
        obj.getScore("§d§o\"Eng. punta de llança!\"").setScore(3); // La teva frase
        obj.getScore("§7---------------- ").setScore(2);
    }
}