package cat.tuplugin.uhc.commands;

import cat.tuplugin.uhc.UHCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UHCCommand implements CommandExecutor {

    private final UHCPlugin plugin;

    public UHCCommand(UHCPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Només jugadors poden usar això.");
            return true;
        }

        Player player = (Player) sender;

        // Comprovació de /uhc start
        if (args.length > 0 && args[0].equalsIgnoreCase("start")) {
            World world = player.getWorld();
            int radi = 500; // Pots canviar el radi aquí

            Bukkit.broadcastMessage("§6§lUHC §fIniciant la partida...");

            // 1. Teleportem a tothom (Scatter)
            plugin.getScatterManager().scatterPlayers(world, radi);

            // 2. Configurem les regles del món
            world.setGameRule(org.bukkit.GameRule.NATURAL_REGENERATION, false);
            world.getWorldBorder().setCenter(0, 0);
            world.getWorldBorder().setSize(radi * 2);

            // 3. Iniciem el compte enrere de 10 segons
            plugin.getGameManager().iniciarCompteEnrere(10);

            return true;
        }

        player.sendMessage("§cUsa: /uhc start");
        return true;
    }
}