package me.pronil.economy.command;

import me.pronil.economy.Economy;
import me.pronil.economy.account.Account;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Top implements CommandExecutor  {

    private Economy main;

    public Top(Economy main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        int[] page = { 0 };
        if (!sender.hasPermission("economy.top")) {
            sender.sendMessage(main.getPermission());
            return true;
        }
        if (args.length == 1) {
            if (!sender.hasPermission("economy.top.pages")) {
                sender.sendMessage(main.getPermission());
                return true;
            }
            try {
                page[0] = Integer.parseInt(args[0]) - 1;
                if (page[0] < 0) {
                    sender.sendMessage(main.getPositive());
                    return true;
                }
                page[0] *= 10;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args.length > 1) {
            sender.sendMessage("§cInvalid arguments! Please use /baltop <page>");
            return false;
        }
        main.getSQLDatabase().getTopAsync(page[0], (all_balance, list) -> {
            if (list.size() == 0) {
                sender.sendMessage(main.getTopNotFound());
                return;
            }
            sender.sendMessage(main.getTop());
            sender.sendMessage(" ");
            for (Account account : list) {
                page[0]++;
                sender.sendMessage(main.getTopRank().replace("%money%", main.formatCurrency(account.getBalance())).replace("%name%", account.getName()).replace("%rank%", String.valueOf(page[0])));
            }
            if (page[0] <= 10) {
                sender.sendMessage(" ");
                sender.sendMessage(main.getTopAllMoney().replace("%all_money%", main.formatCurrency(all_balance)));
            }
        });
        return true;
    }

}