package com.uppgift2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.uppgift1.MainActor;


public class Producer extends AbstractBehavior<Producer.Command> {

    //Actor variables



    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    //send this producer a task to produce
    public static class Produce implements Command{
        public final Object task;
        public final akka.actor.typed.ActorRef<MainActor2.Command> mainActor;

        public Produce(Object task, ActorRef mainActor){
            this.task = task;
            this.mainActor = mainActor;
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
        //produce an object
        //How/what do we produce?
        Object o = new Object(); //temp produced object

        //send object back
        produceObject.mainActor.tell(new MainActor2.ReceiveProduceObject(o));
        return this;
    }

    private Behavior<Command> onStart() {
        //do things
        return this;
    }
}