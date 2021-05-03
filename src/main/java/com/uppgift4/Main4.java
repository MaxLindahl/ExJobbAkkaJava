package com.uppgift4;

import akka.actor.typed.ActorSystem;
import com.typesafe.config.ConfigFactory;
import com.uppgift2.MainActor2;

import java.io.IOException;

public class Main4 {
    public static void main(String[] args) {
        long timeBeforeSetup = System.nanoTime();
        ConfigFactory.load("src/main/resources/application.conf");
        //#actor-system
        ActorSystem<MainActor4.Command> mainActor = ActorSystem.create(MainActor4.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        mainActor.tell(new MainActor4.SetNumberOfWorkers(200,timeBeforeSetup));
        //tell it to start
        mainActor.tell(MainActor4.Start.INSTANCE);
        //#main-send-messages

    }
}
