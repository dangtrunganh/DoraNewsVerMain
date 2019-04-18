package com.anhdt.doranewsvermain.model;

import com.anhdt.doranewsvermain.model.newsresult.Event;

import java.util.ArrayList;

public class ItemDetailStory {
    public static final String LATEST_EVENT = "Sự kiện gần nhất";
    public static final String TIME_LINE = "Dòng sự kiện";

    public static final String TODAY = "Hôm nay";
    public static final String YESTERDAY = "Hôm qua";
    public static final String LAST_WEEK = "Tuần qua";
    public static final String OLDER = "Cũ hơn";

    public static final int TYPE_EVENT_TOPEST_SINGLE = 0; //Thằng trên cùng, event đơn
    public static final int TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL = 1;
    public static final int TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL = 2;
    public static final int TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL = 3;

    private String titleTop; //Latest or Timeline or NULL - ko phải phần tử đầu đặc biệt
    private String titleTime; //time - TODAY, YESTERDAY,...
    private int type; //1 trong 4 loại trên
    private Event event;
//    private ArrayList<Event> listEventToday;
//    private ArrayList<Event> listEventYesterday;
//    private ArrayList<Event> listEventLastWeek;
//    private ArrayList<Event> listEventOlder;


    public ItemDetailStory(String titleTop, String titleTime, int type, Event event) {
        this.titleTop = titleTop;
        this.titleTime = titleTime;
        this.type = type;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getTitleTop() {
        return titleTop;
    }

    public void setTitleTop(String titleTop) {
        this.titleTop = titleTop;
    }

    public String getTitleTime() {
        return titleTime;
    }

    public void setTitleTime(String titleTime) {
        this.titleTime = titleTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
