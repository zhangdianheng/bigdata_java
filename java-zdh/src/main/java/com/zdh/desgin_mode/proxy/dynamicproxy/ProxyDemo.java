package com.zdh.desgin_mode.proxy.dynamicproxy;

import java.lang.reflect.Proxy;

/**
 * @author zdh
 * @date 2022-06-30 17:01
 * @Version 1.0
 */
public class ProxyDemo {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        ClassLoader classLoader = userService.getClass().getClassLoader();
        Class<?>[] interfaces = userService.getClass().getInterfaces();
        LogHandler logHandler = new LogHandler(userService);
        UserService proxy = (UserService) Proxy.newProxyInstance(classLoader, interfaces, logHandler);
        proxy.select();
        proxy.update();
        ProxyUtils.generateClassFile(userService.getClass(),"UserServiceJDKProxy");

    }
}
