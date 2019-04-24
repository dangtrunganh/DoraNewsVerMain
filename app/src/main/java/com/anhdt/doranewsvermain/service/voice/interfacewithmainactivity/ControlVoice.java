package com.anhdt.doranewsvermain.service.voice.interfacewithmainactivity;

import com.anhdt.doranewsvermain.model.newsresult.Article;

import java.util.ArrayList;

public interface ControlVoice {
    void playVoiceAtPosition(ArrayList<Article> articles, int position);

    void setCurrentListVoiceOnTopStack(ArrayList<Article> articles);

    void deleteCurrentListVoiceOnTopStack();
}
