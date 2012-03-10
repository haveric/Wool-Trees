package haveric.woolTrees;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;



public class WoolTrees extends JavaPlugin{
    static final Logger log = Logger.getLogger("Minecraft");
    
    private Commands commands = new Commands(this);
    
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
    

    
    

    
    
    @Override
    public void onEnable(){
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(playerInteract, this);
    	pm.registerEvents(blockBreak, this);
    	pm.registerEvents(structureGrow, this);
    	
    	
    	// create a new config file
    	Config.init();
        
        
        // Vault
        setupVault();
        
        Config.setupConfig();
        Config.setupPatternConfig();
        
        getCommand(Commands.getMain()).setExecutor(commands);
        log.info(String.format("[%s] v%s Started",getDescription().getName(), getDescription().getVersion()));
    }
    
    @Override
    public void onDisable(){
    	log.info(String.format("[%s] Disabled",getDescription().getName()));
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
    
    

    
    public Permission getPerm(){
    	return perm;
    }
    
    public Economy getEcon(){
    	return econ;
    }
    

}
