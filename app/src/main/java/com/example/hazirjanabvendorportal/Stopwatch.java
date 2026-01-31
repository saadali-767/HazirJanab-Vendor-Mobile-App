package com.example.hazirjanabvendorportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Stopwatch{
    private Button startButton, stopButton;
    private TextView stopwatchTextView;
    private Handler handler;
    private Runnable runnable;
    private boolean isRunning = false;
    private int seconds = 0;

    private int booking_id=-1;

    public interface StopwatchCallback {
        void onStopwatchStop(int bookingId);
    }
    private StopwatchCallback callback;


    public void setup(Activity activity, int booking_id,StopwatchCallback callback) {
        // Setup views
        startButton = activity.findViewById(R.id.startButton);
        stopButton = activity.findViewById(R.id.stopButton);
        this.callback = callback;
        this.booking_id=booking_id;
        stopwatchTextView = activity.findViewById(R.id.stopwatchTextView);
        handler = new Handler();

        // Set onClickListeners
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartConfirmationDialog(activity);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStopConfirmationDialog(activity);
            }
        });
    }

    private void showStartConfirmationDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure you want to start the stopwatch?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startStopwatch();
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
    }

    private void showStopConfirmationDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure you want to stop the stopwatch?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopStopwatch(booking_id);
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
    }

    private void startStopwatch() {
        if (!isRunning) {
            isRunning = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);

            runnable = new Runnable() {
                @Override
                public void run() {
                    seconds++;
                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;
                    int secs = seconds % 60;
                    String time = String.format("%02d:%02d:%02d", hours, minutes, secs);
                    stopwatchTextView.setText(time);
                    handler.postDelayed(this, 1000);
                }
            };

            handler.post(runnable);
        }
    }

    private void stopStopwatch(int booking_id) {
        isRunning = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        handler.removeCallbacks(runnable);
        if (callback != null) {
            callback.onStopwatchStop(this.booking_id);
        }
    }
}
