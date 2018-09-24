package de.superklug.mygames.superapi.managers;

import com.google.common.collect.Maps;
import de.superklug.mygames.superapi.SuperAPI;
import de.superklug.mygames.superapi.events.EventListener;
import de.superklug.mygames.superapi.misc.ListenerExecutor;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EventManager {
    
    private final SuperAPI module;
    
    private final Map<EventListener, CopyOnWriteArrayList<ListenerExecutor>> executors;

    public EventManager(final SuperAPI module) {
        this.module = module;
        
        this.executors = Maps.newConcurrentMap();
    }
    
    public void registerEvent(final Class<? extends Event> clazz, final EventListener listener, final EventPriority eventPriority) {
        final ListenerExecutor executor = new ListenerExecutor(clazz, listener);
        
        Bukkit.getPluginManager().registerEvent(clazz, new Listener() {
            
        }, eventPriority, executor, module.getPlugin());
        
        if(!executors.containsKey(listener)) {
            executors.put(listener, new CopyOnWriteArrayList<>());
        }
        
        executors.get(listener).add(executor);
        
    }
    
    public void unregisterEvent(final Class<? extends Event> clazz, final EventListener listener) {
        
        if(!executors.containsKey(listener)) {
            return;
        }
        
        executors.get(listener).stream().filter((executor) -> (executor.getListener().equals(listener))).forEach((executor) -> {
            executor.setDisable(true);
        });
        
    }

}
