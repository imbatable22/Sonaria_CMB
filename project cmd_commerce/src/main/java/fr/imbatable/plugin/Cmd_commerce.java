package fr.imbatable.plugin;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

 public class Cmd_commerce extends JavaPlugin {
    private static Economy econ = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault n'est pas trouvé ou aucun plugin d'économie n'est installé !");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Le plugin cmd_commerce a été chargé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Le plugin cmd_commerce a été arrêté !");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pub_commerce")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage((ChatColor.DARK_RED) + "❌ Cette commande doit être exécutée par un joueur !");
                return true;
            }

            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage((ChatColor.DARK_RED) + "❌ Vous ne tenez aucun objet en main !");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage((ChatColor.DARK_RED) + "❌ Utilisation : /pub_commerce <prix> <quantité>");
                return true;
            }

            double price;
            int quantity;
            try {
                price = Double.parseDouble(args[0]);
                quantity = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage( (ChatColor.DARK_RED) + "❌ Le prix et la quantité doivent être des nombres valides !");
                return true;
            }

            if (quantity <= 0) {
                player.sendMessage(ChatColor.DARK_RED + "❌ La quantité doit être supérieure à 0 !");
                return true;
            }

            // Vérification de l'argent du joueur
            double pubCost = 200.0;
            if (!econ.has(player, pubCost)) {
                player.sendMessage ( + pubCost + "$)");
                return true;
            }

            // Retirer l'argent
            econ.withdrawPlayer(player, pubCost);
        player.sendMessage((ChatColor.GREEN)+"✅ Vous avez payé " + pubCost + "$ pour publier votre annonce.");

            // Annonce dans le chat global
            Bukkit.broadcastMessage(ChatColor.GOLD +"📢 " + player.getName() + " vend " + quantity + "x " + item.getType().name() + " pour " + price + " $ !");

            return true;
        }
        return false;
    }
}


