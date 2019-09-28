package szymon.kwak.worshipads;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    mpplaying playingC = new mpplaying();
    mpplaying playingDb = new mpplaying();
    mpplaying playingD = new mpplaying();
    mpplaying playingEb = new mpplaying();
    mpplaying playingE = new mpplaying();
    mpplaying playingF = new mpplaying();
    mpplaying playingGb = new mpplaying();
    mpplaying playingG = new mpplaying();
    mpplaying playingAb = new mpplaying();
    mpplaying playingA = new mpplaying();
    mpplaying playingBb = new mpplaying();
    mpplaying playingB = new mpplaying();
    List<mpplaying> mpplayingList = new ArrayList<>();
    Button[] Buttons = new Button[12];

    AudioManager am;
    MediaPlayer mpC, mpD, mpE;
    List<MediaPlayer> playersList = new ArrayList<>();

    int maxVolume;
    float volIn, volOut;

    Handler h1 = new Handler();
    Handler h2 = new Handler();

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Zmiana koloru navigationBar'a
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));

        //pobiera makymalną dostępną głośność z urządzenia
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (am != null) {
            maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            Buttons[0] = findViewById(R.id.btnC);
            Buttons[1] = findViewById(R.id.btnDb);
            Buttons[2] = findViewById(R.id.btnD);
            Buttons[3] = findViewById(R.id.btnEb);
            Buttons[4] = findViewById(R.id.btnE);
            Buttons[5] = findViewById(R.id.btnF);
            Buttons[6] = findViewById(R.id.btnGb);
            Buttons[7] = findViewById(R.id.btnG);
            Buttons[8] = findViewById(R.id.btnAb);
            Buttons[9] = findViewById(R.id.btnA);
            Buttons[10] = findViewById(R.id.btnBb);
            Buttons[11] = findViewById(R.id.btnB);
            mpplayingList.add(playingC);
            mpplayingList.add(playingDb);
            mpplayingList.add(playingD);
            mpplayingList.add(playingEb);
            mpplayingList.add(playingE);
            mpplayingList.add(playingF);
            mpplayingList.add(playingGb);
            mpplayingList.add(playingG);
            mpplayingList.add(playingAb);
            mpplayingList.add(playingA);
            mpplayingList.add(playingBb);
            mpplayingList.add(playingB);

            //inicjalizacja MediaPlayer'ów i dodanie ich do listy 'playersList'
            //mpC = MediaPlayer.create(this, R.raw.cmaj);
            //mpD = MediaPlayer.create(this, R.raw.dmaj);
            //mpE = MediaPlayer.create(this, R.raw.emaj);

            tv = findViewById(R.id.textView);


            mpC = new MediaPlayer(); mpD = new MediaPlayer(); mpE = new MediaPlayer();
            playersList.add(mpC); playersList.add(mpD); playersList.add(mpE);



        }
    }




    public void PlayStop (mpplaying mppl, Button b, MediaPlayer mediaPlayer, int padResId){

        if (!mppl.isPlayingX()){
            setAllMpplayingFalse(); //wszystkie 'czyGra' są false
            mppl.setPlayingX(true); //tylko podany jest 'true'
            setAllButtonsUnclicked(Buttons); //wszystkie przyciski są 'unclicked'
            b.setBackgroundColor(getResources().getColor(R.color.clicked)); //tylko podany jest 'clicked'

            fadeOut();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();}

            fadeIn(mediaPlayer,padResId);

        }
        else {
            mppl.setPlayingX(false);
            fadeOut();
            b.setBackgroundColor(getResources().getColor(R.color.unclicked));
        }

    }

    //https://stackoverflow.com/questions/38380495/android-studio-mediaplayer-how-to-fade-in-and-out
    public void fadeIn(final MediaPlayer mediaPlayer, int padResId) {
        final int FADE_DURATION = 15000;
        final int FADE_INTERVAL = 200;
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL;
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //pobierz aktualną głośność z urządzenia

        volIn = 0.0f;
        final float deltaVolume = (currentVolume / (float) maxVolume) / (float) numberOfSteps;

        //przygotowanie mediaPlayera do wystartowania
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + padResId));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();

        //właściwy runnable który robi Fade In
        if (mediaPlayer.isPlaying()) {
            final Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                    while (volIn <= 1.0)
                        mediaPlayer.setVolume(volIn, volIn);
                    volIn = volIn + deltaVolume;
                    h1.postDelayed(this, FADE_INTERVAL);
                    tv.setText((String.valueOf(volIn)));

                }
            };
            runnable1.run();
        }
    }

    public void fadeOut(){
        final int FADE_DURATION = 15000;
        final int FADE_INTERVAL = 200;
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL;
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //pobierz aktualną głośność z urządzenia

        volOut = currentVolume / (float)maxVolume;
        final float deltaVolume = volOut / (float)numberOfSteps;

        //ta pętla przeszukuje w Liście 'playersList' który MediaPlayer aktualnie gra, i tego właśnie wycisza.
        for (int i = 0; i < playersList.size(); i++) {
            if (playersList.get(i).isPlaying()) {
                final int j = i;
                Runnable runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        playersList.get(j).setVolume(volOut, volOut);
                        volOut = volOut - deltaVolume;
                        if (volOut <= 0) {
                            playersList.get(j).stop();
                            playersList.get(j).reset();
                        }
                        h2.postDelayed(this,FADE_INTERVAL);
                    }
                };
                runnable2.run();
            }
        }

    }


    //ustawia kolor wszystkich Buttonów w tablicy na 'unclicked'
    public void setAllButtonsUnclicked(Button[] btnarr){
        for (Button b: btnarr) {
            b.setBackgroundColor(getResources().getColor(R.color.unclicked));
        }
    }
    //ustawia wysztkie 'czyGra' na false
    public void setAllMpplayingFalse(){
        for (int i = 0; i < mpplayingList.size();i++){
            mpplayingList.get(i).setPlayingX(false);
        }
    }

    public void btnCclick (View view){
        PlayStop(playingC,Buttons[0],mpC,R.raw.cmaj);
    }
    public void btnDbclick (View view){
        //  https://stackoverflow.com/questions/2969242/problems-with-mediaplayer-raw-resources-stop-and-start/4761482
        mpD.reset();
        try {
            mpD.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dmaj));
            mpD.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mpD.setLooping(true);
        mpD.seekTo(0);
        mpD.start();
        Buttons[1].setBackgroundColor(getResources().getColor(R.color.clicked));
    }
    public void btnDclick (View view){
        PlayStop(playingD,Buttons[2],mpD,R.raw.dmaj);
    }
    public void btnEclick (View view){
        PlayStop(playingE,Buttons[4],mpE,R.raw.emaj);
    }




    /**

     //ten runnable wycisza MadiaPlayer'a

     Runnable runnable = new Runnable() {
    @Override
    public void run() {

    currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //pobierz aktualną głośność z urządzenia
    vol = currentVolume/maxVolume;
    vol = vol - 0.1f;
    mp.setVolume(vol,vol);
    mHandler.postDelayed(runnable,100); //odczekaj pół sekundy (500) i powtórz
    }
    };

     */


}


/*
    public void fadeOut(){
        final int FADE_DURATION = 20000;
        final int FADE_INTERVAL = 200;
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL;

        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //pobierz aktualną głośność z urządzenia
        vol = currentVolume/(float)maxVolume;
        final float deltaVolume = vol / (float)numberOfSteps;

        //ta pętla przeszukuje w Liście 'playersList' który MediaPlayer aktualnie gra, i tego właśnie wycisza.
        for (int i = 0; i < playersList.size(); i++) {
            if (playersList.get(i).isPlaying()) {
                final int j = i;
                Toast.makeText(this,"j: " + j,Toast.LENGTH_LONG).show();
                final Timer timer2 = new Timer(true);
                TimerTask timerTask2 = new TimerTask() {
                    @Override
                    public void run() {
                        playersList.get(j).setVolume(vol, vol);
                        vol = vol - deltaVolume;
                        if (vol <= 0) {
                            timer2.cancel();
                            timer2.purge();
                            playersList.get(j).stop();
                        }
                    }
                };
                timer2.schedule(timerTask2, FADE_INTERVAL, FADE_INTERVAL);
            }
        }

    }



    public void fadeIn(final MediaPlayer mediaPlayer){
        final int FADE_DURATION = 20000;
        final int FADE_INTERVAL = 200;
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL;

        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //pobierz aktualną głośność z urządzenia
        vol = 0;
        final float deltaVolume = (currentVolume/(float)maxVolume) / (float)numberOfSteps;

        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();

        if (mediaPlayer.isPlaying()) {
            Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.setVolume(vol, vol);
                    vol = vol + deltaVolume;
                    h1.postDelayed(this,FADE_INTERVAL);
                }
            };
            runnable1.run();

          /*
            final Timer timer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    mediaPlayer.setVolume(vol, vol);
                    vol = vol + deltaVolume;
                    if (vol >= currentVolume/(float)maxVolume) {
                        timer.cancel();
                        timer.purge();
                    }
                }
            };
            timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL);
        */





