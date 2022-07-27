package com.sc.tframe;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
//import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Timber.i("TFTAG HelloBro");
    }
}