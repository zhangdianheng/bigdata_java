package com.zdh.desgin_mode.handler;

/**
 * @author zdh
 * @date 2022-07-05 15:28
 * @Version 1.0
 */
public class ThirdPassHandler extends AbstractHandler {
    private int play() {
        return 95;
    }

    @Override
    public int handler() {
        System.out.println("第三关-->ThirdPassHandler");
        int score = play();
        if (score >= 95) {
            //分数>=95 并且存在下一关才进入下一关
            if (this.next != null) {
                return this.next.handler();
            }
        }
        return score;
    }
}
