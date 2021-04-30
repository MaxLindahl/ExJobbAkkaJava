package com.uppgift4;

public class BankAccount {
    private String id;
    private int money;

    public BankAccount(String id, int money){
        this.id = id;
        this.money = money;
    }

    public void depositMoney(int money){
        this.money+=money;
    }

    public void withdrawMoney(int money){
        this.money-=money;
    }

    public String getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }
}
