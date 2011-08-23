package haveric.woolTrees;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;
import com.nijiko.permissions.PermissionHandler;

public class WTPlayerInteract extends PlayerListener{

    public static WoolTrees plugin;

    public WTPlayerInteract(WoolTrees wt) {
        plugin = wt;
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Inventory inventory = player.getInventory();
        World world = player.getWorld();
        Block block = event.getClickedBlock();

        ItemStack holding = player.getItemInHand();
        
        PermissionHandler permHandler = null;
        boolean permsEnabled = (plugin).permHandler != null;
        if (permsEnabled){
        	permHandler = (plugin).permHandler;
        }
        
        boolean iconEnabled = plugin.iConomy != null && plugin.iConomy.isEnabled() && iConomy.hasAccount(player.getName()) && plugin.cost > 0;
        Holdings balance = null;
        
        
        if(!permsEnabled || (permsEnabled && (permHandler.has(player, "wooltrees.plant") || permHandler.has(player, "woolTrees.plant")))){
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SAPLING){

                if (iconEnabled){
                	balance = iConomy.getAccount(player.getName()).getHoldings();
                	if (!permsEnabled || (permsEnabled && (permHandler.has(player, "wooltrees.ignorecost") || permHandler.has(player, "woolTrees.ignorecost")
                									    || permHandler.has(player, "wooltrees.ignoreCost") || permHandler.has(player, "woolTrees.ignoreCost")))){
                		iconEnabled = false; // doesn't cost anything so we don't need icon.
                	} else if (!balance.hasEnough(plugin.cost)){
                		player.sendMessage(ChatColor.RED + "Not enough money to plant a wool tree. Need " + plugin.cost);
                		return;
                	}
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
                
                if (random(100) <= plugin.multiChance){
                	color = -1;
                }
                
                // -1 = multi
                // 0 = sugar = white wool
                // 1-15 = normal colors
                if (color > -2 && color < 16){
                    if (random(100) <= plugin.treeSpawnPercentage){
                    	int woodType = event.getClickedBlock().getData();

                        int blockX = block.getX();
                        int blockY = block.getY();
                        int blockZ = block.getZ();

                        if (random(100) <= plugin.bigChance){
                            makeBigTree(world,woodType,color,blockX,blockY,blockZ);
                        } else {
                            makeNormalTree(world,woodType,color,blockX,blockY,blockZ);
                        }
                        if (iconEnabled){
                            balance.subtract(plugin.cost);
                        }
                    }
                }
                // Remove item from hand
                int amt = holding.getAmount();
                if (amt > 1){
                    holding.setAmount(--amt);
                } else {
                    inventory.remove(holding);
                }
            }
        }
    }    
    
    private void makeNormalTree(World w, int wood, int c, int x, int y, int z){
        for (int i = 0; i < 6; i ++){
            setLog(w.getBlockAt(x,y+i,z),wood);    
        }

        for(int i = -2; i <= 2; i ++){
            for (int j = -2; j <= 2; j ++){
                if (i == 0 && j == 0){ 
                    // trunk is here
                } else {
                    setColoredBlock(w.getBlockAt(x+i, y+3, z+j),c);
                    setColoredBlock(w.getBlockAt(x+i, y+4, z+j),c);
                    if (i != 2 && i != -2 && j != 2 && j != -2){
                        setColoredBlock(w.getBlockAt(x+i,y+5,z+j),c);
                    }
                }
            }
        }
        setColoredBlock(w.getBlockAt(x,y+6,z),c);
    }

    private void makeBigTree(World w, int wood, int c, int x, int y, int z){
        for (int i = 0; i < 10; i ++){
            setLog(w.getBlockAt(x,y+i,z),wood);
        }
        for (int i = -2; i <= 2; i ++){
            for (int j = -2; j <= 2; j ++){
                if (i == 0 && j == 0){
                    // trunk is here
                } else {
                    setColoredBlock(w.getBlockAt(x+i, y+6, z+j),c);
                    setColoredBlock(w.getBlockAt(x+i, y+7, z+j),c);
                    setColoredBlock(w.getBlockAt(x+i, y+8, z+j),c);
                    if ((i == 2 && j == 2) || (i == 2 && j == -2) || (i == -2 && j == 2) || (i == -2 && j == -2)){
                        // 5x5 corners
                    } else {
                        setColoredBlock(w.getBlockAt(x+i,y+5,z+j),c);
                        setColoredBlock(w.getBlockAt(x+i,y+9,z+j),c);
                    }
                    if (i != 2 && i != -2 && j != 2 && j != -2){ // 3x3
                        setColoredBlock(w.getBlockAt(x+i,y+4,z+j),c);
                        setColoredBlock(w.getBlockAt(x+i,y+10,z+j),c);
                    }
                }
            }
        }
    }

    private void setColoredBlock(Block block, int color){
        int wool = 1+(int)(Math.random() * 100);
        if(wool < plugin.woolSpawnPercentage && block.getType() == Material.AIR){
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
}
