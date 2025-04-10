package quttor.lootdrop;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LootDropExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "lootdrops";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Brayden";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (identifier.startsWith("nextdrop_")) {
            try {
                int id = Integer.parseInt(identifier.substring("nextdrop_".length()));
                long millis = ScheduleManager.getInstance().getTimeUntilNext(id);
                if (millis < 0) return "N/A";

                long seconds = millis / 1000;
                long minutes = seconds / 60;
                seconds %= 60;
                return String.format("%02d:%02d", minutes, seconds);
            } catch (NumberFormatException e) {
                return "Invalid";
            }
        }
        return null;
    }
}
