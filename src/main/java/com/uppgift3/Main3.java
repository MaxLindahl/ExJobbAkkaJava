package com.uppgift3;


import akka.actor.typed.ActorSystem;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
public class Main3 {
    public static void main(String[] args) {
        ConfigFactory.load("src/main/resources/application.conf");

        //#actor-system
        ActorSystem<com.uppgift3.MainActor3.Command> mainActor = ActorSystem.create(com.uppgift3.MainActor3.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        //set how many numbers we will search and how many workers to spawn
        mainActor.tell(new com.uppgift3.MainActor3.SetNumberOfWorkers(3000));
        //tell it to start
        mainActor.tell(com.uppgift3.MainActor3.Start.INSTANCE);
        //#main-send-messages
        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            mainActor.terminate();
        }
    }
}