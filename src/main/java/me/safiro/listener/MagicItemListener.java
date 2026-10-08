package me.safiro.listener;

import me.safiro.Safiro;
import me.safiro.manager.MagicItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class MagicItemListener implements Listener {

    private final Safiro plugin;

    public MagicItemListener(Safiro plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPlayerDataManager().ensurePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().savePlayerData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack held = event.getItem();
        if (held == null || held.getType() == Material.AIR) {
            return;
        }
        String itemId = plugin.getMagicItemManager().getMagicId(held);
        if (itemId == null || itemId.isBlank()) {
            return;
        }
        if (!event.getAction().isRightClick()) {
            return;
        }
        if (!plugin.getMagicItemManager().isEquipped(player, "fireball")) {
            return;
        }
        if (!itemId.equals("fireball")) {
            return;
        }

        double speed = plugin.getConfig().getDouble("magic.fireball.projectile-speed", 1.4D);
        double damage = plugin.getConfig().getDouble("magic.fireball.damage", 5.0D);
        SmallFireball fireball = player.launchProjectile(SmallFireball.class);
        fireball.setVelocity(player.getLocation().getDirection().multiply(speed));
        fireball.setMetadata("safiro_fireball_damage", new org.bukkit.metadata.FixedMetadataValue(plugin, damage));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target)) {
            return;
        }
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (!plugin.getMagicItemManager().isEquipped(attacker, "has_claw")) {
            return;
        }
        int level = plugin.getPlayerDataManager().getLevel(attacker.getUniqueId(), "has_claw");
        double percentage = 0.05D + (level - 1) * 0.02D;
        double heal = target.getMaxHealth() * percentage;
        double maxHeal = Math.min(heal, target.getMaxHealth() - target.getHealth());
        if (maxHeal > 0D) {
            target.setHealth(Math.min(target.getMaxHealth(), target.getHealth() + maxHeal));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }
        Player killer = event.getEntity().getKiller();
        if (event.getEntityType() == EntityType.ELDER_GUARDIAN || event.getEntityType() == EntityType.WITHER || event.getEntityType() == EntityType.ENDER_DRAGON) {
            plugin.getSafiroManager().givePoints(killer, plugin.getSafiroManager().getReward("boss"));
            return;
        }
        if (event.getEntity().getType().isAlive() && event.getEntityType() != EntityType.PLAYER) {
            plugin.getSafiroManager().givePoints(killer, plugin.getSafiroManager().getReward("hostile-mob"));
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        plugin.getSafiroManager().givePoints(event.getPlayer(), plugin.getSafiroManager().getReward("achievement"));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.EMERALD_ORE || event.getBlock().getType() == Material.ANCIENT_DEBRIS) {
            if (!plugin.getSafiroManager().isOnCooldown(player, "rare-ore")) {
                plugin.getSafiroManager().givePoints(player, plugin.getSafiroManager().getReward("rare-ore"));
                plugin.getSafiroManager().setCooldown(player, "rare-ore", 60000L);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.getMagicItemManager().isMagicItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player.isFlying() && !plugin.getMagicItemManager().isEquipped(player, "god_wings")) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    public void applyPassiveEffects() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null || player.isDead()) {
                continue;
            }
            if (plugin.getMagicItemManager().isEquipped(player, "lifebloom")) {
                int level = plugin.getPlayerDataManager().getLevel(player.getUniqueId(), "lifebloom");
                int amplifier = Math.min(2, Math.max(0, level - 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, amplifier, false, false, true));
            } else {
                player.removePotionEffect(PotionEffectType.REGENERATION);
            }

            if (plugin.getMagicItemManager().isEquipped(player, "rabbit_leg")) {
                int level = plugin.getPlayerDataManager().getLevel(player.getUniqueId(), "rabbit_leg");
                int amplifier = Math.min(2, Math.max(0, level - 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, amplifier, false, false, true));
            } else {
                player.removePotionEffect(PotionEffectType.JUMP);
            }

            if (plugin.getMagicItemManager().isEquipped(player, "daisy") && player.getHealth() <= player.getMaxHealth() * 0.25D) {
                long last = plugin.getSafiroManager().isOnCooldown(player, "daisy") ? 1L : 0L;
                if (last == 0L) {
                    int level = plugin.getPlayerDataManager().getLevel(player.getUniqueId(), "daisy");
                    int amount = 2 + level;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 80 + level * 20, amount, false, false, true));
                    plugin.getSafiroManager().setCooldown(player, "daisy", 20000L);
                }
            }
        }
    }

    @EventHandler
    public void onTreeBreak(org.bukkit.event.block.BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getMagicItemManager().isEquipped(player, "chad")) {
            return;
        }
        if (!event.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("AXE")) {
            return;
        }
        if (event.getBlock().getType().toString().endsWith("LOG") || event.getBlock().getType().toString().endsWith("WOOD")) {
            int maxLogs = plugin.getConfig().getInt("magic.chad.max-logs", 200);
            Set<org.bukkit.block.Block> processed = new HashSet<>();
            Queue<org.bukkit.block.Block> queue = new ArrayDeque<>();
            queue.add(event.getBlock());
            int removed = 0;
            while (!queue.isEmpty() && removed < maxLogs) {
                org.bukkit.block.Block current = queue.poll();
                if (!processed.add(current)) {
                    continue;
                }
                if (isTreeBlock(current.getType())) {
                    current.breakNaturally();
                    removed++;
                    for (org.bukkit.block.BlockFace face : org.bukkit.block.BlockFace.values()) {
                        org.bukkit.block.Block adjacent = current.getRelative(face);
                        if (isTreeBlock(adjacent.getType()) && !processed.contains(adjacent)) {
                            queue.add(adjacent);
                        }
                    }
                }
            }
        }
    }

    private boolean isTreeBlock(Material material) {
        String name = material.name();
        return name.contains("LOG") || name.contains("WOOD");
    }
}
