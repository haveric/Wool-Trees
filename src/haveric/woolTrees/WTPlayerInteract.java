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

public class WTPlayerInteract extends PlayerListener{

	public static WoolTrees plugin;

	public WTPlayerInteract(WoolTrees wt) {
		plugin = wt;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		Inventory playerInventory = player.getInventory();
		World world = player.getWorld();
		Block block = event.getClickedBlock();

		ItemStack holding = player.getItemInHand();

		boolean canMakeTree = true;
		if((plugin).permissionHandler == null || ((plugin).permissionHandler != null && ((plugin).permissionHandler.has(player, "wooltrees.plant") || (plugin).permissionHandler.has(player, "woolTrees.plant")))){
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.SAPLING
				&& holding.getType() == Material.INK_SACK || holding.getType() == Material.SUGAR){
				
				int color = 0;
				if (holding.getType() == Material.INK_SACK ){
					int dur=holding.getDurability();
					color = 15-dur;
				} else if (holding.getType() == Material.SUGAR){
					color = 0;
				}
				if ((plugin).permissionHandler == null || ((plugin).permissionHandler != null && ((plugin).permissionHandler.has(player, "wooltrees.ignorecost") || (plugin).permissionHandler.has(player, "woolTrees.ignorecost")))){
					// no cost
				} else if ((plugin.iConomy != null && plugin.iConomy.isEnabled() && iConomy.hasAccount(player.getName()) && plugin.cost > 0)){
					Holdings balance = iConomy.getAccount(player.getName()).getHoldings();
					if (!balance.hasEnough(plugin.cost)){
						canMakeTree = false;
						player.sendMessage(ChatColor.RED + "Not enough money to plant a wool tree. Need " + plugin.cost);
					}
				}
				
				
				if (canMakeTree){
					// -1 = multi
					// 0 = sugar = white wool
					// 1-15 = normal colors
					if ((color > -1 && color < 16) || color == -1){
						int woodType = event.getClickedBlock().getData();

						int makeTree = 1+(int)(Math.random()*100);
						if (makeTree <= plugin.treeSpawnPercentage){

							int blockX = block.getX();
							int blockY = block.getY();
							int blockZ = block.getZ();

							int multiColor = 1+(int)(Math.random()*100);
							if (multiColor <= plugin.multiChance){
								color = -1;
							}

							int typeOfTree = 1+(int)(Math.random()*100);
							if (typeOfTree <= plugin.bigChance){
								makeBigTree(world,woodType,color,blockX,blockY,blockZ);
							} else {
								makeNormalTree(world,woodType,color,blockX,blockY,blockZ);
							}
							if ((plugin).permissionHandler != null && ((plugin).permissionHandler.has(player, "wooltrees.ignorecost") || (plugin).permissionHandler.has(player, "woolTrees.ignorecost"))){
								// cost nothing
							} else if (plugin.iConomy != null && plugin.iConomy.isEnabled()){
									
								Holdings balance = iConomy.getAccount(player.getName()).getHoldings();
								balance.subtract(plugin.cost);
							}
						}
					}

					// Removes one item from your hand 
					int amt = holding.getAmount();
					if (amt > 1){
						holding.setAmount(--amt);
					} else {
						playerInventory.remove(holding);
					}
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
}
