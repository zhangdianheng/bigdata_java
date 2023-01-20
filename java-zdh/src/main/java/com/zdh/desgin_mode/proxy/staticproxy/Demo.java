package com.zdh.desgin_mode.proxy.staticproxy;

/**
 * @author zdh
 * @date 2022-06-30 16:47
 * @Version 1.0
 */
public class Demo {
    public static void main(String[] args) {
        Cat cat = new Cat();
        CatTimeProxy catTimeProxy = new CatTimeProxy(cat);
        CatLogProxy catLogProxy = new CatLogProxy(catTimeProxy);
        catLogProxy.walk();
    }
}
