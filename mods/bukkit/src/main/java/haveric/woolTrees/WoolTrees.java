package haveric.woolTrees;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;



public class WoolTrees extends JavaPlugin {
    public Logger log;

    private Commands commands = new Commands(this);

    // Vault
    private Economy econ = null;

    @Override
    public void onEnable() {
        log = getLogger();

        Supports.init();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new WTPlayerInteract(this), this);
        pm.registerEvents(new WTBlockBreak(), this);
        pm.registerEvents(new WTStructureGrow(), this);

        // create a new config file
        Config.init(this);

        // Vault
        setupVault();

        Config.setupConfig();
        Config.setupPatternConfig();

        getCommand(Commands.getMain()).setExecutor(commands);
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            log.info("Vault not found. Economy disabled.");
            return;
        }

        RegisteredServiceProvider<Economy> econProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (econProvider != null) {
            econ = econProvider.getProvider();
        }
    }

    public Economy getEcon() {
        return econ;
    }
}
