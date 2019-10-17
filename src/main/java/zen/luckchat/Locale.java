// 
// Decompiled by Procyon v0.5.36
// 

package com.ilummc.ooo;

import java.util.HashMap;
import cn.nukkit.Server;
import com.ilummc.ooo.util.Strings;
import cn.nukkit.command.CommandSender;
import java.io.IOException;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.util.Map;

public class Locale
{
    private static Map<String, String> text;
    private static final String PREFIX = "§6[Elementary] ";
    
    static void load() {
        final File file = new File(Elementary.instance().getDataFolder(), "locale.yml");
        if (!file.exists()) {
            Elementary.instance().saveResource("locale.yml");
        }
        final Yaml yaml = new Yaml();
        try {
            final String s = Files.toString(file, StandardCharsets.UTF_8);
            final Object load = yaml.load(s);
            if (load instanceof Map) {
                final String s2;
                ((Map)load).forEach((o1, o2) -> s2 = Locale.text.put(String.valueOf(o1), translateAlternateColorCodes('&', String.valueOf(o2))));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void player(final String path, final CommandSender player, final String... args) {
        player.sendMessage("§6[Elementary] " + Strings.format(Locale.text.getOrDefault(path, path), args));
    }
    
    public static void console(final String path, final String... args) {
        Server.getInstance().getConsoleSender().sendMessage("§6[Elementary] " + format(path, args));
    }
    
    public static String format(final String path, final String... args) {
        return Strings.format(Locale.text.getOrDefault(path, path), args);
    }
    
    public static String translateAlternateColorCodes(final char altColorChar, final String textToTranslate) {
        final char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
    
    static {
        Locale.text = new HashMap<String, String>();
    }
}
