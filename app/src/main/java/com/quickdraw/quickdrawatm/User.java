package com.quickdraw.quickdrawatm;

/**
 * Created by Matthew on 11/8/16.
 */

public class User {

    private String userID;
    private String name;
    private String pin;

    private float balance;
    private float[] transactions = new float[5];
    private String[] dates = {" ", " ", " ", " ", " "};

    public User() {

    }

    public User(String userID, String name, String pin, float balance) {
        this.userID = userID;
        this.name = name;
        this.pin = pin;
        this.balance = balance;
    }

    public String getUserID() { return userID; }

    public String getName() {
        return name;
    }

    public String getPin() { return pin; }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float newBalance) {
        balance = newBalance;
    }

    public float[] getTransactions() {
        return transactions;
    }

    public String[] getDates() {
        return dates;
    }

    public void addTransaction(float amount) {
        for (int i = transactions.length - 1; i > 0; i--) {
            transactions[i] = transactions[i-1];
        }
        transactions[0] = amount;
    }

    public void addDate(String date) {
        for (int i = dates.length - 1; i > 0; i--) {
            dates[i] = dates[i-1];
        }
        dates[0] = date;
    }
}
