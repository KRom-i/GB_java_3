package Lesson_04;

@FunctionalInterface
public interface CallbackDouble {
    String callDouble(String a, String b);

    default void testCD(){
        System.out.println("Default method of CallbackDouble");
    }
}
