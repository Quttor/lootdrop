package quttor.lootdrop;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DropArea {
    private final int id;
    private final World world;
    private final int x1, z1, x2, z2;
    private final Map<String, LootEntry> lootTable;
    private final int interval;
    private final boolean sync;

    public DropArea(int id, World world, int x1, int z1, int x2, int z2, Map<String, LootEntry> lootTable, int interval, boolean sync) {
        this.id = id;
        this.world = world;
        this.x1 = Math.min(x1, x2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.z2 = Math.max(z1, z2);
        this.lootTable = lootTable;
        this.interval = interval;
        this.sync = sync;
    }

    public static class LootEntry {
        public final double chance;
        public final boolean stack;
        public final int min;
        public final int max;
        public final Map<Enchantment, Integer> enchantments;

        public LootEntry(double chance, boolean stack, int min, int max, Map<Enchantment, Integer> enchantments) {
            this.chance = chance;
            this.stack = stack;
            this.min = min;
            this.max = max;
            this.enchantments = enchantments;
        }
    }

    public List<ItemStack> generateLoot() {
        List<ItemStack> loot = new ArrayList<>();
        Random random = new Random();

        for (Map.Entry<String, LootEntry> entry : lootTable.entrySet()) {
            String key = entry.getKey();
            LootEntry lootEntry = entry.getValue();

            if (random.nextDouble() < lootEntry.chance) {
                Material mat = Material.matchMaterial(key);
                if (mat != null) {
                    int amount = lootEntry.stack
                            ? lootEntry.min + random.nextInt(lootEntry.max - lootEntry.min + 1)
                            : 1;

                    ItemStack item = new ItemStack(mat, amount);

                    if (!lootEntry.enchantments.isEmpty()) {
                        lootEntry.enchantments.forEach(item::addUnsafeEnchantment);
                    }

                    loot.add(item);
                }
            }
        }

        return loot;
    }

    public int getId() { return id; }
    public int getInterval() { return interval; }
    public boolean isSynced() { return sync; }
    public World getWorld() { return world; }

    public Location getRandomDropLocation() {
        WorldBorder border = world.getWorldBorder();
        Random random = new Random();

        for (int attempts = 0; attempts < 30; attempts++) {
            int x = x1 + random.nextInt(x2 - x1 + 1);
            int z = z1 + random.nextInt(z2 - z1 + 1);
            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x + 0.5, y, z + 0.5);

            if (!border.isInside(loc)) continue;

            Material type = world.getBlockAt(loc).getType();
            if (type == Material.WATER || type == Material.LAVA) continue;

            return loc;
        }

        return null;
    }
}
