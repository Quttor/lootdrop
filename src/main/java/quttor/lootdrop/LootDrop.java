package quttor.lootdrop;

import org.bukkit.plugin.java.JavaPlugin;
import quttor.lootdrop.DropArea;

public class LootDrop extends JavaPlugin {
    private static LootDrop instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("schedules.yml", false);

        AreaManager.getInstance().loadAreas();
        ScheduleManager.getInstance().loadSchedule();

        LootScheduler.getInstance().startAll();
        getServer().getPluginManager().registerEvents(new LootDropListener(), this);
        getCommand("lootdrop").setExecutor(new LootDropCommand());

        // ✅ Register the PlaceholderAPI expansion
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LootDropExpansion().register();
            getLogger().info("LootDrop placeholders registered.");
        }

        getLogger().info("LootDrop enabled.");
    }


    @Override
    public void onDisable() {
        LootScheduler.getInstance().stopAll();
        getLogger().info("LootDrop disabled.");
    }

    public static LootDrop getInstance() {
        return instance;
    }
}

