package quttor.lootdrop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import quttor.lootdrop.DropArea;

public class LootDropListener implements Listener {

    @EventHandler
    public void onChestBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == org.bukkit.Material.CHEST) {
        }
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
    }
}
