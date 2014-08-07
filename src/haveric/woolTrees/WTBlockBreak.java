package haveric.woolTrees;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class WTBlockBreak implements Listener {

    public WTBlockBreak() {}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.SAPLING) {

            String patternConfig = block.getWorld().getName() + ":" + block.getX() + "," + block.getY() + "," + block.getZ();

            Config.setPattern(patternConfig, null);
        }
    }
}
