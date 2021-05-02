package Lesson_5_Threads.Home_Work.RaceGB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Race {

    private ArrayList<Stage> stages;
    private boolean winRace;
    private Semaphore winSmp;
    private CountDownLatch raceEnd;
    private CountDownLatch preparation;



    public Race(Stage... stages) {
        this.stages = new ArrayList<>(Arrays.asList(stages));
        this.winRace = false;
        this.winSmp = new Semaphore(1);
    }


    public void start(Car[] cars){

        this.preparation = new CountDownLatch(cars.length);
        this.raceEnd = new CountDownLatch(cars.length);

        try {

            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");

            for (int i = 0; i < cars.length; i++) {
                final int I = i;
                cars[i] = new Car(this);
                new Thread(()->cars[I].preparation()).start();
            }

            preparation.await();
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

            for (int i = 0; i < cars.length; i++) {
                final int I = i;
                new Thread(cars[I]).start();
            }

            raceEnd.await();
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");

        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public Semaphore getWinSmp() {
        return winSmp;
    }

    public CountDownLatch getRaceEnd() {
        return raceEnd;
    }

    public CountDownLatch getPreparation() {
        return preparation;
    }

    public ArrayList<Stage> getStages() {
        return stages;
    }

    public boolean isWinRace() {
        return winRace;
    }

    public void setWinRace(boolean winRace) {
        this.winRace = winRace;
    }
}
