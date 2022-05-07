package by.slizh.lab_4.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Timer;

import by.slizh.lab_4.R;
import pl.droidsonroids.gif.GifImageView;

public class SplashScreenActivity extends AppCompatActivity {

    private GifImageView gifImageView;
    private Timer timer = new Timer();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //// TODO: 06.05.2022 Откоментить код ниже (гифка на экране загрузки)
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
//
//        gifImageView = findViewById(R.id.appGif);
//        ((GifDrawable)gifImageView.getDrawable()).setLoopCount(1);
//
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(() -> {
//                    if (((GifDrawable) gifImageView.getDrawable()).isAnimationCompleted()) {
//                        timer.cancel();
//                        timer = null;
//
//                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
//                        startActivity(intent);
//                    }
//                });
//            }
//        }, 0, 20);
    }
}