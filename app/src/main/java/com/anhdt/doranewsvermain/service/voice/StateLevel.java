package com.anhdt.doranewsvermain.service.voice;

import android.support.annotation.IntDef;

@IntDef({StateLevel.PAUSE, StateLevel.PLAY})
public @interface StateLevel {
    int PAUSE = 0;
    int PLAY = 1;
}
