package com.anhdt.doranewsvermain.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import com.anhdt.doranewsvermain.activity.SplashActivity;
import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.model.DatumStory;
import com.anhdt.doranewsvermain.model.ItemDetailStory;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.model.newsresult.Datum;
import com.anhdt.doranewsvermain.model.newsresult.Event;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.time.ZoneOffset.UTC;

public class GeneralTool {
    public static boolean checkIfChildOutIParent(float yChildTop, float yChildBottom, float yParentTop, float yParentBottom) {
        if (yChildTop < yParentTop) {
            return true;
        } else if (yChildBottom > yParentBottom) {
            return true;
        }
        return false;
    }

    public static boolean checkIfParentHasChild(ArrayList<Datum> parents, ArrayList<Datum> childs) {
        String idParent = "";
        String idChild = "";
        for (int i = 0; i < parents.size(); i++) {
            if (parents.get(i) == null) {
                continue;
            }
            idParent += parents.get(i).getId();
        }
        for (int i = 0; i < childs.size(); i++) {
            if(childs.get(i) == null) {
                continue;
            }
            idChild += childs.get(i).getId();
        }
        return idParent.contains(idChild);
    }

    public static int convertDpsToPixels(Context mContext, float valueDps) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (valueDps * scale + 0.5f);
    }

    public static int convertPixelsToDps(Context mContext, float valuePixels) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (int) ((valuePixels - 0.5f) / scale);
    }

    public static int getWidthScreen(Context mContext) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getHeightScreen(Context mContext) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static String getDeviceId(Context mContext) {
        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.e("k-android_id", android_id);
        return android_id;
    }

    private static String getCurrentDay() {
        Date myDate = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdyFormat = new SimpleDateFormat("dd MM yyyy");
        return mdyFormat.format(myDate);
    }

    private static String convertUTCTimeToLocalTime(String timeUTC) {
        String timeUTCBefore = timeUTC.split("T")[0];
        SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = utcDateFormat.parse(timeUTCBefore);
            @SuppressLint("SimpleDateFormat") DateFormat out = new SimpleDateFormat("dd MM yyyy");
            return out.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //startDay ở dạng UTC time, đầu ra là số ngày giữa 2 ngày
    public static long countDayToNow(String startDay) {
        String today = GeneralTool.getCurrentDay(); //today là end day
        String startDayFormatted = GeneralTool.convertUTCTimeToLocalTime(startDay);
        Log.e("result-start", startDayFormatted);
        Log.e("result-today", today);
        if (startDayFormatted == null) {
            return -1;
        }
        return GeneralTool.countDayBetweenTime(startDayFormatted, today);
    }

    private static long countDayBetweenTime(String startDay, String endDay) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
//        DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        String inputString1 = "23 01 1997";
//        String inputString2 = "27 04 1997";
        try {
            Date date1 = myFormat.parse(startDay);
            Date date2 = myFormat.parse(endDay);
            long diff = date2.getTime() - date1.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Hàm này truyền vào một article và type, trả về summary của article đó
     *
     * @param article
     * @param type    - type: ConstParam.MEDIUM, SHORT, LONG
     * @return summary of article
     */
    public static String getSummaryOfArticle(Article article, String type) {
        //type: ConstParam.MEDIUM - "medium"
        for (int i = 0; i < article.getMedias().size(); i++) {
            if (article.getMedias().get(i).getType().equals(ConstParam.MEDIUM)) {
                return article.getMedias().get(i).getBody().get(0).getContent();
            }
        }
        return "\n\n\n\n";
    }

    public static ArrayList<ItemDetailStory> convertToListDatumStory(ArrayList<Event> listEventToConvert) {
        //Output: List rỗng hoặc null thì đểu trả về list rỗng
        //List này phải theo thứ tự!
        //Đầu vào là list đã sắp thứ tự theo trình tự thời gian!
        ArrayList<ItemDetailStory> itemDetailStories = new ArrayList<>();
        boolean today = false; //kiểm tra xem đã có phần tử đầu tiên chưa? Chưa thì cho label lên đầu, gán biến này = true
        boolean yesterday = false;
        boolean lastweek = false;
        boolean older = false;

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
                } else {
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
                } else {
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

    //k - số phần tử xung quanh x, indexSpecificEvent - index của phần tử x, ví dụ k = 4
    public static ArrayList<Event> findKClosestEvent(ArrayList<Event> arrayEventInStory, int indexSpecificEvent, int k) {
        ArrayList<Event> arrayResult = new ArrayList<>();
        if (indexSpecificEvent < 0 || indexSpecificEvent > arrayEventInStory.size() - 1) {
            return arrayResult;
        }
        if (arrayEventInStory.size() <= k) {
            arrayResult = arrayEventInStory;
            return arrayResult;
        }
        int n = arrayEventInStory.size();
        int l = indexSpecificEvent - 1;
        int r = indexSpecificEvent + 1;
        int count = 0;
        int kMinL = k / 2;
        arrayResult.add(arrayEventInStory.get(indexSpecificEvent));
        while (l >= 0 && r < n && count < k) {
            if (kMinL > 0) {
                arrayResult.add(0, arrayEventInStory.get(l--));
                kMinL--;
            } else {
                arrayResult.add(arrayEventInStory.get(r++));
            }
            count++;
        }
        while (count < k && l >= 0) {
            arrayResult.add(0, arrayEventInStory.get(l--));
            count++;
        }
        while (count < k && r < n) {
            arrayResult.add(arrayEventInStory.get(r++));
            count++;
        }
        return arrayResult;
    }
}
