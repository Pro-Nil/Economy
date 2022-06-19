package me.pronil.economy.command;

import me.pronil.economy.Economy;
import me.pronil.economy.account.Account;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Pay implements CommandExecutor {

    private Economy main;

    public Pay(Economy main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (!sender.hasPermission("economy.pay")) {
            sender.sendMessage(main.getPermission());
        } else if (sender == Bukkit.getConsoleSender()) {
            sender.sendMessage("Can be used only by players!");
        } else if (args.length != 2) {
            sender.sendMessage("§cInvalid arguments! /pay <name> <amount>");
        } else {
            Player player = (Player) sender;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (player.getUniqueId() == target.getUniqueId()) {
                sender.sendMessage(main.getMoneyYourself());
            } else {
                try {
                    double amount = Math.abs(Double.parseDouble(args[1]));
                    if (amount < 0) {
                        sender.sendMessage(main.getPositive());
                    } else if (!main.getAccounts().hasAccount(target)) {
                        sender.sendMessage(main.getPayAccount());
                    } else {
                        Account playerAccount = main.getAccounts().getAccount(player);
                        if (playerAccount.getBalance() < amount) {
                            sender.sendMessage(main.getNotEnough());
                        } else {
                            Account targetAccount = main.getAccounts().getAccount(target);
                            if (!targetAccount.canReceive()) {
                                sender.sendMessage(main.getMoneyDenied());
                            } else {
                                playerAccount.setBalance(playerAccount.getBalance() - amount);
                                targetAccount.setBalance(targetAccount.getBalance() + amount);
                                sender.sendMessage(main.getSentTo().replace("%money%", main.formatCurrency(amount)).replace("%name%", target.getName()));
                                if (target.isOnline()) {
                                    ((Player) target).sendMessage(main.getReceivedFrom().replace("%money%", main.formatCurrency(amount)).replace("%name%", player.getName()));
                                }
                                return true;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(main.getPayNotNumber());
                }
            }
        }
        return false;
    }

}
