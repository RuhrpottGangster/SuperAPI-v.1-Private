package de.superklug.mygames.superapi.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.superklug.mygames.superapi.SuperAPI;
import de.superklug.mygames.superapi.entities.Region;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RegionUtil {
    
    private final SuperAPI module;
    
    private final @Getter Map<String, Region> regions = Maps.newConcurrentMap();
    
    private final File folder;
    
    private final @Getter File file;
    private @Getter YamlConfiguration configuration;

    public RegionUtil(final SuperAPI module) {
        this.module = module;
        
        this.folder = new File("plugins/" + module.getPlugin().getName());
        
        this.file = new File("plugins/" + module.getPlugin().getName(), "regions.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        
        createConfigFiles();
    }
    
    /**
     * Reloads all regions from the config into a map
     */
    public void reloadRegions() {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        
        if(!this.regions.isEmpty()) {
            this.regions.clear();
        }
        
        try {
            this.configuration.getConfigurationSection("Regions").getKeys(false).forEach((configRegions) -> {

                final Region region = new Region();

                    region.setName(configRegions);
                    region.setFirstLocation(new Location(Bukkit.getWorld(this.configuration.getString("Regions." + configRegions + ".Location.1.World")),
                        this.configuration.getDouble("Regions." + configRegions + ".Location.1.X"),
                        this.configuration.getDouble("Regions." + configRegions + ".Location.1.Y"),
                        this.configuration.getDouble("Regions." + configRegions + ".Location.1.Z")));

                    region.setSecondLocation(new Location(Bukkit.getWorld(this.configuration.getString("Regions." + configRegions + ".Location.2.World")),
                            this.configuration.getDouble("Regions." + configRegions + ".Location.2.X"),
                            this.configuration.getDouble("Regions." + configRegions + ".Location.2.Y"),
                            this.configuration.getDouble("Regions." + configRegions + ".Location.2.Z")));

                    region.setRegionFlags(this.configuration.getStringList("Regions." + configRegions + ".Flags"));

                this.regions.put(region.getName(), region);

            });
        } catch(Exception exception) {}
        
    }
    
    /**
     * Saves/Creates a region
     * 
     * @param name The name
     * @param location1 The first location
     * @param location2 The second location
     */
    public void saveRegion(final String name, final Location location1, final Location location2) {
                
        this.configuration.set("Regions." + name + ".Flags", Lists.newArrayList());
        
        this.configuration.set("Regions." + name + ".Location.1.World", location1.getWorld().getName());
        this.configuration.set("Regions." + name + ".Location.1.X", location1.getX());
        this.configuration.set("Regions." + name + ".Location.1.Y", location1.getY());
        this.configuration.set("Regions." + name + ".Location.1.Z", location1.getZ());
        
        this.configuration.set("Regions." + name + ".Location.2.World", location2.getWorld().getName());
        this.configuration.set("Regions." + name + ".Location.2.X", location2.getX());
        this.configuration.set("Regions." + name + ".Location.2.Y", location2.getY());
        this.configuration.set("Regions." + name + ".Location.2.Z", location2.getZ());
        
        saveConfig();
        reloadRegions();
        
    }
    
    /**
     * Saves/Creates a region
     * 
     * @param name The name
     * @param world The world
     * @param x1 X - 1 Location
     * @param y1 Y - 1 Location
     * @param z1 Z - 1 Location
     * @param x2 X - 2 Location
     * @param y2 Y - 2 Location
     * @param z2 Z - 2 Location
     */
    public void saveRegion(final String name, final String world, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        
        this.configuration.set("Regions." + name + ".Flags", Lists.newArrayList());
        
        this.configuration.set("Regions." + name + ".Location.1.World", world);
        this.configuration.set("Regions." + name + ".Location.1.X", x1);
        this.configuration.set("Regions." + name + ".Location.1.Y", y1);
        this.configuration.set("Regions." + name + ".Location.1.Z", z1);

        this.configuration.set("Regions." + name + ".Location.2.World", world);
        this.configuration.set("Regions." + name + ".Location.2.X", x2);
        this.configuration.set("Regions." + name + ".Location.2.Y", y2);
        this.configuration.set("Regions." + name + ".Location.2.Z", z2);

        saveConfig();
        reloadRegions();
        
    }
    
    /**
     * Deletes a region
     * 
     * @param name The region name 
     */
    public void deleteRegion(final String name) {
        createConfigFiles();
        
        this.configuration.set("Regions." + name, null);
        
        saveConfig();
        reloadRegions();
        
        
    }
    
    /**
     * Check if the region already exists
     * 
     * @param name The region name
     * @return A boolean- flag
     */
    public boolean doesRegionExists(final String name) {
        final AtomicBoolean result = new AtomicBoolean(false);
        
        try {
            this.configuration.getConfigurationSection("Regions").getKeys(false).forEach((configRegions) -> {

                if(configRegions.equalsIgnoreCase(name)) {
                    result.set(true);
                }

            });
        } catch(Exception exception) {}
        
        return result.get();
    }
    
    /**
     * 
     * @param player The player
     * @param name The region name
     * @return A boolean- flag
     */
    public boolean isPlayerIn(final Player player, final String name) {
        final Region region = this.regions.get(name);
        
        final double x = player.getLocation().getX();
        final double y = player.getLocation().getY();
        final double z = player.getLocation().getZ();
        
        final double minX = Math.min(region.getFirstLocation().getX(), region.getSecondLocation().getX());
        final double minY = Math.min(region.getFirstLocation().getY(), region.getSecondLocation().getY());
        final double minZ = Math.min(region.getFirstLocation().getZ(), region.getSecondLocation().getZ());
        
        final double maxX = Math.max(region.getFirstLocation().getX(), region.getSecondLocation().getX());
        final double maxY = Math.max(region.getFirstLocation().getY(), region.getSecondLocation().getY());
        final double maxZ = Math.max(region.getFirstLocation().getZ(), region.getSecondLocation().getZ());
        
        if(x <= maxX && x >= minX) {
            if(y <= maxY && y >= minY) {
                if(z <= maxZ && z >= minZ) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     *
     * @param location The location
     * @param name The region name
     * @return A boolean- flag
     */
    public boolean isIn(final Location location, final String name) {
        final Region region = this.regions.get(name);

        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();

        final double minX = Math.min(region.getFirstLocation().getX(), region.getSecondLocation().getX());
        final double minY = Math.min(region.getFirstLocation().getY(), region.getSecondLocation().getY());
        final double minZ = Math.min(region.getFirstLocation().getZ(), region.getSecondLocation().getZ());

        final double maxX = Math.max(region.getFirstLocation().getX(), region.getSecondLocation().getX());
        final double maxY = Math.max(region.getFirstLocation().getY(), region.getSecondLocation().getY());
        final double maxZ = Math.max(region.getFirstLocation().getZ(), region.getSecondLocation().getZ());

        if (x <= maxX && x >= minX) {
            if (y <= maxY && y >= minY) {
                if (z <= maxZ && z >= minZ) {
                    return true;
                }
            }
        }

        return false;
    }
    
    /**
     *
     * @param entity The entity
     * @param name The region name
     * @return A boolean- flag
     */
    public boolean isEntityIn(final Entity entity, final String name) {
        final Region region = this.regions.get(name);

        final double x = entity.getLocation().getX();
        final double y = entity.getLocation().getY();
        final double z = entity.getLocation().getZ();

        final double minX = Math.min(region.getFirstLocation().getX(), region.getSecondLocation().getX());
        final double minY = Math.min(region.getFirstLocation().getY(), region.getSecondLocation().getY());
        final double minZ = Math.min(region.getFirstLocation().getZ(), region.getSecondLocation().getZ());

        final double maxX = Math.max(region.getFirstLocation().getX(), region.getSecondLocation().getX());
        final double maxY = Math.max(region.getFirstLocation().getY(), region.getSecondLocation().getY());
        final double maxZ = Math.max(region.getFirstLocation().getZ(), region.getSecondLocation().getZ());

        if (x <= maxX && x >= minX) {
            if (y <= maxY && y >= minY) {
                if (z <= maxZ && z >= minZ) {
                    return true;
                }
            }
        }

        return false;
    }
    
    /**
     *
     * @param block The block
     * @param name The region name
     * @return A boolean- flag
     */
    public boolean isBlockIn(final Block block, final String name) {
        final Region region = this.regions.get(name);

        final double x = block.getLocation().getX();
        final double y = block.getLocation().getY();
        final double z = block.getLocation().getZ();

        final double minX = Math.min(region.getFirstLocation().getX(), region.getSecondLocation().getX());
        final double minY = Math.min(region.getFirstLocation().getY(), region.getSecondLocation().getY());
        final double minZ = Math.min(region.getFirstLocation().getZ(), region.getSecondLocation().getZ());

        final double maxX = Math.max(region.getFirstLocation().getX(), region.getSecondLocation().getX());
        final double maxY = Math.max(region.getFirstLocation().getY(), region.getSecondLocation().getY());
        final double maxZ = Math.max(region.getFirstLocation().getZ(), region.getSecondLocation().getZ());

        if (x <= maxX && x >= minX) {
            if (y <= maxY && y >= minY) {
                if (z <= maxZ && z >= minZ) {
                    return true;
                }
            }
        }

        return false;
    }
    
    private void createConfigFiles() {
        
        if(!this.folder.exists()) {
            this.folder.mkdir();
        }
        
        if(!this.file.exists()) {
            try {
                this.file.createNewFile();
                this.configuration.options().copyDefaults(true);
                saveConfig();
            } catch (IOException ex) {}
        }
        
    }
    
    private void saveConfig() {
        try {
            this.configuration.save(this.file);
        } catch (IOException ex) {}
    }

}