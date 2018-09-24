package de.superklug.mygames.superapi.managers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.superklug.mygames.superapi.SuperAPI;
import de.superklug.mygames.superapi.events.EventListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandManager {
    
    private final SuperAPI module;
    
    private final Map<String, CommandExecutor> commands;

    public CommandManager(final SuperAPI module) {
        this.module = module;
        
        this.commands = Maps.newConcurrentMap();
        
        init();
    }
    
    private void init() {
        module.registerEvent(PlayerCommandPreprocessEvent.class, (EventListener<PlayerCommandPreprocessEvent>) (PlayerCommandPreprocessEvent event) -> {
            final Player player = event.getPlayer();
            final List<String> arguments = Lists.newArrayList(Arrays.asList(event.getMessage().replaceAll("/", "").split(" ")));
            final String command = arguments.remove(0).toLowerCase();
            
            if(commands.containsKey(command)) {
                final CommandExecutor executor = commands.get(command);
                
                executor.onCommand(player, null, command, arguments.toArray(new String[0]));
                event.setCancelled(true);
                
            }
            
        }, EventPriority.NORMAL);
    }
    
    public void onCommand(final String command, final CommandExecutor executor) {
        commands.put(command.toLowerCase(), executor);
    }

}
