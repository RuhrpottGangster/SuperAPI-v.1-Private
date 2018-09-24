package de.superklug.mygames.superapi.misc;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import de.superklug.mygames.superapi.events.EventListener;

public class ListenerExecutor implements EventExecutor {
    
    private final Class<? extends Event> clazz;
    
    private final @Getter EventListener listener;
    
    private @Setter boolean disable;

    public ListenerExecutor(final Class<? extends Event> clazz, final EventListener listener) {
        this.clazz = clazz;
        this.listener = listener;
    }

    @Override
    public void execute(Listener ll, Event event) throws EventException {
        if(disable) {
            event.getHandlers().unregister(ll);
            return;
        }
        if(clazz.equals(event.getClass())) {
            listener.on(event);
        }
    }

}
