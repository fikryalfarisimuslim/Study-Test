package com.sunway.averychoke.studywifidirect3.controller;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.util.PreferenceHelper;

public class SplashScreen extends AppCompatActivity {

    PreferenceHelper mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mPref = new PreferenceHelper(getApplicationContext());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if(mPref.getFirstTime()){
                    startActivity(new Intent(getBaseContext(), IntroScreen.class));
                }else/* if(mPref.getNIM().equals("nim") ){
                    startActivity(new Intent(getBaseContext(), InputName.class));
                }else*/{
                    Intent i1 = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i1);
                }
                finish();
                /*Intent i1 = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i1);
                finish();*/
            }
        }, 3000);



    }
}
