package com.uppgift2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class Producer extends AbstractBehavior<Producer.Command> {

    //Actor variables



    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    //send this producer a task to produce
    public static class Produce implements Command{
        public final akka.actor.typed.ActorRef<MessageHandler.Command> messageHandler;

        public Produce(ActorRef messageHandler){
            this.messageHandler = messageHandler;
        }
    }


    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessage(Produce.class, this::onProduce)
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private Producer(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Producer.Command> create() {
        return Behaviors.setup(Producer::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////


    private Behavior<Command> onProduce(Produce produceObject) {
        //produce a task
        Task task = new Task();
        task.setNumber();
        System.out.println("Task produced!");
        //send task back
        produceObject.messageHandler.tell(new MessageHandler.ReceiveProduceObject(task));
        return this;
    }

    private Behavior<Command> onStart() {
        //do things
        return this;
    }
}