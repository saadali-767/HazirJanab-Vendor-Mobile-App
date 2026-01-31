package com.example.hazirjanabvendorportal.SplashScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.hazirjanabvendorportal.Activity_RequestsList;
import com.example.hazirjanabvendorportal.R;

public class Animation_LoginSplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_login_splash_screen);

//        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_gradient_background_1);
//        findViewById(R.id.animation_login_splash_screen).startAnimation(animation);

        // Hide the status bar and make the splash screen as a full screen activity
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Taking the reference of the image to perform animation
        ImageView backgroundImage = findViewById(R.id.SplashScreenImage);
        Animation slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide);
        backgroundImage.startAnimation(slideAnimation);

        // Using a handler to start MainActivity after a delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Animation_LoginSplashScreen.this, Activity_RequestsList.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // 3000 ms delay for the splash screen
    }
}