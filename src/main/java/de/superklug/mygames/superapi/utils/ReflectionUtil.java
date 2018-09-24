package de.superklug.mygames.superapi.utils;

import com.mojang.authlib.GameProfile;
import de.superklug.mygames.superapi.SuperAPI;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReflectionUtil {
    
    private final SuperAPI module;

    /**
     * 
     * @param module The main class
     */
    public ReflectionUtil(final SuperAPI module) {
        this.module = module;
    }
    
    /**
     * 
     * @param player The player
     * @return 
     */
    public String getTexture(final Player player) {
        try {
            String craftPath = Bukkit.getServer().getClass().getPackage().getName();
            Object craftPlayer = Class.forName(craftPath + ".entity.CraftPlayer").cast(player);
            Method getHandle = craftPlayer.getClass().getDeclaredMethod("getHandle");
            Object entityPlayer = getHandle.invoke(craftPlayer);
            Method getProfile = entityPlayer.getClass().getMethod("getProfile");
            GameProfile profile = (GameProfile) getProfile.invoke(entityPlayer);

            return profile.getProperties().get("textures").iterator().next().getValue();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
            return null;
        }
    }

    /**
     * 
     * @param object The object
     * @param name The name/path
     * @param value The new value
     */
    public void set(final Object object, final String name, final Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);

            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ignore) {
        }
    }

    /**
     * 
     * @param object The object
     * @param name The name/path
     * @return 
     */
    public Object get(final Object object, final String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);

            field.setAccessible(true);
            
            return field.get(object);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ignore) {
            return null;
        }
    }

}
