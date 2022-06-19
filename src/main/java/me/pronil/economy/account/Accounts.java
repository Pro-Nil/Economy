package me.pronil.economy.account;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.pronil.economy.Economy;

public class Accounts {

    private Economy main;
    private Map<UUID, Account> cached = new HashMap<>();

    public Accounts(Economy main) {
        this.main = main;
    }

    public Map<UUID, Account> getCached() {
        return cached;
    }

    public void onJoin(Player p) {
        Account account = new Account(p.getUniqueId(), p.getName());
        account.setBalance(main.getDefaultMoney());
        main.getSQLDatabase().getOrInsertAccountAsync(account);
        cached.put(p.getUniqueId(), account);
    }

    public void onQuit(Player p) {
        Account account = cached.remove(p.getUniqueId());
        if (account != null) {
            main.getSQLDatabase().updateAccountAsync(account);
        }
    }

    public boolean createAccount(OfflinePlayer player) {
        Account account = cached.get(player.getUniqueId());
        if (account == null) {
            account = new Account(player);
            account.setBalance(main.getDefaultMoney());
            if (!main.getSQLDatabase().hasAccount(account)) {
                return main.getSQLDatabase().createAccount(account);
            }
        }
        return false;
    }

    public Account getAccount(OfflinePlayer player) {
        Account account = cached.get(player.getUniqueId());
        if (account == null) {
            account = new Account(player);
            main.getSQLDatabase().getAccount(account, null);
        }
        return account;
    }

    public boolean hasAccount(OfflinePlayer player) {
        Account account = cached.get(player.getUniqueId());
        if (account == null) {
            return main.getSQLDatabase().hasAccount(new Account(player));
        }
        return true;
    }

}
