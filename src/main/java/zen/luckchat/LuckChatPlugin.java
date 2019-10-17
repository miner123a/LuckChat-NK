package zen.luckchat;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import cn.nukkit.event.Listener;
import java.util.function.Consumer;

public class LuckChatPlugin extends PluginBase 
{
    static PlaceholderAPI placeholderApi = null;
    static Config config;
    private static String pf = (TextFormat.WHITE + "[ " + TextFormat.AQUA + "E" + TextFormat.DARK_AQUA + "L" + TextFormat.RED + "M" + TextFormat.YELLOW + "E" + TextFormat.AQUA + "N" + TextFormat.RED + "T" + TextFormat.YELLOW + "A" + TextFormat.AQUA + "R" + TextFormat.RED + "Y" + TextFormat.WHITE + " ]" + TextFormat.DARK_AQUA);

       public void onEnable() {
        Elementary.ins = this;
        Locale.load();
        Locale.console("loading_config", new String[0]);
        this.getServer().getCommandMap().register("el", (Command)new Commands());
        this.saveDefaultConfig();
        this.saveResource("default.yml", false);
        this.getConfig().getStringList("gui").forEach(Menus::load);
        Locale.console("load_menu", String.valueOf(Menus.getMenusMap().size()));
        this.getServer().getPluginManager().registerEvents((Listener)new Events(), (Plugin)this);


        // Check for Placeholder API
        try {
            placeholderApi = PlaceholderAPI.getInstance();
        } catch (Throwable ignored) {
        }


    }


