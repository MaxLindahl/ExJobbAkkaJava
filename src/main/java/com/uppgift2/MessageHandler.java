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
    private int noProducers;
    private int noConsumers;
    private ArrayList<ActorRef<Consumer.Command>> consumers = new ArrayList<>();
    private int consumerCounter = 0;
    private ActorRef mainActor;


    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public static class SetupThings implements MessageHandler.Command {
        public final ArrayList<ActorRef<Consumer.Command>> consumers;
        public final ActorRef mainActor;
        public final int noProducers;
        public final int noConsumers;

        SetupThings(ArrayList<ActorRef<Consumer.Command>> consumers, int noProducers, int noConsumers, ActorRef mainActor){
            this.consumers = consumers;
            this.noProducers = noProducers;
            this.noConsumers = noConsumers;
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
        this.noProducers = setups.noProducers;
        this.noConsumers = setups.noConsumers;
        this.mainActor = setups.mainActor;
        return this;
    }

    private Behavior<Command> onReceiveProduceObject(ReceiveProduceObject task) {
        sendTaskToConsumer(task.task);
        return this;
    }


    /////////////////////////////////////////////////////////////////////////

    //prob only works properly with even number of workers
    private void sendTaskToConsumer(Task task){
        consumers.get(consumerCounter).tell(new Consumer.Consume(task, mainActor));
        if(consumerCounter==(noConsumers-1))
            consumerCounter = 0;
        else
            consumerCounter++;
    }
}