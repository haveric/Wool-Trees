package haveric.woolTrees;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

public class WTStructureGrow implements Listener{

    public static WoolTrees plugin;

    public WTStructureGrow(WoolTrees wt) {
        plugin = wt;
    }

    @EventHandler
    public void onTreeGrowth(StructureGrowEvent event){
    	Location l = event.getLocation();

    	String patternConfig = event.getWorld().getName() + ":" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
    	if (Config.getPattern(patternConfig) != null){
    		Config.setPattern(patternConfig, null);
    	}
    }

}
