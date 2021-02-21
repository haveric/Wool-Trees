package haveric.woolTrees;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

public class WTStructureGrow implements Listener {


    public WTStructureGrow() { }

    @EventHandler
    public void onTreeGrowth(StructureGrowEvent event) {
        Location l = event.getLocation();

        String patternConfig = event.getWorld().getName() + ":" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
        String colors = Config.getPattern(patternConfig);

        Player player = event.getPlayer();

        boolean fromBonemeal = event.isFromBonemeal();

        if ((fromBonemeal && Config.isBonemealGenEnabled() && (player == null || Perms.canPlant(player))) || (!fromBonemeal && Config.isNaturalGenEnabled())) {
            TreeType treeType = event.getSpecies();

            if (isActuallyTree(treeType)) {
                List<BlockState> blockStates = event.getBlocks();
                ListIterator<BlockState> iter = blockStates.listIterator();

                ArrayList<Integer> colorArray = new ArrayList<>();

                // if patterns enabled
                if (Config.isPatternEnabled()) {
                    for (int i = -2; i <= 15; i++) {
                        if (colors.contains("(" + i + ")")) {
                            colorArray.add(i);
                        }
                    }
                }
                if (colorArray.size() == 0) {
                    colorArray.add((int) (Math.random() * 15));
                }


                while (iter.hasNext()) {
                    BlockState state = iter.next();
                    Material mat = state.getType();

                    if (WTTools.isLog(mat) && Config.isWoolTrunksEnabled()) {
                        state.setType(Material.BROWN_WOOL);
                    } else if (WTTools.isLeaves(mat)) {
                        int wool = WTTools.random(100);
                        if (wool < Config.getWool()) {
                            int color = getRandomColor(colorArray);
                            if (color == -1) {
                                color = (int) (Math.random() * 16); // 0-15
                            }

                            state.setType(WTTools.getWool(color));
                        } else {
                            state.setType(Material.AIR);
                        }
                    }
                }
            }
        }

        Config.setPattern(patternConfig, null);
    }

    private int getRandomColor(ArrayList<Integer> array) {
        int color;
        if (array.contains(0) && array.contains(15) && array.contains(7)) {
            color = -1;
        } else {
            color = array.get((int) (Math.random() * array.size()));
        }
        return color;
    }

    private boolean isActuallyTree(TreeType treeType) {
        boolean isTree = true;

        switch(treeType) {
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
                isTree = false;
                break;
            default:
                break;
        }
        return isTree;
    }

}
