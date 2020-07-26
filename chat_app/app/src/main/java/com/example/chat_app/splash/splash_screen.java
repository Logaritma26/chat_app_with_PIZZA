package com.example.chat_app.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.chat_app.R;
import com.example.chat_app.login.MainActivity;

import gr.net.maroulis.library.EasySplashScreen;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen easySplashScreen = new EasySplashScreen(splash_screen.this)
                .withFullScreen()
                .withBackgroundResource(R.drawable.ic_splashh_hack)
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2000);

        View splash = easySplashScreen.create();
        setContentView(splash);
    }
}