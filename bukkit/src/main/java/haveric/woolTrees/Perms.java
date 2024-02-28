package haveric.woolTrees;

import org.bukkit.entity.Player;

public class Perms {
    private static final String permAdjust = "wooltrees.adjust";
    private static final String permPlant = "wooltrees.plant";
    private static final String permIC = "wooltrees.ignorecost";

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
