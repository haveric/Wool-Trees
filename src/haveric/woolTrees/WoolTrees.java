package haveric.woolTrees;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;



public class WoolTrees extends JavaPlugin {
    static final Logger log = Logger.getLogger("Minecraft");

    private Commands commands = new Commands(this);

    private final WTPlayerInteract playerInteract = new WTPlayerInteract(this);
    private final WTBlockBreak blockBreak = new WTBlockBreak(this);
    private final WTStructureGrow structureGrow = new WTStructureGrow(this);

    // Vault
    private Economy econ = null;

    @Override
    public void onEnable() {
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

    }

    @Override
    public void onDisable() {

    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            log.info(String.format("[%s] Vault not found. Permissions and Economy disabled.", getDescription().getName()));
            return;
        }

        RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permProvider != null) {
            Perms.setPerm(permProvider.getProvider());
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
