package quttor.lootdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Particle;
import quttor.lootdrop.DropArea;
import quttor.lootdrop.AreaManager;
import quttor.lootdrop.LootDrop;


import java.util.*;

public class LootScheduler {
    private static final LootScheduler instance = new LootScheduler();
    private final Map<Integer, BukkitRunnable> tasks = new HashMap<>();
    private final Map<Integer, Long> nextDrops = new HashMap<>();

    public static LootScheduler getInstance() {
        return instance;
    }

    public void startAll() {
        for (DropArea area : AreaManager.getInstance().getAreas()) {
            if (area.isSynced()) continue;
            schedule(area);
        }
        // Sync mode areas handled in one task
        if (AreaManager.getInstance().getAreas().stream().anyMatch(DropArea::isSynced)) {
            Bukkit.getScheduler().runTaskTimer(LootDrop.getInstance(), () -> {
                for (DropArea area : AreaManager.getInstance().getAreas()) {
                    if (area.isSynced()) doDrop(area);
                }
            }, 0L, 20L * AreaManager.getInstance().getAreas().stream().filter(DropArea::isSynced).findFirst().get().getInterval());
        }
    }

    public void schedule(DropArea area) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                doDrop(area);
                nextDrops.put(area.getId(), System.currentTimeMillis() + area.getInterval() * 1000L);
            }
        };
        task.runTaskTimer(LootDrop.getInstance(), 0L, area.getInterval() * 20L);
        tasks.put(area.getId(), task);
        nextDrops.put(area.getId(), System.currentTimeMillis() + area.getInterval() * 1000L);
    }

    public void forceDrop(DropArea area) {
        doDrop(area);
    }

    private void doDrop(DropArea area) {
        Location loc = area.getRandomDropLocation();
        loc.getBlock().setType(org.bukkit.Material.CHEST);
        Chest chest = (Chest) loc.getBlock().getState();
        Inventory inv = chest.getBlockInventory();
        inv.clear();
        area.generateLoot().forEach(inv::addItem);

        //loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(0.5, 1, 0.5), 50, 0.3, 0.6, 0.3);
        loc.getWorld().playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f);

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("lootdrops.player"))
                .forEach(p -> {
                    Location playerLoc = p.getLocation();
                    double dist = playerLoc.distance(loc);
                    p.sendTitle("§6Loot Drop!", "§fNearest drop at " + loc.getBlockX() + ", " + loc.getBlockZ(), 10, 60, 10);
                    p.sendMessage("§e[LootDrop] Nearest loot drop at §f" + loc.getBlockX() + ", " + loc.getBlockZ() + " §e(Distance: " + String.format("%.1f", dist) + ")");
                });
    }

    public void stopAll() {
        tasks.values().forEach(BukkitRunnable::cancel);
        tasks.clear();
        nextDrops.clear();
    }

    public long getTimeUntilNext(int id) {
        return nextDrops.containsKey(id) ? nextDrops.get(id) - System.currentTimeMillis() : -1;
    }
}
