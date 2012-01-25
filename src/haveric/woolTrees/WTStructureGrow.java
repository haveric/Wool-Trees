package haveric.woolTrees;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

public class WTStructureGrow implements Listener{

    public static WoolTrees plugin;

    public WTStructureGrow(WoolTrees wt) {
        plugin = wt;
    }
    
    @EventHandler
    public void onStructureGrow(StructureGrowEvent event){
    	int bX = event.getLocation().getBlockX();
    	int bY = event.getLocation().getBlockY();
    	int bZ = event.getLocation().getBlockZ();
    	World world = event.getWorld();
    	
    	if (plugin.getPatternConfig(world.getName()+":"+bX+","+bY+","+bZ) != null){
    		plugin.setPatternConfig(world.getName()+":"+bX+","+bY+","+bZ, null);
    	}
    }

}
