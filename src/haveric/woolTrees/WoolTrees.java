package haveric.woolTrees;

import haveric.woolTrees.mcstats.Metrics;
import haveric.woolTrees.mcstats.Metrics.Graph;

import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;



public class WoolTrees extends JavaPlugin {
    static Logger log;

    private Commands commands = new Commands(this);

    private final WTPlayerInteract playerInteract = new WTPlayerInteract(this);
    private final WTBlockBreak blockBreak = new WTBlockBreak(this);
    private final WTStructureGrow structureGrow = new WTStructureGrow(this);

    // Vault
    private Economy econ = null;

    private Metrics metrics;

    @Override
    public void onEnable() {
        log = getLogger();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerInteract, this);
        pm.registerEvents(blockBreak, this);
        pm.registerEvents(structureGrow, this);

        // create a new config file
        Config.init(this);

        // Vault
        setupVault();

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

    public Economy getEcon() {
        return econ;
    }

    private void setupMetrics() {
        try {
            metrics = new Metrics(this);

            // Custom data
            Graph javaGraph = metrics.createGraph("Java Version");
            String javaVersion = System.getProperty("java.version");
            javaGraph.addPlotter(new Metrics.Plotter(javaVersion) {
                @Override
                public int getValue() {
                    return 1;
                }
            });
            metrics.addGraph(javaGraph);
            // End Custom data

            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
