package quttor.lootdrop;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class AreaManager {
    private static final AreaManager instance = new AreaManager();
    private final Map<Integer, DropArea> areas = new HashMap<>();

    public static AreaManager getInstance() {
        return instance;
    }

    public void loadAreas() {
        areas.clear();
        ConfigurationSection section = LootDrop.getInstance().getConfig().getConfigurationSection("areas");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                ConfigurationSection areaSection = section.getConfigurationSection(key);
                if (areaSection == null) continue;

                World world = Bukkit.getWorld(areaSection.getString("world"));
                if (world == null) continue;

                int x1 = areaSection.getInt("x1");
                int z1 = areaSection.getInt("z1");
                int x2 = areaSection.getInt("x2");
                int z2 = areaSection.getInt("z2");
                int interval = areaSection.getInt("interval", 300);
                boolean sync = areaSection.getBoolean("sync", false);

                Map<String, DropArea.LootEntry> lootTable = new HashMap<>();
                ConfigurationSection lootSection = areaSection.getConfigurationSection("lootTable");

                if (lootSection != null) {
                    for (String itemKey : lootSection.getKeys(false)) {
                        if (lootSection.isConfigurationSection(itemKey)) {
                            ConfigurationSection itemSection = lootSection.getConfigurationSection(itemKey);
                            double chance = itemSection.getDouble("chance", 1.0);
                            boolean stack = itemSection.getBoolean("stack", false);
                            int min = itemSection.getInt("min", 1);
                            int max = itemSection.getInt("max", 1);

                            // Handle enchantments
                            Map<Enchantment, Integer> enchants = new HashMap<>();
                            ConfigurationSection enchSection = itemSection.getConfigurationSection("enchantments");
                            if (enchSection != null) {
                                for (String enchKey : enchSection.getKeys(false)) {
                                    Enchantment ench = Enchantment.getByName(enchKey.toUpperCase());
                                    if (ench != null) {
                                        enchants.put(ench, enchSection.getInt(enchKey));
                                    } else {
                                        Bukkit.getLogger().warning("[LootDrop] Unknown enchantment: " + enchKey);
                                    }
                                }
                            }

                            lootTable.put(itemKey, new DropArea.LootEntry(chance, stack, min, max, enchants));
                        } else {
                            double chance = lootSection.getDouble(itemKey);
                            lootTable.put(itemKey, new DropArea.LootEntry(chance, false, 1, 1, new HashMap<>()));
                        }
                    }
                }

                areas.put(id, new DropArea(id, world, x1, z1, x2, z2, lootTable, interval, sync));
            } catch (Exception e) {
                Bukkit.getLogger().warning("[LootDrop] Failed to load area: " + key);
                e.printStackTrace();
            }
        }
    }

    public DropArea getArea(int id) {
        return areas.get(id);
    }

    public Collection<DropArea> getAreas() {
        return areas.values();
    }

    public Set<Integer> getIds() {
        return areas.keySet();
    }
}
