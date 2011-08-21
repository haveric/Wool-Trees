package haveric.woolTrees;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import com.iConomy.*;

public class WoolTrees extends JavaPlugin{
	private static final Logger log = Logger.getLogger("Minecraft");
	private final WTPlayerInteract playerInteract = new WTPlayerInteract(this);
	
	public PermissionHandler permissionHandler;
	
	// Config variables
	public Configuration config;
	public Double treeSpawnPercentage;
	public Double treeDefault = 20.0;
	public Double woolSpawnPercentage;
	public Double woolDefault = 90.0;
	
	public Double bigChance;
	public Double bigDefault = 10.0;
	public Double multiChance;
	public Double multiDefault = 1.0;
	
	public Double cost;
	public Double costDefault = 1000.0;
	
	// iConomy
	public iConomy iConomy = null;
	

	
	@Override
	public void onEnable(){
		String version = this.getDescription().getVersion();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Normal, this);
		
		log.info("[WoolTrees] v"+ version +" Started");
		setupPermissions();
		
		setupConfig();
		
		//iConomy
        getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, new Server(this), Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, new Server(this), Priority.Monitor, this);

	}
	
	@Override
	public void onDisable(){
		log.info("[WoolTrees] Disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(permissionHandler.has((Player)sender, "wooltrees.adjust")){
			if (commandLabel.equalsIgnoreCase("wooltrees") || commandLabel.equalsIgnoreCase("wt")){
				if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))){
					sender.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + "WoolTrees" + ChatColor.DARK_AQUA + "] Command List, [optional]");
					sender.sendMessage("/wt tree [0-100] - " + ChatColor.DARK_AQUA + "% of a wool tree spawning.");
					sender.sendMessage("/wt wool [0-100] - " + ChatColor.DARK_AQUA + "% of wool blocks not replaced with air.");
					sender.sendMessage("/wt big [0-100] - " + ChatColor.DARK_AQUA + "% of trees that become big trees.");
					sender.sendMessage("/wt multi [0-100] - " + ChatColor.DARK_AQUA + "% of trees that become multicolored.");
					sender.sendMessage("/wt cost [0+] - " + ChatColor.DARK_AQUA + "Cost to plant a tree. [iConomy]");
				} else if (args.length == 1){
					String msg = "";
					
					if (args[0].equalsIgnoreCase("tree")){
						msg = "Tree Spawn % is " + ChatColor.DARK_AQUA + config.getProperty("Tree Spawn %");
					} else if (args[0].equalsIgnoreCase("wool")){
						msg = "Wool Spawn % is " + ChatColor.DARK_AQUA + config.getProperty("Wool Spawn %");
					} else if (args[0].equalsIgnoreCase("big")){
						msg = "Big Tree Spawn % is " + ChatColor.DARK_AQUA + config.getProperty("Big Tree Spawn %");
					} else if (args[0].equalsIgnoreCase("multi")){
						msg = "Multicolored Tree Spawn % is " + ChatColor.DARK_AQUA + config.getProperty("Multicolored Tree Spawn %");
					} else if (args[0].equalsIgnoreCase("cost")){
						msg = "Cost to plant a tree is " + ChatColor.DARK_AQUA + config.getProperty("iConomy cost to plant");
					}		
					if (msg != ""){
						sender.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + "WoolTrees" + ChatColor.DARK_AQUA + "] " + ChatColor.WHITE + msg);
					}
				} else if (args.length == 2){
					Double val = Double.parseDouble(args[1]);
					if (val >= 0){
						String msg = "";
						if (args[0].equalsIgnoreCase("tree")){
							if (val <= 100){
								config.setProperty("Tree Spawn %", val);
								treeSpawnPercentage = val;
								msg = "Tree Spawn % set to: " + ChatColor.DARK_AQUA + val;
							} else {
								msg = ChatColor.RED + "Value cannnot be above 100";
							}
						} else if (args[0].equalsIgnoreCase("wool")){
							if (val <= 100){
								config.setProperty("Wool Spawn %", val);
								woolSpawnPercentage = val;
								msg = "Wool Spawn % set to: " + ChatColor.DARK_AQUA + "" + val;
							} else {
								msg = ChatColor.RED + "Value cannnot be above 100";
							}
						} else if (args[0].equalsIgnoreCase("big")){
							if (val <= 100){
								config.setProperty("Big Tree Spawn %", val);
								bigChance = val;
								msg = "Big Tree Spawn % set to: " + ChatColor.DARK_AQUA + val;
							} else {
								msg = ChatColor.RED + "Value cannnot be above 100";
							}
						} else if (args[0].equalsIgnoreCase("multi")){
							if (val <= 100){
								config.setProperty("Multicolored Tree Spawn %", val);
								multiChance = val;
								msg = "Multicolored Tree Span % set to: " + ChatColor.DARK_AQUA + val;
							} else {
								msg = ChatColor.RED + "Value cannnot be above 100";
							}
						} else if (args[0].equalsIgnoreCase("cost")){
							config.setProperty("iConomy cost to plant", val);
							cost = val;
							msg = "Cost set to: " + ChatColor.DARK_AQUA + val;
						}
						if (msg != ""){
							sender.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + "WoolTrees" + ChatColor.DARK_AQUA + "] " + ChatColor.WHITE + msg);
						}
						config.save();
					} else {
						sender.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + "WoolTrees" + ChatColor.DARK_AQUA + "] " + ChatColor.RED + "Value cannot be below 0");
					}
				}
			}
		}
		return false;
	}
	
	private void setupPermissions(){
		if(permissionHandler != null){
			return;
		}
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if (permissionsPlugin == null){
			log.info("[WoolTrees] Permission system not detected, defaulting to OP");
			return;
		} 
		
		permissionHandler = ((Permissions) permissionsPlugin).getHandler();
		log.info("[WoolTrees] Found and will use plugin "+((Permissions)permissionsPlugin).getDescription().getFullName());
	}
	
	private void setupConfig(){
		config = getConfiguration();
		treeSpawnPercentage = config.getDouble("Tree Spawn %", treeDefault);
		if (treeSpawnPercentage < 0){
			config.setProperty("Tree Spawn %", 0.0);
			treeSpawnPercentage = 0.0;
			log.info("[WoolTrees] WARNING: Tree Spawn % below 0.  Defaulting to 0.");
		} else if (treeSpawnPercentage > 100){
			config.setProperty("Tree Spawn %", 100.0);
			treeSpawnPercentage = 100.0;
			log.info("[WoolTrees] WARNING: Tree Spawn % above 100.  Defaulting to 100");
		}
		
		woolSpawnPercentage = config.getDouble("Wool Spawn %", woolDefault);
		if (woolSpawnPercentage < 0){
			config.setProperty("Wool Spawn %", 0.0);
			woolSpawnPercentage = 0.0;
			log.info("[WoolTrees] WARNING: Wool Spawn % below 0. Defaulting to 0.");
		} else if (woolSpawnPercentage > 100){
			config.setProperty("Wool Spawn %", 100.0);
			woolSpawnPercentage = 100.0;
			log.info("[WoolTrees] WARNING: Wool Spawn % above 100. Defaulting to 100");
		}
		
		bigChance = config.getDouble("Big Tree Spawn %", bigDefault);
		if (bigChance < 0){
			config.setProperty("Big Tree Spawn %", 0.0);
			bigChance = 0.0;
			log.info("[WoolTrees] WARNING: Big Tree Spawn % below 0. Defaulting to 0.");
		} else if (bigChance > 100){
			config.setProperty("Big Tree Spawn %", 100.0);
			bigChance = 100.0;
			log.info("[WoolTrees] WARNING: Big Tree Spawn % above 100. Defaulting to 100.");
		}
		
		multiChance = config.getDouble("Multicolored Tree Spawn %", multiDefault);
		if (multiChance < 0){
			config.setProperty("Multicolored Tree Spawn %", 0.0);
			multiChance = 0.0;
			log.info("[WoolTrees] WARNING: Multicolored Tree Spawn % below 0. Defaulting to 0");
		} else if (multiChance > 100){
			config.setProperty("Multicolored Tree Spawn %", 100.0);
			multiChance = 100.0;
			log.info("[WoolTrees] WARNING: Multicolored Tree Spawn % above 100. Defaulting to 100");
		}
		
		cost = config.getDouble("iConomy cost to plant", costDefault);
		if (cost < 0){
			config.setProperty("iConomy cost to plant", 0.0);
			cost = 0.0;
			log.info("[WoolTrees] WARNING: Cost below 0. Defaulting to 0.");
		}
		
		config.save();
	}
}
