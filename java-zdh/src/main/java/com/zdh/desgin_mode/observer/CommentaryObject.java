package com.zdh.desgin_mode.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zdh
 * @date 2022-07-05 17:37
 * @Version 1.0
 */
public class CommentaryObject implements Subject,Commentary {
    private List<Observer> observerList = new ArrayList<Observer>();
    private String commentary;

    public CommentaryObject(String commentary) {
        this.commentary = commentary;
    }
    public void subscribeObserver(Observer observer) {
        observerList.add(observer);
    }

    public void unSubscribeObserver(Observer observer) {
        observerList.remove(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observerList) {
            observer.update(getCommentary());
        }
    }

    public String subjectDetails() {
        return getCommentary();
    }


    public void setCommentary(String commentary) {
        this.commentary = commentary;
// 这里只要一更新评论，那么则会立即通知订阅者
        notifyObservers();
    }

    public String getCommentary() {
        return this.commentary;
    }
}
