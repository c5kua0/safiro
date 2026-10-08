package me.safiro.command;

import me.safiro.Safiro;
import me.safiro.gui.MainGUI;
import me.safiro.gui.SlotGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SafiroCommand implements CommandExecutor {

    private final Safiro plugin;

    public SafiroCommand(Safiro plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                MainGUI.open(player);
            } else {
                sender.sendMessage("§cOnly players can open the Safiro GUI.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("points")) {
            if (args.length == 1) {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cOnly players can check their own Safiro Points.");
                    return true;
                }
                int points = plugin.getPlayerDataManager().getPoints(player.getUniqueId());
                player.sendMessage("§bSafiro Points: §f" + points);
                return true;
            }

            if (args.length == 3) {
                if (!sender.hasPermission("safiro.admin")) {
                    sender.sendMessage("§cYou do not have permission to use this command.");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    plugin.getPlayerDataManager().setPoints(target.getUniqueId(), Math.max(0, amount));
                    sender.sendMessage("§aSet Safiro Points for " + target.getName() + " to " + amount + ".");
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§cUsage: /safiro points <player> <amount>");
                }
                return true;
            }

            sender.sendMessage("§cUsage: /safiro points or /safiro points <player> <amount>");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("safiro.admin")) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }
            if (args.length != 3) {
                sender.sendMessage("§cUsage: /safiro give <magic_item> <player>");
                return true;
            }
            String itemId = args[1].toLowerCase();
            Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }
            if (!plugin.getMagicItemManager().getItemMaterials().containsKey(itemId)) {
                sender.sendMessage("§cUnknown magic item: " + itemId);
                sender.sendMessage("§7Available items: fireball, lifebloom, rabbit_leg, has_claw, daisy, chad, god_wings");
                return true;
            }
            ItemStack magicItem = plugin.getMagicItemManager().createItem(itemId, 1);
            target.getInventory().addItem(magicItem);
            sender.sendMessage("§aGave " + plugin.getMagicItemManager().getItemDisplayName(itemId) + "§a to " + target.getName() + ".");
            target.sendMessage("§aYou received " + plugin.getMagicItemManager().getItemDisplayName(itemId) + "§a!");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("safiro.admin")) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }
            plugin.reloadConfig();
            plugin.getPlayerDataManager().reloadData();
            if (sender instanceof Player) {
                sender.sendMessage("§bSafiro configuration reloaded successfully.");
            } else {
                sender.sendMessage("[Safiro] Configuration reloaded successfully.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("slots")) {
            if (sender instanceof Player player) {
                SlotGUI.open(player);
            } else {
                sender.sendMessage("§cOnly players can use this menu.");
            }
            return true;
        }

        sender.sendMessage("§cUsage: /safiro, /safiro points, /safiro give <item> <player>, /safiro reload");
        return true;
    }
}
