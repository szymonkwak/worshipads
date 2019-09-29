package szymon.kwak.worshipads;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    MediaPlayer mpC, mpDb, mpD, mpEb, mpE, mpF, mpGb, mpG, mpAb, mpA, mpBb, mpB;
    List<MediaPlayer> playersList = new ArrayList<>();


    Handler hFadeIn = new Handler();
    Handler hFadeOut = new Handler();
    Handler hProgressB = new Handler();
    Handler hClock = new Handler();

    int maxVolume;
    float volIn, volOut;

    TextView textTime, tv1, tv2;
    ProgressBar progressBarFadeIn;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    long date;
    String dateString;

    private void creatempplayingList(){
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); //ukryj TitleBar
        setContentView(R.layout.activity_main);

        //Zmiana koloru navigationBar'a
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));

        //pobiera makymalną dostępną głośność z urządzenia
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (am != null) {
            maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            progressBarFadeIn = findViewById(R.id.progressBarFadeIn);
            textTime = findViewById(R.id.textTime);
            createAndStartClock();
            creatempplayingList();

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

            mpC = new MediaPlayer(); mpDb = new MediaPlayer(); mpD = new MediaPlayer(); mpEb = new MediaPlayer();
            mpE = new MediaPlayer(); mpF = new MediaPlayer(); mpGb = new MediaPlayer(); mpG = new MediaPlayer();
            mpAb = new MediaPlayer(); mpA = new MediaPlayer(); mpBb = new MediaPlayer(); mpB = new MediaPlayer();
            playersList.add(mpC); playersList.add(mpDb); playersList.add(mpD); playersList.add(mpEb);
            playersList.add(mpE); playersList.add(mpF); playersList.add(mpGb); playersList.add(mpG);
            playersList.add(mpAb); playersList.add(mpA); playersList.add(mpBb); playersList.add(mpB);

            tv1 = findViewById(R.id.textView);
            tv2 = findViewById(R.id.textView2);


        }
    }



    private void PlayStop (mpplaying mppl, Button b, MediaPlayer mediaPlayer, int padResId){

        //mppl to "identyfikator" wciśniętego przycisku. Decyduje czy wykonuje się if czy else.

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

    private void fadeIn(final MediaPlayer mediaPlayer, int padResId) {
        //https://stackoverflow.com/questions/38380495/android-studio-mediaplayer-how-to-fade-in-and-out
        final int FADE_DURATION = 15000;
        final int FADE_INTERVAL = 200;
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL;
        final int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //pobierz aktualną głośność z urządzenia

        volIn = 0.0f;
        final float deltaVolume = (currentVolume / (float) maxVolume) / (float) numberOfSteps;

        progressBarFadeIn.setProgress(0);
        progressBarFadeIn.setVisibility(View.VISIBLE);

        //przygotowanie mediaPlayera do wystartowania
        prepareMediaPlayerToStart(mediaPlayer, padResId);

        //właściwy runnable który robi Fade In
        if (mediaPlayer.isPlaying()) {
            final Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                    if (volIn >= (currentVolume / (float) maxVolume)){
                        progressBarFadeIn.setVisibility(View.INVISIBLE);
                        return; //zatrzymuje run() runnable
                    }
                 mediaPlayer.setVolume(volIn, volIn);
                 volIn = volIn + deltaVolume;
                 hFadeIn.postDelayed(this, FADE_INTERVAL);
                 setProgressOfBar(progressBarFadeIn, (int)((volIn / (currentVolume/(float)maxVolume))*100) );
                 tv1.setText((String.valueOf(volIn)));

                }
            };
            runnable1.run();
        }
    }

    private void fadeOut(){
        final int FADE_DURATION = 12000;
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
                            return; //zatrzymuje run() runnable
                        }
                        hFadeOut.postDelayed(this,FADE_INTERVAL);
                        tv2.setText((String.valueOf(volOut)));
                    }
                };
                runnable2.run();
            }
        }

    }



    private void setProgressOfBar(final ProgressBar progressBar, final int progress){
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
            }
        });
    }

    private void prepareMediaPlayerToStart(MediaPlayer mediaPlayer, int padResId){
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
    }

    //ustawia kolor wszystkich Buttonów w tablicy na 'unclicked'
    private void setAllButtonsUnclicked(Button[] btnarr){
        for (Button b: btnarr) {
            b.setBackgroundColor(getResources().getColor(R.color.unclicked));
        }
    }
    //ustawia wysztkie 'czyGra' na false
    private void setAllMpplayingFalse(){
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


    private void createAndStartClock() {
        final Runnable runnableClock = new Runnable() {
            @Override
            public void run() {
                date = System.currentTimeMillis(); //Pobierz czas teraz
                dateString = sdf.format(date); //Konwert long --do-- zadany przeze mnie format
                textTime.setText(dateString); //Wyświetl
                hClock.postDelayed(this, 60000); //Odczekaj 60 sek. i powtórz
            }
        };

        runnableClock.run();
    }


}






