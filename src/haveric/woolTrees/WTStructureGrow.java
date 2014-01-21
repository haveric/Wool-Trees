package haveric.woolTrees;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

public class WTStructureGrow implements Listener {

    public WTStructureGrow() {}

    @EventHandler
    public void onTreeGrowth(StructureGrowEvent event) {
        Location l = event.getLocation();

        String patternConfig = event.getWorld().getName() + ":" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
        if (Config.getPattern(patternConfig) != null) {
            Config.setPattern(patternConfig, null);
        }

        boolean fromBonemeal = event.isFromBonemeal();

        TreeType treeType = event.getSpecies();
        if (isActuallyTree(treeType)) {
            if (fromBonemeal) {
                List<BlockState> blockStates = event.getBlocks();
                ListIterator<BlockState> iter = blockStates.listIterator();

                while(iter.hasNext()) {
                    BlockState state = iter.next();
                    Material mat = state.getType();

                    if (mat == Material.LOG || mat == Material.LOG_2) {
                        state.setType(Material.WOOL);
                        state.setRawData((byte) 12);
                    }
                }
            }
        }
    }

    public boolean isActuallyTree(TreeType treeType) {
        boolean isTree = true;

        switch(treeType) {
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
                isTree = false;
                break;
        }
        return isTree;
    }

}
