package de.superklug.mygames.superapi.utils.builders;

import de.superklug.mygames.superapi.SuperAPI;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

public class LocationBuilder {
    
    private final SuperAPI module;
    
    private final @Getter File file;
    private final @Getter YamlConfiguration configuration;

    public LocationBuilder(final SuperAPI module, final File file) {
        this.module = module;
        
        this.file = file;
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }
    
    public boolean doesLocationExists(final String name) {
        return (configuration.getString(name + ".World") != null);
    }
    
    public Location getLocation(final String name) {
        return new Location(Bukkit.getWorld(configuration.getString(name + ".World")), configuration.getDouble(name + ".X"), configuration.getDouble(name + ".Y"), configuration.getDouble(name + ".Z"), (float) configuration.getDouble(name + ".Yaw"), (float) configuration.getDouble(name + ".Pitch"));
    }
    
    public void setLocation(final String name, final Location location) {
        inizializeFile();
        
        configuration.set(name + ".World", location.getWorld().getName());
        configuration.set(name + ".X", location.getX());
        configuration.set(name + ".Y", location.getY());
        configuration.set(name + ".Z", location.getZ());
        configuration.set(name + ".Yaw", location.getYaw());
        configuration.set(name + ".Pitch", Float.valueOf(0));
        saveFile();
        
    }
    
    private void inizializeFile() {
        
        try {
            if(!this.file.getParentFile().exists()) {
                this.file.getParentFile().createNewFile();
            }
            if(!this.file.exists()) {
                this.file.createNewFile();
                this.configuration.options().copyDefaults(true);
                saveFile();
            }
        } catch (IOException ex) {
        }
        
    }
    
    private void saveFile() {
        try {
            this.configuration.save(this.file);
        } catch (IOException ex) {}
    }

}
