package com.example.hazirjanabvendorportal;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.util.Arrays;

public class NotificationBadge extends Activity{
    String[] Name;
    int[] Counter;
    private static NotificationBadge instance;
    private Handler handler;
    Activity activity;

    private NotificationBadge(Activity activity) {
        Name = new String[5];
        for(int i=0; i<5; i++){
            switch (i){
                case 0:
                    Name[i] = "Home";
                    break;
                case 1:
                    Name[i] = "Accepted_services";
                    break;
                case 2:
                    Name[i] = "Completed_Services";
                    break;
                case 3:
                    Name[i] = "Rewards";
                    break;
                case 4:
                    Name[i] = "Support";
                    break;
            }
        }
        Counter = new int[5];
        Arrays.fill(Counter, 0);
        this.activity = activity;
        Log.d("NotificationBadge", "Activity Name: "+activity.getLocalClassName());
    }

    public static synchronized NotificationBadge getInstance(Activity activity) {
        if (instance == null) {
            instance = new NotificationBadge(activity);
            Log.d("NotificationBadge", "New instance created");
        }
        Log.d("NotificationBadge", "Returning instance");
        instance.activity = activity;
        Log.d("NotificationBadge", "Activity Name: "+activity.getLocalClassName());
        return instance;
    }

    public void setCounter(String name, int counter){
        for(int i=0; i<5; i++){
            if(Name[i].equals(name)){
                Counter[i] = counter;
                setToolbarBadge();
                break;
            }
        }
    }

    public int getCounter(String name){
        for(int i=0; i<5; i++){
            if(Name[i].equals(name)){
                return Counter[i];
            }
        }
        return 0;
    }

    public void incrementCounter(String name){
        for(int i=0; i<5; i++){
            if(Name[i].equals(name)){
                Log.d("NotificationBadge", "Incrementing counter for "+name);
                Counter[i]++;
                setToolbarBadge();
                break;
            }
        }
    }

    public void decrementCounter(String name){
        for(int i=0; i<5; i++){
            if(Name[i].equals(name)){
                Counter[i]--;
                setToolbarBadge();
                break;
            }
        }
    }

    public int getTotalCount(){
        int total = 0;
        for(int i=0; i<5; i++){
            total += Counter[i];
        }
        return total;
    }

    public void setToolbarBadge(){
        //do not include count of the current activity
        switch (activity.getLocalClassName()){
            case "Activity_RequestsList":
                Log.d("NotificationBadge", "Setting count for Home");
                Log.d("NotificationBadge", "Total count: "+getTotalCount());
                Toolbar.getInstance().setCount(getTotalCount()-getCounter("Home"));
                break;
            case "Activity_AcceptedBookingsList":
                Log.d("NotificationBadge", "Setting count for Accepted Services");
                Log.d("NotificationBadge", "Total count: "+getTotalCount());
                Toolbar.getInstance().setCount(getTotalCount()-getCounter("Accepted_services"));
                break;
            case "Activity_CompletedOrdersList":
                Log.d("NotificationBadge", "Setting count for Completed Services");
                Log.d("NotificationBadge", "Total count: "+getTotalCount());
                Toolbar.getInstance().setCount(getTotalCount()-getCounter("Completed_Services"));
                break;
            case "Activity_Rewards":
                Toolbar.getInstance().setCount(getTotalCount()-getCounter("Rewards"));
                break;
            case "Activity_Support":
                Log.d("NotificationBadge", "Setting count for Support");
                Log.d("NotificationBadge", "Total count: "+getTotalCount());
                Toolbar.getInstance().setCount(getTotalCount()-getCounter("Support"));
                break;
            default:
                Log.d("NotificationBadge", "Activity not found\n Activity Name: "+activity.getLocalClassName());
                Log.d("NotificationBadge", "Total count: "+getTotalCount());
                Toolbar.getInstance().setCount(getTotalCount());

        }
    }
}
