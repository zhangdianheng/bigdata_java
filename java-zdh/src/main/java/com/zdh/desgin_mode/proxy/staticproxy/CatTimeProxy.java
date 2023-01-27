package com.zdh.desgin_mode.proxy.staticproxy;

import com.zdh.desgin_mode.proxy.staticproxy.Walkable;

public class CatTimeProxy implements Walkable {
            private Walkable walkable;

            public CatTimeProxy(Walkable walkable) {
                this.walkable = walkable;
            }

            @Override
    public void walk() {
                long start = System.currentTimeMillis();

                walkable.walk();

                long end = System.currentTimeMillis();
                System.out.println("Walk time = " + (end - start));
            }
        }