// 
// Decompiled by Procyon v0.5.36
// 

package com.ilummc.ooo;

import cn.nukkit.Server;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import cn.nukkit.command.CommandSender;
import java.util.ArrayList;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import java.util.function.Consumer;
import java.util.Map;

public class Actions
{
    public static Consumer<PlayerFormRespondedEvent> parse(final String text, final Map<String, Object> origin) {
        final List<Consumer<PlayerFormRespondedEvent>> consumers = new ArrayList<Consumer<PlayerFormRespondedEvent>>();
        final Map<String, String> map = map(origin);
        if (map.containsKey("permission")) {
            consumers.add(event -> {
                if (!event.getPlayer().hasPermission((String)map.get("permission"))) {
                    Locale.player("no_permission", (CommandSender)event.getPlayer(), new String[0]);
                    throw new RuntimeException();
                }
                else {
                    return;
                }
            });
        }
        if (map.containsKey("command")) {
            consumers.add(command(map.get("command")));
        }
        return combined(consumers);
    }
    
    private static Consumer<PlayerFormRespondedEvent> combined(final List<Consumer<PlayerFormRespondedEvent>> consumers) {
        final Iterator<Consumer<PlayerFormRespondedEvent>> iterator;
        Consumer<PlayerFormRespondedEvent> consumer;
        return event -> {
            try {
                consumers.iterator();
                while (iterator.hasNext()) {
                    consumer = iterator.next();
                    consumer.accept(event);
                }
            }
            catch (Exception ex) {}
        };
    }
    
    private static Map<String, String> map(final Map<String, Object> map) {
        final Map<String, String> ret = new HashMap<String, String>();
        final String s;
        map.forEach((k, v) -> s = ret.put(k, String.valueOf(v)));
        return ret;
    }
    
    private static Consumer<PlayerFormRespondedEvent> command(final String text) {
        final List<String> collect = Arrays.stream(text.split(";")).map((Function<? super String, ?>)String::trim).filter(s -> !s.isEmpty()).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
        boolean isOp;
        return event -> collect.forEach(cmd -> {
            if (cmd.startsWith("op:")) {
                isOp = event.getPlayer().isOp();
                event.getPlayer().setOp(true);
                Server.getInstance().dispatchCommand((CommandSender)event.getPlayer(), cmd.substring(3).trim());
                event.getPlayer().setOp(isOp);
            }
            else if (cmd.startsWith("console:")) {
                Server.getInstance().dispatchCommand((CommandSender)Server.getInstance().getConsoleSender(), cmd.substring(8).trim());
            }
            else {
                Server.getInstance().dispatchCommand((CommandSender)event.getPlayer(), cmd);
            }
        });
    }
}
