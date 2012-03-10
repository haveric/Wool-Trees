package haveric.woolTrees;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;

public class Perms {
    static Permission perm = null;
    
    public static String permAdjust    = "wooltrees.adjust";
    public static String permAdjustAlt = "woolTrees.adjust";
    
    public static String permPlant    = "wooltrees.plant";
    public static String permPlantAlt = "woolTrees.plant";
    
    public static String permIC     = "wooltrees.ignorecost";
    public static String permICAlt1 = "woolTrees.ignorecost";
    public static String permICAlt2 = "wooltrees.ignoreCost";
    public static String permICAlt3 = "woolTrees.ignoreCost";
    
    public static void setPerm(Permission newPerm){
    	perm = newPerm;
    }
    
    public static Permission getPerm(){
    	return perm;
    }
    
    public static boolean permEnabled(){
    	return (perm != null);
    }
    
    public static boolean hasAdjust(Player player){
    	if (permEnabled()){
    		return perm.has(player, permAdjust) || perm.has(player, permAdjustAlt);
    	} else {
    		return false;
    	}
    }
    
    public static boolean hasPlant(Player player){
    	if (permEnabled()){
    		return perm.has(player, permPlant) || perm.has(player, permPlantAlt);
    	} else {
    		return false;
    	}
    }
    
    public static boolean hasIC(Player player){
    	if (permEnabled()){
    		return perm.has(player, permIC) || perm.has(player, permICAlt1) || perm.has(player, permICAlt2) || perm.has(player, permICAlt3);
    	} else {
    		return false;
    	}
    }
}
