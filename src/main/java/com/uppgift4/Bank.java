package com.uppgift4;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;

public class Bank extends AbstractBehavior<Bank.Command> {

    //actor variables
    private BankAccount bankAccount;
    //this bank accounts unique id
    private int id;


    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////


    public static class GetMoneyFromAccount implements Command {
        public final akka.actor.typed.ActorRef<MainActor4.Command> mainActor;

        public GetMoneyFromAccount(ActorRef mainActor){
            this.mainActor = mainActor;
        }
    }

    public static class DepositMoneyToAccount implements Command {
        private final int money;
        public DepositMoneyToAccount(int money){
            this.money = money;
        }
    }

    public static class WithdrawMoneyFromAccount implements Command {
        private final int money;
        public WithdrawMoneyFromAccount(int money){
            this.money = money;
        }
    }

    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetMoneyFromAccount.class, this::onGetMoneyFromAccount)
                .onMessage(DepositMoneyToAccount.class, this::onDepositMoneyToAccount)
                .onMessage(WithdrawMoneyFromAccount.class, this::onWithdrawMoneyFromAccount)
                .build();
    }

    /**
     * Constructor
     * @param context
     */
    private Bank(ActorContext<Command> context, int id) {
        super(context);
        this.id = id;
        this.bankAccount = new BankAccount(0);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Command> create(int id) {
        return Behaviors.setup(context -> new Bank(context, id));
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////



    private Behavior<Command> onGetMoneyFromAccount(GetMoneyFromAccount getMoneyFromAccount){
        int money = bankAccount.getMoney();
        System.out.println("Account " + id + " has " + money + " money");
        return this;
    }

    private Behavior<Command> onDepositMoneyToAccount(DepositMoneyToAccount depositMoneyToAccount){
        bankAccount.depositMoney(depositMoneyToAccount.money);
        return this;
    }

    private Behavior<Command> onWithdrawMoneyFromAccount(WithdrawMoneyFromAccount withdrawMoneyFromAccount){
        bankAccount.withdrawMoney(withdrawMoneyFromAccount.money);
        return this;
    }

}