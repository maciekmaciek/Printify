package com.maciekwski.printify.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.maciekwski.printify.R;

public class SplashScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Uruchom wątek otwierający główną aktywność
        ActivityStarter starter = new ActivityStarter();
        starter.start();
    }

    private class ActivityStarter extends Thread {

        @Override
        public void run() {
            try {
                // tutaj wrzucamy wszystkie akcje potrzebne podczas ładowania aplikacji
                Thread.sleep(getResources().getInteger(R.integer.splash_time));
            } catch (Exception e) {
                Log.e("SplashScreen", e.getMessage());
            }

            // Włącz główną aktywność
            Intent intent = new Intent(SplashScreen.this, StartActivity.class);
            SplashScreen.this.startActivity(intent);
            SplashScreen.this.finish();
        }
    }

}