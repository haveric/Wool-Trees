package haveric.woolTrees;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class WoolTrees extends JavaPlugin{
	private static final Logger log = Logger.getLogger("Minecraft");
	private final WTPlayerInteract playerInteract = new WTPlayerInteract(this);
	
	public PermissionHandler permissionHandler;
	
	public Configuration config;
	public Double treeSpawnPercentage;
	public Double treeDefault = 20.0;
	public Double woolSpawnPercentage;
	public Double woolDefault = 90.0;
	
	public Double bigChance;
	public Double bigDefault = 10.0;
	public Double multiChance;
	public Double multiDefault = 1.0;
	
	@Override
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Normal, this);
		
		log.info("[Wool Trees] Started");
		setupPermissions();
		
		setupConfig();
	}
	
	@Override
	public void onDisable(){
		log.info("[Wool Trees] Disabled");
	}
	
	
	private void setupPermissions(){
		if(permissionHandler != null){
			return;
		}
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if (permissionsPlugin == null){
			log.info("[Wool Trees] Permission system not detected, defaulting to OP");
			return;
		} 
		
		permissionHandler = ((Permissions) permissionsPlugin).getHandler();
		log.info("[Wool Trees] Found and will use plugin "+((Permissions)permissionsPlugin).getDescription().getFullName());
	}
	
	private void setupConfig(){
		config = getConfiguration();
		treeSpawnPercentage = config.getDouble("Tree Spawn %", treeDefault);
		if (treeSpawnPercentage < 0){
			config.setProperty("Tree Spawn %", 0.0);
			treeSpawnPercentage = 0.0;
			log.info("[Wool Trees] WARNING: Tree Spawn % below 0.  Defaulting to 0.");
		} else if (treeSpawnPercentage > 100){
			config.setProperty("Tree Spawn %", 100.0);
			treeSpawnPercentage = 100.0;
			log.info("[Wool Trees] WARNING: Tree Spawn % above 100.  Defaulting to 100");
		}
		
		woolSpawnPercentage = config.getDouble("Wool Spawn %", woolDefault);
		if (woolSpawnPercentage < 0){
			config.setProperty("Wool Spawn %", 0.0);
			woolSpawnPercentage = 0.0;
			log.info("[Wool Trees] WARNING: Wool Spawn % below 0. Defaulting to 0.");
		} else if (woolSpawnPercentage > 100){
			config.setProperty("Wool Spawn %", 100.0);
			woolSpawnPercentage = 100.0;
			log.info("[Wool Trees] WARNING: Wool Spawn % above 100. Defaulting to 100");
		}
		
		bigChance = config.getDouble("Big Tree Spawn %", bigDefault);
		if (bigChance < 0){
			config.setProperty("Big Tree Spawn %", 0.0);
			bigChance = 0.0;
			log.info("[Wool Trees] WARNING: Big Tree Spawn % below 0. Defaulting to 0.");
		} else if (bigChance > 100){
			config.setProperty("Big Tree Spawn %", 100.0);
			bigChance = 100.0;
			log.info("[Wool Trees] WARNING: Big Tree Spawn % above 100. Defaulting to 100.");
		}
		
		multiChance = config.getDouble("Multicolored Tree Spawn %", multiDefault);
		if (multiChance < 0){
			config.setProperty("Multicolored Tree Spawn %", 0.0);
			multiChance = 0.0;
			log.info("[Wool Trees] WARNING: Multicolored Tree Spawn % below 0. Defaulting to 0");
		} else if (multiChance > 100){
			config.setProperty("Multicolored Tree Spawn %", 100.0);
			multiChance = 100.0;
			log.info("[Wool Trees] WARNING: Multicolored Tree Spawn % above 100. Defaulting to 100");
		}
		
		config.save();
	}
}
