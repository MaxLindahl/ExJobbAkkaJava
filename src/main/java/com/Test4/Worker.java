package com.Test4;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.uppgift4.Bank;
import com.uppgift4.MainActor4;

import java.util.ArrayList;

public class Worker extends AbstractBehavior<Worker.Command> {

    //the bank we will work with
    private ArrayList<ActorRef<Account.Command>> bankAccounts;
    private ActorRef<com.Test4.Bank.Command> bank;
    //how many times we want to interact with the bank per loop
    private int loops;
    private int accounts;

    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }



    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private Worker(ActorContext<Command> context, int loops, int accounts, ActorRef bank, ArrayList<ActorRef<Account.Command>> bankAccounts) {
        super(context);
        this.loops = loops;
        this.accounts = accounts;
        this.bank = bank;
        this.bankAccounts = bankAccounts;
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Command> create(int loops, int accounts, ActorRef bank, ArrayList<ActorRef<Account.Command>> bankAccounts) {
        return Behaviors.setup(context -> new Worker(context, loops, accounts, bank, bankAccounts));
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////



    private Behavior<Command> onStart() {
        int currentAccount = 0;
        //loop deposit 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bankAccounts.get(currentAccount).tell(new com.Test4.Account.DepositMoneyToAccount(10));
            System.out.println("Deposit");
            currentAccount++;
            if(currentAccount== accounts)
                currentAccount = 0;
        }
        //loop withdraw 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bankAccounts.get(currentAccount).tell(new com.Test4.Account.WithdrawMoneyFromAccount(10));
            System.out.println("Withdraw");
            currentAccount++;
            if(currentAccount== accounts)
                currentAccount = 0;
        }
        //loop deposit and withdraw 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bankAccounts.get(currentAccount).tell(new com.Test4.Account.DepositMoneyToAccount(10));
            System.out.println("Deposit");
            bankAccounts.get(currentAccount).tell(new com.Test4.Account.WithdrawMoneyFromAccount(10));
            System.out.println("Withdraw");
            currentAccount++;
            if(currentAccount== accounts)
                currentAccount = 0;
        }
        bank.tell(com.Test4.Bank.WorkFinished.INSTANCE);
        return this;
    }

}
