package haveric.woolTrees;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class WoolTrees extends JavaPlugin{

	private static final Logger log = Logger.getLogger("Minecraft");
	private final WTPlayerInteract playerInteract = new WTPlayerInteract(this);
	
	public static PermissionHandler permissionHandler;
	
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Normal, this);
		log.info("Wool Trees Started");
		setupPermissions();
	}
	@Override
	public void onDisable() {
		log.info("Wool Trees Disabled");
	}
	
	private void setupPermissions() {
		if(permissionHandler != null) {
			return;
		}
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if (permissionsPlugin == null){
			log.info("Wool Trees: Permission system not detected, defaulting to OP");
			return;
		} 
		
		permissionHandler = ((Permissions) permissionsPlugin).getHandler();
		log.info("Wool Trees: Found and will use plugin "+((Permissions)permissionsPlugin).getDescription().getFullName());
		
	}
}
