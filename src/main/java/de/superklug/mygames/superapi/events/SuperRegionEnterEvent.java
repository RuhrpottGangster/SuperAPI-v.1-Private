package de.superklug.mygames.superapi.events;

import de.superklug.mygames.superapi.entities.Region;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SuperRegionEnterEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final @Getter Player player;
    private final @Getter Region region;
    
    private boolean cancelled = false;

    public SuperRegionEnterEvent(final Player player, final Region region) {
        this.player = player;
        this.region = region;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
}