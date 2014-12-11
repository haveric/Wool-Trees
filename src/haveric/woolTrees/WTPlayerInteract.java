package haveric.woolTrees;


import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class WTPlayerInteract implements Listener {

    private static WoolTrees plugin;

    public WTPlayerInteract(WoolTrees wt) {
        plugin = wt;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Perms.canPlant(player)) {
            Block block = event.getClickedBlock();

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.SAPLING) {
                ItemStack holding = player.getItemInHand();

                // colors
                // 0, 7, 15 = multi
                // 0 = sugar = white wool
                // 1-15 = normal colors
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

                World world = player.getWorld();

                int blockX = block.getX();
                int blockY = block.getY();
                int blockZ = block.getZ();

                if (world.getBlockAt(blockX, blockY+1, blockZ).getLightLevel() < Config.getLight()) {
                    player.sendMessage(ChatColor.RED + "The block above the sapling is too dark.");
                    return;
                }

                Economy econ = plugin.getEcon();
                boolean currencyEnabled = true;
                if (econ == null || Perms.hasIC(player)) {
                    currencyEnabled = false; // doesn't cost anything so we don't need economy.
                } else if (!econ.has(player.getName(), Config.getCost())) {
                    player.sendMessage(ChatColor.RED + "Not enough money to plant a wool tree. Need " + Config.getCost());
                    return;
                }

                boolean bigTree = (random(100) <= Config.getBig());

                // if not blocked
                if (!Config.isHeightEnabled() || (bigTree && !treeBlocked(world, player, blockX, blockY, blockZ, 10))
                    || (!bigTree && !treeBlocked(world, player, blockX, blockY, blockZ, 6))) {

                    int woodType = block.getData();
                    boolean canBuild = false;
                    BlockState state = block.getState();
                    setLog(player.getWorld().getBlockAt(blockX, blockY, blockY), woodType);

                    // Check to see if the player is allowed to build here.
                    BlockPlaceEvent placeEvent = new BlockPlaceEvent(state.getBlock(), state, block, player.getItemInHand(), player, true);
                    plugin.getServer().getPluginManager().callEvent(placeEvent);

                    if (placeEvent.isCancelled()) {
                        state.update(true);
                    } else {
                        canBuild = true;
                        state.update(true);
                        placeEvent.setCancelled(true);
                    }

                    if (canBuild) {
                        // if a tree will spawn
                        if (Config.isDefaultGenEnabled() && random(100) <= Config.getTree()) {
                            boolean patternsEnabled = Config.isPatternEnabled();

                            if (patternsEnabled) {
                                addPattern(world, color, blockX, blockY, blockZ);
                            }

                            if (bigTree) {
                                makeBigTree(player, woodType, color, blockX, blockY, blockZ, null, -1);
                            } else {
                                makeNormalTree(player, woodType, color, blockX, blockY, blockZ, null, -1);
                            }

                            if (patternsEnabled) {
                                Config.setPattern(world.getName() + ":" + blockX + "," + blockY + "," + blockZ, null);
                            }
                            if (currencyEnabled) {
                                econ.withdrawPlayer(player.getName(), Config.getCost());
                            }
                        } else {
                            addPattern(world, color, blockX, blockY, blockZ);
                            world.playEffect(block.getLocation().add(1, 0, 1), Effect.SMOKE, 0);
                        }

                        removeFromHand(player);
                    }
                }
            }
        }
    }

    // Remove one item from hand
    private void removeFromHand(Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack holding = player.getItemInHand();

            int amt = holding.getAmount();
            if (amt > 1) {
                holding.setAmount(--amt);
            } else {
                player.getInventory().setItemInHand(null);
            }
        }
    }

    private void addPattern(World w, int color, int blockX, int blockY, int blockZ) {
        String colors = Config.getPattern(w.getName() + ":" + blockX + "," + blockY + "," + blockZ);

        if (!colors.contains("(" + color + ")")) {
            colors += "(" + color + ")";
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

    private static ArrayList<Integer> updateColorArray(ArrayList<Integer> colorArray, int color, String patternString) {
        if (colorArray == null) {
            colorArray = new ArrayList<Integer>();

            // if patterns enabled
            if (Config.isPatternEnabled()) {
                String colors = Config.getPattern(patternString);

                for (int i = -2; i <= 15; i ++) {
                    if (colors.contains("(" + i + ")")) {
                        colorArray.add(i);
                    }
                }
            } else {
                colorArray.add(color);
            }
        }
        return colorArray;
    }
    public static void makeNormalTree(Player player, int wood, int color, int x, int y, int z, ArrayList<Integer> colorArray, double leaves) {
        World world = player.getWorld();

        String patternString = world.getName() + ":" + x + "," + y + "," + z;
        colorArray = updateColorArray(colorArray, color, patternString);

        Block block = world.getBlockAt(x, y, z);
        BlockState state = block.getState();

        setLog(block, wood);

        // Check to see if the player is allowed to build here.
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(state.getBlock(), state, block, player.getItemInHand(), player, true);
        plugin.getServer().getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled()) {
            state.update(true);
        } else {
            for (int i = 1; i < 6; i++) {
                setLog(world.getBlockAt(x, y + i, z), wood);
            }

            for (int i = -2; i <= 2; i ++) {
                for (int j = -2; j <= 2; j ++) {
                    if (i == 0 && j == 0) {
                        // trunk is here
                    } else {
                        setColoredBlock(world.getBlockAt(x+i, y+3, z+j), getRandomColor(colorArray), leaves);
                        setColoredBlock(world.getBlockAt(x+i, y+4, z+j), getRandomColor(colorArray), leaves);

                        if (i != 2 && i != -2 && j != 2 && j != -2) {
                            setColoredBlock(world.getBlockAt(x+i, y+5, z+j), getRandomColor(colorArray), leaves);
                        }
                    }
                }
            }
            setColoredBlock(world.getBlockAt(x, y+6, z),getRandomColor(colorArray), leaves);
        }
    }

    public static void makeBigTree(Player player, int wood, int color, int x, int y, int z, ArrayList<Integer> colorArray, double leaves) {
        World world = player.getWorld();

        String patternString = world.getName() + ":" + x + "," + y + "," + z;
        colorArray = updateColorArray(colorArray, color, patternString);

        Block block = world.getBlockAt(x, y, z);
        BlockState state = block.getState();

        setLog(block, wood);

        // Check to see if the player is allowed to build here.
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(state.getBlock(), state, block, player.getItemInHand(), player, true);
        plugin.getServer().getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled()) {
            state.update(true);
        } else {
            // Make rest of tree
            for (int i = 1; i < 10; i ++) {
                setLog(world.getBlockAt(x, y + i, z), wood);
            }
            for (int i = -2; i <= 2; i ++) {
                for (int j = -2; j <= 2; j ++) {
                    if (i == 0 && j == 0) {
                        // trunk is here
                    } else {
                        setColoredBlock(world.getBlockAt(x+i, y+6, z+j), getRandomColor(colorArray), leaves);
                        setColoredBlock(world.getBlockAt(x+i, y+7, z+j), getRandomColor(colorArray), leaves);
                        setColoredBlock(world.getBlockAt(x+i, y+8, z+j), getRandomColor(colorArray), leaves);

                        if ((i == 2 && j == 2) || (i == 2 && j == -2) || (i == -2 && j == 2) || (i == -2 && j == -2)) {
                            // 5x5 corners
                        } else {
                            setColoredBlock(world.getBlockAt(x+i, y+5, z+j), getRandomColor(colorArray), leaves);
                            setColoredBlock(world.getBlockAt(x+i, y+9, z+j), getRandomColor(colorArray), leaves);
                        }
                        if (i != 2 && i != -2 && j != 2 && j != -2) { // 3x3
                            setColoredBlock(world.getBlockAt(x+i, y+4, z+j), getRandomColor(colorArray), leaves);
                            setColoredBlock(world.getBlockAt(x+i, y+10, z+j), getRandomColor(colorArray), leaves);
                        }
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
                if (type < 4) {
                    block.setType(Material.LOG);
                    block.setData((byte) type);
                } else {
                    block.setType(Material.LOG_2);
                    block.setData((byte) (type - 4));
                }
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
