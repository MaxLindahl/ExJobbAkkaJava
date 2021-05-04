package com.uppgift4;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.uppgift3.Worker3;

import java.util.ArrayList;

public class MainActor4 extends AbstractBehavior<MainActor4.Command> {

    //number of workers who will interact with the bank
    private int numberOfWorkers;
    //the bank
    private ActorRef<Bank.Command> bank;
    //references to all workers
    private ArrayList<ActorRef<Worker4.Command>> workers = new ArrayList<>();
    //accounts we will create in the bank(prob need to change accountsCreated in Worker4 if you touch this :)
    private int accountsToCreate = 0;
    //track how many workers have completed the work
    private int workersFinished = 0;
    private int loops = 0;
    private long timeBeforeSetup;
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
        public final int number;
        public final int accounts;
        public final int loops;
        public final long timeBeforeSetup;
        public SetNumberOfWorkersAndAccountsAndLoops(int number, int accounts, int loops, long timeBeforeSetup){
            this.number = number;
            this.accounts = accounts;
            this.loops = loops;
            this.timeBeforeSetup = timeBeforeSetup;
        }
    }



    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<MainActor4.Command> createReceive() {
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




    /**
     * Setup method used from main to set the amount of workers we want to use
     * probably need to implement custom dispatcher again if we want to use more than 8 workers
     * @param setNumberOfWorkersAndAccountsAndLoops
     * @return
     */
    private Behavior<MainActor4.Command> onSetNumberOfWorkersAndAccountsAndLoops(SetNumberOfWorkersAndAccountsAndLoops setNumberOfWorkersAndAccountsAndLoops){
        this.numberOfWorkers = setNumberOfWorkersAndAccountsAndLoops.number;
        this.accountsToCreate = setNumberOfWorkersAndAccountsAndLoops.accounts;
        this.loops = setNumberOfWorkersAndAccountsAndLoops.loops;
        this.timeBeforeSetup = setNumberOfWorkersAndAccountsAndLoops.timeBeforeSetup;
        return this;
    }

    /**
     * is called from worker4.onStart once it finishes, keeps track of how many workers have returned
     * and asks for final print of all accounts money when every worker has returned
     * @return
     */
    private Behavior<Command> onWorkFinished() {
        workersFinished++;
        if(workersFinished==numberOfWorkers){
            timeDone = System.nanoTime();
            System.out.println("All workers have finished!");
            System.out.println("Setup time: " + (timeAfterSetup-timeBeforeSetup)/1.0E9);
            System.out.println("Execution time: " + (timeDone-timeAfterSetup)/1.0E9);
            System.out.println("Total time: " + (timeDone-timeBeforeSetup)/1.0E9);
            for(int i = 0; i < accountsToCreate; i++) {
                System.out.println("Message to get money sent");
                bank.tell(new Bank.GetMoneyFromAccount(i, getContext().getSelf()));
            }
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
            workers.add(getContext().spawn(Worker4.create(loops, accountsToCreate, getContext().getSelf(), bank), "Worker"+i, DispatcherSelector.fromConfig("fourth-dispatcher")));
        }

        timeAfterSetup = System.nanoTime();
        //tell the workers to start working, send reference to ourselves so it can message us when work is done
        for (int i = 0; i< numberOfWorkers; i++){
            workers.get(i).tell(Worker4.Start.INSTANCE);
        }
        return this;
    }

}
