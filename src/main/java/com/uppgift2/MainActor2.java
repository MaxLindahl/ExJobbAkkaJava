package com.uppgift2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;


public class MainActor2 extends AbstractBehavior<MainActor2.Command> {

    //Actor variables
    private int numberOfTasks = 0;
    private long tasksFinished = 0;
    private int noProducers = 0;
    private int noConsumers = 0;
    private int producerCounter = 0;
    private ArrayList<ActorRef<Producer.Command>> producers = new ArrayList<>();
    private ArrayList<ActorRef<Consumer.Command>> consumers = new ArrayList<>();
    private ActorRef<MessageHandler.Command> messageHandler;
    private long timeBeforeSetup;
    private long timeAfterSetup;
    private long timeDone;



    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////

    public enum Start implements Command {
        INSTANCE
    }

    public enum TaskConsumed implements  Command {
        INSTANCE
    }

    public static class SetNumberOfTasksAndNumberOfWorkers implements Command {
        public final int numberOfTasks;
        public final int noProducers;
        public final int noConsumers;
       public final long timeBeforeSetup;

        public SetNumberOfTasksAndNumberOfWorkers(int numberOfTasks, int noProducers, int noConsumers, long timeBeforeSetup){
            this.numberOfTasks = numberOfTasks;
            this.noProducers = noProducers;
            this.noConsumers = noConsumers;
            this.timeBeforeSetup = timeBeforeSetup;
        }
    }




    /////////////////////////////////////////// Handle the received message //////////////////////////////////////////////////////////

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Start.INSTANCE, this::onStart)  //Call onStart when Start.INSTANCE is received
                .onMessageEquals(TaskConsumed.INSTANCE, this::onConsumed)
                .onMessage(SetNumberOfTasksAndNumberOfWorkers.class, this::onSetNumberOfTasksAndNumberOfWorkers)
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

    private Behavior<Command> onConsumed() {
        tasksFinished++;
        if(tasksFinished==numberOfTasks){
            timeDone = System.nanoTime();
            System.out.println("All tasks finished!");
            System.out.println("Setup time: " + (timeAfterSetup-timeBeforeSetup)/1.0E9);
            System.out.println("Execution time: " + (timeDone-timeAfterSetup)/1.0E9);
            System.out.println("Total time: " + (timeDone-timeBeforeSetup)/1.0E9);
        }
        return this;
    }

    private Behavior<Command> onSetNumberOfTasksAndNumberOfWorkers(SetNumberOfTasksAndNumberOfWorkers command) {
        numberOfTasks = command.numberOfTasks;
        noProducers = command.noProducers;
        noConsumers = command.noConsumers;
        timeBeforeSetup = command.timeBeforeSetup;
        return this;
    }

    private Behavior<Command> onStart() {
        //spawn consumers/producers
        for(int i = 0; i < noProducers; i++){
            producers.add(getContext().spawn(Producer.create(), "Producer"+i, DispatcherSelector.fromConfig("second-dispatcher")));
        }
        for(int i = 0; i < noConsumers; i++){
            consumers.add(getContext().spawn(Consumer.create(), "Consumer"+i, DispatcherSelector.fromConfig("second-dispatcher")));
        }
        messageHandler = getContext().spawn(MessageHandler.create(), "MessageHandler");
        messageHandler.tell(new MessageHandler.SetupThings(consumers, noProducers, noConsumers, getContext().getSelf()));
        timeAfterSetup = System.nanoTime();
        for(int i = 0; i<numberOfTasks; i++){
            tellProducerToProduce();
        }
        //start producing
        return this;
    }


    private void tellProducerToProduce(){
        producers.get(producerCounter).tell(new Producer.Produce(messageHandler));
        if(producerCounter==(noProducers-1))
            producerCounter = 0;
        else
            producerCounter++;
    }
}