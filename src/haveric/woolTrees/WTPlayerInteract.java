package haveric.woolTrees;


import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class WTPlayerInteract implements Listener {

    private static WoolTrees plugin;

    public WTPlayerInteract(WoolTrees wt) {
        plugin = wt;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Economy econ = plugin.getEcon();
        Player player = event.getPlayer();

        PlayerInventory inventory = player.getInventory();
        World world = player.getWorld();
        Block block = event.getClickedBlock();

        ItemStack holding = player.getItemInHand();

        boolean currencyEnabled = true;

        boolean patternsEnabled = Config.isPatternEnabled();

        if (Perms.canPlant(player) && Guard.canPlace(player, block.getLocation())) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SAPLING) {

                int blockX = block.getX();
                int blockY = block.getY();
                int blockZ = block.getZ();


                if (world.getBlockAt(blockX, blockY+1, blockZ).getLightLevel() < Config.getLight()) {
                    player.sendMessage(ChatColor.RED + "The block above the sapling is too dark.");
                    return;
                }

                if (econ == null || Perms.hasIC(player)) {
                    currencyEnabled = false; // doesn't cost anything so we don't need economy.
                } else if (!econ.has(player.getName(), Config.getCost())) {
                    player.sendMessage(ChatColor.RED + "Not enough money to plant a wool tree. Need " + Config.getCost());
                    return;
                }

                int color = 0;
                if (holding.getType() == Material.INK_SACK) {
                    int dur = holding.getDurability();
                    if (dur == 15) {
                        return; // bonemeal should do nothing!
                    }
                    color = 15 - dur;

                } else if (holding.getType() == Material.SUGAR) {
                    color = 0;
                } else {
                    return; // Any other items should not work.
                }

                boolean bigTree = (random(100) <= Config.getBig());


                // colors
                // 0, 7, 15 = multi
                // 0 = sugar = white wool
                // 1-15 = normal colors

                // if not blocked
                if (!Config.isHeightEnabled() || (bigTree && !treeBlocked(world, player, blockX, blockY, blockZ, 10))
                    || (!bigTree && !treeBlocked(world, player, blockX, blockY, blockZ, 6))) {

                    // if a tree will spawn
                    if (random(100) <= Config.getTree()) {
                        int woodType = event.getClickedBlock().getData();

                        if (patternsEnabled) {
                            addPattern(world, color, blockX, blockY, blockZ);
                        }

                        if (bigTree) {
                            makeBigTree(world, woodType, color, blockX, blockY, blockZ, null, -1);
                        } else {
                            makeNormalTree(world, woodType, color, blockX, blockY, blockZ, null, -1);
                        }

                        if (patternsEnabled) {
                            Config.setPattern(world.getName() + ":" + blockX + "," + blockY + "," + blockZ, null);
                        }
                        if (currencyEnabled) {
                            econ.withdrawPlayer(player.getName(), Config.getCost());
                        }

                        // TODO: move to helper method?
                        // Remove item from hand
                        if (player.getGameMode() == GameMode.SURVIVAL) {
                            int amt = holding.getAmount();
                            if (amt > 1) {
                                holding.setAmount(--amt);
                            } else {
                                inventory.setItemInHand(null);
                            }
                        }

                    } else {
                        addPattern(world, color, blockX, blockY, blockZ);

                        // Remove item from hand
                        if (player.getGameMode() == GameMode.SURVIVAL) {
                            int amt = holding.getAmount();
                            if (amt > 1) {
                                holding.setAmount(--amt);
                            } else {
                                inventory.setItemInHand(null);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addPattern(World w, int color, int blockX, int blockY, int blockZ) {
        String colors = Config.getPattern(w.getName() + ":" + blockX + "," + blockY + "," + blockZ);

        if (colors != null && !colors.contains("(" + color + ")")) {
            colors += "(" + color + ")";
            Config.setPattern(w.getName() + ":" + blockX + "," + blockY + "," + blockZ, colors);
        } else if (colors == null) {
            colors = "(" + color + ")";
            Config.setPattern(w.getName() + ":" + blockX + "," + blockY + "," + blockZ, colors);
        }
    }

    private boolean treeBlocked(World w, Player p, int x, int y, int z, int height) {
        boolean blocked = false;

        for (int i = 1; i < height && !blocked; i++) {
            if (w.getBlockAt(x, y + i, z).getType() != Material.AIR) {
                p.sendMessage(ChatColor.RED + "Tree is blocked " + i + " levels above sapling");
                blocked = true;
            }
        }
        return blocked;
    }

    public static void makeNormalTree(World w, int wood, int color, int x, int y, int z, ArrayList<Integer> colorArray, double leaves) {
        if (colorArray == null) {
            colorArray = new ArrayList<Integer>();

            // if patterns enabled
            if (Config.isPatternEnabled()) {
                String colors = Config.getPattern(w.getName() + ":" + x + "," + y + "," + z);

                for (int i = -2; i <= 15; i ++) {
                    if (colors.contains("(" + i + ")")) {
                        colorArray.add(i);
                    }
                }
            } else {
                colorArray.add(color);
            }
        }
        for (int i = 0; i < 6; i++) {
            setLog(w.getBlockAt(x, y + i, z), wood);
        }


        for (int i = -2; i <= 2; i ++) {
            for (int j = -2; j <= 2; j ++) {
                if (i == 0 && j == 0) {
                    // trunk is here
                } else {
                    setColoredBlock(w.getBlockAt(x+i, y+3, z+j), getRandomColor(colorArray), leaves);
                    setColoredBlock(w.getBlockAt(x+i, y+4, z+j), getRandomColor(colorArray), leaves);
                    // TODO: restructure to be an else if?
                    if (i != 2 && i != -2 && j != 2 && j != -2) {
                        setColoredBlock(w.getBlockAt(x+i, y+5, z+j), getRandomColor(colorArray), leaves);
                    }
                }
            }
        }
        setColoredBlock(w.getBlockAt(x, y+6, z),getRandomColor(colorArray), leaves);
    }

    public static void makeBigTree(World w, int wood, int color, int x, int y, int z, ArrayList<Integer> colorArray, double leaves) {
        if (colorArray == null) {
            colorArray = new ArrayList<Integer>();

            // if patterns enabled
            if (Config.isPatternEnabled()) {
                String colors = Config.getPattern(w.getName() + ":" + x + "," + y + "," + z);

                for (int i = -2; i <= 15; i ++) {
                    if (colors.contains("(" + i + ")")) {
                        colorArray.add(i);
                    }
                }
            } else {
                colorArray.add(color);
            }
        }
        for (int i = 0; i < 10; i ++) {
            setLog(w.getBlockAt(x, y + i, z), wood);
        }
        for (int i = -2; i <= 2; i ++) {
            for (int j = -2; j <= 2; j ++) {
                if (i == 0 && j == 0) {
                    // trunk is here
                } else {
                    setColoredBlock(w.getBlockAt(x+i, y+6, z+j), getRandomColor(colorArray), leaves);
                    setColoredBlock(w.getBlockAt(x+i, y+7, z+j), getRandomColor(colorArray), leaves);
                    setColoredBlock(w.getBlockAt(x+i, y+8, z+j), getRandomColor(colorArray), leaves);
                    // TODO: structure into an else if?
                    if ((i == 2 && j == 2) || (i == 2 && j == -2) || (i == -2 && j == 2) || (i == -2 && j == -2)) {
                        // 5x5 corners
                    } else {
                        setColoredBlock(w.getBlockAt(x+i, y+5, z+j), getRandomColor(colorArray), leaves);
                        setColoredBlock(w.getBlockAt(x+i, y+9, z+j), getRandomColor(colorArray), leaves);
                    }
                    if (i != 2 && i != -2 && j != 2 && j != -2) { // 3x3
                        setColoredBlock(w.getBlockAt(x+i, y+4, z+j), getRandomColor(colorArray), leaves);
                        setColoredBlock(w.getBlockAt(x+i, y+10, z+j), getRandomColor(colorArray), leaves);
                    }
                }
            }
        }
    }


    private static void setColoredBlock(Block block, int color, double leaves) {
        int wool = random(100);
        if (leaves == -1) {
            if (wool < Config.getWool() && block.getType() == Material.AIR) {
                if (color == -1) {
                    color = (int) (Math.random() * 16); // 0-15
                }
                block.setType(Material.WOOL);
                block.setData((byte) color);
            }
        } else if (wool < leaves && block.getType() == Material.AIR) {
            if (color == -1) {
                color = (int) (Math.random() * 16); // 0-15
            }
            block.setType(Material.WOOL);
            block.setData((byte) color);
        }
    }

    private static void setLog(Block block, int type) {
        if (block.getType() == Material.AIR || block.getType() == Material.SAPLING) {
            if (Config.isWoolTrunksEnabled()) {
                block.setType(Material.WOOL);
                block.setData((byte) 12);
            } else {
                block.setType(Material.LOG);
                block.setData((byte) type);
            }
        }
    }

    private static int random(int num) {
        return 1 + (int) (Math.random() * num);
    }

    private static int getRandomColor(ArrayList<Integer> array) {
        int color = 0;
        if (array.contains(0) && array.contains(15) && array.contains(7)) {
            color = -1;
        } else {
            color = array.get((int) (Math.random() * array.size()));
        }
        return color;

    }
}
