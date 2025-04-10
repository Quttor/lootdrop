package quttor.lootdrop;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.YamlConfiguration;
import quttor.lootdrop.DropArea;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class ScheduleManager {
    private static final ScheduleManager instance = new ScheduleManager();
    private final Map<Integer, List<Duration>> schedules = new HashMap<>();
    private final Map<Integer, Boolean> loops = new HashMap<>();
    private long startMillis;
    private BukkitRunnable ticker;
    private final Set<Integer> triggered = new HashSet<>();

    public static ScheduleManager getInstance() {
        return instance;
    }

    public void loadSchedule() {
        schedules.clear();
        loops.clear();
        triggered.clear();

        File file = new File(LootDrop.getInstance().getDataFolder(), "schedules.yml");
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                List<String> rawTimes = yaml.getStringList(key + ".times");
                boolean loop = yaml.getBoolean(key + ".loop", false);

                List<Duration> durations = new ArrayList<>();
                for (String time : rawTimes) {
                    String[] parts = time.split(":");
                    int h = parts.length == 3 ? Integer.parseInt(parts[0]) : 0;
                    int m = Integer.parseInt(parts[parts.length - 2]);
                    int s = Integer.parseInt(parts[parts.length - 1]);
                    durations.add(Duration.ofSeconds(h * 3600L + m * 60L + s));
                }

                schedules.put(id, durations);
                loops.put(id, loop);
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to parse schedule for area: " + key);
            }
        }
    }

    public void begin() {
        startMillis = System.currentTimeMillis();
        triggered.clear();
        if (ticker != null) ticker.cancel();

        ticker = new BukkitRunnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startMillis;

                for (int id : schedules.keySet()) {
                    DropArea area = AreaManager.getInstance().getArea(id);
                    if (area == null) continue;

                    List<Duration> times = schedules.get(id);
                    for (int i = 0; i < times.size(); i++) {
                        Duration d = times.get(i);
                        long dMillis = d.toMillis();
                        int uid = Objects.hash(id, i);
                        if (elapsed >= dMillis && !triggered.contains(uid)) {
                            triggered.add(uid);
                            Bukkit.getScheduler().runTask(LootDrop.getInstance(), () -> LootScheduler.getInstance().forceDrop(area));
                        }
                    }
                }

                // Check for loop restart
                if (allTriggered() && anyLooping()) {
                    begin();
                }
            }
        };
        ticker.runTaskTimerAsynchronously(LootDrop.getInstance(), 0L, 20L);
    }

    private boolean allTriggered() {
        for (int id : schedules.keySet()) {
            List<Duration> list = schedules.get(id);
            for (int i = 0; i < list.size(); i++) {
                if (!triggered.contains(Objects.hash(id, i))) return false;
            }
        }
        return true;
    }

    private boolean anyLooping() {
        return loops.values().stream().anyMatch(v -> v);
    }

    public void stop() {
        if (ticker != null) ticker.cancel();
    }
    public long getTimeUntilNext(int id) {
        if (!schedules.containsKey(id)) return -1;
        long now = System.currentTimeMillis();

        for (int i = 0; i < schedules.get(id).size(); i++) {
            Duration d = schedules.get(id).get(i);
            long scheduledTime = startMillis + d.toMillis();
            int uid = Objects.hash(id, i);

            if (!triggered.contains(uid)) {
                return scheduledTime - now;
            }
        }
        return -1;
    }
}

