package me.safiro.gui;

import me.safiro.Safiro;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
            slot++;
            if (slot >= 54) {
                break;
            }
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§7Back");
            back.setItemMeta(backMeta);
        }
        inv.setItem(53, back);
        player.openInventory(inv);
    }
}
