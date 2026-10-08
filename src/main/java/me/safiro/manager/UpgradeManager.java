package me.safiro.manager;

import me.safiro.Safiro;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UpgradeManager {

    private final Safiro plugin;

    public UpgradeManager(Safiro plugin) {
        this.plugin = plugin;
    }

    public int getCurrentLevel(Player player, String itemId) {
        return plugin.getPlayerDataManager().getLevel(player.getUniqueId(), itemId);
    }

    public int getNextLevel(Player player, String itemId) {
        int level = getCurrentLevel(player, itemId);
        return Math.min(level + 1, 5);
    }

    public boolean isUpgradeable(String itemId) {
        return plugin.getMagicItemManager().getNormalItems().contains(itemId);
    }

    public int getCost(String itemId, int currentLevel) {
        if (!isUpgradeable(itemId) || currentLevel >= 5) {
            return 0;
        }
        return plugin.getConfig().getInt("upgrades.costs." + itemId + "." + currentLevel, 0);
    }

    public String getCurrentEffect(String itemId, int level) {
        return switch (itemId) {
            case "fireball" -> "Damage " + (level * 2) + " / cooldown " + (2.0 - (level * 0.15));
            case "lifebloom" -> "Regeneration II";
            case "rabbit_leg" -> "Jump Boost II";
            case "has_claw" -> "Lifesteal " + (5 + (level - 1) * 2) + "%";
            case "daisy" -> "Absorption +" + (level * 2) + " for " + (80 + level * 10) + " ticks";
            default -> "Passive";
        };
    }

    public String getNextEffect(String itemId, int level) {
        int next = Math.min(level + 1, 5);
        return switch (itemId) {
            case "fireball" -> "Damage " + (next * 2) + " / cooldown " + (2.0 - (next * 0.15));
            case "lifebloom" -> "Regeneration " + next;
            case "rabbit_leg" -> "Jump Boost " + next;
            case "has_claw" -> "Lifesteal " + (5 + (next - 1) * 2) + "%";
            case "daisy" -> "Absorption +" + (next * 2) + " for " + (80 + next * 10) + " ticks";
            default -> "Passive";
        };
    }

    public boolean upgrade(Player player, String itemId) {
        if (!isUpgradeable(itemId)) {
            return false;
        }
        int level = getCurrentLevel(player, itemId);
        if (level >= 5) {
            return false;
        }
        int cost = getCost(itemId, level);
        if (!plugin.getSafiroManager().hasEnoughPoints(player, cost)) {
            player.sendMessage(plugin.getConfig().getString("messages.no-points", "You don't have enough Safiro Points."));
            return false;
        }
        plugin.getSafiroManager().takePoints(player, cost);
        plugin.getPlayerDataManager().setLevel(player.getUniqueId(), itemId, level + 1);
        player.sendMessage("§aUpgraded " + plugin.getMagicItemManager().getItemDisplayName(itemId) + " to Level " + (level + 1) + ".");
        return true;
    }
}
