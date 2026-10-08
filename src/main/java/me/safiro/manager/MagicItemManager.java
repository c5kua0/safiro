package me.safiro.manager;

import me.safiro.Safiro;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MagicItemManager {

    public static final String MAGIC_ID_KEY = "safiro_magic_id";
    public static final String MAGIC_LEVEL_KEY = "safiro_magic_level";

    private final Safiro plugin;

    public MagicItemManager(Safiro plugin) {
        this.plugin = plugin;
    }

    public Map<String, Material> getItemMaterials() {
        Map<String, Material> map = new HashMap<>();
        map.put("fireball", Material.FIRE_CHARGE);
        map.put("lifebloom", Material.MOSS_BLOCK);
        map.put("rabbit_leg", Material.RABBIT_FOOT);
        map.put("has_claw", Material.PHANTOM_MEMBRANE);
        map.put("daisy", Material.OXEYE_DAISY);
        map.put("chad", Material.DIAMOND_AXE);
        map.put("god_wings", Material.PHANTOM_MEMBRANE);
        return map;
    }

    public Set<String> getNormalItems() {
        return Set.of("fireball", "lifebloom", "rabbit_leg", "has_claw", "daisy");
    }

    public Set<String> getSecretItems() {
        return Set.of("chad", "god_wings");
    }

    public String getItemDisplayName(String id) {
        return switch (id) {
            case "fireball" -> "§6Fireball";
            case "lifebloom" -> "§aLifebloom";
            case "rabbit_leg" -> "§fRabbitLeg";
            case "has_claw" -> "§cHa's Claw";
            case "daisy" -> "§eDaisy";
            case "chad" -> "§8Chad";
            case "god_wings" -> "§bGod Wings";
            default -> "Magic Item";
        };
    }

    public ItemStack createItem(String id, int level) {
        Material material = getItemMaterials().getOrDefault(id, Material.BLAZE_POWDER);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(getItemDisplayName(id));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, MAGIC_ID_KEY), PersistentDataType.STRING, id);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, MAGIC_LEVEL_KEY), PersistentDataType.INTEGER, level);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isMagicItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        if (item.getItemMeta() == null) {
            return false;
        }
        String id = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, MAGIC_ID_KEY), PersistentDataType.STRING);
        return id != null && !id.isBlank();
    }

    public String getMagicId(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null) {
            return "";
        }
        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, MAGIC_ID_KEY), PersistentDataType.STRING);
    }

    public int getMagicLevel(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null) {
            return 1;
        }
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, MAGIC_LEVEL_KEY), PersistentDataType.INTEGER, 1);
    }

    public boolean isEquipped(org.bukkit.entity.Player player, String itemId) {
        for (Map.Entry<Integer, String> entry : plugin.getPlayerDataManager().getEquipped(player.getUniqueId()).entrySet()) {
            if (entry.getValue() != null && entry.getValue().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMagicInInventory(org.bukkit.entity.Player player, String itemId) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && isMagicItem(stack) && getMagicId(stack).equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public boolean toggleWing(org.bukkit.entity.Player player) {
        if (!isEquipped(player, "god_wings")) {
            player.sendMessage(plugin.getConfig().getString("messages.no-wing", "§cYou need God Wings equipped to use this command."));
            return true;
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        return true;
    }

    public List<String> getRecipesForGuide() {
        List<String> result = new ArrayList<>();
        result.addAll(getNormalItems());
        return result;
    }
}
