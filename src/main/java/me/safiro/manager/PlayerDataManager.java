package me.safiro.manager;

import me.safiro.Safiro;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final Safiro plugin;
    private final File dataFolder;
    private final Map<UUID, FileConfiguration> cache = new HashMap<>();

    public PlayerDataManager(Safiro plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void reloadData() {
        cache.clear();
    }

    public FileConfiguration getPlayerData(UUID uuid) {
        FileConfiguration config = cache.get(uuid);
        if (config != null) {
            return config;
        }
        File file = new File(dataFolder, uuid + ".yml");
        if (!file.exists()) {
            config = new YamlConfiguration();
            config.set("points", 0);
            config.set("unlocked-slots", 1);
            config.set("equipped", new HashMap<String, String>());
            config.set("levels", new HashMap<String, Integer>());
            savePlayerData(uuid, config);
        } else {
            config = YamlConfiguration.loadConfiguration(file);
        }
        cache.put(uuid, config);
        return config;
    }

    public void savePlayerData(UUID uuid) {
        savePlayerData(uuid, cache.get(uuid));
    }

    private void savePlayerData(UUID uuid, FileConfiguration config) {
        if (config == null) {
            config = new YamlConfiguration();
        }
        File file = new File(dataFolder, uuid + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save player data for " + uuid + ": " + e.getMessage());
        }
    }

    public void saveAllPlayers() {
        for (UUID uuid : cache.keySet()) {
            savePlayerData(uuid, cache.get(uuid));
        }
    }

    public int getPoints(UUID uuid) {
        return getPlayerData(uuid).getInt("points", 0);
    }

    public void setPoints(UUID uuid, int amount) {
        getPlayerData(uuid).set("points", Math.max(0, amount));
        savePlayerData(uuid);
    }

    public void addPoints(UUID uuid, int amount) {
        setPoints(uuid, getPoints(uuid) + amount);
    }

    public int getUnlockedSlots(UUID uuid) {
        return getPlayerData(uuid).getInt("unlocked-slots", 1);
    }

    public void setUnlockedSlots(UUID uuid, int amount) {
        getPlayerData(uuid).set("unlocked-slots", Math.max(1, Math.min(amount, 3)));
        savePlayerData(uuid);
    }

    public Map<Integer, String> getEquipped(UUID uuid) {
        Map<Integer, String> result = new HashMap<>();
        for (String key : getPlayerData(uuid).getConfigurationSection("equipped") == null ? java.util.Collections.emptyList() : getPlayerData(uuid).getConfigurationSection("equipped").getKeys(false)) {
            int slot = Integer.parseInt(key);
            String value = getPlayerData(uuid).getString("equipped." + key, "");
            if (!value.isEmpty()) {
                result.put(slot, value);
            }
        }
        return result;
    }

    public void setEquipped(UUID uuid, int slot, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            getPlayerData(uuid).set("equipped." + slot, null);
        } else {
            getPlayerData(uuid).set("equipped." + slot, itemId);
        }
        savePlayerData(uuid);
    }

    public String getEquippedInSlot(UUID uuid, int slot) {
        return getPlayerData(uuid).getString("equipped." + slot, "");
    }

    public boolean isSlotLocked(UUID uuid, int slot) {
        return slot > getUnlockedSlots(uuid);
    }

    public int getLevel(UUID uuid, String itemId) {
        return getPlayerData(uuid).getInt("levels." + itemId, 1);
    }

    public void setLevel(UUID uuid, String itemId, int level) {
        getPlayerData(uuid).set("levels." + itemId, Math.max(1, level));
        savePlayerData(uuid);
    }

    public void ensurePlayer(Player player) {
        getPlayerData(player.getUniqueId());
    }
}
