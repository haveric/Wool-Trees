package haveric.woolTrees;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

    private static WoolTrees plugin;

    // Config variables
    private static FileConfiguration config;
    private static File configFile;
    private static FileConfiguration patternConfig;
    private static File patternConfigFile;

    private static final String cfgTree = "Tree Spawn %";
    private static final String cfgWool = "Wool Spawn %";
    private static final String cfgBig = "Big Tree Spawn %";
    private static final String cfgCost = "Cost to plant";
    private static final String cfgHeight = "Height Check";
    private static final String cfgLight = "Light Level";
    private static final String cfgPattern = "Pattern Trees";
    private static final String cfgWoolTrunk = "Wool Trunks";

    private static final String cfgDefaultGen = "Wool Trees Default Generation";
    private static final String cfgBonemealGen = "Bonemeal Generation of Wool Trees";
    private static final String cfgNaturalGen = "Natural Generation of Wool Trees";


    private static final double TREE_DEFAULT = 20.0;
    private static final double WOOL_DEFAULT = 90.0;
    private static final double BIG_DEFAULT = 10.0;
    private static final double COST_DEFAULT = 1000.0;
    private static final boolean HEIGHT_CHECK_DEFAULT = true;
    private static final int LIGHT_LEVEL_DEFAULT = 9;
    private static final boolean PATTERN_TREES_DEFAULT = true;
    private static final boolean WOOL_TRUNKS_DEFAULT = true;

    private static final boolean DEFAULT_GEN_DEFAULT = true;
    private static final boolean BONEMEAL_GEN_DEFAULT = false;
    private static final boolean NATURAL_GEN_DEFAULT = false;

    public static void init(WoolTrees wt) {
        plugin = wt;
        configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
        patternConfigFile = new File(plugin.getDataFolder() + File.separator + "patterns.yml");
    }

    public static void setupConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);

        double treeSpawnPercentage = config.getDouble(cfgTree, TREE_DEFAULT);

        if (treeSpawnPercentage < 0) {
            config.set(cfgTree, 0.0);
            plugin.log.warning("Tree Spawn % below 0.  Defaulting to 0.");
        } else if (treeSpawnPercentage > 100) {
            config.set(cfgTree, 100.0);
            plugin.log.warning("Tree Spawn % above 100.  Defaulting to 100");
        } else {
            config.set(cfgTree, treeSpawnPercentage);
        }

        double woolSpawnPercentage = config.getDouble(cfgWool, WOOL_DEFAULT);
        if (woolSpawnPercentage < 0) {
            config.set(cfgWool, 0.0);
            plugin.log.warning("Wool Spawn % below 0. Defaulting to 0.");
        } else if (woolSpawnPercentage > 100) {
            config.set(cfgWool, 100.0);
            plugin.log.warning("Wool Spawn % above 100. Defaulting to 100");
        } else {
            config.set(cfgWool, woolSpawnPercentage);
        }

        double bigChance = config.getDouble(cfgBig, BIG_DEFAULT);
        if (bigChance < 0) {
            config.set(cfgBig, 0.0);
            plugin.log.warning("Big Tree Spawn % below 0. Defaulting to 0.");
        } else if (bigChance > 100) {
            config.set(cfgBig, 100.0);
            plugin.log.warning("Big Tree Spawn % above 100. Defaulting to 100.");
        } else {
            config.set(cfgBig, bigChance);
        }

        double cost = config.getDouble(cfgCost, COST_DEFAULT);
        if (cost < 0) {
            config.set(cfgCost, 0.0);
            plugin.log.warning("Cost below 0. Defaulting to 0.");
        } else {
            config.set(cfgCost, cost);
        }

        if (!config.isSet(cfgHeight)) {
            config.set(cfgHeight, HEIGHT_CHECK_DEFAULT);
        }

        int lightLevel = config.getInt(cfgLight, LIGHT_LEVEL_DEFAULT);
        if (lightLevel < 0) {
            config.set(cfgLight, 0);
            plugin.log.warning("Light Level too low. Defaulting to 0");
        } else if (lightLevel > 15) {
            config.set(cfgLight, 15);
            plugin.log.warning("Light Level too high. Defaulting to 15");
        } else {
            config.set(cfgLight, lightLevel);
        }

        if (!config.isSet(cfgPattern)) {
            config.set(cfgPattern, PATTERN_TREES_DEFAULT);
        }

        if (!config.isSet(cfgWoolTrunk)) {
            config.set(cfgWoolTrunk, WOOL_TRUNKS_DEFAULT);
        }

        if (!config.isSet(cfgDefaultGen)) {
            config.set(cfgDefaultGen, DEFAULT_GEN_DEFAULT);
        }

        if (!config.isSet(cfgBonemealGen)) {
            config.set(cfgBonemealGen, BONEMEAL_GEN_DEFAULT);
        }

        if (!config.isSet(cfgNaturalGen)) {
            config.set(cfgNaturalGen, NATURAL_GEN_DEFAULT);
        }

        saveConfig();
    }

    /**
     * Saves the configuration to the file.
     */
    public static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setupPatternConfig() {
        patternConfig = YamlConfiguration.loadConfiguration(patternConfigFile);
        try {
            patternConfig.save(patternConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setPattern(String loc, String colors) {
        patternConfig.set(loc, colors);
        try {
            patternConfig.save(patternConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPattern(String loc) {
        String pattern = patternConfig.getString(loc);
        if (pattern == null) {
            pattern = "";
        }
        return pattern;
    }

    public static double getTree() {
        return config.getDouble(cfgTree);
    }

    public static double getWool() {
        return config.getDouble(cfgWool);
    }

    public static double getBig() {
        return config.getDouble(cfgBig);
    }

    public static double getCost() {
        return config.getDouble(cfgCost);
    }

    public static boolean isHeightEnabled() {
        return config.getBoolean(cfgHeight);
    }

    public static int getLight() {
        return config.getInt(cfgLight);
    }

    public static boolean isPatternEnabled() {
        return config.getBoolean(cfgPattern);
    }

    public static boolean isWoolTrunksEnabled() {
        return config.getBoolean(cfgWoolTrunk);
    }

    public static boolean isDefaultGenEnabled() {
        return config.getBoolean(cfgDefaultGen);
    }

    public static boolean isBonemealGenEnabled() {
        return config.getBoolean(cfgBonemealGen);
    }

    public static boolean isNaturalGenEnabled() {
        return config.getBoolean(cfgNaturalGen);
    }


    public static void setHeight(boolean height) {
        config.set(cfgHeight, height);
    }

    public static void setPatternEnabled(boolean enabled) {
        config.set(cfgPattern, enabled);
    }

    public static void setWoolTrunk(boolean woolTrunk) {
        config.set(cfgWoolTrunk, woolTrunk);
    }

    public static void setTree(Double tree) {
        config.set(cfgTree, tree);
    }

    public static void setWool(Double wool) {
        config.set(cfgWool, wool);
    }

    public static void setBig(Double big) {
        config.set(cfgBig, big);
    }

    public static void setCost(Double cost) {
        config.set(cfgCost, cost);
    }

    public static void setLight(int light) {
        config.set(cfgLight, light);
    }

    public static void setDefaultGen(boolean defaultGen) {
        config.set(cfgDefaultGen, defaultGen);
    }

    public static void setBonemealGen(boolean bonemealGen) {
        config.set(cfgBonemealGen, bonemealGen);
    }

    public static void setNaturalGen(boolean naturalGen) {
        config.set(cfgNaturalGen, naturalGen);
    }
}
