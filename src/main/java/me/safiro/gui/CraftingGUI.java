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

        ItemStack result = plugin.getMagicItemManager().createItem(itemId, plugin.getPlayerDataManager().getLevel(player.getUniqueId(), itemId));
        inv.setItem(22, result);

        ItemStack craftButton = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = craftButton.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aCraft");
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING, "craft:" + itemId);
            craftButton.setItemMeta(meta);
        }
        inv.setItem(49, craftButton);

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
