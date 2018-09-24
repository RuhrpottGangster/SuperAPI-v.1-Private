package de.superklug.mygames.superapi.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.superklug.mygames.superapi.SuperAPI;
import de.superklug.mygames.superapi.utils.mojang.GameProfileBuilder;
import de.superklug.mygames.superapi.utils.mojang.UUIDFetcher;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

public class NickUtil {
    
    private final SuperAPI module;
    
    private final @Getter List<String> nicknames = Lists.newArrayList();

    /**
     * 
     * @param module The main plugin class
     */
    public NickUtil(final SuperAPI module) {
        this.module = module;
    }
    
    /**
     * Sets this player a new skin
     * 
     * @param player The player
     * @param name The new skin name
     * @param updateSelf A boolean- flag
     */
    public void changeSkin(final Player player, final String name, final boolean updateSelf) {
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile gameProfile = craftPlayer.getProfile();
        
        /**
         * Saved things for the respawn
         */
        final Map<CraftPlayer, Integer> savedHeldItemSlot = Maps.newConcurrentMap();
        final Map<CraftPlayer, ItemStack[]> savedArmorContents = Maps.newConcurrentMap();
        final Map<CraftPlayer, ItemStack[]> savedInventoryContents = Maps.newConcurrentMap();
        final Map<CraftPlayer, Double> savedHealth = Maps.newConcurrentMap();
        final Map<CraftPlayer, Integer> savedFoodlevel = Maps.newConcurrentMap();
        final Map<CraftPlayer, Location> savedLocation = Maps.newConcurrentMap();
        final Map<CraftPlayer, Scoreboard> savedScoreboards = Maps.newConcurrentMap();
        
        savedHeldItemSlot.put(craftPlayer, craftPlayer.getInventory().getHeldItemSlot());
        savedArmorContents.put(craftPlayer, craftPlayer.getInventory().getArmorContents());
        savedInventoryContents.put(craftPlayer, craftPlayer.getInventory().getContents());
        savedHealth.put(craftPlayer, craftPlayer.getHealth());
        savedFoodlevel.put(craftPlayer, craftPlayer.getFoodLevel());
        savedLocation.put(craftPlayer, craftPlayer.getLocation().add(0.0D, 0.75D, 0.0D));
        savedScoreboards.put(craftPlayer, craftPlayer.getScoreboard());
        
        
        try {
            if(UUIDFetcher.getUUID(name) != null) {
                gameProfile = GameProfileBuilder.fetch(UUIDFetcher.getUUID(name));
            } else {
                gameProfile = GameProfileBuilder.fetch(UUIDFetcher.getUUID("Steve"));
            }
        } catch(Exception exception) {}
        
        Collection<Property> properties = gameProfile.getProperties().get("textures");
        craftPlayer.getProfile().getProperties().removeAll("textures");
        craftPlayer.getProfile().getProperties().putAll("textures", properties);
        
        
        destroyPlayer(craftPlayer, updateSelf);
        removePlayerFromTab(craftPlayer, updateSelf);
        
        try {
            if(updateSelf) {
                EntityPlayer entityPlayer = craftPlayer.getHandle();

                module.runTaskLater(() -> {
                    //PacketPlayOutRespawn packetPlayOutRespawn = new PacketPlayOutRespawn(craftPlayer.getPlayer().getWorld().getEnvironment().getId(), entityPlayer.getWorld().getDifficulty(), entityPlayer.getWorld().getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode());
                    //entityPlayer.playerConnection.sendPacket(packetPlayOutRespawn);
                
                    //player.spigot().respawn();

                    entityPlayer.playerConnection.teleport(new Location(craftPlayer.getPlayer().getWorld(), entityPlayer.locX, entityPlayer.locY, entityPlayer.locZ, entityPlayer.yaw, entityPlayer.pitch));
                    craftPlayer.getPlayer().updateInventory();
                }, 4);
            }
        } catch(Exception exception) {}
        
        
        try {
            module.getReflectionUtil().set(craftPlayer.getProfile(), "id", UUID.fromString(craftPlayer.getUniqueId().toString()));
        } catch(Exception exception) {}
        
        module.runTaskLater(() -> {
            addPlayerToTab(craftPlayer, updateSelf);
            spawnPlayer(craftPlayer);
        }, 2L);
        
        
        craftPlayer.teleport(savedLocation.get(craftPlayer));
        craftPlayer.getInventory().setArmorContents(savedArmorContents.get(craftPlayer));
        craftPlayer.getInventory().setContents(savedInventoryContents.get(craftPlayer));
        craftPlayer.setHealth(savedHealth.get(craftPlayer));
        craftPlayer.setFoodLevel(savedFoodlevel.get(craftPlayer));
        craftPlayer.setScoreboard(savedScoreboards.get(craftPlayer));
        craftPlayer.getInventory().setHeldItemSlot(savedHeldItemSlot.get(craftPlayer));
        
    }
    
    /**
     * Sets this player a new name
     * 
     * @param player The player
     * @param color The new color
     * @param name The new name
     * @param updateSelf A boolean- flag
     */
    public void setName(final Player player, final String color, String name, final boolean updateSelf) {
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        
        if(name.length() > 16) {
            name = name.substring(0, 16);
        }
        
        try {
            Field field = craftPlayer.getProfile().getClass().getDeclaredField("name");

            field.setAccessible(true);
            field.set(craftPlayer.getProfile(), name);
        } catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception) {}
        
        player.setDisplayName(color + player.getName());
        
        destroyPlayer(craftPlayer, updateSelf);
        removePlayerFromTab(craftPlayer, updateSelf);
        
        try {
            module.getReflectionUtil().set(craftPlayer.getProfile(), "id", UUID.fromString(craftPlayer.getUniqueId().toString()));
        } catch (Exception exception) {}
        
        module.runTaskLater(() -> {
            addPlayerToTab(craftPlayer, updateSelf);
            spawnPlayer(craftPlayer);
        }, 2L);
        
    }
    
    /**
     * Gives the player a player "rank"/color
     * 
     * @param player The player
     * @param color The new color
     */
    public void toggleRank(final Player player, final String color) {
        
        //TODO: Scoreboard incl.
        
        player.setDisplayName(color + player.getName());
        
    }
    
    /**
     * 
     * @param name The name
     * @return "true" when the name is used and "false" when not
     */
    public boolean isNameUsed(final String name) {
        
        for(Player players : Bukkit.getServer().getOnlinePlayers()) {
            
            if(ChatColor.stripColor(players.getDisplayName()).equalsIgnoreCase(name)) {
                return true;
            }
            
            if(players.getName().equalsIgnoreCase(name)) {
                return true;
            }
            
        }
        
        return false;
    }
    
    public boolean isNameValid(final String name) {
        
        if(name.length() <= 2) {
            return false;
        }
        
        if(name.length() > 16) {
            return false;
        }
        
        
        String replaceAll = name.replaceAll("[^a-zA-Z0-9_]", "");
        
        
        return replaceAll.isEmpty();
    }
    
    /**
     * 
     * @return A random name from the nicknames-list
     */
    public String getRandomName() {
        String randomName = this.nicknames.get(new Random().nextInt(this.nicknames.size()));
        
        if(isNameUsed(randomName)) {
            getRandomName();
        }
        
        return randomName;
    }
    
    /**
     * Easily send packets to a craftplayer
     * 
     * @param craftPlayer The player
     * @param packet The packet
     */
    private void sendPacket(final CraftPlayer craftPlayer, final Packet<?> packet) {
        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }
    
    /**
     * Updated all nicked players for this player
     * 
     * @param player The player
     */
    public void updateAllForPlayer(final Player player) {
        
        //if(!player.hasPermission("nick.bypass")) {
        //    return;
        //}
        
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        
        
        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            final CraftPlayer craftPlayers = (CraftPlayer) players;

            if(!craftPlayers.getUniqueId().equals(craftPlayer.getUniqueId())) {
                
                sendPacket(craftPlayer, new PacketPlayOutEntityDestroy(new int[]{ craftPlayers.getEntityId() }));
                sendPacket(craftPlayer, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{ craftPlayers.getHandle() }));
                
                module.runTaskLater(() -> {
                    sendPacket(craftPlayer, new PacketPlayOutNamedEntitySpawn(craftPlayers.getHandle()));
                    sendPacket(craftPlayer, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{ craftPlayers.getHandle() }));
                }, 2L);
                
            }

        });
        
    }
    
    /**
     * Destroys the player from the server
     * 
     * @param craftPlayer The player
     * @param updateSelf A boolean- flag
     */
    private void destroyPlayer(final CraftPlayer craftPlayer, final boolean updateSelf) {
        
        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            final CraftPlayer craftPlayers = (CraftPlayer) players;
            
            if(!craftPlayers.getUniqueId().equals(craftPlayer.getUniqueId())) {
                //if(!craftPlayers.hasPermission("nick.bypass")) {
                    sendPacket(craftPlayers, new PacketPlayOutEntityDestroy(new int[] { craftPlayer.getEntityId() }));
                //}
            }
            else if(updateSelf) {
                sendPacket(craftPlayers, new PacketPlayOutEntityDestroy(new int[]{ craftPlayer.getEntityId() }));
            }
            
        });
        
    }
    
    /**
     * Destroys the player from the tablist
     *
     * @param craftPlayer The player
     * @param updateSelf A boolean- flag
     */
    private void removePlayerFromTab(final CraftPlayer craftPlayer, final boolean updateSelf) {

        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            final CraftPlayer craftPlayers = (CraftPlayer) players;

            if (!craftPlayers.getUniqueId().equals(craftPlayer.getUniqueId())) {
                //if (!craftPlayers.hasPermission("nick.bypass")) {
                    sendPacket(craftPlayers, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{ craftPlayer.getHandle() }));
                //}
            } else if (updateSelf) {
                sendPacket(craftPlayers, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{ craftPlayer.getHandle() }));
            }

        });

    }
    
    /**
     * Spawns the player on the server
     * 
     * @param craftPlayer The player
     */
    private void spawnPlayer(final CraftPlayer craftPlayer) {
        
        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            final CraftPlayer craftPlayers = (CraftPlayer) players;

            //if((!craftPlayers.getUniqueId().equals(craftPlayer.getUniqueId())) && (!craftPlayers.hasPermission("nick.bypass"))) {
            if(!craftPlayers.getUniqueId().equals(craftPlayer.getUniqueId())) {
                sendPacket(craftPlayers, new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
            }

        });
        
    }
    
    /**
     * Adds the player to the tablist
     *
     * @param craftPlayer The player
     * @param updateSelf A boolean- flag
     */
    private void addPlayerToTab(final CraftPlayer craftPlayer, final boolean updateSelf) {

        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            final CraftPlayer craftPlayers = (CraftPlayer) players;

            if (!craftPlayers.getUniqueId().equals(craftPlayer.getUniqueId())) {
                //if (!craftPlayers.hasPermission("nick.bypass")) {
                    sendPacket(craftPlayers, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{ craftPlayer.getHandle() }));
                //}
            } else if (updateSelf) {
                sendPacket(craftPlayers, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[]{ craftPlayer.getHandle() }));
            }

        });

    }

}
