package com.uppgift3;

public class MainSMM {

    public static void main(String[] args) {
        int noThreads = 500;
        Thread[] threads = new Thread[noThreads];

        for(int i=0; i<noThreads;i++) {
            threads[i] = new Thread(new Multitasker(i));
            System.out.println("Thread "+ i + " created");
        }
        for(int i=0; i<noThreads;i++) {
            threads[i].start();
        }
        for(int i=0; i<noThreads;i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("finished");
    }
}
