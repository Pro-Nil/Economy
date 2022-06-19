package me.pronil.economy;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.pronil.economy.account.Accounts;
import me.pronil.economy.command.Money;
import me.pronil.economy.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import me.pronil.economy.command.Eco;
import me.pronil.economy.command.Pay;
import me.pronil.economy.command.Top;
import me.pronil.economy.database.Database;

public class Economy extends JavaPlugin {

    private Accounts accounts;
    private Database database;
    private double defaultMoney;
    private DecimalFormat formatter;
    private ExecutorService executor;
    private String table = "economy";

    private String money;
    private String moneyPlayer;
    private String sentTo;
    private String receivedFrom;
    private String currencyPlural;
    private String currencySingular;
    private String notEnough;
    private String permission;
    private String moneytoggleon;
    private String moneytoggleoff;
    private String top;
    private String top_rank;
    private String top_not_found;
    private String positive;
    private String moneydenied;
    private String payaccount;
    private String paynotnumber;
    private String moneyyourself;
    private String currencyformat;
    private String top_all_money;
    private VaultHook vault_hook;

    public void onEnable() {
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        console.sendMessage("§a" + getName() + " plugin is loading... ");
        console.sendMessage("§a - Loading config...");
        FileConfiguration config = null;
        try {
            config = getConfig();
            defaultMoney = config.getDouble("Settings.DefaultMoney");
            currencyPlural = getMessage(config, "CurrencyPlural");
            currencySingular = getMessage(config, "CurrencySingular");
            receivedFrom = getMessage(config, "ReceivedFrom");
            moneyPlayer = getMessage(config, "MoneyPlayer");
            notEnough = getMessage(config, "NotEnough");
            sentTo = getMessage(config, "SentTo");
            money = getMessage(config, "Money");
            permission = getMessage(config, "NoPermission");
            moneytoggleon = getMessage(config, "MoneyToggleOn");
            moneytoggleoff = getMessage(config, "MoneyToggleOff");
            moneyyourself = getMessage(config, "MoneyYourself");
            moneydenied = getMessage(config, "MoneyDenied");
            payaccount = getMessage(config, "PayAccount");
            paynotnumber = getMessage(config, "PayNotNumber");
            currencyformat = getMessage(config, "CurrencyFormat");
            top = getMessage(config, "Top");
            top_rank = getMessage(config, "TopRank");
            top_not_found = getMessage(config, "TopNotFound");
            top_all_money = getMessage(config, "TopAllMoney");
            positive = getMessage(config, "Positive");
            saveDefaultConfig();
        } catch (Exception e) {
            e.printStackTrace();
            setEnabled(false);
            return;
        }
        console.sendMessage("§a - Loading database...");
        if (config.getBoolean("MySQL.Enabled")) {
            try {
                String host = config.getString("MySQL.Host");
                String database = config.getString("MySQL.Database");
                String username = config.getString("MySQL.Username");
                String password = config.getString("MySQL.Password");
                boolean reconnect = config.getBoolean("MySQL.AutoReconnect");
                int port = config.getInt("MySQL.Port");
                table = config.getString("MySQL.Table");
                this.database = new Database(this, reconnect, host, database, username, password, port);
            } catch (Exception e) {
                console.sendMessage("§cMySQL has failed to load: " + e.getMessage());
                setEnabled(false);
                return;
            }
        } else {
            try {
                database = new Database(this, getDataFolder());
            } catch (Exception e) {
                console.sendMessage("§cSQLite has failed to load: " + e.getMessage());
                setEnabled(false);
                return;
            }
        }
        try {
            database.createTable("CREATE TABLE IF NOT EXISTS " + table + " (UUID VARCHAR(36) UNIQUE, NAME VARCHAR(60), BALANCE DOUBLE(64,2));");
        } catch (SQLException e) {
            console.sendMessage("§cThe table has failed to load: " + e.getMessage());
            setEnabled(false);
            return;
        }
        formatter = new DecimalFormat("#,##0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        executor = Executors.newSingleThreadExecutor();
        accounts = new Accounts(this);
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            console.sendMessage("§a - Hooking into Vault...");
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, vault_hook = new VaultHook(this), this, ServicePriority.High);
        }
        console.sendMessage("§a - Loading events...");
        getServer().getPluginManager().registerEvents(new Events(this), this);
        console.sendMessage("§a - Loading commands...");
        getCommand("pay").setExecutor(new Pay(this));
        getCommand("eco").setExecutor(new Eco(this));
        getCommand("baltop").setExecutor(new Top(this));
        getCommand("money").setExecutor(new Money(this));
        Bukkit.getOnlinePlayers().forEach(p -> accounts.onJoin(p));
        console.sendMessage("§a" + getName() + " has been loaded!");
        console.sendMessage("§a=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
    }

    public void onDisable() {
        if (database != null) {
            getServer().getServicesManager().unregister(vault_hook);
            Bukkit.getOnlinePlayers().forEach(p -> accounts.onQuit(p));
            executor.shutdownNow().forEach(r -> r.run());
            database.close();
        }
    }

    private String getMessage(FileConfiguration config, String path) {
        return config.getString("Messages." + path).replace('&', '§');
    }

    public String getMoney() {
        return money;
    }

    public String getMoneyPlayer() {
        return moneyPlayer;
    }

    public String getPermission() {
        return permission;
    }

    public String getNotEnough() {
        return notEnough;
    }

    public String getSentTo() {
        return sentTo;
    }

    public String getReceivedFrom() {
        return receivedFrom;
    }

    public String getTable() {
        return table;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public double getDefaultMoney() {
        return defaultMoney;
    }

    public Database getSQLDatabase() {
        return database;
    }

    public String getMoneyToggleOn() {
        return moneytoggleon;
    }

    public String getMoneyToggleOff() {
        return moneytoggleoff;
    }

    public String getTop() {
        return top;
    }

    public String getTopRank() {
        return top_rank;
    }

    public String getTopNotFound() {
        return top_not_found;
    }

    public String getTopAllMoney() {
        return top_all_money;
    }

    public String getPositive() {
        return positive;
    }

    public String getMoneyDenied() {
        return moneydenied;
    }

    public String getPayAccount() {
        return payaccount;
    }

    public String getPayNotNumber() {
        return paynotnumber;
    }

    public String getMoneyYourself() {
        return moneyyourself;
    }

    public String getCurrency(double amount) {
        return amount == 1.0 ? currencySingular : currencyPlural;
    }

    public String formatCurrency(double amount) {
        return currencyformat.replace("%money%", formatter.format(amount)).replace("%currency%", getCurrency(amount));
    }

}