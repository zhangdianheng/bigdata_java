package com.zdh.desgin_mode.factory;

/**
 * @author zdh
 * @date 2022-07-05 17:23
 * @Version 1.0
 */
public class MacOSButton implements Button {
    @Override
    public void paint() {
        System.out.println("You have created MacOSButton.");
    }
}
