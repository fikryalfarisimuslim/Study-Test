package com.sunway.averychoke.studywifidirect3.controller;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.sunway.averychoke.studywifidirect3.R;
import com.sunway.averychoke.studywifidirect3.util.PreferenceHelper;

public class IntroScreen extends AppIntro {

    PreferenceHelper mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_intro_screen);

        mPref = new PreferenceHelper(IntroScreen.this);

        addSlide(new Intro_Slide_1());
        addSlide(new Intro_Slide_2());
        addSlide(new Intro_Slide_3());

        showStatusBar(false);
        showSkipButton(true);
        setProgressButtonEnabled(true);

        setBarColor(ContextCompat.getColor(IntroScreen.this, R.color.white));
        setSeparatorColor(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimaryLight));

        setColorDoneText(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimary));
        setColorSkipButton(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimary));
        setNextArrowColor(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimary));

        setIndicatorColor(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimary),
                ContextCompat.getColor(IntroScreen.this, R.color.iconsLight));
    }


    //*********** Called when the active Slide Changes ********//

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        mPref.setFirstTime(false);
        /*if(mPref.getNIM().equals("nim") ){
            startActivity(new Intent(getBaseContext(), InputName.class));
        }else{
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }*/
        startActivity(new Intent(getBaseContext(), MainActivity.class));

        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }


    //*********** Called when the Skip Button pressed on IntroScreen ********//

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        mPref.setFirstTime(false);

        /*if(mPref.getNIM().equals("nim") ){
            startActivity(new Intent(getBaseContext(), InputName.class));
        }else{
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }*/
        startActivity(new Intent(getBaseContext(), MainActivity.class));

        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);

    }


    //*********** Called when the Activity has detected the User pressed the Back key ********//

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mPref.setFirstTime(false);
        /*
        if(mPref.getNIM().equals("nim") ){
            startActivity(new Intent(getBaseContext(), InputName.class));
        }else{
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }*/
        startActivity(new Intent(getBaseContext(), MainActivity.class));

        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);

    }
}
