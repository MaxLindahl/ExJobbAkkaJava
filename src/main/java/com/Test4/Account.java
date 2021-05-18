package com.Test4;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.uppgift4.BankAccount;
import com.uppgift4.MainActor4;

public class Account extends AbstractBehavior<Account.Command> {

    //actor variables
    //this bank accounts unique id

    private int id;
    private int balance = 0;


    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////


    public static class GetMoneyFromAccount implements Command {

        public GetMoneyFromAccount(){
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
    private Account(ActorContext<Command> context, int id) {
        super(context);
        this.id = id;
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Command> create(int id) {

        return Behaviors.setup(context -> new Account(context, id));
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////



    private Behavior<Command> onGetMoneyFromAccount(GetMoneyFromAccount getMoneyFromAccount){
        System.out.println("Account " + id + " has " + balance + " money");
        return this;
    }

    private Behavior<Command> onDepositMoneyToAccount(DepositMoneyToAccount depositMoneyToAccount){
        balance = balance + depositMoneyToAccount.money;
        return this;
    }

    private Behavior<Command> onWithdrawMoneyFromAccount(WithdrawMoneyFromAccount withdrawMoneyFromAccount){
        balance = balance - withdrawMoneyFromAccount.money;
        return this;
    }

}
