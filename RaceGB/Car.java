package Lesson_5_Threads.Home_Work.RaceGB;


public class Car implements Runnable {

    private static int CARS_COUNT;

    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;
    private boolean kill;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isKill() {
        return kill;
    }

    public void setKill(boolean kill) {
        this.kill = kill;
    }

    public Car(Race race) {
        this.race = race;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.kill = false;
    }

    public void preparation(){

        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            this.speed = 20 + (int) (Math.random() * 10);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(this.name + " готов");
            race.getPreparation().countDown();
        }

    }

    @Override
    public void run() {

        for (int i = 0; i < race.getStages().size(); i++) {
            if (!this.isKill()){
                race.getStages().get(i).go(this);
            }
        }

        try {
            race.getWinSmp().acquire();
            if (!race.isWinRace() && !this.isKill()){
                race.setWinRace(true);
                System.out.println(this.name + " WIN");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            race.getWinSmp().release();
        }

        race.getRaceEnd().countDown();
    }
}
