package de.superklug.mygames.superapi.listeners;

import de.superklug.mygames.superapi.SuperAPI;
import de.superklug.mygames.superapi.entities.BorderPlayer;
import de.superklug.mygames.superapi.events.EventListener;
import de.superklug.mygames.superapi.events.SuperBorderLeaveEvent;
import de.superklug.mygames.superapi.events.SuperRegionEnterEvent;
import de.superklug.mygames.superapi.events.SuperRegionLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    
    private final SuperAPI module;

    public PlayerMoveListener(final SuperAPI module) {
        this.module = module;
        
        border();
        region();
    }
    
    //<editor-fold defaultstate="collapsed" desc="border">
    private void border() {
        
        module.registerEvent(PlayerMoveEvent.class, (EventListener<PlayerMoveEvent>) (PlayerMoveEvent event) -> {
            final Player player = event.getPlayer();
            
            /**
             * Has this player a arena border?
             */
            if (module.getArenaBorderUtil().getBorders().containsKey(player)) {
                
                /**
                 * The BorderPlayer entity from the map with all borders
                 */
                final BorderPlayer get = module.getArenaBorderUtil().getBorders().get(player);
                
                /**
                 * Is the player outside or in the border?
                 */
                if (get.getCenter().distance(player.getLocation()) >= get.getRadius()) {
                    
                    Bukkit.getPluginManager().callEvent(new SuperBorderLeaveEvent(player, get.getRadius(), get.getCenter()));
                    
                }
                
            }
            
        }, EventPriority.HIGH);
        
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="region">
    private void region() {
        
        module.registerEvent(PlayerMoveEvent.class, (EventListener<PlayerMoveEvent>) (PlayerMoveEvent event) -> {
            final Player player = event.getPlayer();
            
            final Location oldLocation = event.getFrom();
            final Location newLocation = event.getTo();
            
            if(!module.getRegionUtil().getRegions().isEmpty()) {
                module.getRegionUtil().getRegions().keySet().forEach((configRegions) -> {

                    if(!module.getRegionUtil().isIn(oldLocation, configRegions) && module.getRegionUtil().isIn(newLocation, configRegions)) {
                        Bukkit.getPluginManager().callEvent(new SuperRegionEnterEvent(player, module.getRegionUtil().getRegions().get(configRegions)));
                    }

                    if(module.getRegionUtil().isIn(oldLocation, configRegions) && !module.getRegionUtil().isIn(newLocation, configRegions)) {
                        Bukkit.getPluginManager().callEvent(new SuperRegionLeaveEvent(player, module.getRegionUtil().getRegions().get(configRegions)));
                    }

                });
            }
            
        }, EventPriority.HIGH);
        
    }
    //</editor-fold>

}
