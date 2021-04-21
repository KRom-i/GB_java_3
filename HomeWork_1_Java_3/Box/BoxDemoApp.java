package HomeWork_1_Java_3.Box;

import java.util.ArrayList;

public class BoxDemoApp {

    public static void main(String[] args) {

        BoxFruit<Apple> appleBoxFruit = new BoxFruit<>();
        appleBoxFruit.add(new Apple());
        appleBoxFruit.add(new Apple());
        appleBoxFruit.add(new Apple());
        appleBoxFruit.add(new Apple());
        appleBoxFruit.add(new Apple());


        BoxFruit<Orange> orangeBoxFruit = new BoxFruit<>();
        orangeBoxFruit.add(new Orange());
        orangeBoxFruit.add(new Orange());
        orangeBoxFruit.add(new Orange());
        orangeBoxFruit.add(new Orange());
        orangeBoxFruit.add(new Orange());
        orangeBoxFruit.add(new Orange());


        System.out.println(orangeBoxFruit.compare(orangeBoxFruit));

        BoxFruit<Apple> appleBoxFruit1 = new BoxFruit<>();
        appleBoxFruit.moveFruitToBox(appleBoxFruit1);

    }

}
