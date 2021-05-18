package com.uppgift4;

public class BankAccount {
    private int money;

    public BankAccount(int money){
        this.money = money;
    }

    public void depositMoney(int money){
        this.money+=money;
    }

    public void withdrawMoney(int money){
        this.money-=money;
    }


    public int getMoney() {
        return money;
    }
}
