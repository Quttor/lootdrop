package quttor.lootdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import quttor.lootdrop.DropArea;

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
            int id = Integer.parseInt(key);
            ConfigurationSection areaSection = section.getConfigurationSection(key);
            if (areaSection == null) continue;
            World world = Bukkit.getWorld(areaSection.getString("world"));
            if (world == null) continue;

            int x1 = areaSection.getInt("x1");
            int z1 = areaSection.getInt("z1");
            int x2 = areaSection.getInt("x2");
            int z2 = areaSection.getInt("z2");

            Map<String, Double> lootTable = new HashMap<>();
            ConfigurationSection lootSection = areaSection.getConfigurationSection("lootTable");
            if (lootSection != null) {
                for (String itemKey : lootSection.getKeys(false)) {
                    lootTable.put(itemKey, lootSection.getDouble(itemKey));
                }
            }

            int interval = areaSection.getInt("interval", 300);
            boolean sync = areaSection.getBoolean("sync", false);

            areas.put(id, new DropArea(id, world, x1, z1, x2, z2, lootTable, interval, sync));
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
