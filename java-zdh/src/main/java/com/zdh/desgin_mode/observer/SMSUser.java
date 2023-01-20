package com.zdh.desgin_mode.observer;

/**
 * @author zdh
 * @date 2022-07-05 17:38
 * @Version 1.0
 */
public class SMSUser implements Observer{
    private Subject subject;
    private String commentary;
    private String userInfo;

    public SMSUser(Subject subject, String userInfo) {
        if(subject==null){
            throw new IllegalArgumentException("No Publisher found.");
        }
        this.subject = subject;
        this.userInfo = userInfo;
    }

    public void update(String commentary) {
        this.commentary = commentary;
// 这里只要subject一发布事件，那么这里订阅者就会马上知道消息
        display();
// System.out.println("Subscribed successfully.");
    }

    public void subscribe() {
        System.out.println("Subscribing "+userInfo+" to "+subject.subjectDetails() + " ...");
        subject.subscribeObserver(this);
        System.out.println("Subscribed successfully.");
    }

    public void unSubcribe() {
        System.out.println("Unsubscribing "+userInfo+" to "+subject.subjectDetails()+" ...");
        subject.unSubscribeObserver(this);
        System.out.println("Unsubscribed successfully.");
    }

    public void display() {
        System.out.println("["+userInfo+"]: " + this.commentary);
    }
}
