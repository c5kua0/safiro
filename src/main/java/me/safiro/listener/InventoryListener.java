package me.safiro.listener;

import me.safiro.Safiro;
import me.safiro.gui.CraftingGUI;
import me.safiro.gui.MainGUI;
import me.safiro.gui.RecipeGUI;
import me.safiro.gui.SlotGUI;
import me.safiro.gui.UpgradeGUI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class InventoryListener implements Listener {

    private final Safiro plugin;

    public InventoryListener(Safiro plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();
        if (title.equals("§8Safiro System") || title.equals("§8Safiro Crafting") || title.equals("§8Safiro Recipe Guide") || title.equals("§8Safiro Upgrades") || title.equals("§8Safiro Slots") || title.startsWith("§8Recipe:")) {
            event.setCancelled(true);
        }

        if (title.equals("§8Safiro System")) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            String action = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING);
            if (action == null) {
                return;
            }
            if (action.equals("open_slots")) {
                SlotGUI.open(player);
            } else if (action.equals("open_crafting")) {
                CraftingGUI.open(player, "fireball");
            } else if (action.equals("open_recipe")) {
                RecipeGUI.open(player);
            } else if (action.equals("open_upgrade")) {
                UpgradeGUI.open(player);
            }
        }

        if (title.equals("§8Safiro Crafting")) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            String action = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING);
            if (action == null) {
                return;
            }
            if (action.startsWith("craft:")) {
                String id = action.substring("craft:".length());
                plugin.getRecipeManager().craft(player, id);
            } else if (action.equals("back")) {
                MainGUI.open(player);
            }
        }

        if (title.equals("§8Safiro Recipe Guide")) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            String action = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING);
            if (action != null && action.equals("back")) {
                MainGUI.open(player);
            } else {
                String recipeId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "recipe_item_id"), PersistentDataType.STRING);
                if (recipeId != null) {
                    RecipeGUI.openRecipePage(player, recipeId);
                }
            }
        }

        if (title.startsWith("§8Recipe:")) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            String action = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING);
            if (action != null && action.equals("back_recipe")) {
                RecipeGUI.open(player);
            } else if (action != null && action.startsWith("craft:")) {
                String id = action.substring("craft:".length());
                plugin.getRecipeManager().craft(player, id);
            }
        }

        if (title.equals("§8Safiro Upgrades")) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            String action = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING);
            if (action != null && action.equals("back")) {
                MainGUI.open(player);
            } else {
                String itemId = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "upgrade_item_id"), PersistentDataType.STRING);
                if (itemId != null) {
                    plugin.getUpgradeManager().upgrade(player, itemId);
                }
            }
        }

        if (title.equals("§8Safiro Slots")) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            String action = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "gui_action"), PersistentDataType.STRING);
            if (action != null && action.equals("back")) {
                MainGUI.open(player);
            } else {
                Integer slot = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "slot_index"), PersistentDataType.INTEGER);
                if (slot != null) {
                    int unlocked = plugin.getPlayerDataManager().getUnlockedSlots(player.getUniqueId());
                    if (slot <= unlocked) {
                        player.sendMessage("§aSlot " + slot + " is unlocked.");
                    } else {
                        int cost = plugin.getSafiroManager().getSlotCost(slot);
                        if (plugin.getSafiroManager().hasEnoughPoints(player, cost)) {
                            plugin.getSafiroManager().takePoints(player, cost);
                            plugin.getPlayerDataManager().setUnlockedSlots(player.getUniqueId(), slot);
                            player.sendMessage("§aUnlocked slot " + slot + ".");
                            SlotGUI.open(player);
                        } else {
                            player.sendMessage(plugin.getConfig().getString("messages.no-points", "You don't have enough Safiro Points."));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals("§8Safiro System") || event.getView().getTitle().equals("§8Safiro Crafting") || event.getView().getTitle().equals("§8Safiro Recipe Guide") || event.getView().getTitle().equals("§8Safiro Upgrades") || event.getView().getTitle().equals("§8Safiro Slots") || event.getView().getTitle().startsWith("§8Recipe:")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.equals("§8Safiro Crafting") || title.equals("§8Safiro Upgrades") || title.equals("§8Safiro Slots") || title.equals("§8Safiro Recipe Guide") || title.equals("§8Safiro System") || title.startsWith("§8Recipe:")) {
            // Custom GUI close handling
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack() != null && plugin.getMagicItemManager().isMagicItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }
}
