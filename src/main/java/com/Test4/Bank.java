package com.Test4;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.uppgift4.MainActor4;
import com.uppgift4.Worker4;

import java.util.ArrayList;

public class Bank extends AbstractBehavior<Bank.Command> {

    //number of workers who will interact with the bank
    //the bank

    //references to all workers
    private ArrayList<ActorRef<Account.Command>> accounts = new ArrayList<>();
    private ArrayList<ActorRef<Worker.Command>> workers = new ArrayList<>();
    //accounts we will create in the bank(prob need to change accountsCreated in Worker4 if you touch this :)
    private int accountsToCreate = 0;
    //track how many workers have completed the work
    private int numberOfWorkers = 0;
    private int loops = 0;
    private long timeBeforeSetup;
    private int workersFinished;
    private long timeAfterSetup;
    private long timeDone;

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
    public static class SetNumberOfWorkersAndAccountsAndLoops implements Command {
        public final int numberOfWorkers;
        public final int accounts;
        public final int loops;
        public final long timeBeforeSetup;
        public SetNumberOfWorkersAndAccountsAndLoops(int accounts, int loops,int numberOfWorkers, long timeBeforeSetup){
            this.numberOfWorkers = numberOfWorkers;
            this.accounts = accounts;
            this.loops = loops;
            this.timeBeforeSetup = timeBeforeSetup;
        }
    }



    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessageEquals(WorkFinished.INSTANCE, this::onWorkFinished)
                .onMessage(SetNumberOfWorkersAndAccountsAndLoops.class, this::onSetNumberOfWorkersAndAccountsAndLoops)
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




    /**
     * Setup method used from main to set the amount of workers we want to use
     * probably need to implement custom dispatcher again if we want to use more than 8 workers
     * @param setNumberOfWorkersAndAccountsAndLoops
     * @return
     */
    private Behavior<Command> onSetNumberOfWorkersAndAccountsAndLoops(SetNumberOfWorkersAndAccountsAndLoops setNumberOfWorkersAndAccountsAndLoops){
        this.accountsToCreate = setNumberOfWorkersAndAccountsAndLoops.accounts;
        this.numberOfWorkers = setNumberOfWorkersAndAccountsAndLoops.numberOfWorkers;
        this.loops = setNumberOfWorkersAndAccountsAndLoops.loops;
        this.timeBeforeSetup = setNumberOfWorkersAndAccountsAndLoops.timeBeforeSetup;
        return this;
    }

    private Behavior<Bank.Command> onWorkFinished() {
        workersFinished++;
        if(workersFinished==numberOfWorkers){
            timeDone = System.nanoTime();
            System.out.println("All workers have finished!");
            System.out.println("Setup time: " + (timeAfterSetup-timeBeforeSetup)/1.0E9);
            System.out.println("Execution time: " + (timeDone-timeAfterSetup)/1.0E9);
            System.out.println("Total time: " + (timeDone-timeBeforeSetup)/1.0E9);
            for(ActorRef<Account.Command> a : accounts) {
                a.tell(new com.Test4.Account.GetMoneyFromAccount());
            }
        }

        return this;
    }

    /**
     * is called from worker4.onStart once it finishes, keeps track of how many workers have returned
     * and asks for final print of all accounts money when every worker has returned
     * @return
     */


    private Behavior<Command> onStart() {
        for(int i = 0; i < accountsToCreate; i++) {
            accounts.add(getContext().spawn(Account.create(i), "Account" + i, DispatcherSelector.fromConfig("fourth-dispatcher")));
        }

        for(int i = 0; i<numberOfWorkers;i++){
            workers.add(getContext().spawn(Worker.create(loops, accountsToCreate, getContext().getSelf(), accounts), "Worker"+i, DispatcherSelector.fromConfig("fourth-dispatcher")));
        }
        timeAfterSetup = System.nanoTime();

        for (int i = 0; i< numberOfWorkers; i++){
            workers.get(i).tell(Worker.Start.INSTANCE);
        }


        return this;
    }

}
