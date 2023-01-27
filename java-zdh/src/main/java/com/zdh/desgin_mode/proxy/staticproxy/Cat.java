package com.zdh.desgin_mode.proxy.staticproxy;


import java.util.Random;

public class Cat implements Walkable {

            @Override
    public void walk() {
                long start = System.currentTimeMillis();
                System.out.println("cat is walking...");
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long end = System.currentTimeMillis();
                System.out.println("walk time = " + (end - start));
            }
        }