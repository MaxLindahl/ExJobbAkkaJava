package com.uppgift3;

class Multitasker implements Runnable{

    private int id;

    public Multitasker(int id){
        this.id = id;
    }

    @Override
    public void run() {
        doWork();
    }

    private void doWork(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread "+ id +" done");
    }
}