package com.uppgift4;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;

public class MainActor4 extends AbstractBehavior<MainActor4.Command> {

    //number of workers who will interact with the bank
    private int numberOfWorkers = 8;
    //the bank
    private ActorRef<Bank.Command> bank;
    //references to all workers
    private ArrayList<ActorRef<Worker4.Command>> workers = new ArrayList<>();
    //accounts we will create in the bank(prob need to change accountsCreated in Worker4 if you touch this :)
    private int accountsToCreate = 8;
    //track how many workers have completed the work
    private int workersFinished = 0;

    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    //sent from main, starts everything
    public enum Start implements Command {
        INSTANCE
    }

    //sent back from worker4 once work is finished
    public enum WorkFinished implements Command {
        INSTANCE
    }

    //send from main to set the number of workers to use
    public static class SetNumberOfWorkers implements Command {
        int number;
        public SetNumberOfWorkers(int number){
            this.number = number;
        }
    }

    //sent from the bank to give us the money in a given bank account
    public static class AccountMoney implements Command {
        int money;
        String id;
        public AccountMoney(int money, String id){
            this.money = money;
            this.id = id;
        }
    }


    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<MainActor4.Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessageEquals(WorkFinished.INSTANCE, this::onWorkFinished)
                .onMessage(SetNumberOfWorkers.class, this::onSetNumberOfWorkers)
                .onMessage(AccountMoney.class, this::onAccountMoney)
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private MainActor4(ActorContext<MainActor4.Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<MainActor4.Command> create() {
        return Behaviors.setup(MainActor4::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////


    //should all return as 0 money since we withdraw the same amount of money we deposit in worker4.onStart
    private Behavior<MainActor4.Command> onAccountMoney(AccountMoney accountMoney){
        System.out.println("Account id: " + accountMoney.id + " has " + accountMoney.money + " money in it!");

        return this;
    }

    /**
     * Setup method used from main to set the amount of workers we want to use
     * probably need to implement custom dispatcher again if we want to use more than 8 workers
     * @param setNumberOfWorkers
     * @return
     */
    private Behavior<MainActor4.Command> onSetNumberOfWorkers(SetNumberOfWorkers setNumberOfWorkers){
        this.numberOfWorkers = setNumberOfWorkers.number;
        return this;
    }

    /**
     * is called from worker4.onStart once it finishes, keeps track of how many workers have returned
     * and asks for final print of all accounts money when every worker has returned
     * @return
     */
    private Behavior<Command> onWorkFinished() {
        workersFinished++;
        System.out.println("A worker finished");
        if(workersFinished==numberOfWorkers){
            System.out.println("All workers have finished!");
            for(int i = 0; i < accountsToCreate; i++)
                bank.tell(new Bank.GetMoneyFromAccount(i+"", getContext().getSelf()));
        }

        return this;
    }


    private Behavior<MainActor4.Command> onStart() {
        //create a bank
        bank = getContext().spawn(Bank.create(), "Bank");

        //create accountsToCreate many accounts in the bank
        for(int i = 0; i < accountsToCreate; i++) {
            bank.tell(new Bank.CreateAccount(0));
        }

        //create workers (actors who will interact with the bank)
        for(int i = 0; i < numberOfWorkers; i++){
            workers.add(getContext().spawn(Worker4.create(), "Worker"+i));
        }
        //tell the workers where to find the bank
        for(int i = 0; i < numberOfWorkers; i++){
            workers.get(i).tell(new Worker4.SetupBank(bank));
        }
        //tell the workers to start working, send reference to ourselves so it can message us when work is done
        for (int i = 0; i< numberOfWorkers; i++){
            workers.get(i).tell(new Worker4.Start(getContext().getSelf()));
        }
        return this;
    }

}
