package com.anhdt.doranewsvermain.service.voice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.util.VoiceTool;

import java.util.ArrayList;

public class VoicePlayerService extends Service implements VoicePlayerManager.OnListenerService {
    private static final String ACTION_CHANGE_MEDIA_STATE = "action_change_media_state";
    private static final String ACTION_NEXT = "action_next";
    private static final String ACTION_PREVIOUS = "action_previous";

    private OnListenerActivity mListenerActivity;
    private VoicePlayerManager mVoicePlayerManager;
    private final IBinder mIBinder = new ArticleBinder();

    public void setmListenerActivity(OnListenerActivity mListenerActivity) {
        this.mListenerActivity = mListenerActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mVoicePlayerManager = new VoicePlayerManager(this);
        mVoicePlayerManager.setmListenerService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        handleIntent(intent);
        return mIBinder;
    }

    @Override
    public void updateArticle(Article article) {
        //interface từ bên VoicePlayerManager truyền sang
        //Service gửi yêu cầu sang MainActivity để gọi update
        if (mListenerActivity == null) {
            return;
        }
        mListenerActivity.updateArticle(article);
    }

    @Override
    public void updateButtonWhenFinishing(Article article) {
        if (mListenerActivity == null) {
            return;
        }
        mListenerActivity.updateButtonWhenFinishing(article);
    }

    public class ArticleBinder extends Binder {
        public VoicePlayerService getService() {
            return VoicePlayerService.this;
        }
    }

    private void handleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            return;
        }
        switch (action) {
            case ACTION_CHANGE_MEDIA_STATE:
                playArticle();
                break;
            case ACTION_NEXT:
                nextArticle();
                break;
            case ACTION_PREVIOUS:
                previousArticle();
                break;
            default:
                break;
        }
    }

    public int getIndexCurrentArticle() {
        return mVoicePlayerManager.getmIndexCurrentArticle();
    }

    public void setIndexCurrentArticle(int position) {
        mVoicePlayerManager.setmIndexCurrentArticle(position);
    }

    public void setArticleList(ArrayList<Article> articles) {
        mVoicePlayerManager.setmCurrentArrayArticles(articles);
    }

    public ArrayList<Article> getCurrentArticlesList() {
        return mVoicePlayerManager.getmCurrentArrayArticles();
    }

    public void playArticle() {
        mVoicePlayerManager.playArticle();
    }

    public void playArticle(int position) {
        if (mVoicePlayerManager == null) {
            return;
        }
        setIndexCurrentArticle(position);
        mVoicePlayerManager.playArticle(position);
    }

    public void nextArticle() {
        mVoicePlayerManager.nextArticle();
    }

    public void stopArticle() {
        mVoicePlayerManager.stopArticle();
    }

    public void previousArticle() {
        mVoicePlayerManager.previousArticle();
    }

    public Article getCurrentArticle() {
        return mVoicePlayerManager.getCurrentArticle();
    }

    public int getCurrentTime() {
        return mVoicePlayerManager.getCurrentTime();
    }

    public String getTextTimeSeekBar() {
        return VoiceTool.getTextTimeSeekBar(getCurrentTime(),
                mVoicePlayerManager.getTotalDuration());
    }

    public String getTotalTime() {
        return VoiceTool.getTextTimeString(mVoicePlayerManager.getTotalDuration());
    }

    public String getTextExistTime() {
        return VoiceTool.parseMilliSecondsToTimer(mVoicePlayerManager.getTotalDuration() - getCurrentTime());
    }

    public boolean isFinishPlaying() {
        return mVoicePlayerManager.isFinishPlaying();
    }

    //Voice đang ở trạng thái Playing hoặc paused
    public boolean isPlaying() {
        return mVoicePlayerManager.isPlaying();
    }

    //Voice dang playing ma khong paused
    public boolean isOnlyPlaying() {
        return mVoicePlayerManager.isOnlyPlaying();
    }

    public void pauseArticle() {
        mVoicePlayerManager.pauseArticle();
    }

    public void seek(int progress) {
        mVoicePlayerManager.seek(progress);
    }


    //Dung de ra lenh cho MainActivity cap nhat ArticleUI
    public interface OnListenerActivity {
        /**
         * Hàm này để khi
         */
        void updateArticle(Article article);
        void updateButtonWhenFinishing(Article article);
    }
}
