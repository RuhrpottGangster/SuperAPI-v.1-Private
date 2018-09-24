package de.superklug.mygames.superapi.utils;

import com.google.common.collect.Maps;
import de.superklug.mygames.superapi.SuperAPI;
import de.superklug.mygames.superapi.entities.BorderPlayer;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ArenaBorderUtil {
    
    private final SuperAPI module;
    
    private final @Getter Map<Player, BorderPlayer> borders = Maps.newConcurrentMap();

    /**
     * 
     * @param module The main plugin class
     */
    public ArenaBorderUtil(final SuperAPI module) {
        this.module = module;
    }
    
    /**
     * Creates a arena border for the player (He can't get out)
     * 
     * @param player The player
     * @param radius The radius
     * @param center The middle/center Location
     */
    public void createBorder(final Player player, final int radius, final Location center) {
        deleteBorder(player);
        
        {
            BorderPlayer borderPlayer = new BorderPlayer();
            
                borderPlayer.setRadius(radius);
                borderPlayer.setCenter(center);
                
            this.borders.put(player, borderPlayer);
        }
    }
    
    /**
     * Creates a fake wall (Only this player see it)
     * 
     * @param player The player
     * @param material The material
     * @param x1 The X-1 Coordinate
     * @param y1 The Y-1 Coordinate
     * @param z1 The Z-1 Coordinate
     * @param x2 The X-2 Coordinate
     * @param y2 The Y-2 Coordinate
     * @param z2 The Z-2 Coordinate
     */
    public void createFakeWall(final Player player, final Material material, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        
        final double minX = Math.min(x1, x2);
        final double minY = Math.min(y1, y2);
        final double minZ = Math.min(z1, z2);
        
        final double maxX = Math.max(x1, x2);
        final double maxY = Math.max(y1, y2);
        final double maxZ = Math.max(z1, z2);
        
        
        for(double x = minX; x <= maxX; x++) {
            for(double y = minY; y <= maxY; y++) {
                for(double z = minZ; z <= maxZ; z++) {
                    
                    player.sendBlockChange(new Location(player.getWorld(), x, y, z), material, (byte) 0);;
                    
                }
            } 
        } 
        
    }
    
    /**
     * Deletes the arena border for the player
     * 
     * @param player The player
     */
    public void deleteBorder(final Player player) {
        if(this.borders.containsKey(player))
            this.borders.remove(player);
    }

}
