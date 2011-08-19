package haveric.woolTrees;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
		
		if((plugin).permissionHandler.has(player, "wooltrees.plant")){
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& event.getClickedBlock().getType() == Material.SAPLING
				&& holding.getType() == Material.INK_SACK){
				
				int dur=holding.getDurability();
				int color = 15-dur;
				
				if (color > -1 && color < 15){ // not bonemeal(15) or invalid
					int woodType = event.getClickedBlock().getData();
	
					int blockX = block.getX();
					int blockY = block.getY();
					int blockZ = block.getZ();
					
					int makeTree = 1+(int)(Math.random()*100);
					if (makeTree <= plugin.treeSpawnPercentage){
					
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
					}
					
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
