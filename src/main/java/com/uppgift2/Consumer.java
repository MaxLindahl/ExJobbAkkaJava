package com.uppgift2;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Consumer extends AbstractBehavior<Consumer.Command> {

    //Actor variables



    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    public static class Consume implements Consumer.Command {
        public final Object task;

        public Consume(Object task){
            this.task = task;
        }

    }

    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessage(Consume.class, this::onConsume)
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private Consumer(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Consumer.Command> create() {
        return Behaviors.setup(Consumer::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////

    private Behavior<Command> onConsume(Consume consumeTask) {
        //Consume the object?
        return this;
    }

    private Behavior<Command> onStart() {
        //do things
        return this;
    }
}