package haveric.woolTrees;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    // Commands
    private static String cmdMain    = "wooltrees";
    private String cmdMainAlt = "wt";

    private String cmdHelp = "help";

    private String cmdTree  = "tree";
    private String cmdWool  = "wool";
    private String cmdBig   = "big";
    private String cmdCost  = "cost";
    private String cmdCheck = "check";
    private String cmdLight = "light";
    private String cmdPattern = "pattern";
    private String cmdWoolTrunk = "trunk";
    private String cmdHere = "here";

    private WoolTrees plugin;

    public Commands(WoolTrees wt) {
        plugin = wt;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        ChatColor msgColor = ChatColor.DARK_AQUA;
        ChatColor valColor = ChatColor.GOLD;
        ChatColor defColor = ChatColor.WHITE;
        String wtTitle = msgColor + "[" + ChatColor.GRAY + "WoolTrees" + msgColor + "] ";

        if (Perms.canAdjust((Player) sender)) {
            if (commandLabel.equalsIgnoreCase(cmdMain) || commandLabel.equalsIgnoreCase(cmdMainAlt)) {

                if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(cmdHelp))) {
                    sender.sendMessage(wtTitle + "github.com/haveric/wool-trees - v" + plugin.getDescription().getVersion());
                    sender.sendMessage("/" + cmdMain + " " + cmdTree  + " <0-100> (" + valColor + Config.getTree() + defColor + ") - " + msgColor + "% of a wool tree spawning.");
                    sender.sendMessage("/" + cmdMain + " " + cmdWool  + " <0-100> (" + valColor + Config.getWool() + defColor + ") - " + msgColor + "% wool blocks kept.");
                    sender.sendMessage("/" + cmdMain + " " + cmdBig   + " <0-100> (" + valColor + Config.getBig() + defColor + ") - " + msgColor + "% big trees.");
                    sender.sendMessage("/" + cmdMain + " " + cmdCost  + " <0+> ("    + valColor + Config.getCost() + defColor + ") - " + msgColor + "Cost to plant a tree.");
                    sender.sendMessage("/" + cmdMain + " " + cmdCheck + " <true,false> (" + valColor + Config.isHeightEnabled() + defColor + ") - " + msgColor + "Height Check.");
                    sender.sendMessage("/" + cmdMain + " " + cmdLight + " <0-15> ("  + valColor + Config.getLight() + defColor + ") - " + msgColor + "Light Level.");
                    sender.sendMessage("/" + cmdMain + " " + cmdPattern + " <true,false> (" + valColor + Config.isPatternEnabled() + defColor + ") - " + msgColor + "Pattern Trees.");
                    sender.sendMessage("/" + cmdMain + " " + cmdWoolTrunk + " <true,false> (" + valColor + Config.isWoolTrunksEnabled() + defColor + ") - " + msgColor + "Wool Trunks.");
                    sender.sendMessage("/" + cmdMain + " " + cmdHere + " [wool%] [big] [color]" + defColor + " - " + msgColor + "Create Tree at Mouse.");
                } else if (args.length >= 1 && args[0].equalsIgnoreCase(cmdHere)) {
                    boolean big = false;
                    ArrayList<Integer> colorArray = new ArrayList<Integer>();
                    Double val = 100.0;
                    for (int i = 1; i < args.length; i++) {
                        if (args[i].equalsIgnoreCase("big")) {
                            big = true;
                        } else if (args[i].equalsIgnoreCase("white")) {
                            colorArray.add(0);
                        } else if (args[i].equalsIgnoreCase("orange")) {
                            colorArray.add(1);
                        } else if (args[i].equalsIgnoreCase("magenta")) {
                            colorArray.add(2);
                        } else if (args[i].equalsIgnoreCase("lightblue")) {
                            colorArray.add(3);
                        } else if (args[i].equalsIgnoreCase("yellow")) {
                            colorArray.add(4);
                        } else if (args[i].equalsIgnoreCase("lightgreen")) {
                            colorArray.add(5);
                        } else if (args[i].equalsIgnoreCase("pink")) {
                            colorArray.add(6);
                        } else if (args[i].equalsIgnoreCase("gray") || args[i].equalsIgnoreCase("grey")) {
                            colorArray.add(7);
                        } else if (args[i].equalsIgnoreCase("lightgray") || args[i].equalsIgnoreCase("lightgrey")) {
                            colorArray.add(8);
                        } else if (args[i].equalsIgnoreCase("cyan")) {
                            colorArray.add(9);
                        } else if (args[i].equalsIgnoreCase("purple")) {
                            colorArray.add(10);
                        } else if (args[i].equalsIgnoreCase("blue")) {
                            colorArray.add(11);
                        } else if (args[i].equalsIgnoreCase("brown")) {
                            colorArray.add(12);
                        } else if (args[i].equalsIgnoreCase("green") || args[i].equalsIgnoreCase("darkgreen")) {
                            colorArray.add(13);
                        } else if (args[i].equalsIgnoreCase("red")) {
                            colorArray.add(14);
                        } else if (args[i].equalsIgnoreCase("black")) {
                            colorArray.add(15);
                        } else {
                            try {
                                val = Double.parseDouble(args[i]);
                            } catch (NumberFormatException e) {
                                val = 100.0;
                            }
                            if (val < 0 || val > 100) {
                                val = 100.0;
                            }
                        }
                    }
                    if (colorArray.isEmpty()) {
                        colorArray.add(0);
                    }
                    Block b = ((Player) sender).getTargetBlock(null, 100);
                    if (b.getType() == Material.AIR) {
                        sender.sendMessage(wtTitle + "Out of range. Try getting closer");
                    } else {
                        if (big) {
                            WTPlayerInteract.makeBigTree(((Player) sender).getWorld(), 0, 0, b.getX(), b.getY(), b.getZ(), colorArray, val);
                        } else {
                            WTPlayerInteract.makeNormalTree(((Player) sender).getWorld(), 0, 0, b.getX(), b.getY(), b.getZ(), colorArray, val);
                        }
                    }
                } else if (args.length == 2) {
                    Double val;
                    String msg = "";
                    String err = "";

                    if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                        val = -1.0;
                    } else {
                        try {
                            val = Double.parseDouble(args[1]);
                        } catch (NumberFormatException e) {
                            val = 0.0;
                        }
                    }

                    if (val < 0) {
                        if (val == -1.0 && args[0].equalsIgnoreCase(cmdCheck)) {
                            if (args[1].equalsIgnoreCase("true")) {
                                Config.setHeight(args[1]);
                                msg = "Height Check set to true ";
                            } else if (args[1].equalsIgnoreCase("false")) {
                                Config.setHeight(args[1]);
                                msg = "Height Check set to false ";
                            } else {
                                err = "Value is not true or false";
                            }
                        } else if (val == -1.0 && args[0].equalsIgnoreCase(cmdPattern)) {
                            if (args[1].equalsIgnoreCase("true")) {
                                Config.setPatternEnabled(true);
                                msg = "Pattern Trees set to true ";
                            } else if (args[1].equalsIgnoreCase("false")) {
                                Config.setPatternEnabled(false);
                                msg = "Pattern Trees set to false";
                            } else {
                                err = "Value is not true or false";
                            }
                        } else if (val == -1.0 && args[0].equalsIgnoreCase(cmdWoolTrunk)) {
                            if (args[1].equalsIgnoreCase("true")) {
                                Config.setWoolTrunk(args[1]);
                                msg = "Wool Trunks set to true";
                            } else if (args[1].equalsIgnoreCase("false")) {
                                Config.setWoolTrunk(args[1]);
                                msg = "Wool Trunks set to false";
                            } else {
                                err = "Value is not true or false";
                            }
                        } else {
                            err = "Value cannot be below 0";
                        }
                    } else if (args[0].equalsIgnoreCase(cmdTree)) {
                        if (val <= 100) {
                            Config.setTree(val);
                            msg = "Tree Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase(cmdWool)) {
                        if (val <= 100) {
                            Config.setWool(val);
                            msg = "Wool Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase(cmdBig)) {
                        if (val <= 100) {
                            Config.setBig(val);
                            msg = "Big Tree Spawn % set to: ";
                        } else {
                            err = "Value cannnot be above 100";
                        }
                    } else if (args[0].equalsIgnoreCase(cmdCost)) {
                        Config.setCost(val);
                        msg = "Cost set to: ";
                    } else if (args[0].equalsIgnoreCase(cmdLight)) {
                        if (val >= 0 && val <= 15) {
                            Config.setLight(val.intValue());
                            msg = "Light Level set to: ";
                        } else {
                            err = "Value is less than 0 or above 15.";
                        }
                    }

                    Config.saveConfig();

                    if (msg != "") {
                        if (val != -1.0) {
                            sender.sendMessage(wtTitle + ChatColor.WHITE + msg + msgColor + val);
                        } else {
                            sender.sendMessage(wtTitle + ChatColor.WHITE + msg);
                        }
                    } else if (err != "") {
                        sender.sendMessage(wtTitle + ChatColor.RED + err);
                    }
                }
            }
        } else {
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(cmdHelp))) {
                sender.sendMessage(wtTitle + "github.com/haveric/wool-trees - v" + plugin.getDescription().getVersion());
                sender.sendMessage("Dye saplings to grow wool trees.");
                sender.sendMessage("Each sapling has a " + valColor + Config.getTree() + "% " + defColor + "chance to spawn");
                sender.sendMessage("Each tree will have " + valColor + Config.getWool() + "% " + defColor + "wool leaves");
                if (plugin.getEcon() != null) {
                    if (Perms.hasIC((Player) sender)) {
                        sender.sendMessage("Each successful tree costs " + valColor + "nothing!");
                    } else {
                        sender.sendMessage("Each successful tree costs " + valColor + Config.getCost());
                    }
                }
            } else {
                sender.sendMessage(wtTitle + ChatColor.RED + "You do not have access to this command or it may not exist.");
            }
        }
        return false;
    }

    public static String getMain() {
        return cmdMain;
    }
}
