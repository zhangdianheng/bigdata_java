package com.zdh.desgin_mode.proxy.dynamicproxy;

/**
 * @author zdh
 * @date 2022-06-30 16:56
 * @Version 1.0
 */
public class UserServiceImpl implements UserService{
    @Override
    public void select() {
        System.out.println("查询 selectById");
    }

    @Override
    public void update() {
        System.out.println("更新 update");

    }
}
