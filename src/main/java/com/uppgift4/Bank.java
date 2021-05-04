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
    ArrayList<BankAccount> accountList = new ArrayList<>();


    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////


    public static class CreateAccount implements Command {
        private final int balance;
        public CreateAccount(int balance){
            this.balance = balance;
        }
    }

    public static class GetMoneyFromAccount implements Command {
        private final int id;
        public final akka.actor.typed.ActorRef<MainActor4.Command> mainActor;

        public GetMoneyFromAccount(int id, ActorRef mainActor){
            this.id = id;
            this.mainActor = mainActor;
        }
    }

    public static class DepositMoneyToAccount implements Command {
        private final int id;
        private final int balance;
        public DepositMoneyToAccount(int id, int balance){
            this.id = id;
            this.balance = balance;
        }
    }

    public static class WithdrawMoneyFromAccount implements Command {
        private final int id;
        private final int balance;
        public WithdrawMoneyFromAccount(int id, int balance){
            this.id = id;
            this.balance = balance;
        }
    }

    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(CreateAccount.class, this::onCreateAccount)
                .onMessage(GetMoneyFromAccount.class, this::onGetMoneyFromAccount)
                .onMessage(DepositMoneyToAccount.class, this::onDepositMoneyToAccount)
                .onMessage(WithdrawMoneyFromAccount.class, this::onWithdrawMoneyFromAccount)
                .build();
    }

    /**
     * Constructor
     * @param context
     */
    private Bank(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Command> create() {
        return Behaviors.setup(Bank::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////

    private Behavior<Command> onCreateAccount(CreateAccount createAccount){
        accountList.add(new BankAccount(accountList.size(), createAccount.balance));
        return this;
    }

    private Behavior<Command> onGetMoneyFromAccount(GetMoneyFromAccount getBalanceFromAccount){
        int balance = accountList.get(getBalanceFromAccount.id).getBalance();
        System.out.println("Account " + getBalanceFromAccount.id + " has " + balance + " balance");
        return this;
    }

    private Behavior<Command> onDepositMoneyToAccount(DepositMoneyToAccount depositToAccount){
        accountList.get(depositToAccount.id).deposit(depositToAccount.balance);

        return this;
    }

    private Behavior<Command> onWithdrawMoneyFromAccount(WithdrawMoneyFromAccount withdrawFromAccount){
        accountList.get(withdrawFromAccount.id).withdraw(withdrawFromAccount.balance);

        return this;
    }

}
