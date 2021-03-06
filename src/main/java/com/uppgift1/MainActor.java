package com.uppgift1;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;
import java.util.List;

public class MainActor extends AbstractBehavior<MainActor.Command> {

    //Actor variables
    private long numberToSearch = 0;
    private int numberOfWorkers = 0;
    private long primesFound = 0;
    private int workersReturned = 0;
    private long timeBeforeSetup;
    private long timeAfterSetup;
    private long timeDone;


    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    public static class SetNumbersToSearchAndNumberOfWorkers implements Command {
        public final long numbersToSearch;
        public final int numberOfWorkers;
        public final long timeBeforeSetup;

        public SetNumbersToSearchAndNumberOfWorkers(long numbersToSearch, int numberOfWorkers, long timeBeforeSetup){
            this.numbersToSearch = numbersToSearch;
            this.numberOfWorkers = numberOfWorkers;
            this.timeBeforeSetup = timeBeforeSetup;
        }
    }

    public static class SendPrimesFound implements Command {
        public final long primes;

        public SendPrimesFound(long primes){
            this.primes = primes;
        }
    }

    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessage(SendPrimesFound.class, this::onSendPrimesFound) //Call onSendPrimesFound when SendPrimesFound class is received
                .onMessage(SetNumbersToSearchAndNumberOfWorkers.class, this::onSetNumbersToSearchAndNumberOfWorkers) //Call onSetNumbersToSearchAndNumberOfWorkers when SetNumbersToSearchAndNumberOfWorkers is received
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private MainActor(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<MainActor.Command> create() {
        return Behaviors.setup(MainActor::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////

    private Behavior<Command> onSetNumbersToSearchAndNumberOfWorkers(SetNumbersToSearchAndNumberOfWorkers command){
        numberToSearch = command.numbersToSearch;
        numberOfWorkers = command.numberOfWorkers;
        timeBeforeSetup = command.timeBeforeSetup;
        return this;
    }

    private Behavior<Command> onSendPrimesFound(SendPrimesFound command){
        primesFound += command.primes;
        workersReturned++;
        if(workersReturned==numberOfWorkers) {
            timeDone = System.nanoTime();
            System.out.println("Work completed!");
            System.out.println("Number of primes found: " + primesFound);
            System.out.println("Setup time: " + (timeAfterSetup-timeBeforeSetup)/1.0E9);
            System.out.println("Execution time: " + (timeDone-timeAfterSetup)/1.0E9);
            System.out.println("Total time: " + (timeDone-timeBeforeSetup)/1.0E9);
        }
        return this;
    }

    private Behavior<Command> onStart() {
        //Create a list to store the workers in
        List<ActorRef<Worker.Command>> workerList = new ArrayList<>();
        //Spawn workers and store them in the list
        for(int i = 0; i < numberOfWorkers; i++){
            workerList.add(getContext().spawn(Worker.create(), "Worker"+i, DispatcherSelector.fromConfig("first-dispatcher")));
        }
        timeAfterSetup = System.nanoTime();
        int startNumber = 0;
        for(ActorRef<Worker.Command> worker : workerList){
            worker.tell(new Worker.DoWork(numberToSearch, numberOfWorkers, startNumber, getContext().getSelf()));
            startNumber++;
        }

        return this;
    }
}
