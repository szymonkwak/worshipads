package szymon.kwak.worshipads;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String PACKAGE_NAME;

    PlayerObject playerC, playerDb, playerD, playerEb, playerE;
    List<PlayerObject> playersObjectList = new ArrayList<>();

    Button[] Buttons = new Button[12];

    AudioManager am;
    MediaPlayer mpC, mpDb, mpD, mpEb, mpE, mpF, mpGb, mpG, mpAb, mpA, mpBb, mpB;

    ProgressBar progressBarFadeIn;

    TextView textTime;
    Handler hClock = new Handler();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    long date;
    String dateString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); //ukryj TitleBar
        setContentView(R.layout.activity_main);

        //Zmiana koloru navigationBar'a
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));

        //inicjalizacja AudioManager'a w celu przekazania go do fadeIn lub fadeOut, żeby pobrać głośność z urządzenia
        am = (AudioManager) getSystemService(AUDIO_SERVICE);

        //potrzebuję tego do określenia ścieżki PADa w PlayerObject - metoda prepareMediaPlayerToStart

        PACKAGE_NAME = getPackageName();

        progressBarFadeIn = findViewById(R.id.progressBarFadeIn);
        progressBarFadeIn.setVisibility(View.INVISIBLE);
        textTime = findViewById(R.id.textTime);
        createAndStartClock();


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


        playerC = new PlayerObject(mpC,Buttons[0],R.raw.cmaj,this);
        playerD = new PlayerObject(mpD,Buttons[2],R.raw.dmaj,this);
        playerE = new PlayerObject(mpE,Buttons[4],R.raw.emaj,this);

        playersObjectList.add(playerC);
        //playersObjectList.add(playerDb);
        playersObjectList.add(playerD);
        //playersObjectList.add(playerEb);
        playersObjectList.add(playerE);

    }



    private void PlayStop (PlayerObject playerX, List<PlayerObject> playerObjectList){

        // Lista playerObjectList jest potrzebna, żeby sprawdzić, czy nie trwa właśnie fadeIn albo fadeOut
        // player X to podany do metody konkretny PlayerObject powiązany do przycisku

        boolean fadingInProcess = false;

        for (PlayerObject playerObj : playerObjectList) {
            if (playerObj.isFadingOut() || playerObj.isFadingIn()){ //Sprawdzam czy którykolwiek gra
                fadingInProcess = true;
                break;
            }
        }

        if (fadingInProcess){
            Toast.makeText(this,"Poczekaj ♫ Trwa wczytywanie PADa ♪",Toast.LENGTH_LONG).show();
        }
        else if (!fadingInProcess){

            if (playerX.isPlaying()){
                //Jeżeli kliknięty player gra, to:
                playerX.fadeOut(am);
                playerX.button.setBackgroundColor(getResources().getColor(R.color.unclicked));
            }
            else if (!playerX.isPlaying()){
                setAllButtonsUnclicked(Buttons); //wszystkie przyciski ustaw jako 'unclicked'
                playerX.button.setBackgroundColor(getResources().getColor(R.color.clicked)); //tylko podany zrób 'clicked'

                for (PlayerObject playerObj2 : playerObjectList) { //Znajdź czy którykolwiek gra i zrób z nim fadeOut
                    if (playerObj2.isPlaying()){
                        playerObj2.fadeOut(am);
                        break;
                    }
                }

                try { //Poczekaj 500milsek
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                playerX.fadeIn(am,progressBarFadeIn); //Wczytaj kliknięty player
            }
        }
    }


    //ustawia kolor wszystkich Buttonów w tablicy na 'unclicked'
    private void setAllButtonsUnclicked(Button[] btnarr){
        for (Button b: btnarr) {
            b.setBackgroundColor(getResources().getColor(R.color.unclicked));
        }
    }


    public void btnCclick (View view){
        PlayStop(playerC,playersObjectList);
    }
    public void btnDbclick (View view){

    }
    public void btnDclick (View view){
        PlayStop(playerD,playersObjectList);
    }
    public void btnEclick (View view){
        PlayStop(playerE,playersObjectList);
    }

    public void btnStopClick (View view){
        for (PlayerObject playerObj : playersObjectList) {
            if (playerObj.isPlaying()){
                playerObj.stopPlaying();
                playerObj.setFadingIn(false);
                playerObj.setFadingOut(false);
                setAllButtonsUnclicked(Buttons);
                progressBarFadeIn.setProgress(0);
                progressBarFadeIn.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void createAndStartClock() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                date = System.currentTimeMillis(); //Pobierz czas teraz
                dateString = sdf.format(date); //Konwert long --do-- zadany przeze mnie format
                textTime.setText(dateString); //Wyświetl
                hClock.postDelayed(this, 60000); //Odczekaj 60 sek. i powtórz
            }
        }).start();
    }


}






