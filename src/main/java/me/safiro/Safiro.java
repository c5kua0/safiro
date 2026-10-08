package me.safiro;

import me.safiro.command.SafiroCommand;
import me.safiro.gui.CraftingGUI;
import me.safiro.gui.MainGUI;
import me.safiro.gui.RecipeGUI;
import me.safiro.gui.SlotGUI;
import me.safiro.gui.UpgradeGUI;
import me.safiro.listener.InventoryListener;
import me.safiro.listener.MagicItemListener;
import me.safiro.listener.PlayerListener;
import me.safiro.manager.MagicItemManager;
import me.safiro.manager.PlayerDataManager;
import me.safiro.manager.RecipeManager;
import me.safiro.manager.SafiroManager;
import me.safiro.manager.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Safiro extends JavaPlugin {

    private static Safiro instance;

    private PlayerDataManager playerDataManager;
    private SafiroManager safiroManager;
    private MagicItemManager magicItemManager;
    private RecipeManager recipeManager;
    private UpgradeManager upgradeManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();

        this.playerDataManager = new PlayerDataManager(this);
        this.safiroManager = new SafiroManager(this);
        this.magicItemManager = new MagicItemManager(this);
        this.recipeManager = new RecipeManager(this);
        this.upgradeManager = new UpgradeManager(this);

        getCommand("safiro").setExecutor(new SafiroCommand(this));
        getCommand("wing").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof org.bukkit.entity.Player player)) {
                sender.sendMessage("§cThis command can only be used by a player.");
                return true;
            }
            return this.magicItemManager.toggleWing(player);
        });

        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MagicItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        MainGUI.initialize(this);
        CraftingGUI.initialize(this);
        RecipeGUI.initialize(this);
        UpgradeGUI.initialize(this);
        SlotGUI.initialize(this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            new MagicItemListener(this).applyPassiveEffects();
        }, 20L, 20L);

        getLogger().info("Safiro enabled successfully.");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.saveAllPlayers();
        }
    }

    public static Safiro getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public SafiroManager getSafiroManager() {
        return safiroManager;
    }

    public MagicItemManager getMagicItemManager() {
        return magicItemManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }
}
