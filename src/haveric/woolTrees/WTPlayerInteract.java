package haveric.woolTrees;


import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
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


public class WTPlayerInteract implements Listener{

    public static WoolTrees plugin;

    public WTPlayerInteract(WoolTrees wt) {
        plugin = wt;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Permission perm = plugin.getPerm();
    	Economy econ = plugin.getEcon();
        Player player = event.getPlayer();

        PlayerInventory inventory = player.getInventory();
        World world = player.getWorld();
        Block block = event.getClickedBlock();

        ItemStack holding = player.getItemInHand();

        boolean currencyEnabled = true;

        boolean patternsEnabled = plugin.getConfigPattern();

        if(perm == null || perm.has(player, plugin.permPlant) || perm.has(player, plugin.permPlantAlt)){
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SAPLING){

                int blockX = block.getX();
                int blockY = block.getY();
                int blockZ = block.getZ();


            	if (world.getBlockAt(blockX, blockY+1, blockZ).getLightLevel() < plugin.getConfigLight()){
            		player.sendMessage(ChatColor.RED + "The block above the sapling is too dark.");
            		return;
            	}

            	// TODO: Is the check for perm == null correct?
            	if (econ == null || perm == null || perm.has(player, plugin.permIC) || perm.has(player, plugin.permICAlt)
            					 || perm.has(player, plugin.permICAlt2) || perm.has(player, plugin.permICAlt3)){
            		currencyEnabled = false; // doesn't cost anything so we don't need economy.
            	} else if (!econ.has(player.getName(), plugin.getConfigCost())){
            		player.sendMessage(ChatColor.RED + "Not enough money to plant a wool tree. Need " + plugin.getConfigCost());
            		return;
            	}

                int color = 0;
                if (holding.getType() == Material.INK_SACK ){
                    int dur=holding.getDurability();
                    if (dur == 15){
                    	return; // bonemeal should do nothing!
                    } else {
                    	color = 15-dur;
                    }
                } else if (holding.getType() == Material.SUGAR){
                    color = 0;
                } else {
                	return; // Any other items should not work.
                }

                boolean bigTree = (random(100) <= plugin.getConfigBig());


                // colors
                // 0, 7, 15 = multi
                // 0 = sugar = white wool
                // 1-15 = normal colors

                // if not blocked
                if (!plugin.getConfigHeight() || (bigTree && !treeBlocked(world,player, blockX,blockY,blockZ, 10))
            		|| (!bigTree && !treeBlocked(world,player, blockX,blockY,blockZ, 6))) {

                	// if a tree will spawn
	                if (random(100) <= plugin.getConfigTree()){
	                	int woodType = event.getClickedBlock().getData();

	                	if (patternsEnabled){
	                		addPattern(world, color, blockX, blockY, blockZ);
	                	}
	                	
	                	if (bigTree){
	                		makeBigTree(world,woodType,blockX,blockY,blockZ);
	                	} else {
	                		makeNormalTree(world,woodType,blockX,blockY,blockZ);
	                	}

	                	if (patternsEnabled){
	                		plugin.setPatternConfig(world.getName()+":"+blockX+","+blockY+","+blockZ, null);
	                	}
	                	if (currencyEnabled){
	                    	econ.withdrawPlayer(player.getName(), plugin.getConfigCost());
	                    }

	                	// TODO: move to helper method?
	                    // Remove item from hand
	                    int amt = holding.getAmount();
	                    if (amt > 1){
	                        holding.setAmount(--amt);
	                    } else {
	                    	inventory.setItemInHand(null);
	                    }

	                } else {
	                	addPattern(world, color, blockX, blockY, blockZ);

	                    // Remove item from hand
	                    int amt = holding.getAmount();
	                    if (amt > 1){
	                        holding.setAmount(--amt);
	                    } else {
	                    	inventory.setItemInHand(null);
	                    }
	            	}
                }
            }
        }
    }

    private void addPattern(World w, int color, int blockX, int blockY, int blockZ){
    	String colors = plugin.getPatternConfig(w.getName()+":"+blockX+","+blockY+","+blockZ);

    	if (colors != null && !colors.contains("("+color + ")")){
    		colors += "(" + color + ")";
    		plugin.setPatternConfig(w.getName()+":"+blockX+","+blockY+","+blockZ, colors);
    	} else if (colors == null){
    		colors = "(" + color + ")";
    		plugin.setPatternConfig(w.getName()+":"+blockX+","+blockY+","+blockZ, colors);
    	}
    }

    private boolean treeBlocked(World w, Player p, int x, int y, int z, int height){
    	boolean blocked = false;

        for (int i = 1; i < height && !blocked; i ++){
        	if (w.getBlockAt(x,y+i,z).getType() != Material.AIR){
        		p.sendMessage(ChatColor.RED + "Tree is blocked " + i + " levels above sapling");
    			blocked = true;
        	}
        }
        return blocked;
    }

    private void makeNormalTree(World w, int wood, int x, int y, int z){
    	// TODO: if patterns enabled
    	String colors = plugin.getPatternConfig(w.getName()+":"+x+","+y+","+z);

    	ArrayList<Integer> colorArray = new ArrayList<Integer>();

    	// TODO: if patterns enabled
    	for(int i = -2; i <= 15; i ++){
    		if (colors.contains("(" + i + ")")){
    			colorArray.add(i);
    		}
    	}

    	for (int i = 0; i < 6; i ++){
            setLog(w.getBlockAt(x,y+i,z),wood);
        }


        for(int i = -2; i <= 2; i ++){
            for (int j = -2; j <= 2; j ++){
                if (i == 0 && j == 0){
                    // trunk is here
                } else {
                    setColoredBlock(w.getBlockAt(x+i, y+3, z+j),getRandomColor(colorArray));
                    setColoredBlock(w.getBlockAt(x+i, y+4, z+j),getRandomColor(colorArray));
                    // TODO: restructure to be an else if?
                    if (i != 2 && i != -2 && j != 2 && j != -2){
                        setColoredBlock(w.getBlockAt(x+i,y+5,z+j),getRandomColor(colorArray));
                    }
                }
            }
        }
        setColoredBlock(w.getBlockAt(x,y+6,z),getRandomColor(colorArray));
    }

    private void makeBigTree(World w, int wood, int x, int y, int z){
    	String colors = plugin.getPatternConfig(w.getName()+":"+x+","+y+","+z);
    	ArrayList<Integer> colorArray = new ArrayList<Integer>();
    	for(int i = -2; i <= 15; i ++){
    		if (colors.contains("(" + i + ")")){
    			colorArray.add(i);
    		}
    	}

        for (int i = 0; i < 10; i ++){
            setLog(w.getBlockAt(x,y+i,z),wood);
        }
        for (int i = -2; i <= 2; i ++){
            for (int j = -2; j <= 2; j ++){
                if (i == 0 && j == 0){
                    // trunk is here
                } else {
                    setColoredBlock(w.getBlockAt(x+i, y+6, z+j),getRandomColor(colorArray));
                    setColoredBlock(w.getBlockAt(x+i, y+7, z+j),getRandomColor(colorArray));
                    setColoredBlock(w.getBlockAt(x+i, y+8, z+j),getRandomColor(colorArray));
                    // TODO: structure into an else if?
                    if ((i == 2 && j == 2) || (i == 2 && j == -2) || (i == -2 && j == 2) || (i == -2 && j == -2)){
                        // 5x5 corners
                    } else {
                        setColoredBlock(w.getBlockAt(x+i,y+5,z+j),getRandomColor(colorArray));
                        setColoredBlock(w.getBlockAt(x+i,y+9,z+j),getRandomColor(colorArray));
                    }
                    if (i != 2 && i != -2 && j != 2 && j != -2){ // 3x3
                        setColoredBlock(w.getBlockAt(x+i,y+4,z+j),getRandomColor(colorArray));
                        setColoredBlock(w.getBlockAt(x+i,y+10,z+j),getRandomColor(colorArray));
                    }
                }
            }
        }
    }


    private void setColoredBlock(Block block, int color){
        int wool = 1+(int)(Math.random() * 100); // TODO: is there a reason i'm not calling helper method random()?

        if(wool < plugin.getConfigWool() && block.getType() == Material.AIR){
            if (color == -1){
                color = (int)(Math.random()*16); // 0-15
            }
            block.setType(Material.WOOL);
            block.setData((byte)color);
        }
    }

    private void setLog(Block block, int type){
        if(block.getType() == Material.AIR || block.getType() == Material.SAPLING){
            block.setType(Material.LOG);
            block.setData((byte)type);
        }
    }

    private int random(int num){
    	return 1+(int)(Math.random()*num);
    }

    // TODO: Call something else for patterns...
    private int getRandomColor(ArrayList<Integer> array){
    	int color = 0;
    	if (array.contains(0) && array.contains(15) && array.contains(7)){
    		color = -1;
    	} else {
    		color = array.get((int)(Math.random() * array.size()));
    	}
		return color;

    }
}
