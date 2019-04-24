package com.anhdt.doranewsvermain.service.voice;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.anhdt.doranewsvermain.constant.ConstParam;
import com.anhdt.doranewsvermain.model.newsresult.Article;
import com.anhdt.doranewsvermain.util.GeneralTool;
import com.anhdt.doranewsvermain.util.VoiceTool;

import java.io.IOException;
import java.util.ArrayList;

public class VoicePlayerManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private OnListenerService mListenerService; //Service sẽ implements cái này
    private MediaPlayer mPlayer;
    private ArrayList<Article> mCurrentArrayArticles;
    private int mIndexCurrentArticle;
    private int mState = MediaPlayerState.IDLE;
    private Context mContext;

    public VoicePlayerManager(Context mContext) {
        this.mPlayer = new MediaPlayer();
        this.mContext = mContext;
    }

    public int getmIndexCurrentArticle() {
        return mIndexCurrentArticle;
    }

    public ArrayList<Article> getmCurrentArrayArticles() {
        return mCurrentArrayArticles;
    }

    public void setmCurrentArrayArticles(ArrayList<Article> mCurrentArrayArticles) {
        if (mCurrentArrayArticles == null) {
            return;
        }
        this.mCurrentArrayArticles = mCurrentArrayArticles;
    }

    //Sau khi MainAct khởi tạo VoicePLayerManager, sẽ gọi hàm này, set tham số = this --> Hàm này đã có MainAct để gọi updateUI
    public void setmListenerService(OnListenerService mListenerService) {
        this.mListenerService = mListenerService;
    }

    //Kiểm tra trạng thái hiện tại có đang playing không? (only playing)
    public boolean isOnlyPlaying() {
        return mState == MediaPlayerState.PLAYING;
    }

    public boolean isPlaying() {
        return mState == MediaPlayerState.PLAYING || mState == MediaPlayerState.PAUSED;
    }

    public void setmIndexCurrentArticle(int mIndexCurrentArticle) {
        this.mIndexCurrentArticle = mIndexCurrentArticle;
    }

    public Article getCurrentArticle() {
        return mCurrentArrayArticles.get(mIndexCurrentArticle);
    }

    public String getURrlVoice(Article article) {
        String content = article.getTitle() + ". " + GeneralTool.getSummaryOfArticle(article, ConstParam.MEDIUM);
        int length = content.length();
        if (length >= 999) {
            content = content.substring(0, 500);
        }
        return "https://tts.vbeecore.com/api/tts?app_id=5cbdc6a45f942864b59ce724&key=49e53f296478fbc5fcc5438d135757de&voice=sg_female_xuanhong_vdts_48k-hsmm&rate=1&time=1555941100680&user_id=47734&service_type=1&input_text="
                + content;
    }

    public void playArticle() {
        if (mState == MediaPlayerState.IDLE || mState == MediaPlayerState.STOPPED) {
            Article article = mCurrentArrayArticles.get(mIndexCurrentArticle);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(getURrlVoice(article));
            } catch (IOException e) {
                Toast.makeText(mContext, "Quá 2000 ký tự, lỗi!", Toast.LENGTH_SHORT).show();
//                nextSong();
            }
            //Mỗi khi chạy bài hát mới sẽ update thông tin lại trên thanh control music, bắn list sang service để nó làm
            //Vì MediaManager ko làm việc trực tiếp với MainActivity
            if (mListenerService != null) {
                mListenerService.updateArticle(getCurrentArticle());
            }

            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.prepareAsync();
            mState = MediaPlayerState.PLAYING;
            return;
        }

        //Đang chạy ở PLAYING thì sẽ chuyển sang PAUSED
        if (mState == MediaPlayerState.PLAYING) {
            mPlayer.pause();
            mState = MediaPlayerState.PAUSED;
            return;
        }

        //Nếu đang ở PAUSED thì start lại
        mPlayer.start();
        mState = MediaPlayerState.PLAYING;
    }

    public void stopArticle() {
        if (mState == MediaPlayerState.PLAYING || mState == MediaPlayerState.PAUSED) {
            mPlayer.stop();
            mPlayer.reset();
            mState = MediaPlayerState.STOPPED;
        }
    }

    public void playArticle(int position) {
        mIndexCurrentArticle = position;
        stopArticle();
        playArticle();
    }

    public void nextArticle() {
        if (mIndexCurrentArticle == mCurrentArrayArticles.size() - 1) {
            return;
        }
        mIndexCurrentArticle++;
        stopArticle();
        playArticle();
    }

    public void previousArticle() {
        if (mIndexCurrentArticle == 0) {
            return;
        }
        mIndexCurrentArticle--;
        stopArticle();
        playArticle();
    }

    public void pauseArticle() {
        if (mState == MediaPlayerState.PLAYING) {
            mPlayer.pause();
            mState = MediaPlayerState.PAUSED;
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mIndexCurrentArticle < mCurrentArrayArticles.size() - 1) {
            mIndexCurrentArticle++;
            stopArticle();
            playArticle();
        }
    }

    public int getCurrentTime() {
        return mPlayer.getCurrentPosition();
    }

    public int getTotalDuration() {
        return mPlayer.getDuration();
    }

    public String getTextTimeSeekBar() {
        return VoiceTool.getTextTimeSeekBar(getCurrentTime(), mPlayer.getDuration());
    }

    public void seek(int progress) {
        mPlayer.seekTo(progress);
    }

    //Dung de ra lenh cho Service update
    public interface OnListenerService {
        void updateArticle(Article article);
    }
}
