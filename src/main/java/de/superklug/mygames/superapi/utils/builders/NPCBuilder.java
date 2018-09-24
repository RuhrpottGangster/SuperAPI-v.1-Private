package de.superklug.mygames.superapi.utils.builders;

import com.mojang.authlib.GameProfile;
import de.superklug.mygames.superapi.SuperAPI;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NPCBuilder {
    
    private final SuperAPI module;
    
    private final EntityPlayer npc;
    private boolean addToTab = false;

    
    /**
     * 
     * @param module The main plugin class
     * @param uuid The uuid
     * @param name The name
     */
    public NPCBuilder(final SuperAPI module, final String uuid, final String name) {
        this.module = module;
        
        final MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        final WorldServer worldServer = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

        npc = new EntityPlayer(minecraftServer, worldServer, new GameProfile(UUID.fromString(uuid), name), new PlayerInteractManager(worldServer));
        
    }
    
    public NPCBuilder addToTablist(final boolean flag) {
        this.addToTab = flag;
        return this;
    }
    
    public NPCBuilder setAbsorptionHearts(final float hearts) {
        npc.setAbsorptionHearts(hearts);
        return this;
    }
    
    public NPCBuilder setAirTicks(final int ticks) {
        npc.setAirTicks(ticks);
        return this;
    }
    
    public NPCBuilder setCustomName(final String name) {
        npc.setCustomName(name);
        return this;
    }
    
    public NPCBuilder setCustomNameVisibile(final boolean flag) {
        npc.setCustomNameVisible(flag);
        return this;
    }
    
    public NPCBuilder setEquipment(final int slot, final ItemStack item) {
        npc.setEquipment(slot, CraftItemStack.asNMSCopy(item));
        return this;
    }
    
    public NPCBuilder setHealth(final float health) {
        npc.setHealth(health);
        return this;
    }
    
    public NPCBuilder setInvisible(final boolean flag) {
        npc.setInvisible(flag);
        return this;
    }
    
    public NPCBuilder setLocation(final Location location) {
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return this;
    }
    
    public NPCBuilder setLocation(final double x, final double y, final double z, final float yaw, final float pitch) {
        npc.setLocation(x, y, z, yaw, pitch);
        return this;
    }
    
    public NPCBuilder setOnFire(final int ticks) {
        npc.setOnFire(ticks);
        return this;
    }
    
    public NPCBuilder setPosition(final double x, final double y, final double z) {
        npc.setPosition(x, y, z);
        return this;
    }
    
    public NPCBuilder setPositionRotation(final double x, final double y, final double z, final float yaw, final float pitch) {
        npc.setPositionRotation(x, y, z, yaw, pitch);
        return this;
    }
    
    public NPCBuilder setScore(final int score) {
        npc.setScore(score);
        return this;
    }
    
    public NPCBuilder setSize(final float f, final float j) {
        npc.setSize(f, j);
        return this;
    }
    
    public NPCBuilder setSneaking(final boolean flag) {
        npc.setSneaking(flag);
        return this;
    }
    
    public NPCBuilder setSpectatorTarget(final Entity entity) {
        npc.setSpectatorTarget(entity);
        return this;
    }
    
    public NPCBuilder setSprinting(final boolean flag) {
        npc.setSprinting(flag);
        return this;
    }
    
    public void showNPCForAll() {
        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            
            if(addToTab) {
                ((CraftPlayer) players).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
            }
            
        });
    }

    public void showNPCForAllTemporary(final long time) {
        showNPCForAll();
        
        module.runTaskLater(this::hideNPCForAll, time);
    }

    public void hideNPCForAll() {
        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
            
            if(addToTab) {
                ((CraftPlayer) players).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc));
            }
            
        });
    }

    public void showNPCForPlayer(final Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        
        if(addToTab) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
        }
        
    }

    public void showNPCForPlayerTemporary(final Player player, final long time) {
        showNPCForPlayer(player);
        
        module.runTaskLater(() -> {
            hideNPCForPlayer(player);
        }, time);
    }

    public void hideNPCForPlayer(final Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        
        if(addToTab) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc));
        }
        
    }

}
