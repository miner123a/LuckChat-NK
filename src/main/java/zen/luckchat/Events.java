// 
// Decompiled by Procyon v0.5.36
// 

package com.ilummc.ooo;

import java.util.HashMap;
import cn.nukkit.form.window.FormWindow;
import java.util.Iterator;
import java.util.Optional;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.EventHandler;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.item.Item;
import java.util.Map;
import cn.nukkit.event.Listener;

public class Events implements Listener
{
    private static Map<Item, String> itemOpen;
    private static Map<String, String> commandOpen;
    
    @EventHandler
    public void onResponse(final PlayerFormRespondedEvent event) {
        if (event.wasClosed()) {
            return;
        }
        if (event.getResponse() == null || !(event.getResponse() instanceof FormResponseSimple)) {
            return;
        }
        final FormResponseSimple response;
        FormId.find(event.getFormID()).ifPresent(window -> Menus.findByWindow(window).ifPresent(menus -> {
            response = (FormResponseSimple)event.getResponse();
            menus.actionOf(response.getClickedButton()).accept(event);
        }));
    }
    
    @EventHandler
    public void onClick(final PlayerInteractEvent event) {
        if (event.getItem() != null) {
            this.matchId(event.getItem()).ifPresent(name -> Menus.findByName(name).ifPresent(menus -> {
                if (event.getPlayer().hasPermission(menus.getOpenPermission())) {
                    FormId.open(event.getPlayer(), menus.build());
                }
            }));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        if (hasOnCommand(event.getMessage().substring(1))) {
            Menus.findByName(Events.commandOpen.get(event.getMessage().substring(1))).ifPresent(menus -> {
                if (event.getPlayer().hasPermission(menus.getOpenPermission())) {
                    FormId.open(event.getPlayer(), menus.build());
                    event.setCancelled();
                }
            });
        }
    }
    
    private Optional<String> matchId(final Item item) {
        for (final Map.Entry<Item, String> entry : Events.itemOpen.entrySet()) {
            if (entry.getKey().getId() == item.getId()) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }
    
    public static boolean hasOnClick(final Item item) {
        for (final Map.Entry<Item, String> entry : Events.itemOpen.entrySet()) {
            if (entry.getKey().equals(item, false, false)) {
                return true;
            }
        }
        return false;
    }
    
    public static void addOnClick(final Item item, final String name) {
        Events.itemOpen.put(item, name);
    }
    
    public static void addOnCommand(final String command, final String name) {
        Events.commandOpen.put(command, name);
    }
    
    public static boolean hasOnCommand(final String command) {
        return Events.commandOpen.containsKey(command);
    }
    
    public static void clear() {
        Events.commandOpen.clear();
        Events.itemOpen.clear();
    }
    
    static {
        Events.itemOpen = new HashMap<Item, String>();
        Events.commandOpen = new HashMap<String, String>();
    }
}
