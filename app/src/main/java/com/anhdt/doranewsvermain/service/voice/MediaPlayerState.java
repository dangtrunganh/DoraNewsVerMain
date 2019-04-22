package com.anhdt.doranewsvermain.service.voice;

import android.support.annotation.IntDef;

@IntDef({MediaPlayerState.IDLE, MediaPlayerState.PLAYING,
        MediaPlayerState.PAUSED, MediaPlayerState.STOPPED})
public @interface MediaPlayerState {
    int IDLE = 0;
    int PLAYING = 1;
    int PAUSED = 2;
    int STOPPED = 3;
}
