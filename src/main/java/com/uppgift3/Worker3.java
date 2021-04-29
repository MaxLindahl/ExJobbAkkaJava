package com.uppgift3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Worker3 extends AbstractBehavior<Worker3.Command> {

    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////


    public static class DoWork implements Command {
        public final akka.actor.typed.ActorRef<com.uppgift3.MainActor3.Command> mainActor;

        public DoWork(ActorRef mainActor){
            this.mainActor = mainActor;
        }
    }

    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(DoWork.class, this::onDoWork)  //Call onDoWork when DoWork class is received
                .build();

    }

    /**
     * Constructor
     * @param context
     */
    private Worker3(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Command> create() {
        return Behaviors.setup(Worker3::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////

    private Behavior<Command> onDoWork(DoWork command){
        System.out.println("zug zug");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //long time1 = System.currentTimeMillis();
        //while ((time1+5000)>System.currentTimeMillis()){
//
        //}
        command.mainActor.tell(new com.uppgift3.MainActor3.WorkerReturn());
        return this;
    }
}
