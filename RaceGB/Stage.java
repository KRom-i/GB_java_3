package Lesson_5_Threads.Home_Work.MyRace;

public abstract class Stage {

    protected int length;
    protected String description;

    public String getDescription() {
        return description;
    }

    public abstract void go(Car c);
}