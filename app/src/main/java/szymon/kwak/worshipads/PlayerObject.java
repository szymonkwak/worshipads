package szymon.kwak.worshipads;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.os.Handler; //Uwaga! Android SDK imprtuje złą klasę. https://stackoverflow.com/questions/19873063/handler-is-abstract-cannot-be-instantiated
import java.io.IOException;


public class PlayerObject extends ContextWrapper {

    public Button button;
    private MediaPlayer mediaPlayer;
    private int resPadId;
    private float volIn, volOut;
    private boolean fadingIn, fadingOut;
    Handler hFadeOut = new Handler(Looper.getMainLooper());
    Handler hFadeIn = new Handler(Looper.getMainLooper());

    public PlayerObject(MediaPlayer mediaPlayer, Button button, int resPadId, Context base) {
        super(base);
        this.button = button;
        this.mediaPlayer = mediaPlayer;
        this.resPadId = resPadId;
    }


    public void fadeIn(AudioManager am, final ProgressBar progressBarFadeIn) {
        final int FADE_DURATION = 15000;
        final int FADE_INTERVAL = 200;
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL;
        final int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //pobierz maksymalną głośność z urządzenia
        final int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //pobierz aktualną głośność z urządzenia
        volIn = 0.0f;

        final float deltaVolume = (currentVolume / (float) maxVolume) / (float) numberOfSteps;

        setFadingIn(true);
        progressBarFadeIn.setProgress(0);
        progressBarFadeIn.setVisibility(View.VISIBLE);
        prepareMediaPlayerToStart(mediaPlayer,resPadId);


        if (mediaPlayer.isPlaying()) {
            final Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                    if (volIn >= (currentVolume / (float) maxVolume) | fadingIn == false) {
                        progressBarFadeIn.setVisibility(View.INVISIBLE);
                        setFadingIn(false);
                        return; //zatrzymuje run() runnable
                    }
                    mediaPlayer.setVolume(volIn, volIn);
                    volIn = volIn + deltaVolume;
                    hFadeIn.postDelayed(this,FADE_INTERVAL);
                    setProgressOfBar(progressBarFadeIn, (int) ((volIn / (currentVolume / (float) maxVolume)) * 100));
                }
            };
            runnable1.run();
        }
    }

    public void fadeOut(AudioManager am){

        if (mediaPlayer.isPlaying()) {
            final int FADE_DURATION = 12000;
            final int FADE_INTERVAL = 200;
            int numberOfSteps = FADE_DURATION/FADE_INTERVAL;
            int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

            volOut = currentVolume / (float)maxVolume;
            final float deltaVolume = volOut / (float)numberOfSteps;

            setFadingOut(true);

             Runnable runnable2 = new Runnable() {
                 @Override
                 public void run() {
                     mediaPlayer.setVolume(volOut, volOut);
                     volOut = volOut - deltaVolume;
                     if (volOut <= 0 | fadingOut == false) {
                         mediaPlayer.stop();
                         mediaPlayer.reset();
                         setFadingOut(false);
                         return; //zatrzymuje run() runnable
                     }
                     hFadeOut.postDelayed(this,FADE_INTERVAL);
                 }
             };
             runnable2.run();
        }
    }

    public boolean isFadingIn(){
        return fadingIn;
    }
    public void setFadingIn(boolean fadingIn) {
        this.fadingIn = fadingIn;
    }
    public boolean isFadingOut() {
        return fadingOut;
    }
    public void setFadingOut(boolean fadingOut) {
        this.fadingOut = fadingOut;
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public void stopPlaying(){
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    private void prepareMediaPlayerToStart(MediaPlayer mediaPlayer, int padResId){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + padResId));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    private void setProgressOfBar(ProgressBar progressBar, int progress) {
        progressBar.setProgress(progress);
    }

}