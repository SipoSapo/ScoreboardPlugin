package cat.tuplugin.uhc.commands;

import cat.tuplugin.uhc.UHCPluginGame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UHCCommand implements CommandExecutor {

    private final UHCPluginGame plugin;

    public UHCCommand(UHCPluginGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // 1. Comprovem que sigui un jugador
        if (!(sender instanceof Player)) {
            sender.sendMessage("Només jugadors poden usar això.");
            return true;
        }

        Player player = (Player) sender;

        // 2. Comprovem que hagi escrit algun argument (per evitar errors de "ArrayIndexOutOfBounds")
        if (args.length == 0) {
            enviarAjuda(player);
            return true;
        }

        // --- SUBCOMANDES ---

        // /uhc setteam <p1> <p2>
        if (args[0].equalsIgnoreCase("setteam") && args.length >= 3) {
            Player p1 = Bukkit.getPlayer(args[1]);
            Player p2 = Bukkit.getPlayer(args[2]);

            if (p1 == null || p2 == null) {
                player.sendMessage("§cUn dels jugadors no està connectat.");
                return true;
            }

            plugin.getTeamManager().definirEquipManual(p1, p2);
            player.sendMessage("§aEquip creat manualment: §f" + p1.getName() + " i " + p2.getName());
            return true;
        }
        // /uhc soloteam <p1>
        if (args[0].equalsIgnoreCase("soloteam") && args.length >= 2) {
            Player p1 = Bukkit.getPlayer(args[1]);
            if (p1 == null ) {
                player.sendMessage("§cEl jugador no està connectat.");
                return true;
            }

            plugin.getTeamManager().definirEquipManual(p1);
            player.sendMessage("§aEquip creat manualment: §f" + p1.getName());
        }

        // /uhc start
        if (args[0].equalsIgnoreCase("start")) {

            if (plugin.getGameManager().isPartidaActiva()) {
                player.sendMessage("§c§lUHC §8» §fJa hi ha una partida en curs! Fes servir /uhc stop primer.");
                return true;
            }

            World world = player.getWorld();
            int radi = 1750;

            Bukkit.broadcastMessage("§6§lUHC §8» §fIniciant preparatius...");

            // 1. Scatter (teleport)
            plugin.getScatterManager().scatterPlayers(world, radi);

            // 2. Regles del món
            world.setGameRule(org.bukkit.GameRule.NATURAL_REGENERATION, false);
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(radi * 2);
            world.setTime(1000);

            // 3. Iniciem compte enrere i partida
            plugin.getGameManager().iniciarCompteEnrere(10);

            return true;
        }

        // /uhc removeteam <jugador>
        if (args[0].equalsIgnoreCase("removeteam") && args.length >= 2) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                player.sendMessage("§cJugador no trobat.");
                return true;
            }
            plugin.getTeamManager().eliminarEquipManual(p);
            player.sendMessage("§aS'ha eliminat l'equip on era §f" + p.getName());
            return true;
        }

        // /uhc clearteams
        if (args[0].equalsIgnoreCase("clearteams")) {
            plugin.getTeamManager().resetearEquipsManuals();
            player.sendMessage("§aS'han esborrat tots els equips manuals.");
            return true;
        }

        // /uhc stop
        if (args[0].equalsIgnoreCase("stop")) {
            if (!player.isOp()) {
                player.sendMessage("§cNo tens permís per aturar la partida.");
                return true;
            }
            if (!plugin.getGameManager().isPartidaActiva()) {
                player.sendMessage("§c§lUHC §8» §fNo hi ha cap partida activa per aturar.");
                return true;
            }

            // 1. Cridem a la neteja de dades

            plugin.getGameManager().acabarPartida();

            // 2. Netegem els equips del TeamManager
            plugin.getTeamManager().resetearEquipsManuals();
            // També eliminem els equips de la Scoreboard principal
            for (org.bukkit.scoreboard.Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                if (team.getName().startsWith("UHC_")) team.unregister();
            }

            Bukkit.broadcastMessage("§6§lUHC §8» §cLa partida ha estat finalitzada per un administrador.");
            Bukkit.broadcastMessage("§7Regeneració activa i border restaurat.");

            return true;
        }

        // Si no és cap de les anteriors
        enviarAjuda(player);
        return true;


    }

    private void enviarAjuda(Player p) {
        p.sendMessage("§6§lSISTEMA UHC - AJUDA");
        p.sendMessage("§e/uhc start §7- Comença la partida");
        p.sendMessage("§e/uhc stop §7- Atura tot i restaura el món");
        p.sendMessage("§e/uhc setteam <p1> <p2> §7- Crea equip manual");
        p.sendMessage("§e/uhc soloteam <p1> §7- Crea equip 'SOLO' manual");
        p.sendMessage("§e/uhc removeteam <p> §7- Elimina equip d'un jugador");
        p.sendMessage("§e/uhc clearteams §7- Reseteja equips manuals");

    }
}