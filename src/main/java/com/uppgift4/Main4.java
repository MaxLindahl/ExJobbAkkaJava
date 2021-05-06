package com.uppgift4;

import akka.actor.typed.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class Main4 {
    public static void main(String[] args) {
        long timeBeforeSetup = System.nanoTime();
        int workers = 1024;
        int accounts = 2;
        int loops = 1000;

        ConfigFactory.load("src/main/resources/application.conf");
        //#actor-system
        ActorSystem<MainActor4.Command> mainActor = ActorSystem.create(MainActor4.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        mainActor.tell(new MainActor4.SetNumberOfWorkersAndAccountsAndLoops(workers, accounts, loops, timeBeforeSetup));
        //tell it to start
        mainActor.tell(MainActor4.Start.INSTANCE);
        //#main-send-messages
    }
}
