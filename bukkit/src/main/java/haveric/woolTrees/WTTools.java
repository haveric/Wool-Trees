package haveric.woolTrees;

import org.bukkit.Material;

public class WTTools {

    public static boolean isLog(Material material) {
        return switch (material) {
            case ACACIA_LOG, BIRCH_LOG, DARK_OAK_LOG, JUNGLE_LOG, OAK_LOG, SPRUCE_LOG -> true;
            default -> false;
        };
    }

    public static boolean isLeaves(Material material) {
        return switch (material) {
            case ACACIA_LEAVES, BIRCH_LEAVES, DARK_OAK_LEAVES, JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES -> true;
            default -> false;
        };
    }

    public static boolean isSapling(Material material) {
        return switch (material) {
            case ACACIA_SAPLING, BIRCH_SAPLING, DARK_OAK_SAPLING, JUNGLE_SAPLING, OAK_SAPLING, SPRUCE_SAPLING -> true;
            default -> false;
        };
    }

    public static Material getLogType(Material saplingType) {
        return switch (saplingType) {
            case ACACIA_SAPLING -> Material.ACACIA_LOG;
            case BIRCH_SAPLING -> Material.BIRCH_LOG;
            case DARK_OAK_SAPLING -> Material.DARK_OAK_LOG;
            case JUNGLE_SAPLING -> Material.JUNGLE_LOG;
            case SPRUCE_SAPLING -> Material.SPRUCE_LOG;
            default -> Material.OAK_LOG;
        };
    }

    public static boolean isDye(Material material) {
        return switch (material) {
            case WHITE_DYE, ORANGE_DYE, MAGENTA_DYE, LIGHT_BLUE_DYE, YELLOW_DYE, LIME_DYE, PINK_DYE, GRAY_DYE,
                 LIGHT_GRAY_DYE, CYAN_DYE, PURPLE_DYE, BLUE_DYE, BROWN_DYE, GREEN_DYE, RED_DYE, BLACK_DYE -> true;
            default -> false;
        };
    }

    public static Material getWool(int woolId) {
        return switch (woolId) {
            case 0 -> Material.WHITE_WOOL;
            case 1 -> Material.ORANGE_WOOL;
            case 2 -> Material.MAGENTA_WOOL;
            case 3 -> Material.LIGHT_BLUE_WOOL;
            case 4 -> Material.YELLOW_WOOL;
            case 5 -> Material.LIME_WOOL;
            case 6 -> Material.PINK_WOOL;
            case 7 -> Material.GRAY_WOOL;
            case 8 -> Material.LIGHT_GRAY_WOOL;
            case 9 -> Material.CYAN_WOOL;
            case 10 -> Material.PURPLE_WOOL;
            case 11 -> Material.BLUE_WOOL;
            case 12 -> Material.BROWN_WOOL;
            case 13 -> Material.GREEN_WOOL;
            case 14 -> Material.RED_WOOL;
            default -> Material.BLACK_WOOL;
        };
    }
/*
    public static Material getDye(int dyeId) {
        return switch (dyeId) {
            case 0 -> Material.WHITE_DYE;
            case 1 -> Material.ORANGE_DYE;
            case 2 -> Material.MAGENTA_DYE;
            case 3 -> Material.LIGHT_BLUE_DYE;
            case 4 -> Material.YELLOW_DYE;
            case 5 -> Material.LIME_DYE;
            case 6 -> Material.PINK_DYE;
            case 7 -> Material.GRAY_DYE;
            case 8 -> Material.LIGHT_GRAY_DYE;
            case 9 -> Material.CYAN_DYE;
            case 10 -> Material.PURPLE_DYE;
            case 11 -> Material.BLUE_DYE;
            case 12 -> Material.BROWN_DYE;
            case 13 -> Material.GREEN_DYE;
            case 14 -> Material.RED_DYE;
            default -> Material.BLACK_DYE;
        };
    }
 */
    public static int getId(Material material) {
        return switch (material) {
            case WHITE_WOOL, WHITE_DYE -> 0;
            case ORANGE_WOOL, ORANGE_DYE -> 1;
            case MAGENTA_WOOL, MAGENTA_DYE -> 2;
            case LIGHT_BLUE_WOOL, LIGHT_BLUE_DYE -> 3;
            case YELLOW_WOOL, YELLOW_DYE -> 4;
            case LIME_WOOL, LIME_DYE -> 5;
            case PINK_WOOL, PINK_DYE -> 6;
            case GRAY_WOOL, GRAY_DYE -> 7;
            case LIGHT_GRAY_WOOL, LIGHT_GRAY_DYE -> 8;
            case CYAN_WOOL, CYAN_DYE -> 9;
            case PURPLE_WOOL, PURPLE_DYE -> 10;
            case BLUE_WOOL, BLUE_DYE -> 11;
            case BROWN_WOOL, BROWN_DYE -> 12;
            case GREEN_WOOL, GREEN_DYE -> 13;
            case RED_WOOL, RED_DYE -> 14;
            default -> 15;
        };
    }

    public static int random(int num) {
        return 1 + (int) (Math.random() * num);
    }
}
