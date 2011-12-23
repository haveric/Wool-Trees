package haveric.woolTrees;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
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
    private Economy econ;
    private Permission perm;
    
    // Perms
    // adjust values
    public String permAdjust    = "wooltrees.adjust";
    public String permAdjustAlt = "woolTrees.adjust";
    
    // plant a tree
    public String permPlant    = "wooltrees.plant";
    public String permPlantAlt = "woolTrees.plant";
    
    // ignore cost
    public String permIC     = "wooltrees.ignorecost";
    public String permICAlt  = "woolTrees.ignorecost";
    public String permICAlt2 = "wooltrees.ignoreCost";
    public String permICAlt3 = "woolTrees.ignoreCost";
    
    // Commands
    private String cmdMain    = "wt";
    private String cmdMainAlt = "wooltrees";
    
    private String cmdHelp = "help";
    
    private String cmdTree  = "tree";
    private String cmdWool  = "wool";
    private String cmdBig   = "big";
    private String cmdCost  = "cost";
    private String cmdCheck = "check";
    private String cmdLight = "light";
    
    
    // Config variables
    public FileConfiguration config;
    public File configFile;
    public FileConfiguration patternConfig;
    public File patternConfigFile;
    
    private String cfgTree   = "Tree Spawn %";
    private String cfgWool   = "Wool Spawn %";
    private String cfgBig    = "Big Tree Spawn %";
    private String cfgCost   = "Cost to plant";
    private String cfgHeight = "Height Check";
    private String cfgLight  = "Light Level";
    //private String cfgPattern;
    
    private static final double TREE_DEFAULT = 20.0;
    private static final double WOOL_DEFAULT = 90.0;
    private static final double BIG_DEFAULT = 10.0;
    private static final double COST_DEFAULT = 1000.0;
    private static final boolean HEIGHT_CHECK_DEFAULT = true;
    private static final int LIGHT_LEVEL_DEFAULT = 9;

    private static final boolean PATTERN_TREES_DEFAULT = true;
    
    
    @Override
    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockBreak, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.STRUCTURE_GROW, structureGrow, Event.Priority.Normal, this);
        

        // create a new config file
        configFile = new File(getDataFolder() + "/config.yml");
        patternConfigFile = new File(getDataFolder() + "/patterns.yml");
        
        // Vault
        setupVault();
        
        setupConfig();
        setupPatternConfig();
        
        log.info(String.format("[%s] v%s Started",getDescription().getName(), getDescription().getVersion()));
    }
    
    @Override
    public void onDisable(){
    	log.info(String.format("[%s] Disabled",getDescription().getName()));
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	ChatColor msgColor = ChatColor.DARK_AQUA;
    	
    	String wtTitle = msgColor + "[" + ChatColor.GRAY + "WoolTrees" + msgColor + "] ";
    	
        if(sender.isOp() || (perm != null && (perm.has((Player)sender, permAdjust) || perm.has((Player)sender, permAdjustAlt)))){
            if (commandLabel.equalsIgnoreCase(cmdMain) || commandLabel.equalsIgnoreCase(cmdMainAlt)){
            	
            	
                if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(cmdHelp))){
                    sender.sendMessage(wtTitle + "Command List, [optional]");
                    sender.sendMessage("/" + cmdMain + " " + cmdTree  + " [0-100] - " + msgColor + "% of a wool tree spawning.");
                    sender.sendMessage("/" + cmdMain + " " + cmdWool  + " [0-100] - " + msgColor + "% of wool blocks not replaced with air.");
                    sender.sendMessage("/" + cmdMain + " " + cmdBig   + " [0-100] - " + msgColor + "% of trees that become big trees.");
                    sender.sendMessage("/" + cmdMain + " " + cmdCost  + " [0+] - " + msgColor + "Cost to plant a tree.");
                    sender.sendMessage("/" + cmdMain + " " + cmdCheck + " [true,false] - " + msgColor + "Height Check.");
                    sender.sendMessage("/" + cmdMain + " " + cmdLight + " [0-15] - " + msgColor + "Light Level.");
                } else if (args.length == 1){
                    String msg = "";
                    String val = "";
                    
                    if (args[0].equalsIgnoreCase(cmdTree)){
                        msg = "Tree Spawn % is ";
                        val += config.get(cfgTree);
                    } else if (args[0].equalsIgnoreCase(cmdWool)){
                        msg = "Wool Spawn % is ";
                        val += config.get(cfgWool);
                    } else if (args[0].equalsIgnoreCase(cmdBig)){
                        msg = "Big Tree Spawn % is ";
                        val += config.get(cfgBig);
                    } else if (args[0].equalsIgnoreCase(cmdCost)){
                        msg = "Cost to plant a tree is ";
                        val += config.get(cfgCost);
                    } else if (args[0].equalsIgnoreCase(cmdCheck)){
                    	msg = "Height Check: ";
                    	val += config.get(cfgHeight);
                    } else if (args[0].equalsIgnoreCase(cmdLight)){
                    	msg = "Light Level: ";
                    	val += config.get(cfgLight);
                    }
                    
                    try {
            			config.save(configFile);
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
                    
                    if (msg != ""){
                        sender.sendMessage(wtTitle + ChatColor.WHITE + msg + msgColor + val);
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
                    	if (val == -1.0 && args[0].equalsIgnoreCase(cmdCheck)){
                        	if (args[1].equalsIgnoreCase("true")){
                        		config.set(cfgHeight, args[1]);
                        		msg = "Height Check set to true ";
                        	} else if (args[1].equalsIgnoreCase("false")){
                        		config.set(cfgHeight, args[1]);
                        		msg = "Height Check set to false ";
                        	} else {
                        		err = "Value is not true or false";
                        	}
                        } else {
                        	err = "Value cannot be below 0";
                        }
                    } else if (args[0].equalsIgnoreCase(cmdTree)){
                        if (val <= 100){
                            config.set(cfgTree, val);
                            msg = "Tree Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase(cmdWool)){
                        if (val <= 100){
                            config.set(cfgWool, val);
                            msg = "Wool Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase(cmdBig)){
                        if (val <= 100){
                            config.set(cfgBig, val);
                            msg = "Big Tree Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase(cmdCost)){
                        config.set(cfgCost, val);
                        msg = "Cost set to: ";
                    } else if (args[0].equalsIgnoreCase(cmdLight)){
                    	if (val >= 0 && val <= 15){
                    		config.set(cfgLight, val.intValue());
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
                    		sender.sendMessage(wtTitle + ChatColor.WHITE + msg + msgColor + val);
                    	} else {
                    		sender.sendMessage(wtTitle + ChatColor.WHITE + msg);
                    	}
                    	
                    } else if (err !=""){
                    	sender.sendMessage(wtTitle + ChatColor.RED + err);
                    }
                }
            }
        } else {
        	sender.sendMessage(wtTitle+" You do not have access to this command.");
        }
        return false;
    }

    public void setupVault() {       
        RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permProvider != null) {
            perm = permProvider.getProvider();
        }
        
        
        RegisteredServiceProvider<Economy> econProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (econProvider != null) {
            econ = econProvider.getProvider();
        }
    }
    
    
    private void setupConfig(){
    	config = YamlConfiguration.loadConfiguration(configFile);
    	
    	
    	String warn = String.format("[%s] WARNING: ", getDescription().getName());
    	
    	double treeSpawnPercentage = config.getDouble(cfgTree, TREE_DEFAULT);
        
        if (treeSpawnPercentage < 0){
            config.set(cfgTree, 0.0);
            log.info(warn+"Tree Spawn % below 0.  Defaulting to 0.");
        } else if (treeSpawnPercentage > 100){
            config.set(cfgTree, 100.0);
            log.info(warn+"Tree Spawn % above 100.  Defaulting to 100");
        } else {
        	config.set(cfgTree, treeSpawnPercentage);
        }
        
        double woolSpawnPercentage = config.getDouble(cfgWool, WOOL_DEFAULT);
        if (woolSpawnPercentage < 0){
            config.set(cfgWool, 0.0);
            log.info(warn+"Wool Spawn % below 0. Defaulting to 0.");
        } else if (woolSpawnPercentage > 100){
            config.set(cfgWool, 100.0);
            log.info(warn+"Wool Spawn % above 100. Defaulting to 100");
        } else {
        	config.set(cfgWool, woolSpawnPercentage);
        }
        
        double bigChance = config.getDouble(cfgBig, BIG_DEFAULT);
        if (bigChance < 0){
            config.set(cfgBig, 0.0);
            log.info(warn+"Big Tree Spawn % below 0. Defaulting to 0.");
        } else if (bigChance > 100){
            config.set(cfgBig, 100.0);
            log.info(warn+"Big Tree Spawn % above 100. Defaulting to 100.");
        } else {
        	config.set(cfgBig, bigChance);
        }
        
        double cost = config.getDouble(cfgCost, COST_DEFAULT);
        if (cost < 0){
            config.set(cfgCost, 0.0);
            log.info(warn+"Cost below 0. Defaulting to 0.");
        } else {
        	config.set(cfgCost, cost);
        }
        
        boolean heightCheck = config.getBoolean(cfgHeight, HEIGHT_CHECK_DEFAULT);
        if (heightCheck != true && heightCheck != false){
        	config.set(cfgHeight, HEIGHT_CHECK_DEFAULT);
        	log.info(warn+"Height Check not true or false. Defaulting to true");
        } else {
        	config.set(cfgHeight, heightCheck);
        }
        
        int lightLevel = config.getInt(cfgLight, LIGHT_LEVEL_DEFAULT);
        if (lightLevel < 0){
        	config.set(cfgLight, 0);
        	log.info(warn+"Light Level too low. Defaulting to 0");
        } else if (lightLevel > 15){
        	config.set(cfgLight, 15);
        	log.info(warn+"Light Level too high. Defaulting to 15");
        } else {
        	config.set(cfgLight, lightLevel);
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
    
    public String getPatternConfig(String loc){
    	return patternConfig.getString(loc);
    }
    
    public Permission getPerm(){
    	return perm;
    }
    
    public Economy getEcon(){
    	return econ;
    }
    
    public double getConfigTree(){
    	return config.getDouble(cfgTree);
    }
    
    public double getConfigWool(){
    	return config.getDouble(cfgWool);
    }
    
    public double getConfigBig(){
    	return config.getDouble(cfgBig);
    }
    
    public double getConfigCost(){
    	return config.getDouble(cfgCost);
    }
    
    public boolean getConfigHeight(){
    	return config.getBoolean(cfgHeight);
    }
    
    public int getConfigLight(){
    	return config.getInt(cfgLight);
    }
    
    public boolean getConfigPattern(){
    	// TODO: implement
    	return false;
    }
}
