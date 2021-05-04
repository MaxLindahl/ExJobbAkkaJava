package com.uppgift2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;


public class MessageHandler extends AbstractBehavior<MessageHandler.Command> {

    //Actor variables
    private int numberOfWorkers;
    private ArrayList<ActorRef<Consumer.Command>> consumers = new ArrayList<>();
    private int consumerCounter = 0;
    private ActorRef mainActor;


    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    public static class SetupThings implements MessageHandler.Command {
        public ArrayList<ActorRef<Consumer.Command>> consumers;
        public ActorRef mainActor;
        public int numberOfWorkers;

        SetupThings(ArrayList<ActorRef<Consumer.Command>> consumers, int numberOfWorkers, ActorRef mainActor){
            this.consumers = consumers;
            this.numberOfWorkers = numberOfWorkers;
            this.mainActor = mainActor;
        }
    }

    public static class ReceiveProduceObject implements MessageHandler.Command {
        public final Task task;

        public ReceiveProduceObject(Task task){
            this.task = task;
        }
    }

    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessage(ReceiveProduceObject.class, this::onReceiveProduceObject)
                .onMessage(SetupThings.class, this::onSetupThings)
                .build();

    }


    /**
     * Constructor
     * @param context
     */
    private MessageHandler(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<MessageHandler.Command> create() {
        return Behaviors.setup(MessageHandler::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////

    private Behavior<Command> onSetupThings(SetupThings setups) {
        this.consumers = setups.consumers;
        this.numberOfWorkers = setups.numberOfWorkers;
        this.mainActor = setups.mainActor;
        return this;
    }

    private Behavior<Command> onReceiveProduceObject(ReceiveProduceObject task) {
        sendTaskToConsumer(task.task);
        return this;
    }

    private Behavior<Command> onStart() {
        //do things
        return this;
    }

    /////////////////////////////////////////////////////////////////////////

    //prob only works properly with even number of workers
    private void sendTaskToConsumer(Task task){
        consumers.get(consumerCounter).tell(new Consumer.Consume(task,mainActor));
        if(++consumerCounter == consumers.size())
            consumerCounter = 0;

    }
}