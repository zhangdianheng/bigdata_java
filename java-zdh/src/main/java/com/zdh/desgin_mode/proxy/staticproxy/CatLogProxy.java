package com.zdh.desgin_mode.proxy.staticproxy;

import com.zdh.desgin_mode.proxy.staticproxy.Walkable;

public class CatLogProxy implements Walkable {
            private Walkable walkable;

            public CatLogProxy(Walkable walkable) {
                this.walkable = walkable;
            }

            @Override
    public void walk() {
                System.out.println("Cat walk start...");

                walkable.walk();

                System.out.println("Cat walk end...");

            }
        }
