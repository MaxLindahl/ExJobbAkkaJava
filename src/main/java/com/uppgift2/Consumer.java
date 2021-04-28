package com.uppgift2;

import akka.actor.typed.ActorRef;
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
        public final Task task;
        public final akka.actor.typed.ActorRef<MainActor2.Command> mainActor;

        public Consume(Task task, ActorRef mainActor){
            this.task = task;
            this.mainActor = mainActor;
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
        consumeTask.task.consume();
        System.out.println("Task consumed!");
        consumeTask.mainActor.tell(MainActor2.TaskConsumed.INSTANCE);
        return this;
    }

    private Behavior<Command> onStart() {
        //do things
        return this;
    }
}