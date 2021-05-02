package Lesson_5_Threads.Home_Work.RaceGB;

public class MainClass {

    public static final int CARS_COUNT = 4;

    public static void main(String[] args) {

        Race race = new Race(new Road(60), new Tunnel(CARS_COUNT), new Road(40));

        race.start(new Car[CARS_COUNT]);

    }
}





