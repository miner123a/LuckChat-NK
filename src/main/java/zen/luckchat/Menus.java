// 
// Decompiled by Procyon v0.5.36
// 

package com.ilummc.ooo;

import java.util.regex.Pattern;
import java.util.HashMap;
import cn.nukkit.form.window.FormWindowSimple;
import java.util.ArrayList;
import cn.nukkit.form.element.ElementButtonImageData;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Optional;
import com.google.common.collect.Lists;
import cn.nukkit.item.Item;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import java.io.File;
import java.util.function.Predicate;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import java.util.function.Consumer;
import cn.nukkit.form.element.ElementButton;
import java.util.List;
import java.util.Map;

public class Menus
{
    private static Map<String, Menus> menusMap;
    private String title;
    private String description;
    private String openPermission;
    private List<ElementButton> elements;
    private List<Consumer<PlayerFormRespondedEvent>> actions;
    private transient FormWindow window;
    private static final Predicate<String> URL;
    
    public static void load(final String name) {
        final File file = new File(Elementary.instance().getDataFolder(), name);
        if (!file.exists()) {
            Locale.console("no_such_menu", name);
            return;
        }
        final Config config = new Config(file, 2);
        final String title = config.getString("title");
        if (title == null) {
            Locale.console("no_title", name);
            return;
        }
        final String description = config.getString("description", title);
        final String command = config.getString("command");
        if (command != null) {
            if (Events.hasOnCommand(command) || Server.getInstance().getCommandMap().getCommand(command) != null) {
                Locale.console("conflict_command", name, command);
            }
            else {
                Events.addOnCommand(command, name);
            }
        }
        final String openPermission = config.getString("open_permission", "elementary.open");
        final Object openItem = config.get("open_item");
        if (openItem != null) {
            try {
                final int id = (int)openItem;
                if (Events.hasOnClick(new Item(id))) {
                    Locale.console("conflict_item", name, String.valueOf(openItem));
                }
                else {
                    Events.addOnClick(new Item(id), name);
                }
            }
            catch (Throwable t) {
                if (!(openItem instanceof String)) {
                    Locale.console("bad_id", name, String.valueOf(openItem));
                    return;
                }
                if (!((String)openItem).matches("[0-9]+:[0-9]+")) {
                    Locale.console("bad_id", name, String.valueOf(openItem));
                    return;
                }
                final String[] split = ((String)openItem).split(":");
                final Item item = new Item(Integer.parseInt(split[0]), Integer.valueOf(Integer.parseInt(split[1])));
                if (Events.hasOnClick(item)) {
                    Locale.console("conflict_item", name, String.valueOf(openItem));
                }
                else {
                    Events.addOnClick(item, name);
                }
            }
        }
        final List buttons = Lists.newArrayList((Iterable)config.getSection("elements").values());
        final Menus menus = loadButtons(title, description, buttons, openPermission);
        Menus.menusMap.put(name, menus);
    }
    
    public static boolean has(final String name) {
        return Menus.menusMap.containsKey(name);
    }
    
    public static FormWindow build(final String name) {
        return Menus.menusMap.get(name).build();
    }
    
    public static Optional<Menus> findByName(final String name) {
        return Optional.ofNullable(Menus.menusMap.get(name));
    }
    
    public static Optional<Menus> findByTitle(final String name) {
        return Menus.menusMap.values().stream().filter(menus -> menus.title.equals(name)).findAny();
    }
    
    public static Optional<Menus> findByWindow(final FormWindow window) {
        return Menus.menusMap.values().stream().filter(menu -> menu.build().equals(window)).findAny();
    }
    
    public static Map<String, Menus> getMenusMap() {
        return Menus.menusMap;
    }
    
    public static void clear() {
        Menus.menusMap.clear();
    }
    
    private static Menus loadButtons(final String title, final String description, final List<Map<String, Object>> list, final String openPermission) {
        final Menus menus = new Menus();
        menus.title = title;
        menus.description = ((description == null) ? "" : description);
        menus.openPermission = openPermission;
        ElementButton button;
        ElementButtonImageData icon1;
        final Menus menus2;
        menus.elements = list.stream().map(map -> {
            if (!map.containsKey("text")) {
                return null;
            }
            else {
                button = new ElementButton(String.valueOf(map.get("text")));
                if (map.containsKey("icon")) {
                    icon1 = parseImage(String.valueOf(map.get("icon")));
                    button.addImage(icon1);
                }
                menus2.actions.add(Actions.parse(String.valueOf(map.get("click")), map));
                return button;
            }
        }).filter(Objects::nonNull).collect((Collector<? super Object, ?, List<ElementButton>>)Collectors.toList());
        return menus;
    }
    
    private Menus() {
        this.actions = new ArrayList<Consumer<PlayerFormRespondedEvent>>();
    }
    
    public Consumer<PlayerFormRespondedEvent> actionOf(final ElementButton button) {
        return this.actions.get(this.elements.indexOf(button));
    }
    
    public String getOpenPermission() {
        return this.openPermission;
    }
    
    public FormWindow build() {
        return (this.window == null) ? (this.window = (FormWindow)new FormWindowSimple(this.title, this.description, (List)this.elements)) : this.window;
    }
    
    private static ElementButtonImageData parseImage(final String path) {
        if (Menus.URL.test(path)) {
            return new ElementButtonImageData("url", path);
        }
        return new ElementButtonImageData("path", path);
    }
    
    static {
        Menus.menusMap = new HashMap<String, Menus>();
        URL = Pattern.compile("^((ht|f)tps?)://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?$").asPredicate();
    }
}
