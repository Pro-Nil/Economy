package me.pronil.economy.command;

import me.pronil.economy.Economy;
import me.pronil.economy.account.Account;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Eco implements CommandExecutor {

    private Economy main;

    public Eco(Economy main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (sender.hasPermission("economy.eco")) {
            if (args.length == 0) {
                sender.sendMessage("§e - /eco give <name> <amount>");
                sender.sendMessage("§e - /eco set <name> <amount>");
                sender.sendMessage("§e - /eco take <name> <amount>");
                return true;
            } else if (args.length == 3) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                try {
                    double amount = Double.parseDouble(args[2]);
                    if (args[0].equals("give")) {
                        if (main.getAccounts().hasAccount(target)) {
                            Account account = main.getAccounts().getAccount(target);
                            account.setBalance(account.getBalance() + amount);
                            main.getSQLDatabase().updateAccountAsync(account);
                            sender.sendMessage("§2You gave §f" + main.formatCurrency(amount) + " §2to §f" + target.getName());
                        } else {
                            sender.sendMessage("§cThe account doesn't exists!");
                        }
                        return true;
                    } else if (args[0].equals("set")) {
                        if (main.getAccounts().hasAccount(target)) {
                            Account account = main.getAccounts().getAccount(target);
                            account.setBalance(amount);
                            main.getSQLDatabase().updateAccountAsync(account);
                            sender.sendMessage("§2You set §f" + target.getName() + " §2to §f"  + main.formatCurrency(amount));
                        } else {
                            sender.sendMessage("§cThe account doesn't exists!");
                        }
                        return true;
                    } else if (args[0].equals("take")) {
                        if (main.getAccounts().hasAccount(target)) {
                            Account account = main.getAccounts().getAccount(target);
                            if (account.getBalance() < amount) {
                                sender.sendMessage("§cNot enough funds!");
                            } else {
                                account.setBalance(account.getBalance() - amount);
                                main.getSQLDatabase().updateAccountAsync(account);
                                sender.sendMessage("§2You took §f" + main.formatCurrency(amount) + " §2from §f" + target.getName());
                            }
                        } else {
                            sender.sendMessage("§cThe account doesn't exists!");
                        }
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cThe amount must be a number!");
                }
            }
            sender.sendMessage("§cInvalid arguments! Type /eco for available commands!");
        } else {
            sender.sendMessage(main.getPermission());
        }
        return false;
    }

}