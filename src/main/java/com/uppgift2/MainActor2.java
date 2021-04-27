package com.uppgift2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;
import java.util.List;


public class MainActor2 extends AbstractBehavior<MainActor2.Command> {

    //Actor variables
    private long numberOfTasks = 0;
    private int numberOfWorkers = 0;
    private ArrayList<ActorRef> Producers;
    private ArrayList<ActorRef> Consumers;



    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    public static class SetNumberOfTasksAndNumberOfWorkers implements Command {
        public final long numberOfTasks;
        public final int numberOfWorkers;

        public SetNumberOfTasksAndNumberOfWorkers(long numberOfTasks, int numberOfWorkers){
            this.numberOfTasks = numberOfTasks;
            this.numberOfWorkers = numberOfWorkers;
        }
    }

    public static class ReceiveProduceObject implements Command {
        public final Object object;

        public ReceiveProduceObject(Object object){
            this.object = object;
        }
    }


    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessage(SetNumberOfTasksAndNumberOfWorkers.class, this::onSetNumberOfTasksAndNumberOfWorkers)
                .onMessage(ReceiveProduceObject.class, this::onReceiveProduceObject)
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private MainActor2(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<MainActor2.Command> create() {
        return Behaviors.setup(MainActor2::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////

    private Behavior<Command> onReceiveProduceObject(ReceiveProduceObject producedObject) {
        //save the produced object?
        //hand it over to a consumer?
        return this;
    }

    private Behavior<Command> onSetNumberOfTasksAndNumberOfWorkers(SetNumberOfTasksAndNumberOfWorkers command) {
        numberOfTasks = command.numberOfTasks;
        numberOfWorkers = command.numberOfWorkers;
        return this;
    }

    private Behavior<Command> onStart() {
        //do things
        //spawn consumers/producers
        //start producing
        return this;
    }
}