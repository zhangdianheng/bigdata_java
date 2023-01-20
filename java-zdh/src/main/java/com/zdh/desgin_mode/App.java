package com.zdh.desgin_mode;

import com.zdh.desgin_mode.builder.BuilderMode;

/**
 * @author zdh
 * @date 2022-06-30 14:43
 * @Version 1.0
 */
public class App {
    public static void main(String[] args) {
        BuilderMode builderMode = new BuilderMode.Builder("3","2")
                .setDisplay("s")
                .setKeyboard("ssc")
//                .setUsbCount(1)
                .build();
        System.out.println(builderMode);
    }
}
