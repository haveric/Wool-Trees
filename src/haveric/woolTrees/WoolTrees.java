package haveric.woolTrees;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
    private String cmdPattern = "pattern";
    private String cmdWoolTrunk = "trunk";
    private String cmdHere = "here"; 
    
    
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
    private String cfgPattern = "Pattern Trees";
    private String cfgWoolTrunk = "Wool Trunks";
    
    private static final double TREE_DEFAULT = 20.0;
    private static final double WOOL_DEFAULT = 90.0;
    private static final double BIG_DEFAULT = 10.0;
    private static final double COST_DEFAULT = 1000.0;
    private static final boolean HEIGHT_CHECK_DEFAULT = true;
    private static final int LIGHT_LEVEL_DEFAULT = 9;
    private static final boolean PATTERN_TREES_DEFAULT = true;
    private static final boolean WOOL_TRUNKS_DEFAULT = true;
    
    
    @Override
    public void onEnable(){
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(playerInteract, this);
    	pm.registerEvents(blockBreak, this);
    	pm.registerEvents(structureGrow, this);
    	
    	
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
    	ChatColor valColor = ChatColor.GOLD;
    	ChatColor defColor = ChatColor.WHITE; 
    	String wtTitle = msgColor + "[" + ChatColor.GRAY + "WoolTrees" + msgColor + "] ";
    	
        if(sender.isOp() || (perm != null && (perm.has((Player)sender, permAdjust) || perm.has((Player)sender, permAdjustAlt)))){
            if (commandLabel.equalsIgnoreCase(cmdMain) || commandLabel.equalsIgnoreCase(cmdMainAlt)){
            	            	
                if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(cmdHelp))){
                    sender.sendMessage(wtTitle + "github.com/haveric/wool-trees - v" + getDescription().getVersion());
                    sender.sendMessage("/" + cmdMain + " " + cmdTree  + " <0-100> (" + valColor + config.get(cfgTree) + defColor + ") - " + msgColor + "% of a wool tree spawning.");
                    sender.sendMessage("/" + cmdMain + " " + cmdWool  + " <0-100> (" + valColor + config.get(cfgWool) + defColor + ") - " + msgColor + "% wool blocks kept.");
                    sender.sendMessage("/" + cmdMain + " " + cmdBig   + " <0-100> (" + valColor + config.get(cfgBig) + defColor + ") - " + msgColor + "% big trees.");
                    sender.sendMessage("/" + cmdMain + " " + cmdCost  + " <0+> ("    + valColor + config.get(cfgCost) + defColor + ") - " + msgColor + "Cost to plant a tree.");
                    sender.sendMessage("/" + cmdMain + " " + cmdCheck + " <true,false> (" + valColor + config.get(cfgHeight) + defColor + ") - " + msgColor + "Height Check.");
                    sender.sendMessage("/" + cmdMain + " " + cmdLight + " <0-15> ("  + valColor + config.get(cfgLight) + defColor + ") - " + msgColor + "Light Level.");
                    sender.sendMessage("/" + cmdMain + " " + cmdPattern + " <true,false> (" + valColor + config.get(cfgPattern) + defColor + ") - " + msgColor + "Pattern Trees.");
                    sender.sendMessage("/" + cmdMain + " " + cmdWoolTrunk + " <true,false> (" + valColor + config.get(cfgWoolTrunk) + defColor + ") - " + msgColor + "Wool Trunks.");
                    sender.sendMessage("/" + cmdMain + " " + cmdHere + " [wool%] [big] [color]" + defColor + " - " + msgColor + "Create Tree at Mouse.");
                } else if (args.length >= 1 && args[0].equalsIgnoreCase(cmdHere)){
                	boolean big = false;
                	ArrayList<Integer> colorArray = new ArrayList<Integer>();
                	Double val = 100.0;
                	for (int i = 1; i < args.length; i ++){
                		if (args[i].equalsIgnoreCase("big")){
                			big = true;
                		} else if (args[i].equalsIgnoreCase("white")){
                			colorArray.add(0);
                		} else if (args[i].equalsIgnoreCase("orange")){
                			colorArray.add(1);
                		} else if (args[i].equalsIgnoreCase("magenta")){
                			colorArray.add(2);
                		} else if (args[i].equalsIgnoreCase("lightblue")){
                			colorArray.add(3);
                		} else if (args[i].equalsIgnoreCase("yellow")){
                			colorArray.add(4);
                		} else if (args[i].equalsIgnoreCase("lightgreen")){
                			colorArray.add(5);
                		} else if (args[i].equalsIgnoreCase("pink")){
                			colorArray.add(6);
                		} else if (args[i].equalsIgnoreCase("gray") || args[i].equalsIgnoreCase("grey")){
                			colorArray.add(7);
                		} else if (args[i].equalsIgnoreCase("lightgray") || args[i].equalsIgnoreCase("lightgrey")){
                			colorArray.add(8);
                		} else if (args[i].equalsIgnoreCase("cyan")){
                			colorArray.add(9);
                		} else if (args[i].equalsIgnoreCase("purple")){
                			colorArray.add(10);
                		} else if (args[i].equalsIgnoreCase("blue")){
                			colorArray.add(11);
                		} else if (args[i].equalsIgnoreCase("brown")){
                			colorArray.add(12);
                		} else if (args[i].equalsIgnoreCase("green") || args[i].equalsIgnoreCase("darkgreen")){
                			colorArray.add(13);
                		} else if (args[i].equalsIgnoreCase("red")){
                			colorArray.add(14);
                		} else if (args[i].equalsIgnoreCase("black")){
                			colorArray.add(15);
                		} else {
                			try{
                				val = Double.parseDouble(args[i]);
                				
                			} catch (NumberFormatException e){
                				val = 100.0;
                			}
                			if (val < 0 || val > 100){
                				val = 100.0;
                			}
                		}
                	}
                	if (colorArray.isEmpty()){
                		colorArray.add(0);
                	}
                	Block b = ((Player)sender).getTargetBlock(null, 100);
                	if (b.getType() == Material.AIR){
                		sender.sendMessage(wtTitle + "Out of range. Try getting closer");
                	} else {
	                	if (big){
	                		WTPlayerInteract.makeBigTree(((Player)sender).getWorld(), 0, 0, b.getX(), b.getY(), b.getZ(), colorArray, val);
	                	} else {
	                		WTPlayerInteract.makeNormalTree(((Player)sender).getWorld(), 0, 0, b.getX(), b.getY(), b.getZ(), colorArray, val);
	                	}
                	}
                } else if (args.length == 2){
                	Double val;
                    String msg = "";
                    String err = "";
                    
                	if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
                		val = -1.0;
                	} else {
                		try{
                			val = Double.parseDouble(args[1]);
                		} catch (NumberFormatException e){
                			val = 0.0;
                		}
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
                        } else if (val == -1.0 && args[0].equalsIgnoreCase(cmdPattern)){
                        	if (args[1].equalsIgnoreCase("true")){
                        		config.set(cfgPattern, args[1]);
                        		msg = "Pattern Trees set to true ";
                        	} else if (args[1].equalsIgnoreCase("false")){
                        		config.set(cfgPattern, args[1]);
                        		msg = "Pattern Trees set to false";
                        	} else {
                        		err = "Value is not true or false";
                        	}
                        } else if (val == -1.0 && args[0].equalsIgnoreCase(cmdWoolTrunk)){
                        	if (args[1].equalsIgnoreCase("true")){
	                        	config.set(cfgWoolTrunk, args[1]);
	                        	msg = "Wool Trunks set to true";
                        	} else if (args[1].equalsIgnoreCase("false")){
                        		config.set(cfgWoolTrunk, args[1]);
                        		msg = "Wool Trunks set to false";
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
        	if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(cmdHelp))){
                sender.sendMessage(wtTitle + "github.com/haveric/wool-trees - v" + getDescription().getVersion());
                sender.sendMessage("Dye saplings to grow wool trees.");
                sender.sendMessage("Each sapling has a " + valColor + config.get(cfgTree) + "% " + defColor + "chance to spawn");
                sender.sendMessage("Each tree will have " + valColor + config.get(cfgWool) + "% " + defColor + "wool leaves");
                if (econ != null){
                	if (perm != null && (perm.has(sender, permIC) || perm.has(sender, permICAlt)
       					 || perm.has(sender, permICAlt2) || perm.has(sender, permICAlt3))){
                		sender.sendMessage("Each successful tree costs " + valColor + "nothing!");
                	} else {
                		sender.sendMessage("Each successful tree costs " + valColor + config.get(cfgCost));
                	}
                }
                
        	} else {
        		sender.sendMessage(wtTitle + ChatColor.RED + "You do not have access to this command or it may not exist.");
        	}
        }
        return false;
    }

    private void setupVault() {       
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
        
        boolean patternTrees = config.getBoolean(cfgPattern, PATTERN_TREES_DEFAULT);
        if (patternTrees != true && patternTrees != false){
        	config.set(cfgPattern, PATTERN_TREES_DEFAULT);
        	log.info(warn+"Pattern trees not true or false. Defaulting to true");
        } else {
        	config.set(cfgPattern, patternTrees);
        }
        
        boolean woolTrunks = config.getBoolean(cfgWoolTrunk, WOOL_TRUNKS_DEFAULT);
        if (woolTrunks != true && woolTrunks != false){
        	config.set(cfgWoolTrunk, WOOL_TRUNKS_DEFAULT);
        	log.info(warn+"Wool Trunks not true or false. Defaulting to true");
        } else {
        	config.set(cfgWoolTrunk, woolTrunks);
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
    	return config.getBoolean(cfgPattern);
    }
    
    public boolean getWoolTrunks(){
    	return config.getBoolean(cfgWoolTrunk);
    }
}
