package de.superklug.mygames.superapi.events;

import org.bukkit.event.Event;

public interface EventListener<T extends Event> {
    
    public void on(T event);

}