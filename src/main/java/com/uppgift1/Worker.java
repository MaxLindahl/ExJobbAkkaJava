package com.uppgift1;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Worker extends AbstractBehavior<Worker.Command> {

    interface Command{}

    /////////////////////////////////////////// Commands we can receive //////////////////////////////////////////////////////////


    public static class DoWork implements Command {
        public final long numbersToSearch;
        public final long skipLength;
        public final long startNumber;
        public final akka.actor.typed.ActorRef<MainActor.Command> mainActor;

        public DoWork(long numbersToSearch, long skipLength, long startNumber, ActorRef mainActor){
            this.numbersToSearch = numbersToSearch;
            this.skipLength = skipLength;
            this.startNumber = startNumber;
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
    private Worker(ActorContext<Command> context) {
        super(context);
    }


    /**
     * Factory method
     * @return
     */
    public static Behavior<Command> create() {
        return Behaviors.setup(Worker::new);
    }


    /////////////////////////////////////////// Do things after a message has been received //////////////////////////////////////////////////////////

    private Behavior<Command> onDoWork(DoWork command){
        long counter = findPrimes(command.numbersToSearch, command.skipLength, command.startNumber);
        command.mainActor.tell(new MainActor.SendPrimesFound(counter));
        return this;
    }

    private static long findPrimes(long maxNumber, long skipLength, long startNumber) {
        long counter = 0;
        boolean isDividable = false;
        for(long number = startNumber; number<=maxNumber; number+=skipLength) {
            for (long d = 2; d <= Math.sqrt(number); d++) {
                if (number % d == 0)
                    isDividable = true;
            }
            if(isDividable == false)
                counter++;
            isDividable = false;
        }
        return counter;
    }
}
