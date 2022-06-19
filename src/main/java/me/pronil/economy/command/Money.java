package me.pronil.economy.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.pronil.economy.Economy;
import me.pronil.economy.account.Account;

public class Money implements CommandExecutor {

    private Economy main;

    public Money(Economy main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("economy.money")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    Account account = main.getAccounts().getAccount(p);
                    p.sendMessage(main.getMoney().replace("%money%", main.formatCurrency(account.getBalance())));
                    return true;
                } else {
                    sender.sendMessage("§cInvalid arguments! Please use /money <name>");
                }
            } else {
                sender.sendMessage(main.getPermission());
            }
        } else if (args.length == 1) {
            if (args[0].equals("toggle")) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    Account account = main.getAccounts().getAccount(p);
                    account.setReceive(!account.canReceive());
                    if (account.canReceive()) {
                        p.sendMessage(main.getMoneyToggleOn());
                    } else {
                        p.sendMessage(main.getMoneyToggleOff());
                    }
                    return true;
                } else {
                    sender.sendMessage("§cInvalid arguments! Please use /money <name>");
                }
            } else if (sender.hasPermission("economy.money.name")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (main.getAccounts().hasAccount(target)) {
                    Account account = main.getAccounts().getAccount(target);
                    sender.sendMessage(main.getMoneyPlayer().replace("%money%", main.formatCurrency(account.getBalance())).replace("%name%", target.getName()));
                    return true;
                } else {
                    sender.sendMessage("§cThe account was not found!");
                }
            } else {
                sender.sendMessage(main.getPermission());
            }
        }
        return false;
    }

}