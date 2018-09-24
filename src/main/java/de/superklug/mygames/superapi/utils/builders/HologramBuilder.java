package de.superklug.mygames.superapi.utils.builders;

import com.google.common.collect.Lists;
import de.superklug.mygames.superapi.SuperAPI;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class HologramBuilder {

    private final SuperAPI module;

    private final List<EntityArmorStand> armorStands = Lists.newArrayList();
    private final Location location;
    private final double distance;
    private int count;

    /**
     * 
     * @param module The main plugin class
     * @param location The location
     * @param lines The text lines
     */
    public HologramBuilder(final SuperAPI module, final Location location, final String... lines) {
        this.module = module;

        this.location = location;
        this.distance = 0.25D;
        this.count = 0;

        final List<String> hologramLines = Lists.newArrayList();
        hologramLines.addAll(Arrays.asList(lines));

        hologramLines.forEach((text) -> {
            EntityArmorStand myArmorStand = new EntityArmorStand(((CraftWorld) this.location.getWorld()).getHandle(), this.location.getX(), this.location.getY(), this.location.getZ());
            myArmorStand.setCustomName(text);

            if (text.equalsIgnoreCase(" ")) {
                myArmorStand.setCustomNameVisible(false);
            } else {
                myArmorStand.setCustomNameVisible(true);
            }

            myArmorStand.setInvisible(true);
            myArmorStand.setGravity(false);

            this.armorStands.add(myArmorStand);

            this.location.subtract(0, this.distance, 0);
            this.count++;
        });

        for (int i = 0; i < this.count; i++) {
            this.location.add(0, this.distance, 0);
        }
        this.count = 0;
    }

    public void showHologramForAll() {
        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            this.armorStands.forEach((armor) -> {
                ((CraftPlayer) players).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(armor));
            });
        });
    }

    public void showHologramForAllTemporary(final long time) {
        showHologramForAll();
        
        module.runTaskLater(this::hideHologramForAll, time);
    }

    public void hideHologramForAll() {
        Bukkit.getServer().getOnlinePlayers().forEach((players) -> {
            this.armorStands.forEach((armor) -> {
                ((CraftPlayer) players).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armor.getId()));
            });
        });
    }

    public void showHologramForPlayer(final Player player) {
        this.armorStands.forEach((armor) -> {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(armor));
        });
    }

    public void showHologramForPlayerTemporary(final Player player, final long time) {
        showHologramForPlayer(player);
        
        module.runTaskLater(() -> {
            hideHologramForPlayer(player);
        }, time);
    }

    public void hideHologramForPlayer(final Player player) {
        this.armorStands.forEach((armor) -> {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armor.getId()));
        });
    }

}
