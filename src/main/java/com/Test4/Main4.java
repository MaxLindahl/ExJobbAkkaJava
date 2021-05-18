package com.Test4;

import akka.actor.typed.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class Main4 {
    public static void main(String[] args) {
        long timeBeforeSetup = System.nanoTime();
        int accounts = 500;
        int loops = 1000;
        ConfigFactory.load("src/main/resources/application.conf");
        //#actor-system
        ActorSystem<Bank.Command> mainActor = ActorSystem.create(Bank.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        mainActor.tell(new Bank.SetNumberOfWorkersAndAccountsAndLoops(accounts, loops, timeBeforeSetup));
        //tell it to start
        mainActor.tell(Bank.Start.INSTANCE);
        //#main-send-messages
    }
}
