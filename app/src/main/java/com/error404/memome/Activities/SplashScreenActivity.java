package com.error404.memome.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.error404.memome.R;
import com.error404.memome.Utilities.Values;

//Activity SplashScreenActivity è l'Activity principale che dopo SPLASHTIMEOUT secondi fa la finish iniziando l'activity MainActivity
public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, Values.SPLASH_TIME_OUT);
        }
    @Override
    public void onBackPressed() {
        //non fare niente
    }
}
