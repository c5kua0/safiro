package me.safiro.gui;

import me.safiro.Safiro;
import me.safiro.manager.MagicItemManager;
import me.safiro.manager.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MainGUI {

    private static Safiro plugin;
    private static final String TITLE = "§8Safiro System";

    public static void initialize(Safiro instance) {
        plugin = instance;
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        ItemStack magicSlots = createMenuItem(Material.CHEST, "§aMagic Item Slots", "§7Manage your equipped slots");
        setAction(magicSlots, "open_slots");
        inv.setItem(10, magicSlots);

        ItemStack crafting = createMenuItem(Material.CRAFTING_TABLE, "§6Crafting", "§7Craft a new magic item");
        setAction(crafting, "open_crafting");
        inv.setItem(12, crafting);

        ItemStack recipeGuide = createMenuItem(Material.BOOK, "§eRecipe Guide", "§7View normal recipes");
        setAction(recipeGuide, "open_recipe");
        inv.setItem(14, recipeGuide);

        ItemStack upgrades = createMenuItem(Material.ANVIL, "§dUpgrades", "§7Upgrade your magic items");
        setAction(upgrades, "open_upgrade");
        inv.setItem(16, upgrades);

        ItemStack points = createMenuItem(Material.EMERALD, "§bSafiro Points", "§7Balance: " + plugin.getPlayerDataManager().getPoints(player.getUniqueId()));
        setAction(points, "open_points");
        inv.setItem(28, points);

        ItemStack unlocks = createMenuItem(Material.DIAMOND, "§5Slot Unlocks", "§7Unlock additional magic slots");
        setAction(unlocks, "open_slots");
        inv.setItem(30, unlocks);

        player.openInventory(inv);
    }

    private static ItemStack createMenuItem(Material material, String title, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(title);
        meta.setLore(java.util.List.of(lore));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private static void setAction(ItemStack item, String action) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING, action);
        item.setItemMeta(meta);
    }
}
