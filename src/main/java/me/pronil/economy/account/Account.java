package me.pronil.economy.account;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class Account {

    private UUID uuid;
    private String name;
    private double balance = 0.00;
    public boolean canReceive = true;

    public Account(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Account(OfflinePlayer player) {
        name = player.getName();
        uuid = player.getUniqueId();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean canReceive() {
        return canReceive;
    }

    public void setReceive(boolean canReceive) {
        this.canReceive = canReceive;
    }

}
