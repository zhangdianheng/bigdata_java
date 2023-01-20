package com.zdh.desgin_mode.observer;

/**
 * @author zdh
 * @date 2022-07-05 17:39
 * @Version 1.0
 */
public class ObserverTest {
    public static void main(String[] args) {
        testObserverPattern();
    }

    public static void testObserverPattern(){
// 新建一个足球比赛的主题
        Subject subject = new CommentaryObject("Soccer Match [2014AUG24]");

// 新建一个名为“Adam Warner”的观察者
        Observer observer = new SMSUser(subject, "Adam Warner [New York]");
// 订阅主题
        observer.subscribe();
        System.out.println();

// 新建一个名为“Tim Ronney”的观察者
        Observer observer2 = new SMSUser(subject, "Tim Ronney [London]");
// 订阅主题
        observer2.subscribe();

// 此时足球赛事解说开始评论，该评论会实时更新到以上名为为“Tim Ronney”和“Adam Warner”的观察者
        Commentary cObject = ((Commentary)subject);
        cObject.setCommentary("Welcome to live Soccer match");
        cObject.setCommentary("Current score 0-0");
        System.out.println();

// 名为“Tim Ronney”的观察者取消订阅，那么接下来相关主题信息不会推送给他
        observer2.unSubcribe();
        System.out.println();

// 此时足球赛事解说又开始评论，该评论此时只会实时更新到名为“Tim Ronney”的观察者
        cObject.setCommentary("It’s a goal!!");
        cObject.setCommentary("Current score 1-0");
        System.out.println();

// 此时又加入了一个名为Marrie的观察者
        Observer observer3 = new SMSUser(subject, "Marrie [Paris]");
        observer3.subscribe();
        System.out.println();
// 此时足球赛事解说又开始评论，该评论此时会推送给名为“Adam Warner”和“Marrie”的观察者
        cObject.setCommentary("It’s another goal!!");
        cObject.setCommentary("Half-time score 2-0");
    }
}
