package me.safiro.gui;

import me.safiro.Safiro;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CraftingGUI {

    private static Safiro plugin;
    private static final String TITLE = "§8Safiro Crafting";

    public static void initialize(Safiro instance) {
        plugin = instance;
    }

    public static void open(Player player, String itemId) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        // Fill empty recipe slots with grey stained glass
        int[] gridSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
        for (int slot : gridSlots) {
            ItemStack empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta emptyMeta = empty.getItemMeta();
            if (emptyMeta != null) {
                emptyMeta.setDisplayName(" ");
                empty.setItemMeta(emptyMeta);
            }
            inv.setItem(slot, empty);
        }

        // 3x3 Recipe Grid (slots 10-12, 19-21, 28-30)
        List<Material> ingredients = plugin.getRecipeManager().getRecipeIngredients(itemId);
        int gridIndex = 0;

        for (Material material : ingredients) {
            if (gridIndex >= 9) break;
            if (material == Material.AIR) {
                gridIndex++;
                continue;
            }
            ItemStack ingredient = new ItemStack(material);
            ItemMeta meta = ingredient.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(material.name().replace("_", " "));
                ingredient.setItemMeta(meta);
            }
            inv.setItem(gridSlots[gridIndex], ingredient);
            gridIndex++;
        }

        // Result slot (center-right of grid)
        ItemStack result = plugin.getMagicItemManager().createItem(itemId, plugin.getPlayerDataManager().getLevel(player.getUniqueId(), itemId));
        inv.setItem(22, result);

        // Craft Button
        ItemStack craftButton = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = craftButton.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aCraft");
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING, "craft:" + itemId);
            craftButton.setItemMeta(meta);
        }
        inv.setItem(49, craftButton);

        // Info
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            List<String> lore = new ArrayList<>();
            lore.add("§7Cost: " + plugin.getSafiroManager().getCraftCost(itemId) + " Safiro Points");
            lore.add("§7Craft this magic item into your inventory");
            infoMeta.setDisplayName("§fCrafting Info");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(40, info);

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§7Back");
            backMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING, "back");
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);

        player.openInventory(inv);
    }
}
