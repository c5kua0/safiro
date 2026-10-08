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
import java.util.Map;

public class SlotGUI {

    private static Safiro plugin;
    private static final String TITLE = "§8Safiro Slots";

    public static void initialize(Safiro instance) {
        plugin = instance;
    }

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        int unlocked = plugin.getPlayerDataManager().getUnlockedSlots(player.getUniqueId());
        
        // Show unlocked slots with equipped items
        for (int i = 1; i <= 3; i++) {
            Material material = i <= unlocked ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            ItemStack slotItem = new ItemStack(material);
            ItemMeta meta = slotItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§aSlot " + i);
                List<String> lore = new ArrayList<>();
                lore.add("§7Status: " + (i <= unlocked ? "Unlocked" : "Locked"));
                
                // Show equipped item in this slot
                String equipped = plugin.getPlayerDataManager().getEquippedInSlot(player.getUniqueId(), i);
                if (!equipped.isEmpty()) {
                    lore.add("§aEquipped: " + plugin.getMagicItemManager().getItemDisplayName(equipped));
                } else if (i <= unlocked) {
                    lore.add("§7Empty");
                }
                
                if (i > unlocked) {
                    lore.add("§7Cost: " + plugin.getSafiroManager().getSlotCost(i) + " Safiro Points");
                }
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "slot_index"), PersistentDataType.INTEGER, i);
                slotItem.setItemMeta(meta);
            }
            inv.setItem(10 + (i - 1) * 2, slotItem);
        }

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
