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

public class UpgradeGUI {

    private static Safiro plugin;
    private static final String TITLE = "§8Safiro Upgrades";

    public static void initialize(Safiro instance) {
        plugin = instance;
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        int slot = 0;
        for (String itemId : plugin.getMagicItemManager().getNormalItems()) {
            ItemStack item = plugin.getMagicItemManager().createItem(itemId, plugin.getPlayerDataManager().getLevel(player.getUniqueId(), itemId));
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                int level = plugin.getPlayerDataManager().getLevel(player.getUniqueId(), itemId);
                lore.add("§7Current level: " + level);
                lore.add("§7Current effect: " + plugin.getUpgradeManager().getCurrentEffect(itemId, level));
                if (level < 5) {
                    lore.add("§7Next level: " + (level + 1));
                    lore.add("§7Next effect: " + plugin.getUpgradeManager().getNextEffect(itemId, level));
                    lore.add("§7Cost: " + plugin.getUpgradeManager().getCost(itemId, level) + " Safiro Points");
                    lore.add("§aUpgrade");
                } else {
                    lore.add("§aAlready fully upgraded.");
                }
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "upgrade_item_id"), PersistentDataType.STRING, itemId);
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
