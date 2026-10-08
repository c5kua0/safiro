package me.safiro.manager;

import me.safiro.Safiro;
import org.bukkit.entity.Player;

import java.util.*;

public class SafiroManager {

    private final Safiro plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public SafiroManager(Safiro plugin) {
        this.plugin = plugin;
    }

    public int getSlotCost(int slot) {
        return plugin.getConfig().getInt("slots.costs." + slot, 0);
    }

    public int getMaxSlots() {
        return plugin.getConfig().getInt("slots.maximum", 3);
    }

    public int getDefaultSlots() {
        return plugin.getConfig().getInt("slots.default", 1);
    }

    public int getCraftCost(String itemId) {
        return plugin.getConfig().getInt("crafting.costs." + itemId, 0);
    }

    public int getUpgradeCost(String itemId, int level) {
        if (level >= 5) {
            return 0;
        }
        return plugin.getConfig().getInt("upgrades.costs." + itemId + "." + level, 0);
    }

    public int getReward(String key) {
        return plugin.getConfig().getInt("points.rewards." + key, 0);
    }

    public boolean hasEnoughPoints(Player player, int cost) {
        return plugin.getPlayerDataManager().getPoints(player.getUniqueId()) >= cost;
    }

    public void takePoints(Player player, int cost) {
        int current = plugin.getPlayerDataManager().getPoints(player.getUniqueId());
        plugin.getPlayerDataManager().setPoints(player.getUniqueId(), Math.max(0, current - cost));
    }

    public void givePoints(Player player, int amount) {
        plugin.getPlayerDataManager().addPoints(player.getUniqueId(), amount);
        player.sendMessage("§b+" + amount + " Safiro Points§r (Total: " + plugin.getPlayerDataManager().getPoints(player.getUniqueId()) + ")");
    }

    public void applyRewardIfAllowed(Player player, String rewardKey) {
        if (tokenMissing(player, rewardKey)) {
            return;
        }
        int amount = getReward(rewardKey);
        if (amount > 0) {
            givePoints(player, amount);
        }
        setCooldown(player, rewardKey, 120000L);
    }

    public boolean tokenMissing(Player player, String key) {
        long now = System.currentTimeMillis();
        long ms = plugin.getConfig().getLong("points.cooldowns." + key, 60000L);
        Map<String, Long> map = cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        Long last = map.get(key);
        if (last != null && now - last < ms) {
            return true;
        }
        map.put(key, now);
        return false;
    }

    public void setCooldown(Player player, String key, long ms) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(key, System.currentTimeMillis() + ms);
    }

    public boolean isOnCooldown(Player player, String key) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) {
            return false;
        }
        Long time = map.get(key);
        if (time == null) {
            return false;
        }
        return time > System.currentTimeMillis();
    }
}
