// 
// Decompiled by Procyon v0.5.36
// 

package com.ilummc.ooo;

import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import java.util.Map;
import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindow;
import com.google.common.cache.Cache;
import java.util.concurrent.atomic.AtomicInteger;

public class FormId
{
    private static final AtomicInteger ID;
    private static Cache<Integer, FormWindow> cache;
    
    public static void open(final Player player, final FormWindow window) {
        final Optional<Map.Entry<Integer, FormWindow>> any = (Optional<Map.Entry<Integer, FormWindow>>)FormId.cache.asMap().entrySet().stream().filter(entry -> entry.getValue().equals(window)).findAny();
        if (any.isPresent()) {
            FormId.cache.getIfPresent((Object)any.get().getKey());
            player.showFormWindow(window, (int)any.get().getKey());
        }
        else {
            final int id = FormId.ID.getAndDecrement();
            player.showFormWindow(window, id);
            FormId.cache.put((Object)id, (Object)window);
        }
    }
    
    public static Optional<FormWindow> find(final int id) {
        return Optional.ofNullable(FormId.cache.getIfPresent((Object)id));
    }
    
    public static void clear() {
        FormId.cache.invalidateAll();
    }
    
    static {
        ID = new AtomicInteger(-1);
        FormId.cache = (Cache<Integer, FormWindow>)CacheBuilder.newBuilder().expireAfterAccess(60L, TimeUnit.SECONDS).expireAfterWrite(60L, TimeUnit.SECONDS).concurrencyLevel(1).build();
    }
}
