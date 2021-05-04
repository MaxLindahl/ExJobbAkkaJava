package com.uppgift4;

public class BankAccount {
    private int id;
    private int balance;

    public BankAccount(int id, int balance){
        this.id = id;
        this.balance = balance;
    }

    public void deposit(int balance){
        this.balance +=balance;
    }

    public void withdraw(int balance){
        this.balance -=balance;
    }

    public int getBalance() {
        return balance;
    }
}
