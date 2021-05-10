package Lesson_07.HomeWork_07.MyTest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyTestStart {

    private static List<Method> beforeSuiteMethod;
    private static List<Method> testMethods;
    private static List<Method> afterSuiteMethod;

    private MyTestStart(){}

    public static void start(Class cls){

        Object object = initObject(cls);

        if ((beforeSuiteMethod = findMethod(cls, BeforeSuite.class)).size() > 1){
            throw new RuntimeException(String.format("[%s] object [%s] method is not uniquely instantiated",
                    cls.getName(), BeforeSuite.class.getName()));
        } else if (!beforeSuiteMethod.isEmpty()){
            executeListMethods(beforeSuiteMethod, object);
        }

        if ((testMethods = findMethod(cls, Test.class)).isEmpty()){
            System.out.printf("Object [%s] has no test methods.", cls.getName());
            return;
        } else {
            Collections.sort(testMethods, new Comparator<Method>() {
                @Override
                public int compare (Method o1, Method o2){
                    return o1.getAnnotation(Test.class).order() - o2.getAnnotation(Test.class).order();
                }
            });
            executeListMethods(testMethods, object);
        }

        if ((afterSuiteMethod = findMethod(cls, AfterSuite.class)).size() > 1){
          throw new RuntimeException(String.format("[%s] object [%s] method is not uniquely instantiated",
                  cls.getName(), AfterSuite.class.getName()));
        } else if (!afterSuiteMethod.isEmpty()){
            executeListMethods(afterSuiteMethod, object);
        }


    }

    private static void executeListMethods(List<Method> methodList, Object object, Object... args){
        for(Method testM: methodList) {
            executeMethod(testM, object, args);
        }
    }

    private static void executeMethod (Method method, Object object, Object... args){
        try {
            method.setAccessible(true);
            method.invoke(object, args);
            method.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Method> findMethod (Class cls,
                                                Class<? extends Annotation> annotation){
        List<Method> methodList = new ArrayList<>();
        for(Method m: cls.getDeclaredMethods()
            ) {
            if (m.isAnnotationPresent(annotation)){
                methodList.add(m);
            }
        }
        return methodList;
    }

    public static void start(String className){
        try {
            start(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Object initObject(Class cls){
        try {
            return  cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
