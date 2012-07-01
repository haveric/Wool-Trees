package haveric.woolTrees;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;

public class Perms {
    private static Permission perm = null;

    private static String permAdjust    = "wooltrees.adjust";
    private static String permAdjustAlt = "woolTrees.adjust";

    private static String permPlant    = "wooltrees.plant";
    private static String permPlantAlt = "woolTrees.plant";

    private static String permIC     = "wooltrees.ignorecost";
    private static String permICAlt1 = "woolTrees.ignorecost";
    private static String permICAlt2 = "wooltrees.ignoreCost";
    private static String permICAlt3 = "woolTrees.ignoreCost";

    public static void setPerm(Permission newPerm) {
        perm = newPerm;
    }

    public static Permission getPerm() {
        return perm;
    }

    public static boolean permEnabled() {
        return (perm != null);
    }

    public static boolean hasAdjust(Player player) {
        if (permEnabled()) {
            return perm.has(player, permAdjust) || perm.has(player, permAdjustAlt);
        }
        return false;
    }

    public static boolean hasPlant(Player player) {
        if (permEnabled()) {
            return perm.has(player, permPlant) || perm.has(player, permPlantAlt);
        }
        return false;
    }

    public static boolean hasIC(Player player) {
        if (permEnabled()) {
            return perm.has(player, permIC) || perm.has(player, permICAlt1) || perm.has(player, permICAlt2) || perm.has(player, permICAlt3);
        }
        return false;
    }
}
