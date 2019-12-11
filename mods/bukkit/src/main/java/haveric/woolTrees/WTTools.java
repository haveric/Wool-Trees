package haveric.woolTrees;

import org.bukkit.Material;

public class WTTools {

    public static boolean isLog(Material material) {
        switch (material) {
            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLeaves(Material material) {
        switch (material) {
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSapling(Material material) {
        switch (material) {
            case ACACIA_SAPLING:
            case BIRCH_SAPLING:
            case DARK_OAK_SAPLING:
            case JUNGLE_SAPLING:
            case OAK_SAPLING:
            case SPRUCE_SAPLING:
                return true;
            default:
                return false;
        }
    }

    public static Material getLogType(Material saplingType) {
        switch (saplingType) {
            case ACACIA_SAPLING:
                return Material.ACACIA_LOG;
            case BIRCH_SAPLING:
                return Material.BIRCH_LOG;
            case DARK_OAK_SAPLING:
                return Material.DARK_OAK_LOG;
            case JUNGLE_SAPLING:
                return Material.JUNGLE_LOG;
            case SPRUCE_SAPLING:
                return Material.SPRUCE_LOG;
            case OAK_SAPLING:
            default:
                return Material.OAK_LOG;
        }
    }

    public static boolean isDye(Material material) {
        switch (material) {
            case WHITE_DYE:
            case ORANGE_DYE:
            case MAGENTA_DYE:
            case LIGHT_BLUE_DYE:
            case YELLOW_DYE:
            case LIME_DYE:
            case PINK_DYE:
            case GRAY_DYE:
            case LIGHT_GRAY_DYE:
            case CYAN_DYE:
            case PURPLE_DYE:
            case BLUE_DYE:
            case BROWN_DYE:
            case GREEN_DYE:
            case RED_DYE:
            case BLACK_DYE:
                return true;
            default:
                return false;
        }
    }


    public static Material getWool(int woolId) {
        switch(woolId) {
            case 0:
                return Material.WHITE_WOOL;
            case 1:
                return Material.ORANGE_WOOL;
            case 2:
                return Material.MAGENTA_WOOL;
            case 3:
                return Material.LIGHT_BLUE_WOOL;
            case 4:
                return Material.YELLOW_WOOL;
            case 5:
                return Material.LIME_WOOL;
            case 6:
                return Material.PINK_WOOL;
            case 7:
                return Material.GRAY_WOOL;
            case 8:
                return Material.LIGHT_GRAY_WOOL;
            case 9:
                return Material.CYAN_WOOL;
            case 10:
                return Material.PURPLE_WOOL;
            case 11:
                return Material.BLUE_WOOL;
            case 12:
                return Material.BROWN_WOOL;
            case 13:
                return Material.GREEN_WOOL;
            case 14:
                return Material.RED_WOOL;
            case 15:
            default:
                return Material.BLACK_WOOL;
        }
    }

    public static Material getDye(int dyeId) {
        switch(dyeId) {
            case 0:
                return Material.WHITE_DYE;
            case 1:
                return Material.ORANGE_DYE;
            case 2:
                return Material.MAGENTA_DYE;
            case 3:
                return Material.LIGHT_BLUE_DYE;
            case 4:
                return Material.YELLOW_DYE;
            case 5:
                return Material.LIME_DYE;
            case 6:
                return Material.PINK_DYE;
            case 7:
                return Material.GRAY_DYE;
            case 8:
                return Material.LIGHT_GRAY_DYE;
            case 9:
                return Material.CYAN_DYE;
            case 10:
                return Material.PURPLE_DYE;
            case 11:
                return Material.BLUE_DYE;
            case 12:
                return Material.BROWN_DYE;
            case 13:
                return Material.GREEN_DYE;
            case 14:
                return Material.RED_DYE;
            case 15:
            default:
                return Material.BLACK_DYE;
        }
    }

    public static int getId(Material material) {
        switch(material) {
            case WHITE_WOOL:
            case WHITE_DYE:
                return 0;
            case ORANGE_WOOL:
            case ORANGE_DYE:
                return 1;
            case MAGENTA_WOOL:
            case MAGENTA_DYE:
                return 2;
            case LIGHT_BLUE_WOOL:
            case LIGHT_BLUE_DYE:
                return 3;
            case YELLOW_WOOL:
            case YELLOW_DYE:
                return 4;
            case LIME_WOOL:
            case LIME_DYE:
                return 5;
            case PINK_WOOL:
            case PINK_DYE:
                return 6;
            case GRAY_WOOL:
            case GRAY_DYE:
                return 7;
            case LIGHT_GRAY_WOOL:
            case LIGHT_GRAY_DYE:
                return 8;
            case CYAN_WOOL:
            case CYAN_DYE:
                return 9;
            case PURPLE_WOOL:
            case PURPLE_DYE:
                return 10;
            case BLUE_WOOL:
            case BLUE_DYE:
                return 11;
            case BROWN_WOOL:
            case BROWN_DYE:
                return 12;
            case GREEN_WOOL:
            case GREEN_DYE:
                return 13;
            case RED_WOOL:
            case RED_DYE:
                return 14;
            case BLACK_WOOL:
            case BLACK_DYE:
            default:
                return 15;
        }
    }

    public static int random(int num) {
        return 1 + (int) (Math.random() * num);
    }
}
