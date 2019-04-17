package com.anhdt.doranewsvermain.model;

import java.util.ArrayList;

//Ta sẽ sử dụng List<DatumStory>, trong đó
public class DatumStory {
    //List sẽ có 2 phần tử chính, mỗi phần tử là 1 DatumStory
    //Phần tử đầu sẽ có title = LATEST_EVENT
    //Phần tử tiếp theo sẽ có title = TYPE_ITEM_IN_TIME_LINE

    //Today, Yesterday, Recently, Older, các title này sẽ dựa vào các list rỗng hay không để mà set

    public static final String TODAY = "Hôm nay";
    public static final String YESTERDAY = "Hôm qua";
    public static final String LAST_WEEK = "Tuần qua";
    public static final String OLDER = "Cũ hơn";

//    public static final int TYPE_LATEST_EVENT = 0; //Các type này dùng bên ViewHolder là ok rồi
//    public static final int TYPE_ITEM_IN_TIME_LINE = 1;
//    private int type; //Phần tử đầu hay những phần tử còn lại?

    public static final String LATEST_EVENT = "Sự kiện gần nhất";
    public static final String TIME_LINE = "Dòng sự kiện";

    private String title; //Latest event or Timeline, dùng cái này để xác định type của phần tử
    private ArrayList<ItemDetailStory> listItemDetailStory;

    public DatumStory(String title, ArrayList<ItemDetailStory> listItemDetailStory) {
        this.title = title;
        this.listItemDetailStory = listItemDetailStory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ItemDetailStory> getListItemDetailStory() {
        return listItemDetailStory;
    }

    public void setListItemDetailStory(ArrayList<ItemDetailStory> listItemDetailStory) {
        this.listItemDetailStory = listItemDetailStory;
    }
}
