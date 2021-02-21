package haveric.woolTrees;

import org.bukkit.entity.Player;

public class Perms {
    private static String permAdjust = "wooltrees.adjust";
    private static String permPlant = "wooltrees.plant";
    private static String permIC = "wooltrees.ignorecost";

    public static boolean canAdjust(Player player) {
        return player.hasPermission(permAdjust);
    }

    public static boolean canPlant(Player player) {
        return player.hasPermission(permPlant);
    }

    public static boolean hasIC(Player player) {
        return player.hasPermission(permIC);
    }
}
