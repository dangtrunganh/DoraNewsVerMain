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

    public static final int TYPE_EVENT_TOP_SINGLE = 0;
    public static final int TYPE_EVENT_TOP_WITH_TIME_LINE = 1;
    public static final int TYPE_EVENT_IN_TIME_LINE_WITH_HAVE_TIME = 2;
    public static final int TYPE_EVENT_IN_TIME_LINE_WITH_NOT_HAVE_TIME = 3;

    private String titleTop; //Latest or Timeline or null - ko phải phần tử đầu đặc biệt
    private String titleTime;
    private int type; //1 trong 4 loại trên
    private Event event;
    private ArrayList<Event> listEventToday;
    private ArrayList<Event> listEventYesterday;
    private ArrayList<Event> listEventLastWeek;
    private ArrayList<Event> listEventOlder;

    public ItemDetailStory(String titleTop, String titleTime, int type, Event event,
                           ArrayList<Event> listEventToday, ArrayList<Event> listEventYesterday,
                           ArrayList<Event> listEventLastWeek, ArrayList<Event> listEventOlder) {
        this.titleTop = titleTop;
        this.titleTime = titleTime;
        this.type = type;
        this.event = event;
        this.listEventToday = listEventToday;
        this.listEventYesterday = listEventYesterday;
        this.listEventLastWeek = listEventLastWeek;
        this.listEventOlder = listEventOlder;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public ArrayList<Event> getListEventToday() {
        return listEventToday;
    }

    public void setListEventToday(ArrayList<Event> listEventToday) {
        this.listEventToday = listEventToday;
    }

    public ArrayList<Event> getListEventYesterday() {
        return listEventYesterday;
    }

    public void setListEventYesterday(ArrayList<Event> listEventYesterday) {
        this.listEventYesterday = listEventYesterday;
    }

    public ArrayList<Event> getListEventLastWeek() {
        return listEventLastWeek;
    }

    public void setListEventLastWeek(ArrayList<Event> listEventLastWeek) {
        this.listEventLastWeek = listEventLastWeek;
    }

    public ArrayList<Event> getListEventOlder() {
        return listEventOlder;
    }

    public void setListEventOlder(ArrayList<Event> listEventOlder) {
        this.listEventOlder = listEventOlder;
    }
}
