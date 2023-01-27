package com.zdh.desgin_mode.handler;

/**
 * @author zdh
 * @date 2022-07-05 15:26
 * @Version 1.0
 */
public class FirstPassHandler extends AbstractHandler{
    private int play(){
        return 80;
    }
    @Override
    public int handler() {
        System.out.println("第一关-->FirstPassHandler");
        int score = play();
        if(score >= 80){
            //分数>=80 并且存在下一关才进入下一关
            if(this.next != null){
                return this.next.handler();
            }
        }
        return score;
    }
}
