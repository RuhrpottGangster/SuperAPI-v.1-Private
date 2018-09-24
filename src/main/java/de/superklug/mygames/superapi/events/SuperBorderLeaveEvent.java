package de.superklug.mygames.superapi.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SuperBorderLeaveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final @Getter Player player;
    private final @Getter Location center;
    private final @Getter int radius;
    
    private boolean cancelled = false;

    public SuperBorderLeaveEvent(final Player player, final int radius, final Location center) {
        this.player = player;
        this.radius = radius;
        this.center = center;
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
