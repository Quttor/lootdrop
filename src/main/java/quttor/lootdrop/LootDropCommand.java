package quttor.lootdrop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quttor.lootdrop.DropArea;

public class LootDropCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lootdrops.admin")) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("reload")) {
            LootDrop.getInstance().reloadConfig();
            AreaManager.getInstance().loadAreas();
            ScheduleManager.getInstance().loadSchedule(); // optional: reload schedule too
            LootScheduler.getInstance().stopAll();
            LootScheduler.getInstance().startAll();
            sender.sendMessage("§aLootDrop config reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("begin")) {
            ScheduleManager.getInstance().begin();
            sender.sendMessage("§aLoot drop schedule started.");
            return true;
        }

        if (args[0].equalsIgnoreCase("drop") && args.length >= 2) {
            try {
                int id = Integer.parseInt(args[1]);
                DropArea area = AreaManager.getInstance().getArea(id);
                if (area != null) {
                    sender.sendMessage("§aForcing drop in area " + id);
                    LootScheduler.getInstance().schedule(area);
                } else {
                    sender.sendMessage("§cArea with id " + id + " not found.");
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid area id.");
            }
            return true;
        }

        sender.sendMessage("§7/Lootdrop reload §8- §fReload config");
        sender.sendMessage("§7/Lootdrop begin §8- §fStart scheduled drops");
        sender.sendMessage("§7/Lootdrop drop <id> §8- §fForce drop in area");
        return true;
    }
}

