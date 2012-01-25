package haveric.woolTrees;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class WTBlockBreak implements Listener{

    public static WoolTrees plugin;

    public WTBlockBreak(WoolTrees wt) {
        plugin = wt;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
    	Block block = event.getBlock();   	
    	World world = block.getWorld();
    	
    	if (block.getType() == Material.SAPLING){
	    	int bX = block.getX();
	    	int bY = block.getY();
	    	int bZ = block.getZ();
	    	
        	if (plugin.getPatternConfig(world.getName()+":"+bX+","+bY+","+bZ) != null){
        		plugin.setPatternConfig(world.getName()+":"+bX+","+bY+","+bZ, null);
        	}
	    }
    }
}
