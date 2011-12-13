package haveric.woolTrees;

import org.bukkit.World;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldListener;

public class WTStructureGrow extends WorldListener{

    public static WoolTrees plugin;

    public WTStructureGrow(WoolTrees wt) {
        plugin = wt;
    }
    
    public void onStructureGrow(StructureGrowEvent event){
    	int bX = event.getLocation().getBlockX();
    	int bY = event.getLocation().getBlockY();
    	int bZ = event.getLocation().getBlockZ();
    	World world = event.getWorld();
    	
    	if (plugin.patternConfig.get(world.getName()+":"+bX+","+bY+","+bZ) != null){
    		plugin.setPatternConfig(world.getName()+":"+bX+","+bY+","+bZ, null);
    	}
    }

}
