package HomeWork_1_Java_3;

import java.util.ArrayList;

public class DemoHomeWork {

    public static void main(String ... args) {

        methodToSwap(new String[]{"Str1","Str2", "Str3", "Str4"});

        ArrayList<?> arrayList = new ArrayList<>(
                methodToArrayList(new String[]{"testArr1","testArr2", "testArr3", "testArr4"}));

    }

    private static void methodToSwap(Object[] objects){

//        1. Метод меняет два элемента массива (любого ссылочного типа) местами.

        if (objects.length > 1){

            Object val = objects[0];
            objects[0] = objects[objects.length -1];
            objects[objects.length -1] = val;

        } else {
            System.out.println("Длинна массива недопустима");
        }

    }


    private static <T> ArrayList methodToArrayList(T[] objects){
//        2. Метод преобразует массив в ArrayList

        ArrayList<Object> arrayList = new ArrayList<>();
        for (Object o: objects
        ) {
            arrayList.add(o);
        }

        return arrayList;
    }
}
