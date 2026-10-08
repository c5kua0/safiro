package me.safiro.manager;

import me.safiro.Safiro;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeManager {

    private final Safiro plugin;

    public RecipeManager(Safiro plugin) {
        this.plugin = plugin;
    }

    public static final java.util.Map<String, List<Material>> RECIPE_MAP = new java.util.HashMap<>();

    static {
        RECIPE_MAP.put("fireball", List.of(Material.BLAZE_POWDER, Material.FIRE_CHARGE, Material.BLAZE_POWDER, Material.DIAMOND, Material.DIAMOND));
        RECIPE_MAP.put("lifebloom", List.of(Material.MOSS_BLOCK, Material.GOLDEN_APPLE, Material.MOSS_BLOCK, Material.GHAST_TEAR));
        RECIPE_MAP.put("rabbit_leg", List.of(Material.RABBIT_FOOT, Material.FEATHER, Material.RABBIT_FOOT, Material.EMERALD));
        RECIPE_MAP.put("has_claw", List.of(Material.PHANTOM_MEMBRANE, Material.FERMENTED_SPIDER_EYE, Material.PHANTOM_MEMBRANE, Material.NETHERITE_SCRAP));
        RECIPE_MAP.put("daisy", List.of(Material.OXEYE_DAISY, Material.GOLDEN_APPLE, Material.OXEYE_DAISY, Material.AMETHYST_SHARD));
        RECIPE_MAP.put("chad", List.of(Material.DIAMOND_AXE, Material.GOLDEN_APPLE, Material.DIAMOND_AXE, Material.DIAMOND, Material.EMERALD, Material.STICK, Material.ECHO_SHARD));
        RECIPE_MAP.put("god_wings", List.of(Material.PHANTOM_MEMBRANE, Material.DIAMOND, Material.PHANTOM_MEMBRANE, Material.FEATHER, Material.GOLD, Material.FEATHER, Material.PHANTOM_MEMBRANE, Material.DIAMOND, Material.PHANTOM_MEMBRANE));
    }

    public List<Material> getRecipeIngredients(String itemId) {
        return RECIPE_MAP.getOrDefault(itemId, List.of());
    }

    public boolean craft(Player player, String itemId) {
        if (!player.hasPermission("safiro.admin") && !player.isOnline()) {
            return false;
        }

        int requiredPoints = plugin.getSafiroManager().getCraftCost(itemId);
        if (!plugin.getSafiroManager().hasEnoughPoints(player, requiredPoints)) {
            player.sendMessage(plugin.getConfig().getString("messages.no-points", "You don't have enough Safiro Points."));
            return false;
        }

        List<Material> recipe = getRecipeIngredients(itemId);
        if (recipe.isEmpty()) {
            return false;
        }

        for (Material material : recipe) {
            if (material == Material.AIR) {
                continue;
            }
            int count = 0;
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack != null && stack.getType() == material) {
                    count += stack.getAmount();
                }
            }
            if (count < 1) {
                player.sendMessage(plugin.getConfig().getString("messages.no-materials", "You don't have the required materials."));
                return false;
            }
        }

        for (Material material : recipe) {
            if (material == Material.AIR) {
                continue;
            }
            player.getInventory().removeItem(new ItemStack(material, 1));
        }

        plugin.getSafiroManager().takePoints(player, requiredPoints);
        ItemStack result = plugin.getMagicItemManager().createItem(itemId, plugin.getPlayerDataManager().getLevel(player.getUniqueId(), itemId));
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), result);
        } else {
            player.getInventory().addItem(result);
        }

        return true;
    }

    public boolean canCraft(Player player, String itemId) {
        int cost = plugin.getSafiroManager().getCraftCost(itemId);
        if (!plugin.getSafiroManager().hasEnoughPoints(player, cost)) {
            return false;
        }

        for (Material material : getRecipeIngredients(itemId)) {
            if (material == Material.AIR) {
                continue;
            }
            if (!player.getInventory().contains(material)) {
                return false;
            }
        }
        return true;
    }
}
