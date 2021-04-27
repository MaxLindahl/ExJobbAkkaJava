package com.uppgift3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;
import java.util.List;

public class MainActor3 extends AbstractBehavior<MainActor3.Command> {

    //Actor variables

    private int numberOfWorkers = 0;
    private int workersReturned = 0;



    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    public static class SetNumberOfWorkers implements Command {

        public final int numberOfWorkers;

        public SetNumberOfWorkers(int numberOfWorkers){
            this.numberOfWorkers = numberOfWorkers;
        }
    }

    public static class WorkerReturn implements Command {

    }

    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessage(WorkerReturn.class, this::onWorkerReturn) //Call onSendPrimesFound when SendPrimesFound class is received
                .onMessage(SetNumberOfWorkers.class, this::onSetNumberOfWorkers) //Call onSetNumbersToSearchAndNumberOfWorkers when SetNumbersToSearchAndNumberOfWorkers is received
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private MainActor3(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<MainActor3.Command> create() {
        return Behaviors.setup(MainActor3::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////

    private Behavior<Command> onSetNumberOfWorkers(SetNumberOfWorkers command){
        numberOfWorkers = command.numberOfWorkers;
        return this;
    }

    private Behavior<Command> onWorkerReturn(WorkerReturn command){
        System.out.println("Return message received");
        workersReturned++;
        System.out.println("Workers currently returned: " + workersReturned + " || Workers still working: " + (numberOfWorkers-workersReturned));
        return this;
    }

    private Behavior<Command> onStart() {
        System.out.println("Start command received");
        //Create a list to store the workers in
        List<ActorRef<Worker3.Command>> workerList = new ArrayList<>();
        //Spawn workers and store them in the list
        for(int i = 0; i < numberOfWorkers; i++){
            workerList.add(getContext().spawn(Worker3.create(), "Worker"+i));
        }
        System.out.println(numberOfWorkers + " Workers created");
        for(ActorRef<Worker3.Command> worker : workerList){
            worker.tell(new Worker3.DoWork(getContext().getSelf()));
        }


        return this;
    }
}
