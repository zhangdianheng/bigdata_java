package com.zdh.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author zdh
 * @date 2022/12/15 15:54
 * @Version 1.0
 */
public class DemoAnnotation {
    @MyAnno(name = "zdh", age = 18, schools = {"清华"})
    public void demo() {
        System.out.println(18);
    }

    //    @MyAnno01("zdh")
    public void demo01() {
        System.out.println("zdh");
    }

    public static void main(String[] args) {
        Method[] declaredMethods = DemoAnnotation.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(MyAnno.class)) {
                MyAnno annotation1 = method.getAnnotation(MyAnno.class);
                System.out.println(annotation1.name());
                System.out.println(annotation1.age());
                Arrays.stream(annotation1.schools()).iterator().forEachRemaining(a -> System.out.println(a));
            }
        }
    }
}


@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnno {

    String name();

    int age();

    int id() default -1;

    String[] schools();

}
//@Retention(RetentionPolicy.RUNTIME)
//@interface MyAnno01 {
//    String value();
//}
