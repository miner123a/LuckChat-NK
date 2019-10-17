// 
// Decompiled by Procyon v0.5.36
// 

package com.ilummc.ooo;

import cn.nukkit.Player;
import java.util.function.Consumer;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.VanillaCommand;

public class Commands extends VanillaCommand
{
    Commands() {
        super("el", "§3§lElementary§a plugin command", (String)null, new String[] { "elementary" });
    }
    
    public boolean execute(final CommandSender sender, final String command, final String[] args) {
        if (args.length >= 1 && "reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("elementary.reload")) {
                Locale.player("no_permission", sender, new String[0]);
                return true;
            }
            if (args.length == 1) {
                Elementary.instance().reloadConfig();
                Menus.getMenusMap().clear();
                FormId.clear();
                Events.clear();
                Elementary.instance().getConfig().getStringList("gui").forEach(Menus::load);
                Locale.player("reload_menu", sender, String.valueOf(Menus.getMenusMap().size()));
                return true;
            }
        }
        if (args.length < 1 || !"open".equalsIgnoreCase(args[0])) {
            help(sender);
            return true;
        }
        if (args.length == 1) {
            if (sender.hasPermission("elementary.open.list")) {
                Locale.player("list_menu", sender, new String[0]);
                Menus.getMenusMap().forEach((name, menu) -> sender.sendMessage("§a" + name));
            }
            else {
                Locale.player("no_permission", sender, new String[0]);
            }
            return true;
        }
        if (!(sender instanceof Player)) {
            Locale.player("player_only", sender, new String[0]);
            return true;
        }
        if (!sender.hasPermission("elementary.open")) {
            Locale.player("no_permission", sender, new String[0]);
            return true;
        }
        final String menu = args[1];
        if (Menus.has(menu)) {
            if (sender.hasPermission(Menus.getMenusMap().get(menu).getOpenPermission())) {
                FormId.open((Player)sender, Menus.build(menu));
            }
            else {
                Locale.player("no_permission", sender, new String[0]);
            }
        }
        else {
            Locale.player("no_such_menu", sender, menu);
        }
        return true;
    }
    
    private static void help(final CommandSender sender) {
        for (final String s : Locale.format("help_list", new String[0]).split("\n")) {
            sender.sendMessage(s);
        }
    }
}
