package com.uppgift1;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.GreeterMain;

public class MainActor extends AbstractBehavior<MainActor.Start> {
    public MainActor(ActorContext<Start> context) {
        super(context);
    }

    public static Behavior<MainActor.Start> create() {
        return Behaviors.setup(MainActor::new);
    }

    @Override
    public Receive<Start> createReceive() {
        return newReceiveBuilder().onMessage(MainActor.Start.class, this::onStart).build();
    }

    public static class Start {
        public Start() {
            //create workers
        }
    }

    private Behavior<MainActor.Start> onStart(MainActor.Start command) {
        //do work with the actors
        return this;
    }
}
