package de.superklug.mygames.superapi.utils.builders;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.superklug.mygames.superapi.SuperAPI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemBuilder {

    private final SuperAPI module;

    private final ItemStack currentItem;
    
    /**
     * 
     * @param module The main plugin class
     * @param type The material type
     * @param amount The amount
     * @param damage The damage/subId
     */
    public ItemBuilder(final SuperAPI module, final Material type, final int amount, final short damage) {
        this.module = module;
        this.currentItem = new ItemStack(type, amount, damage);
    }
    
    /**
     *
     * @param module The main plugin class
     * @param type The material type
     * @param amount The amount
     */
    public ItemBuilder(final SuperAPI module, final Material type, final int amount) {
        this.module = module;
        this.currentItem = new ItemStack(type, amount, (short) 0);
    }
    
    /**
     *
     * @param module The main plugin class
     * @param type The material type
     */
    public ItemBuilder(final SuperAPI module, final Material type) {
        this.module = module;
        this.currentItem = new ItemStack(type, 1, (short) 0);
    }
    
    /**
     * 
     * @param name The new name of this item
     * @return 
     */
    public ItemBuilder setDisplayname(final String name) {
        ItemMeta itemMeta = this.currentItem.getItemMeta();

        itemMeta.setDisplayName(name);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this item name to none
     * 
     * @return 
     */
    public ItemBuilder setNoName() {
        setDisplayname(" ");
        return this;
    }
    
    /**
     * Sets this item a new lore
     * 
     * @param lore The new lore(s) of this item
     * @return 
     */
    public ItemBuilder setLore(final String... lore) {
        ItemMeta itemMeta = this.currentItem.getItemMeta();

        itemMeta.setLore(Arrays.asList(lore));

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this item a new lore
     *
     * @param lore The new lore(s) of this item
     * @return
     */
    public ItemBuilder setLore(final List<String> lore) {
        ItemMeta itemMeta = this.currentItem.getItemMeta();

        itemMeta.setLore(lore);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Removes the itemflag(s) from this item
     * 
     * @param itemFlags The itemflag(s) of this item
     * @return 
     */
    public ItemBuilder removeItemFlags(final ItemFlag... itemFlags) {
        ItemMeta itemMeta = this.currentItem.getItemMeta();

        itemMeta.removeItemFlags(itemFlags);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Adds the itemflag(s) to this item
     * 
     * @param itemFlags The itemflag(s) of this item
     * @return 
     */
    public ItemBuilder addItemFlags(final ItemFlag... itemFlags) {
        ItemMeta itemMeta = this.currentItem.getItemMeta();

        itemMeta.addItemFlags(itemFlags);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Adds a new enchantment to this item
     * 
     * @param enchantment The enchantment
     * @param value The value/strenght
     * @return 
     */
    public ItemBuilder addEnchantment(final Enchantment enchantment, final int value) {
        ItemMeta itemMeta = this.currentItem.getItemMeta();

        itemMeta.addEnchant(enchantment, value, true);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Removes the enchantment from this item
     *
     * @param enchantment The enchantment
     * @return
     */
    public ItemBuilder removeEnchantment(final Enchantment enchantment) {
        ItemMeta itemMeta = this.currentItem.getItemMeta();

        itemMeta.removeEnchant(enchantment);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets the item unbreakable(true) or breakable(false)
     * 
     * @param flag A boolean- flag
     * @return 
     */
    public ItemBuilder setUnbreakable(final boolean flag) {
        ItemMeta itemMeta = this.currentItem.getItemMeta();

        itemMeta.spigot().setUnbreakable(flag);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this leather-armor item a new color
     * 
     * @param color The new color
     * @return 
     */
    public ItemBuilder setLeatherArmorColor(final Color color) {
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) this.currentItem.getItemMeta();

        itemMeta.setColor(color);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this leather-armor item a new color
     * 
     * @param r The new red color
     * @param g The new green color
     * @param b The new blue color
     * @return 
     */
    public ItemBuilder setLeatherArmorColorRGB(final int r, final int g, final int b) {
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) this.currentItem.getItemMeta();

        itemMeta.setColor(Color.fromRGB(r, g, b));

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this banner item a new base dyecolor
     * 
     * @param dyeColor The dyecolor
     * @return 
     */
    public ItemBuilder setBannerBaseDyeColor(final DyeColor dyeColor) {
        BannerMeta itemMeta = (BannerMeta) this.currentItem.getItemMeta();

        itemMeta.setBaseColor(dyeColor);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this banner item a new patter
     * 
     * @param variable The variable
     * @param pattern The pattern
     * @return 
     */
    public ItemBuilder setBannerPattern(final int variable, final Pattern pattern) {
        BannerMeta itemMeta = (BannerMeta) this.currentItem.getItemMeta();

        itemMeta.setPattern(variable, pattern);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this banner item a new patter
     *
     * @param patter The patter
     * @return
     */
    public ItemBuilder addBannerPattern(final Pattern patter) {
        BannerMeta itemMeta = (BannerMeta) this.currentItem.getItemMeta();

        itemMeta.addPattern(patter);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this banner item new patterns
     * 
     * @param patterns The list with pattern
     * @return 
     */
    public ItemBuilder setBannerPatterns(final List<Pattern> patterns) {
        BannerMeta itemMeta = (BannerMeta) this.currentItem.getItemMeta();

        itemMeta.setPatterns(patterns);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this item a new blockstate
     * 
     * @param blockState The blockstate
     * @return 
     */
    public ItemBuilder setBlockState(final BlockState blockState) {
        BlockStateMeta itemMeta = (BlockStateMeta) this.currentItem.getItemMeta();

        itemMeta.setBlockState(blockState);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this book item a new title
     * 
     * @param title The title
     * @return 
     */
    public ItemBuilder setBookTitle(final String title) {
        BookMeta itemMeta = (BookMeta) this.currentItem.getItemMeta();

        itemMeta.setTitle(title);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this book item a new author
     * 
     * @param author The author
     * @return 
     */
    public ItemBuilder setBookAuthor(final String author) {
        BookMeta itemMeta = (BookMeta) this.currentItem.getItemMeta();

        itemMeta.setAuthor(author);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this book item on a page a new text or message
     * 
     * @param page The page
     * @param text The text/message
     * @return 
     */
    public ItemBuilder setBookPage(final int page, final String text) {
        BookMeta itemMeta = (BookMeta) this.currentItem.getItemMeta();

        itemMeta.setPage(page, text);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this book item new pages
     * 
     * @param pages A list with book pages
     * @return 
     */
    public ItemBuilder setBookPages(final List<String> pages) {
        BookMeta itemMeta = (BookMeta) this.currentItem.getItemMeta();

        itemMeta.setPages(pages);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this book item new pages
     * 
     * @param pages Book pages
     * @return 
     */
    public ItemBuilder setBookPages(final String... pages) {
        BookMeta itemMeta = (BookMeta) this.currentItem.getItemMeta();

        itemMeta.setPages(pages);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this firework item a new effect
     * 
     * @param fireworkEffect The effect
     * @return 
     */
    public ItemBuilder setFireworkEffect(final FireworkEffect fireworkEffect) {
        FireworkEffectMeta itemMeta = (FireworkEffectMeta) this.currentItem.getItemMeta();

        itemMeta.setEffect(fireworkEffect);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this firework item a new flight power
     * 
     * @param power The flight power
     * @return 
     */
    public ItemBuilder setFireworkPower(final int power) {
        FireworkMeta itemMeta = (FireworkMeta) this.currentItem.getItemMeta();

        itemMeta.setPower(power);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Adds this firework item a new effect
     * 
     * @param fireworkEffect The effect
     * @return 
     */
    public ItemBuilder addFireworkEffect(final FireworkEffect fireworkEffect) {
        FireworkMeta itemMeta = (FireworkMeta) this.currentItem.getItemMeta();

        itemMeta.addEffect(fireworkEffect);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Removes this firework item an effect
     * 
     * @param variable The variable
     * @return 
     */
    public ItemBuilder removeFireworkEffect(final int variable) {
        FireworkMeta itemMeta = (FireworkMeta) this.currentItem.getItemMeta();

        itemMeta.removeEffect(variable);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Adds this firework item new effects
     * 
     * @param fireworkEffects The effects
     * @return 
     */
    public ItemBuilder addFireworkEffects(final FireworkEffect... fireworkEffects) {
        FireworkMeta itemMeta = (FireworkMeta) this.currentItem.getItemMeta();

        itemMeta.addEffects(fireworkEffects);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Adds this firework item new effects
     *
     * @param fireworkEffects The effects
     * @return
     */
    public ItemBuilder addFireworkEffects(final Iterable<FireworkEffect> fireworkEffects) {
        FireworkMeta itemMeta = (FireworkMeta) this.currentItem.getItemMeta();

        itemMeta.addEffects(fireworkEffects);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Should the map item have a scaling?
     * 
     * @param flag The boolean- flag
     * @return 
     */
    public ItemBuilder setMapScaling(final boolean flag) {
        MapMeta itemMeta = (MapMeta) this.currentItem.getItemMeta();

        itemMeta.setScaling(flag);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this potion item a new main-effect
     * 
     * @param potionEffectType The effect
     * @return 
     */
    public ItemBuilder setPotionMainEffect(final PotionEffectType potionEffectType) {
        PotionMeta itemMeta = (PotionMeta) this.currentItem.getItemMeta();

        itemMeta.setMainEffect(potionEffectType);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Adds this potion item a new effect
     * 
     * @param potionEffect The effect
     * @param overwrite A boolean- flag
     * @return 
     */
    public ItemBuilder addPotionCustomEffect(final PotionEffect potionEffect, final boolean overwrite) {
        PotionMeta itemMeta = (PotionMeta) this.currentItem.getItemMeta();

        itemMeta.addCustomEffect(potionEffect, overwrite);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Removes this potion item an effect
     *
     * @param potionEffectType The effect
     * @return
     */
    public ItemBuilder removePotionCustomEffect(final PotionEffectType potionEffectType) {
        PotionMeta itemMeta = (PotionMeta) this.currentItem.getItemMeta();

        itemMeta.removeCustomEffect(potionEffectType);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this skull item a new owner
     * 
     * @param skullOwner The skull owner
     * @return 
     */
    public ItemBuilder setSkullOwner(final String skullOwner) {
        SkullMeta itemMeta = (SkullMeta) this.currentItem.getItemMeta();

        itemMeta.setOwner(skullOwner);

        this.currentItem.setItemMeta(itemMeta);
        return this;
    }
    
    /**
     * Sets this skull item a new owner by texture
     * 
     * @param texture The skull texture
     * @return 
     */
    public ItemBuilder setSkullTexture(final String texture) {
        try {
            SkullMeta itemMeta = (SkullMeta) this.currentItem.getItemMeta();
            GameProfile profile = (GameProfile) module.getReflectionUtil().get(itemMeta, "profile");

            if (profile == null) {
                profile = new GameProfile(UUID.randomUUID(), "customSkull");
            }

            profile.getProperties().put("textures", new Property("textures", texture));
            module.getReflectionUtil().set(itemMeta, "profile", profile);

            this.currentItem.setItemMeta(itemMeta);
            return this;
        } catch (Exception ignore) {
        }
        return this;
    }
    
    /**
     * 
     * @return The end item
     */
    public ItemStack build() {
        return this.currentItem;
    }

}
