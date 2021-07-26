package Lesson_04;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambda {

    public static void main (String[] args) {

//        SAM-тип || замыкание

//        () -> System.out.println("test");

/*        () -> {
                .................
                .................
                .................            };
 */

        Callback callback1 = new Callback() {
            @Override
            public void call (String value) {
                System.out.println(value);
            }
        };

        Callback callback2 = abc -> System.out.println(abc);
        Callback callback3 = System.out::println;

        callback1.call("Text 1");
        callback2.call("Text 2");
        callback3.call("Text 3");

        Callback callback4 = Lambda::signal;
        callback4.call("TEXT 4");

//        **********************************************************

        CallbackDouble callbackDouble = new CallbackDouble() {
            @Override
            public String callDouble (String a, String b) {
                return String.format("String a=[%s]\nString b=[%s]", a,b);
            }
        };

        CallbackDouble callbackDouble1 = (text1, text2) -> String.format("String a=[%s]\nString b=[%s]", text1,text2);


        System.out.println(callbackDouble.callDouble("text1", "text2"));
        System.out.println(callbackDouble1.callDouble("text1", "text2"));

//        **********************************************************

        Consumer<Integer> consumer = a ->{
            a++;
            System.out.println(a);
        };

        consumer = consumer.andThen(arg ->{
            arg *= 2;
            System.out.println(arg);
        });

        consumer.accept(10);

//        **********************************************************

        Predicate<Integer> predicate = value -> value % 2 == 0;
        predicate = predicate.and(val -> val > 6).or(v -> v == 5);
        System.out.println(predicate.test(5));

        Function<Integer, String> converter = a -> "test".repeat(a);
        System.out.println(converter.apply(3));

        Function<String, Integer> function = arg -> arg.length();
        System.out.println(function.apply("123456789"));

//        **********************************************************

        Supplier<List<Integer>> getList = ArrayList::new;
        System.out.println(getList.get());


    }

    private static void signal(String text){
        System.out.println("!!! " + text + " !!!");
    }
}
