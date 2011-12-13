package haveric.woolTrees;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.*;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;



public class WoolTrees extends JavaPlugin{
    private static final Logger log = Logger.getLogger("Minecraft");
    private final WTPlayerInteract playerInteract = new WTPlayerInteract(this);
    private final WTBlockBreak blockBreak = new WTBlockBreak(this);
    private final WTStructureGrow structureGrow = new WTStructureGrow(this);
    
    // Vault
    public Vault vault;    
    public Economy econ;
    public Permission perm;
    
    // Config variables
    public FileConfiguration config;
    public File configFile;
    public FileConfiguration patternConfig;
    public File patternConfigFile;
    
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
    
    public boolean heightCheck;
    public boolean heightCheckDefault = true;
    
    public int lightLevel;
    public int lightLevelDefault = 9;
    
    public boolean patternTrees;
    public boolean patternTreesDefault = true;
    
    
    @Override
    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockBreak, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.STRUCTURE_GROW, structureGrow, Event.Priority.Normal, this);
        
        log.info(String.format("[%s] v%s Started",getDescription().getName(), getDescription().getVersion()));

        // create a new config file
        configFile = new File(getDataFolder() + "/config.yml");
        patternConfigFile = new File(getDataFolder() + "/patterns.yml");
        
        // Vault
        setupVault();
        
        setupConfig();
        setupPatternConfig();
    }
    
    @Override
    public void onDisable(){
        log.info("[WoolTrees] Disabled");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(sender.isOp() || (perm != null && (perm.has((Player)sender, "wooltrees.adjust") || perm.has((Player)sender, "woolTrees.adjust")))){
            if (commandLabel.equalsIgnoreCase("wooltrees") || commandLabel.equalsIgnoreCase("wt")){
            	String wtTitle = ChatColor.DARK_AQUA + "[" + ChatColor.GRAY + "WoolTrees" + ChatColor.DARK_AQUA + "] ";
            	
                if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))){
                    sender.sendMessage(wtTitle + "Command List, [optional]");
                    sender.sendMessage("/wt tree [0-100] - " + ChatColor.DARK_AQUA + "% of a wool tree spawning.");
                    sender.sendMessage("/wt wool [0-100] - " + ChatColor.DARK_AQUA + "% of wool blocks not replaced with air.");
                    sender.sendMessage("/wt big [0-100] - " + ChatColor.DARK_AQUA + "% of trees that become big trees.");
                    sender.sendMessage("/wt cost [0+] - " + ChatColor.DARK_AQUA + "Cost to plant a tree.");
                    sender.sendMessage("/wt check [true,false] - " + ChatColor.DARK_AQUA + "Height Check.");
                    sender.sendMessage("/wt light [0-15] - " + ChatColor.DARK_AQUA + "Light Level.");
                } else if (args.length == 1){
                    String msg = "";
                    String val = "";
                    
                    if (args[0].equalsIgnoreCase("tree")){
                        msg = "Tree Spawn % is ";
                        val += config.get("Tree Spawn %");
                    } else if (args[0].equalsIgnoreCase("wool")){
                        msg = "Wool Spawn % is ";
                        val += config.get("Wool Spawn %");
                    } else if (args[0].equalsIgnoreCase("big")){
                        msg = "Big Tree Spawn % is ";
                        val += config.get("Big Tree Spawn %");
                    } else if (args[0].equalsIgnoreCase("cost")){
                        msg = "Cost to plant a tree is ";
                        val += config.get("iConomy cost to plant");
                    } else if (args[0].equalsIgnoreCase("check")){
                    	msg = "Height Check: ";
                    	val += config.get("Height Check");
                    } else if (args[0].equalsIgnoreCase("light")){
                    	msg = "Light Level: ";
                    	val += config.get("Light Level");
                    }
                    
                    try {
            			config.save(configFile);
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
                    
                    if (msg != ""){
                        sender.sendMessage(wtTitle + ChatColor.WHITE + msg + ChatColor.DARK_AQUA + val);
                    }
                } else if (args.length == 2){
                	Double val;
                    String msg = "";
                    String err = "";
                    
                	if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
                		val = -1.0;
                	} else {
                		val = Double.parseDouble(args[1]);
                	}
                    
                    

                    
                    if (val < 0){
                    	if (val == -1.0 && args[0].equalsIgnoreCase("check")){
                        	if (args[1].equalsIgnoreCase("true")){
                        		config.set("Height Check", args[1]);
                        		heightCheck = true;
                        		msg = "Height Check set to true ";
                        	} else if (args[1].equalsIgnoreCase("false")){
                        		config.set("Height Check", args[1]);
                        		heightCheck = false;
                        		msg = "Height Check set to false ";
                        	} else {
                        		err = "Value is not true or false";
                        	}
                        } else {
                        	err = "Value cannot be below 0";
                        }
                    } else if (args[0].equalsIgnoreCase("tree")){
                        if (val <= 100){
                            config.set("Tree Spawn %", val);
                            treeSpawnPercentage = val;
                            msg = "Tree Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase("wool")){
                        if (val <= 100){
                            config.set("Wool Spawn %", val);
                            woolSpawnPercentage = val;
                            msg = "Wool Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase("big")){
                        if (val <= 100){
                            config.set("Big Tree Spawn %", val);
                            bigChance = val;
                            msg = "Big Tree Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase("cost")){
                        config.set("Cost to plant", val);
                        cost = val;
                        msg = "Cost set to: ";
                    } else if (args[0].equalsIgnoreCase("light")){
                    	if (val >= 0 && val <= 15){
                    		config.set("Light Level", val);
                    		lightLevel = val.intValue();
                    		msg = "Light Level set to: ";
                    	} else {
                    		err = "Value is less than 0 or above 15.";
                    	}
                    }
                    
                    try {
            			config.save(configFile);
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
                    
                    if (msg != ""){
                    	if (val != -1.0){
                    		sender.sendMessage(wtTitle + ChatColor.WHITE + msg + ChatColor.DARK_AQUA + val);
                    	} else {
                    		sender.sendMessage(wtTitle + ChatColor.WHITE + msg);
                    	}
                    	
                    } else if (err !=""){
                    	sender.sendMessage(wtTitle + ChatColor.RED + err);
                    }
                }
            }
        }
        return false;
    }

    public void setupVault() {       
        RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permProvider != null) {
            perm = permProvider.getProvider();
            log.info(String.format("[%s] Using Permission Provider %s", getDescription().getName(), perm.getName()));
        } else {
        	log.info(String.format("[%s] No Permission Provider loaded.", getDescription().getName()));
        }
        
        
        RegisteredServiceProvider<Economy> econProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (econProvider != null) {
            econ = econProvider.getProvider();
            log.info(String.format("[%s] Using Economy Provider %s", getDescription().getName(), econ.getName()));
        } else {
        	log.info(String.format("[%s] No Economy Provider loaded.", getDescription().getName()));
        }
    }
    
    
    private void setupConfig(){
    	config = YamlConfiguration.loadConfiguration(configFile);
    	
    	
    	String warn = "[WoolTrees] WARNING: ";
    	
    	treeSpawnPercentage = config.getDouble("Tree Spawn %", treeDefault);
        
        if (treeSpawnPercentage < 0){
            config.set("Tree Spawn %", 0.0);
            treeSpawnPercentage = 0.0;
            log.info(warn+"Tree Spawn % below 0.  Defaulting to 0.");
        } else if (treeSpawnPercentage > 100){
            config.set("Tree Spawn %", 100.0);
            treeSpawnPercentage = 100.0;
            log.info(warn+"Tree Spawn % above 100.  Defaulting to 100");
        } else {
        	config.set("Tree Spawn %", treeSpawnPercentage);
        }
        
        woolSpawnPercentage = config.getDouble("Wool Spawn %", woolDefault);
        if (woolSpawnPercentage < 0){
            config.set("Wool Spawn %", 0.0);
            woolSpawnPercentage = 0.0;
            log.info(warn+"Wool Spawn % below 0. Defaulting to 0.");
        } else if (woolSpawnPercentage > 100){
            config.set("Wool Spawn %", 100.0);
            woolSpawnPercentage = 100.0;
            log.info(warn+"Wool Spawn % above 100. Defaulting to 100");
        } else {
        	config.set("Wool Spawn %", woolSpawnPercentage);
        }
        
        bigChance = config.getDouble("Big Tree Spawn %", bigDefault);
        if (bigChance < 0){
            config.set("Big Tree Spawn %", 0.0);
            bigChance = 0.0;
            log.info(warn+"Big Tree Spawn % below 0. Defaulting to 0.");
        } else if (bigChance > 100){
            config.set("Big Tree Spawn %", 100.0);
            bigChance = 100.0;
            log.info(warn+"Big Tree Spawn % above 100. Defaulting to 100.");
        } else {
        	config.set("Big Tree Spawn %", bigChance);
        }
        
        cost = config.getDouble("Cost to plant", costDefault);
        if (cost < 0){
            config.set("Cost to plant", 0.0);
            cost = 0.0;
            log.info(warn+"Cost below 0. Defaulting to 0.");
        } else {
        	config.set("Cost to plant", cost);
        }
        
        heightCheck = config.getBoolean("Height Check",heightCheckDefault);
        if (heightCheck != true && heightCheck != false){
        	config.set("Height Check", heightCheckDefault);
        	heightCheck = true;
        	log.info(warn+"Height Check not true or false. Defaulting to true");
        } else {
        	config.set("Height Check", heightCheck);
        }
        
        lightLevel = config.getInt("Light Level", lightLevelDefault);
        if (lightLevel < 0){
        	config.set("Light Level", 0);
        	lightLevel = 0;
        	log.info(warn+"Light Level too low. Defaulting to 0");
        } else if (lightLevel > 15){
        	config.set("Light Level", 15);
        	lightLevel = 15;
        	log.info(warn+"Light Level too high. Defaulting to 15");
        } else {
        	config.set("Light Level", lightLevel);
        }
        
        try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void setupPatternConfig(){
        patternConfig = YamlConfiguration.loadConfiguration(patternConfigFile);
        try {
			patternConfig.save(patternConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void setPatternConfig(String loc, String colors){
		patternConfig.set(loc, colors);
		try {
			patternConfig.save(patternConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
