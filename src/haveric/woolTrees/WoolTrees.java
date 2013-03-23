package haveric.woolTrees;

import haveric.woolTrees.mcstats.Metrics;

import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;



public class WoolTrees extends JavaPlugin {
    static Logger log;

    private Commands commands = new Commands(this);

    // Vault
    private Economy econ = null;

    private Metrics metrics;

    @Override
    public void onEnable() {
        log = getLogger();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new WTPlayerInteract(this), this);
        pm.registerEvents(new WTBlockBreak(), this);
        pm.registerEvents(new WTStructureGrow(), this);

        // create a new config file
        Config.init(this);

        // Vault
        setupVault();

        // WorldGuard
        setupWorldGuard(pm);

        Config.setupConfig();
        Config.setupPatternConfig();

        getCommand(Commands.getMain()).setExecutor(commands);

        setupMetrics();
    }

    @Override
    public void onDisable() {

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

    private void setupWorldGuard(PluginManager pm) {
        Plugin worldGuard = pm.getPlugin("WorldGuard");
        if (worldGuard == null || !(worldGuard instanceof WorldGuardPlugin)) {
            log.info(String.format("[%s] WorldGuard not found.", getDescription().getName()));
        } else {
            Guard.setWorldGuard((WorldGuardPlugin) worldGuard);
        }
    }

    public Economy getEcon() {
        return econ;
    }

    private void setupMetrics() {
        try {
            metrics = new Metrics(this);

            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
