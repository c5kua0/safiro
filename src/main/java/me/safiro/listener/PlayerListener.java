package me.safiro.listener;

import me.safiro.Safiro;
import me.safiro.gui.MainGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements Listener {

    private final Safiro plugin;

    public PlayerListener(Safiro plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
        event.setKeepInventory(true);
        event.setKeepLevel(true);
    }

    @EventHandler
    public void onServerLoad() {
        Bukkit.getLogger().info("Safiro data manager ready.");
    }
}
