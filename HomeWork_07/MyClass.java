package Lesson_07.HomeWork_07;


import Lesson_07.HomeWork_07.MyTest.AfterSuite;
import Lesson_07.HomeWork_07.MyTest.BeforeSuite;
import Lesson_07.HomeWork_07.MyTest.Test;

public class MyClass {

    @BeforeSuite
    void methodBeforeSuite(){
        System.out.println("MyClass.methodBeforeSuite(annotated BeforeSuite)");
    }

    @AfterSuite
    void methodAfterSuite(){
        System.out.println("MyClass.methodAfterSuite(annotated AfterSuite)");
    }


    @Test (order = 1)
    void testMethodOneOrder_1(){
        System.out.println("MyClass.testMethodOne(annotated test order = 1)");
    }

    @Test(order = 2)
    void testMethodOneOrder_2(){
        System.out.println("MyClass.testMethodOne(annotated test order = 2)");
    }

    @Test (order = 3)
    void testMethodOneOrder_3(){
        System.out.println("MyClass.testMethodOne(annotated test order = 3)");
    }

    @Test (order = 4)
    void testMethodOneOrder_4(){
        System.out.println("MyClass.testMethodOne(annotated test order = 4)");
    }

    void testMethodTwo(){
        System.out.println("MyClass.testMethodTwo(without annotation)");
    }
}
