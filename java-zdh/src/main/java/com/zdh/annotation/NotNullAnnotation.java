package com.zdh.annotation;

/**
 * @author zdh
 * @date 2023/1/19 13:15
 * @Version 1.0
 */
public class NotNullAnnotation {

    public static void main(String[] args) {
        Apple apple = new Apple("SSS",null);
        boolean valid = NotNullCheck.isValid(apple);
        System.out.println(valid);

    }
}

class Apple{
    @NotNull
    private String name;
    @NotNull
    private String color;

    public Apple(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
