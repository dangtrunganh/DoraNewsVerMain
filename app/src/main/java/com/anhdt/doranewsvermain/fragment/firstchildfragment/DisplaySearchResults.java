package com.anhdt.doranewsvermain.fragment.firstchildfragment;

import android.app.ProgressDialog;

public interface DisplaySearchResults {
    public void displayArticleResults(String keywords, ProgressDialog dialog); //Gọi đến frg con, yêu cầu nó search và hiển thị kết quả
    public void displayEventResults(String keywords, ProgressDialog dialog); //Gọi đến frg con, yêu cầu nó search và hiển thị kết quả
}
