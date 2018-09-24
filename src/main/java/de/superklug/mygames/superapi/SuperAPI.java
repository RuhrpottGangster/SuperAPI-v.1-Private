package de.superklug.mygames.superapi;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import de.superklug.mygames.superapi.events.EventListener;
import de.superklug.mygames.superapi.listeners.PlayerMoveListener;
import de.superklug.mygames.superapi.managers.CommandManager;
import de.superklug.mygames.superapi.managers.EventManager;
import de.superklug.mygames.superapi.utils.ArenaBorderUtil;
import de.superklug.mygames.superapi.utils.NickUtil;
import de.superklug.mygames.superapi.utils.ReflectionUtil;
import de.superklug.mygames.superapi.utils.RegionUtil;
import de.superklug.mygames.superapi.utils.builders.HologramBuilder;
import de.superklug.mygames.superapi.utils.builders.ItemBuilder;
import de.superklug.mygames.superapi.utils.builders.LocationBuilder;
import de.superklug.mygames.superapi.utils.builders.NPCBuilder;
import de.superklug.mygames.superapi.utils.builders.ScoreboardBuilder;
import de.superklug.mygames.superapi.utils.mojang.UUIDFetcher;
import java.io.File;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.parser.JSONParser;

public class SuperAPI {
    
    private final @Getter Plugin plugin;
    private final @Getter long serverStartTime;
    
    private final @Getter String prefix;
    private final @Getter String messageColor;
    private final @Getter String highlightColor;
    private final @Getter String noPermissions = "§cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.";
    private final @Getter String unknownCommand;
    
    private final @Getter Gson gson = new Gson();
    private final @Getter JSONParser parser = new JSONParser();
    private final @Getter ExecutorService executorService = Executors.newCachedThreadPool();
    private final @Getter SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    
    private final @Getter ReflectionUtil reflectionUtil;
    private final @Getter ArenaBorderUtil arenaBorderUtil;
    private final @Getter NickUtil nickUtil;
    private final @Getter RegionUtil regionUtil;
    
    private final EventManager eventManager;
    private final CommandManager commandManager;
    
    //TODO: NPCBuilder fixen (Es spawnt nichts!)
    
    // Schematic Util

    /**
     * 
     * @param plugin The main plugin which used the API
     * @param serverStartTime The time when the server starts
     * @param prefix The prefix
     * @param normalColor The color for normal messages
     * @param highlightColor The color for special things in messages
     */
    public SuperAPI(final Plugin plugin, final long serverStartTime, final String prefix, final String normalColor, final String highlightColor) {
        this.plugin = plugin;
        this.serverStartTime = serverStartTime;
        
        this.prefix = prefix;
        this.messageColor = normalColor;
        this.highlightColor = highlightColor;
        this.unknownCommand = messageColor + "Dieser Befehl existiert nicht§8.";
        
        this.reflectionUtil = new ReflectionUtil(this);
        this.arenaBorderUtil = new ArenaBorderUtil(this);
        this.nickUtil = new NickUtil(this);
        this.regionUtil = new RegionUtil(this);
        this.regionUtil.reloadRegions();
        
        this.eventManager = new EventManager(this);
        this.commandManager = new CommandManager(this);
        
        runTaskTimer(() -> {
            UUIDFetcher.clearCache();
        }, 20 * 600, 20 * 600);
        
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        
            pluginManager.registerEvents(new PlayerMoveListener(this), plugin);
            
            
    }
    
    //<editor-fold defaultstate="collapsed" desc="format">
    /**
     * 
     * @param pattern The message with the patterns ( e.g. {0}, {1}, {2}, {..} )
     * @param objects The objects which will be replace the patterns
     * @return A formatted String
     */
    public String format(final String pattern, final Object... objects) {
        final String string = MessageFormat.format(pattern, objects);

        assert string != null;

        return string;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="actionbar">
    /**
     * Sends the player an actionbar message
     * 
     * @param player The online player
     * @param message The message
     */
    public void actionbar(final Player player, final String message) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTask">
    /**
     * 
     * @param runnable The runnable
     * @return A new BukkitTask
     */
    public BukkitTask runTask(Runnable runnable) {
        return Bukkit.getServer().getScheduler().runTask(plugin, runnable);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskAsync">
    /**
     *
     * @param runnable The runnable
     * @return A new "run task asynchronously" BukkitTask
     */
    public BukkitTask runTaskAsync(Runnable runnable) {
        return Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskLater">
    /**
     *
     * @param runnable The runnable
     * @param delay The delay
     * @return A new "run task later" BukkitTask
     */
    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskLaterAsync">
    /**
     *
     * @param runnable The runnable
     * @param delay The delay
     * @return A new "run task later asynchronously" BukkitTask
     */
    public BukkitTask runTaskLaterAsync(Runnable runnable, long delay) {
        return Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskTimer">
    /**
     *
     * @param runnable The runnable
     * @param delay The delay
     * @param repeat The repeat delay
     * @return A new "run task timer" BukkitTask
     */
    public BukkitTask runTaskTimer(Runnable runnable, long delay, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, repeat);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="runTaskTimerAsync">
    /**
     *
     * @param runnable The runnable
     * @param delay The delay
     * @param repeat The repeat delay
     * @return A new "run task timer asynchronously" BukkitTask
     */
    public BukkitTask runTaskTimerAsync(Runnable runnable, long delay, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, repeat);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="async">
    /**
     * 
     * @param runnable The runnable
     */
    public void async(final Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setMetadata">
    /**
     * Sets new metadata value(s) to path from the player
     * 
     * @param player The player
     * @param name The name of the metadata path
     * @param value The new value of the metadata path
     */
    public void setMetadata(final Player player, final String name, final Object value) {
        removeMetadata(player, name);
        player.setMetadata(name, new FixedMetadataValue(plugin, value));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeMetadata">
    /**
     * Removes the value(s) from the metadata path
     * 
     * @param player The player
     * @param name The name of the metadata path
     */
    public void removeMetadata(final Player player, final String name) {
        if (player.hasMetadata(name)) {
            player.removeMetadata(name, plugin);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setMetadata">
    /**
     * Sets new metadata value(s) to path from the player
     *
     * @param entity The entity
     * @param name The name of the metadata path
     * @param value The new value of the metadata path
     */
    public void setMetadata(final Entity entity, final String name, final Object value) {
        removeMetadata(entity, name);
        entity.setMetadata(name, new FixedMetadataValue(plugin, value));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeMetadata">
    /**
     * Removes the value(s) from the metadata path
     *
     * @param entity The entity
     * @param name The name of the metadata path
     */
    public void removeMetadata(final Entity entity, final String name) {
        if (entity.hasMetadata(name)) {
            entity.removeMetadata(name, plugin);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="setMetadata">
    /**
     * Sets new metadata value(s) to path from the player
     *
     * @param block The block
     * @param name The name of the metadata path
     * @param value The new value of the metadata path
     */
    public void setMetadata(final Block block, final String name, final Object value) {
        removeMetadata(block, name);
        block.setMetadata(name, new FixedMetadataValue(plugin, value));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeMetadata">
    /**
     * Removes the value(s) from the metadata path
     *
     * @param block The block
     * @param name The name of the metadata path
     */
    public void removeMetadata(final Block block, final String name) {
        if (block.hasMetadata(name)) {
            block.removeMetadata(name, plugin);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fakeEquipment">
    /**
     *
     * @param player The players, who can see the result
     * @param entityId The player, who wears the fake equip
     * @param slot The slot id
     * @param item The itemstack
     */
    public void fakeEquipment(final Player player, final int entityId, final int slot, final ItemStack item) {
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(entityId, slot, CraftItemStack.asNMSCopy(item));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityEquipment);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="npc">
    /**
     *
     * @param uuid The uuid
     * @param name The name
     * @return The NPCBuilder class
     */
    public NPCBuilder npc(final String uuid, final String name) {
        return new NPCBuilder(this, uuid, name);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="item">
    /**
     * 
     * @param type The material type
     * @param amount The item amount
     * @param damage The item damage/subId
     * @return The ItemBuilder class
     */
    public ItemBuilder item(final Material type, final int amount, final short damage) {
        return new ItemBuilder(this, type, amount, damage);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="item">
    /**
     *
     * @param type The material type
     * @param amount The item amount
     * @return The ItemBuilder class
     */
    public ItemBuilder item(final Material type, final int amount) {
        return new ItemBuilder(this, type, amount);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="item">
    /**
     *
     * @param type The material type
     * @return The ItemBuilder class
     */
    public ItemBuilder item(final Material type) {
        return new ItemBuilder(this, type);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="hologram">
    /**
     * 
     * @param location The hologram location
     * @param lines The hologram lines
     * @return The HologramBuilder class
     */
    public HologramBuilder hologram(final Location location, final String... lines) {
        return new HologramBuilder(this, location, lines);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="title">
    /**
     *
     * @param player The player
     * @param fadeIn The fade in time
     * @param stay The stay time
     * @param fadeOut The fade out time
     * @param title The main title
     * @param subtitle The sub title
     */
    public void title(final Player player, final int fadeIn, final int stay, final int fadeOut, final String title, final String subtitle) {
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        
        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutTimes);
        
        if(title != null) {
            IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, iChatBaseComponent);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutTitle);
        }
        
        if(subtitle != null) {
            IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}");
            PacketPlayOutTitle packetPlayOutSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChatBaseComponent);
            craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutSubtitle);
        }
        
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="sendHeadFooter">
    /**
     *
     * @param player The player
     * @param header The header (top)
     * @param footer The footer (bottom)
     */
    public void sendHeadFooter(final Player player, String header, String footer) {
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        
        if(header == null) header = "";
        if(footer == null) footer = "";
        
        IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + header + "\"}");
        IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + footer + "\"}");
        
        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(tabHeader);
        
        try {
            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, tabFooter);
        } catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception) {
        } finally {
            craftPlayer.getHandle().playerConnection.sendPacket(headerPacket);
        }
        
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="scoreboard">
    /**
     *
     * @param player The player
     * @return The ScoreboardBuilder class
     */
    public ScoreboardBuilder scoreboard(final Player player) {
        return new ScoreboardBuilder(this, player);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="skin">
    /**
     * Sets this player a new skin
     *
     * @param player The player
     * @param name The new skin name
     * @param updateSelf A boolean- flag
     */
    public void skin(final Player player, final String name, final boolean updateSelf) {
        this.nickUtil.changeSkin(player, name, updateSelf);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="name">
    /**
     * Sets this player a new name
     *
     * @param player The player
     * @param color The new color
     * @param name The new name
     * @param updateSelf A boolean- flag
     */
    public void name(final Player player, final String color, String name, final boolean updateSelf) {
        this.nickUtil.setName(player, color, name, updateSelf);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="toggleRank">
    /**
     * Sets this player a new "rank"/color
     *
     * @param player The player
     * @param color The new color
     */
    public void toggleRank(final Player player, final String color) {
        this.nickUtil.toggleRank(player, color);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="updateAllNickedPlayersFor">
    /**
     * This player can see all unnicked!
     *
     * @param player The player
     */
    public void updateAllNickedPlayersFor(final Player player) {
        this.nickUtil.updateAllForPlayer(player);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getPing">
    /**
     *
     * @param player The player
     * @return The ping of this player
     */
    public int getPing(final Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="registerEvent">
    /**
     * Easily create/register an event
     *
     * @param clazz The class
     * @param listener The listener
     * @param eventPriority The priority
     */
    public void registerEvent(final Class<? extends Event> clazz, final EventListener listener, final EventPriority eventPriority) {
        eventManager.registerEvent(clazz, listener, eventPriority);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="unregisterEvent">
    /**
     * Easily unregisters an event
     *
     * @param clazz The class
     * @param listener The listener
     */
    public void unregisterEvent(final Class<? extends Event> clazz, final EventListener listener) {
        eventManager.unregisterEvent(clazz, listener);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="onCommand">
    /**
     * Easily create/register commands
     *
     * @param command The command
     * @param executor The executor
     */
    public void onCommand(final String command, final CommandExecutor executor) {
        commandManager.onCommand(command, executor);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="location">
    /**
     *
     * @param file The file with all locations
     * @return A LocationBuilder class instance
     */
    public LocationBuilder location(final File file) {
        return new LocationBuilder(this, file);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="addToScoreboardTeam">
    /**
     * Adds a player easily to a team
     *
     * @param player The player
     * @param teamName The scoreboard team name
     */
    public void addToScoreboardTeam(final Player player, final String teamName) {
        try {
            
            if(player.getScoreboard() != null) {
                player.getScoreboard().getTeam(teamName).addPlayer(player);
                player.setDisplayName(player.getScoreboard().getTeam(teamName).getPrefix() + player.getName() + player.getScoreboard().getTeam(teamName).getSuffix());
            }
        
            Bukkit.getOnlinePlayers().forEach((players) -> {
                if(players.getScoreboard() != null) {
                    player.getScoreboard().getTeam(players.getScoreboard().getPlayerTeam(players).getName()).addPlayer(players);

                    players.getScoreboard().getTeam(teamName).addPlayer(player);
                }
            });
        } catch(Exception exception) {}
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="switchServer">
    public void switchServer(final Player player, final String server) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        
        output.writeUTF("Connect");
        output.writeUTF(server);
        
        player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getUniqueId">
    /**
     * 
     * @param name The name
     * @return The uuid of the player
     */
    public UUID getUniqueId(final String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getName">
    /**
     * 
     * @param uuid The uuid
     * @return The name of a player
     */
    public String getName(final UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="playHelixParticles">
    /**
     * 
     * @param location The location
     * @param radius The radius
     * @param high The high
     * @param particle The particle
     * @param players The players (can see them)
     */
    public void playHelixParticles(final Location location, final int radius, final double high, final EnumParticle particle, final Player players) {
        
        for (double y = 0; y <= high; y += 0.05) {
            double x = radius * Math.cos(y);
            double z = radius * Math.sin(y);
            
            final PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float) (location.getX() + x), (float) (location.getY() + y), (float) (location.getZ() + z), 0, 0, 0, 0, 1);
            
            ((CraftPlayer) players).getHandle().playerConnection.sendPacket(packet);
        }
        
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="playHelixParticles">
    /**
     *
     * @param location The location
     * @param radius The radius
     * @param high The high
     * @param particle The particle
     * @param players The players (can see them)
     */
    public void playHelixParticles(final Location location, final int radius, final double high, final Effect particle, final Player players) {

        for (double y = 0; y <= high; y += 0.05) {
            double x = radius * Math.cos(y);
            double z = radius * Math.sin(y);

            players.playEffect(new Location(location.getWorld(), (location.getX() + x), (location.getY() + y), (location.getZ() + z)), particle, 1);
        }

    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="playLineParticles">
    /**
     *
     * @param location The location
     * @param high The high
     * @param particle The particle
     * @param players The players (can see them)
     */
    public void playLineParticles(final Location location, final double high, final Effect particle, final Player players) {

        for (double y = 0; y <= high; y++) {

            players.playEffect(new Location(location.getWorld(), (location.getX()), (location.getY()), (location.getZ())), particle, 1);
        }

    }
    //</editor-fold>

}
