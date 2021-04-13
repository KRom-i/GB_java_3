package HomeWork_1_Java_3.Box;


import java.util.ArrayList;

// 3.b. Класс Box в который можно складывать фрукты,
// коробки условно сортируются по типу фрукта.

// 3.c. Для хранения фруктов внутри коробки используется ArrayList;

// 3.g. Метод добавления фрукта в коробку(используем super.add(T e)).

public class BoxFruit <T extends Fruit> extends ArrayList<T>  {

    public BoxFruit() {}

    public float getWeight(){

//       3.d. Метод getWeight() который высчитывает вес коробки,
//       зная количество фруктов и вес одног.

        float sum = 0;

        for (int i = 0; i < super.size(); i++) {
            sum += super.get(i).getWeightFruit();
        }

       return  sum;
    }

    public boolean compare(BoxFruit boxFruit){

//              3.e. Метод compare, позволяет сравнить текущую коробку с той,
//              которую подадут в compare в качестве параметра,
//              true - если их веса равны,
//              false в противном случае.

        return this.getWeight() == boxFruit.getWeight();
    }

    public void moveFruitToBox(BoxFruit<T> boxFruit){

//        3.f. Метод, который позволяет пересыпать фрукты из текущей коробки
//        в другую коробку, соответственно в текущей коробке фруктов не остается,
//        а в другую перекидываются объекты, которые были в этой коробке;

        for (int i = 0; i < super.size(); i++) {
            boxFruit.add(super.get(i));
        }

        super.clear();
    }



}
