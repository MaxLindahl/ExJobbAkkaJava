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
    ArrayList<BankAccount> accounts = new ArrayList<>();


    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    public static class CreateAccount implements Command {
        private final int money;
        public CreateAccount(int money){
            this.money = money;
        }
    }

    public static class GetMoneyFromAccount implements Command {
        private final String id;
        public final akka.actor.typed.ActorRef<MainActor4.Command> mainActor;

        public GetMoneyFromAccount(String id, ActorRef mainActor){
            this.id = id;
            this.mainActor = mainActor;
        }
    }

    public static class DepositMoneyToAccount implements Command {
        private final String id;
        private final int money;
        public DepositMoneyToAccount(String id, int money){
            this.id = id;
            this.money = money;
        }
    }

    public static class WithdrawMoneyFromAccount implements Command {
        private final String id;
        private final int money;
        public WithdrawMoneyFromAccount(String id, int money){
            this.id = id;
            this.money = money;
        }
    }

    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
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
        accounts.add(new BankAccount(""+accounts.size(), createAccount.money));
        return this;
    }

    private Behavior<Command> onGetMoneyFromAccount(GetMoneyFromAccount getMoneyFromAccount){
        int money = accounts.get(Integer.parseInt(getMoneyFromAccount.id)).getMoney();
        getMoneyFromAccount.mainActor.tell(new MainActor4.AccountMoney(money, getMoneyFromAccount.id));
        return this;
    }

    private Behavior<Command> onDepositMoneyToAccount(DepositMoneyToAccount depositMoneyToAccount){
        accounts.get(Integer.parseInt(depositMoneyToAccount.id)).depositMoney(depositMoneyToAccount.money);
        return this;
    }

    private Behavior<Command> onWithdrawMoneyFromAccount(WithdrawMoneyFromAccount withdrawMoneyFromAccount){
        accounts.get(Integer.parseInt(withdrawMoneyFromAccount.id)).withdrawMoney(withdrawMoneyFromAccount.money);
        return this;
    }

    private Behavior<Command> onStart() {
        //do things
        return this;
    }
}
