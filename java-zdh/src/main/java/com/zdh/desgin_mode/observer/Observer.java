package com.zdh.desgin_mode.observer;

/**
 * @author zdh
 * @date 2022-07-05 17:36
 * @Version 1.0
 */
public interface Observer {
    void update(String commentary);
    void subscribe();
    void unSubcribe();
}
