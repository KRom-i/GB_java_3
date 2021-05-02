package Lesson_5_Threads.Home_Work.MyRace;

import java.util.concurrent.Semaphore;

public class Tunnel extends Stage {

    private Semaphore semaphore;

    public Tunnel(int cars) {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
        this.semaphore = new Semaphore((int) cars / 2);
    }

    @Override
    public void go(Car c) {

        try {

            System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
            semaphore.acquire();
            System.out.println(c.getName() + " начал этап: " + description);
            Thread.sleep(length / c.getSpeed() * 1000);
            System.out.println(c.getName() + " закончил этап: " + description);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(c.getName() + " выбыл из готки на этапе: " + description);
            c.setKill(true);
        }finally {
            semaphore.release();
        }

    }
}