package cat.tuplugin.uhc.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ScatterManager {

    private final Random random = new Random();

    public void scatterPlayers(World world, int radi) {

        world.setStorm(false);
        world.setThundering(false);
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location loc = trobarLocalitzacioSegura(world, radi);
            p.teleport(loc);

            p.setGameMode(GameMode.SURVIVAL);

            p.setLevel(0);
            p.setExp(0);
            p.setTotalExperience(0);

            p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS, 20 * 20, 1));
            p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOW, 20 * 20, 255));
            p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 255));
            p.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.HEAL, 20 * 20, 255));
            p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SATURATION, 20 * 20, 255));

            p.getInventory().clear();
            p.getEnderChest().clear();

            p.setGameMode(GameMode.SURVIVAL);

            p.sendMessage("§aHas estat enviat a: §f" + loc.getBlockX() + ", " + loc.getBlockZ());

        }
    }

    private Location trobarLocalitzacioSegura(World world, int radi) {
        boolean segur = false;
        Location loc = null;

        while (!segur) {
            int x = random.nextInt(radi * 2) - radi;
            int z = random.nextInt(radi * 2) - radi;
            int y = world.getHighestBlockYAt(x, z);

            loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            Block ground = loc.getBlock().getRelative(0, -1, 0);

            // Evitem que apareguin en llocs perillosos
            if (ground.getType() != Material.LAVA && ground.getType() != Material.WATER && ground.getType() != Material.AIR) {
                segur = true;
            }
        }
        return loc;
    }
}