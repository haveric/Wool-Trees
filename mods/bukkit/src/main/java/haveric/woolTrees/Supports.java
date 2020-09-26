package haveric.woolTrees;

import org.bukkit.entity.LivingEntity;

public class Supports {
    private static boolean supportsSwingHand;

    public static void init() {
        checkSwingHandSupport();
    }

    private static void checkSwingHandSupport() {
        try {
            LivingEntity.class.getMethod("swingMainHand", (Class<?>[]) null);
            supportsSwingHand = true;
        } catch (NoSuchMethodException | SecurityException e) {
            supportsSwingHand = false;
        }
    }

    public static boolean swingHand() {
        return supportsSwingHand;
    }
}