package com.uppgift4;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;

public class Worker4 extends AbstractBehavior<Worker4.Command> {

    //the bank we will work with
    private ArrayList<ActorRef> bankAccounts;
    private ActorRef<MainActor4.Command> mainActor;
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
    private Worker4(ActorContext<Command> context, int loops, int accounts, ActorRef mainActor, ArrayList<ActorRef> bankAccounts) {
        super(context);
        this.loops = loops;
        this.accounts = accounts;
        this.mainActor = mainActor;
        this.bankAccounts = bankAccounts;
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Command> create(int loops, int accounts, ActorRef mainActor, ArrayList<ActorRef> bankAccounts) {
        return Behaviors.setup(context -> new Worker4(context, loops, accounts, mainActor, bankAccounts));
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////



    private Behavior<Command> onStart() {
        int currentAccount = 0;

        //loop deposit 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bankAccounts.get(currentAccount).tell(new Bank.DepositMoneyToAccount(10));
            System.out.println("Deposit");
            currentAccount++;
            if(currentAccount== accounts)
                currentAccount = 0;
        }
        //loop withdraw 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bankAccounts.get(currentAccount).tell(new Bank.WithdrawMoneyFromAccount(10));
            System.out.println("Withdraw");
            currentAccount++;
            if(currentAccount== accounts)
                currentAccount = 0;
        }
        //loop deposit and withdraw 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bankAccounts.get(currentAccount).tell(new Bank.DepositMoneyToAccount(10));
            System.out.println("Deposit");
            bankAccounts.get(currentAccount).tell(new Bank.WithdrawMoneyFromAccount(10));
            System.out.println("Withdraw");
            currentAccount++;
            if(currentAccount== accounts)
                currentAccount = 0;
        }
        mainActor.tell(MainActor4.WorkFinished.INSTANCE);
        return this;
    }

}
