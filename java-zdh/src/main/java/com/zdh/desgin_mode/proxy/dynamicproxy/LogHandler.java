package com.zdh.desgin_mode.proxy.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author zdh
 * @date 2022-07-05 13:34
 * @Version 1.0
 */
public class LogHandler implements InvocationHandler{
    private UserService userService;

    public LogHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        Object invoke = method.invoke(userService, args);
        after();
        return invoke;
    }
    public void before(){
        System.out.println(String.format("log start time [%s]",new Date()));

    }
    public void after(){
        System.out.println(String.format("log end time [%s]",new Date()));
    }
}
