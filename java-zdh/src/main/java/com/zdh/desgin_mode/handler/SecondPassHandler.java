package com.zdh.desgin_mode.handler;

/**
 * @author zdh
 * @date 2022-07-05 15:28
 * @Version 1.0
 */
public class SecondPassHandler extends AbstractHandler {
    private int play() {
        return 90;
    }

    @Override
    public int handler() {
        System.out.println("第二关-->SecondPassHandler");

        int score = play();
        if (score >= 90) {
            //分数>=90 并且存在下一关才进入下一关
            if (this.next != null) {
                return this.next.handler();
            }
        }

        return score;
    }
}
