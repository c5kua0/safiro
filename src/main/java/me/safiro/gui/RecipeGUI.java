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

public class RecipeGUI {

    private static Safiro plugin;
    private static final String TITLE = "§8Safiro Recipe Guide";

    public static void initialize(Safiro instance) {
        plugin = instance;
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        int slot = 0;
        for (String itemId : plugin.getMagicItemManager().getRecipesForGuide()) {
            ItemStack item = plugin.getMagicItemManager().createItem(itemId, 1);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (meta != null) {
                lore.add("§7Level: I-V");
                lore.add("§7Cost: " + plugin.getSafiroManager().getCraftCost(itemId) + " Safiro Points");
                lore.add("§7Craftable: yes");
                lore.add("§eClick to view recipe!");
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "recipe_item_id"), PersistentDataType.STRING, itemId);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
            slot++;
            if (slot >= 53) {
                break;
            }
        }

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

    public static void openRecipePage(Player player, String itemId) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Recipe: " + plugin.getMagicItemManager().getItemDisplayName(itemId));

        ItemStack result = plugin.getMagicItemManager().createItem(itemId, 1);
        inv.setItem(22, result);

        List<Material> ingredients = plugin.getRecipeManager().getRecipeIngredients(itemId);
        int index = 0;
        for (Material material : ingredients) {
            if (material == Material.AIR) {
                continue;
            }
            ItemStack ingredient = new ItemStack(material);
            ItemMeta meta = ingredient.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(material.name().replace("_", " "));
                ingredient.setItemMeta(meta);
            }
            inv.setItem(11 + index, ingredient);
            index++;
        }

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            List<String> lore = new ArrayList<>();
            lore.add("§7Cost: " + plugin.getSafiroManager().getCraftCost(itemId) + " Safiro Points");
            lore.add("§7Gather the materials above");
            lore.add("§7and craft this item.");
            infoMeta.setDisplayName("§fRecipe Info");
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(40, info);

        ItemStack craftButton = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta craftMeta = craftButton.getItemMeta();
        if (craftMeta != null) {
            craftMeta.setDisplayName("§aCraft This Item");
            craftMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING, "craft:" + itemId);
            craftButton.setItemMeta(craftMeta);
        }
        inv.setItem(49, craftButton);

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§7Back to Recipes");
            backMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING, "back_recipe");
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);

        player.openInventory(inv);
    }
}
