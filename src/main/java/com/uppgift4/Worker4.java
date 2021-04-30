package com.uppgift4;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Worker4 extends AbstractBehavior<Worker4.Command> {

    //the bank we will work with
    private ActorRef<Bank.Command> bank;
    //how many times we want to interact with the bank per loop
    private int loops = 10000;
    private int accountsCreated = 8;

    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public static class Start implements  Command {
        ActorRef<MainActor4.Command> mainActor;
        public Start(ActorRef mainActor){
            this.mainActor = mainActor;
        }
    }

    public static class SetupBank implements Command {
        ActorRef<Bank.Command> bank;
        public SetupBank(ActorRef bank){
            this.bank = bank;
        }
    }


    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, this::onStart)
                .onMessage(SetupBank.class, this::onSetupBank)
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private Worker4(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Command> create() {
        return Behaviors.setup(Worker4::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////


    private Behavior<Command> onSetupBank(SetupBank setupBank) {
        bank = setupBank.bank;
        return this;
    }

    private Behavior<Command> onStart(Start start) {
        int currentAccount = 0;

        //loop deposit 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bank.tell(new Bank.DepositMoneyToAccount(currentAccount+"",10));
            currentAccount++;
            if(currentAccount==accountsCreated)
                currentAccount = 0;
        }
        //loop withdraw 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bank.tell(new Bank.WithdrawMoneyFromAccount(currentAccount+"", 10));
            currentAccount++;
            if(currentAccount==accountsCreated)
                currentAccount = 0;
        }
        //loop deposit and withdraw 10 moneys into alternating accounts
        for(int i = 0; i < loops; i++){
            bank.tell(new Bank.DepositMoneyToAccount(currentAccount+"", 10));
            bank.tell(new Bank.WithdrawMoneyFromAccount(currentAccount+"", 10));
            currentAccount++;
            if(currentAccount==accountsCreated)
                currentAccount = 0;
        }

        start.mainActor.tell(MainActor4.WorkFinished.INSTANCE);
        return this;
    }

}
