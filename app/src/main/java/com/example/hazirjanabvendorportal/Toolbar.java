package com.example.hazirjanabvendorportal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Toolbar{
    private DrawSideBar drawSideBar; // Add this line
    private static int notificationCount=0;
    private static Toolbar instance;
    private TextView notificationBadge;

    private Toolbar() {

    }

    // Method to get the singleton instance
    public static synchronized Toolbar getInstance() {
        if (instance == null) {
            instance = new Toolbar();
            Log.d("Toolbar", "New instance created");
        }
        Log.d("Toolbar", "Returning instance");
        return instance;
    }

    public void setup(Activity activity){

        drawSideBar = new DrawSideBar(activity);
        drawSideBar.setup(activity);

        Log.d("Toolbar", "Setting up toolbar");

        if(activity instanceof Activity_Profile){
            ImageView ivProfile = activity.findViewById(R.id.ivProfile);
            ivProfile.setVisibility(View.GONE);
        }else {
            ImageView ivProfile = activity.findViewById(R.id.ivProfile);
            ivProfile.setVisibility(View.VISIBLE);
        }

        // delay this line for 2 secs
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Toolbar", "HEREEE");
                NotificationBadge.getInstance(activity).setToolbarBadge();
            }
        }, 1000);



        notificationBadge = activity.findViewById(R.id.notification_badge);
        notificationBadge.setText(String.valueOf(notificationCount));

        ImageView ivProfile = activity.findViewById(R.id.ivProfile);
        if (ivProfile != null) {
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, Activity_Profile.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }
    }

    public void setCount(int count){
        notificationCount=count;
        if (notificationCount == 0) {
            notificationBadge.setVisibility(View.GONE);
        }
        else {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(String.valueOf(notificationCount));
        }
    }

    public void incrementNotificationCount() {
        if (notificationCount == 0) {
            notificationBadge.setVisibility(View.VISIBLE); // Show badge if count becomes greater than zero
        }
        notificationBadge.setText(String.valueOf(++notificationCount));
    }

    public void decrementNotificationCount() {
        notificationBadge.setText(String.valueOf(--notificationCount));
        if (notificationCount == 0) {
            notificationBadge.setVisibility(View.GONE); // Hide badge if count becomes zero
        }
    }
}
