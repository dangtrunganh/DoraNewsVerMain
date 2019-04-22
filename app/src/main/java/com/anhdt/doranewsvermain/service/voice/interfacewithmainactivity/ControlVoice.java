package com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity;

import com.anhdt.doranewsvermain.model.newsresult.Article;

import java.util.ArrayList;

public interface ControlVoice {
    public void playVoiceAtPosition(ArrayList<Article> articles, int position);
}
