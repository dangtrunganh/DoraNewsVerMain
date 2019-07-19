package com.anhdt.doranewsvermain.util;

import com.anhdt.doranewsvermain.model.ItemDetailStory;
import com.anhdt.doranewsvermain.model.newsresult.Event;

import java.util.ArrayList;

public class ToolForStory {
    public static ArrayList<ItemDetailStory> convertToListDatumStory(ArrayList<Event> listEventToConvert) {
        //Output: List rỗng hoặc null thì đểu trả về list rỗng
        //List này phải theo thứ tự!
        //Đầu vào là list đã sắp thứ tự theo trình tự thời gian!
        ArrayList<ItemDetailStory> itemDetailStories = new ArrayList<>();
        boolean today = false; //kiểm tra xem đã có phần tử đầu tiên chưa? Chưa thì cho label lên đầu, gán biến này = true
        boolean yesterday = false;
        boolean lastweek = false;
        boolean older = false;

        boolean oneWeekAgo = false;
        boolean twoWeekAgo = false;
        boolean threeWeekAgo = false;
        boolean aMonthAgo = false;

        boolean timeLineIsSet = false;
        if (listEventToConvert == null) {
            return itemDetailStories;
        }
        if (listEventToConvert.size() == 0) {
            return itemDetailStories;
        }
        //Xét phần tử đầu tiên
        ItemDetailStory itemInit = new ItemDetailStory(ItemDetailStory.LATEST_EVENT, null, ItemDetailStory.TYPE_EVENT_TOPEST_SINGLE, listEventToConvert.get(0));
        itemDetailStories.add(itemInit);
//        if (listEventToConvert.size() == 1) {
//            return itemDetailStories;
//        }
        for (int i = 0; i < listEventToConvert.size(); i++) {
            //Tính tiếp từ index i = 0 trở đi đến hết để thêm vào đoạn còn lại, trùng ko sao ^^, vì thằng trên và list dưới là khác nhau
            //Kiểm tra với mỗi Event, phân vào 1 trong 4 type, tạo phần tử với type đó
            Event event = listEventToConvert.get(i);
            long dayOffset = GeneralTool.countDayToNow(event.getTime());
            if (!timeLineIsSet) {
                //Các trường hợp đầu tiên sẽ phải có

                //Vì với thằng số 1 này, nó sẽ có thêm cái label "Time line" trên đầu nên phải xét riêng, cũng ko hẳn đâu @@,
                //Vì yester day cũng có thể có timeline mà?

                //Tất cả trong cái IF to này đầu tiên sẽ phải có time line lên đầu, type của nó sẽ là
                //ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL
                if (dayOffset == 0) {
                    //Today
                    if (!today) {
                        //thằng today đầu tiên, phải là kiểu ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL,
                        //Có label: ItemDetailStory.TIME_LINE và ItemDetailStory.TODAY
                        itemDetailStories.add(new ItemDetailStory(ItemDetailStory.TIME_LINE, ItemDetailStory.TODAY, ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL, event));
                        today = true;
                        timeLineIsSet = true;
                    } /*else {
                        //bản chất nếu đã set xong timeLineIsSet thì sẽ méo bao giờ vào đây đâu, phí công vcl
                        //Các thằng today khác, ko phải thằng đầu
                        itemDetailStories.add(new ItemDetailStory())
                    }*/

                } else if (dayOffset == 1) {
                    if (!yesterday) {
                        itemDetailStories.add(new ItemDetailStory(ItemDetailStory.TIME_LINE, ItemDetailStory.YESTERDAY, ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL, event));
                        yesterday = true;
                        timeLineIsSet = true;
                    }

                } else if (dayOffset >= 2 && dayOffset <= 7) {
                    if (!lastweek) {
                        itemDetailStories.add(new ItemDetailStory(ItemDetailStory.TIME_LINE, ItemDetailStory.LAST_WEEK, ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL, event));
                        lastweek = true;
                        timeLineIsSet = true;
                    }
                }
                //=====SETTTT=======
                else if (dayOffset >= 8 && dayOffset <= 14) {
                    if (!oneWeekAgo) {
                        itemDetailStories.add(new ItemDetailStory(ItemDetailStory.TIME_LINE, ItemDetailStory.A_WEEK_AGO, ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL, event));
                        oneWeekAgo = true;
                        timeLineIsSet = true;
                    }
                } else if (dayOffset >= 15 && dayOffset <= 21) {
                    if (!twoWeekAgo) {
                        itemDetailStories.add(new ItemDetailStory(ItemDetailStory.TIME_LINE, ItemDetailStory.TWO_WEEK_AGO, ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL, event));
                        twoWeekAgo = true;
                        timeLineIsSet = true;
                    }
                } else if (dayOffset >= 22 && dayOffset <= 30) {
                    if (!threeWeekAgo) {
                        itemDetailStories.add(new ItemDetailStory(ItemDetailStory.TIME_LINE, ItemDetailStory.THREE_WEEK_LATER, ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL, event));
                        threeWeekAgo = true;
                        timeLineIsSet = true;
                    }
                } else if (dayOffset >= 31 && dayOffset <= 61) {
                    if (!aMonthAgo) {
                        itemDetailStories.add(new ItemDetailStory(ItemDetailStory.TIME_LINE, ItemDetailStory.A_MONTH_AGO, ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL, event));
                        aMonthAgo = true;
                        timeLineIsSet = true;
                    }
                }
                //=====SETTTT=======
                else {
                    if (!older) {
                        itemDetailStories.add(new ItemDetailStory(ItemDetailStory.TIME_LINE, ItemDetailStory.OLDER, ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL, event));
                        older = true;
                        timeLineIsSet = true;
                    }
                }

            } else {
                //Trường hợp Timeline đã được set rồi
                if (dayOffset == 0) {
                    //Today
                    if (!today) {
                        //Nếu Timeline đã được set, thì không bao giờ vào case này, vì dữ liệu đã được sắp xếp theo thời gian
                        //thằng today đầu tiên, phải là kiểu ItemDetailStory.TYPE_EVENT_TOP_WITH_TIME_LINE_LABEL,
                        //Có label: ItemDetailStory.TIME_LINE và ItemDetailStory.TODAY
                        itemDetailStories.add(new ItemDetailStory(null, ItemDetailStory.TODAY, ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL, event));
                        today = true;
                    } else {
                        itemDetailStories.add(new ItemDetailStory(null, null, ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL, event));
                    }

                } else if (dayOffset == 1) {
                    if (!yesterday) {
                        itemDetailStories.add(new ItemDetailStory(null, ItemDetailStory.YESTERDAY, ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL, event));
                        yesterday = true;
                    } else {
                        itemDetailStories.add(new ItemDetailStory(null, null, ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL, event));
                    }

                } else if (dayOffset >= 2 && dayOffset <= 7) {
                    if (!lastweek) {
                        itemDetailStories.add(new ItemDetailStory(null, ItemDetailStory.LAST_WEEK, ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL, event));
                        lastweek = true;
                    } else {
                        itemDetailStories.add(new ItemDetailStory(null, null, ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL, event));
                    }
                }
                //=====SETTTT=======
                else if (dayOffset >= 8 && dayOffset <= 14) {
                    if (!oneWeekAgo) {
                        itemDetailStories.add(new ItemDetailStory(null, ItemDetailStory.A_WEEK_AGO, ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL, event));
                        oneWeekAgo = true;
                    } else {
                        itemDetailStories.add(new ItemDetailStory(null, null, ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL, event));
                    }
                } else if (dayOffset >= 15 && dayOffset <= 21) {
                    if (!twoWeekAgo) {
                        itemDetailStories.add(new ItemDetailStory(null, ItemDetailStory.TWO_WEEK_AGO, ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL, event));
                        twoWeekAgo = true;
                    } else {
                        itemDetailStories.add(new ItemDetailStory(null, null, ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL, event));
                    }
                } else if (dayOffset >= 22 && dayOffset <= 30) {
                    if (!threeWeekAgo) {
                        itemDetailStories.add(new ItemDetailStory(null, ItemDetailStory.THREE_WEEK_LATER, ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL, event));
                        threeWeekAgo = true;
                    } else {
                        itemDetailStories.add(new ItemDetailStory(null, null, ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL, event));
                    }
                } else if (dayOffset >= 31 && dayOffset <= 61) {
                    if (!aMonthAgo) {
                        itemDetailStories.add(new ItemDetailStory(null, ItemDetailStory.A_MONTH_AGO, ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL, event));
                        aMonthAgo = true;
                    } else {
                        itemDetailStories.add(new ItemDetailStory(null, null, ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL, event));
                    }
                }
                //=====SETTTT=======
                else {
                    if (!older) {
                        itemDetailStories.add(new ItemDetailStory(null, ItemDetailStory.OLDER, ItemDetailStory.TYPE_EVENT_NORMAL_HAVE_TO_DAY_LABEL, event));
                        older = true;
                    } else {
                        itemDetailStories.add(new ItemDetailStory(null, null, ItemDetailStory.TYPE_EVENT_NORMAL_NOT_HAVE_ANY_LABEL, event));
                    }
                }
            }
        }

        ItemDetailStory itemDetailStory = new ItemDetailStory();
        itemDetailStory.setFooter(true);
        itemDetailStories.add(itemDetailStory);
        return itemDetailStories;
    }
}
