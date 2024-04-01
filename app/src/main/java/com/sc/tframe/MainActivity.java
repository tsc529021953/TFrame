package com.sc.tframe;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import timber.log.Timber;

import static com.nbhope.lib_frame.utils.ViewUtil.immersionTitle;

public class MainActivity extends AppCompatActivity {

    private static final int SWITCH_TIME = 12000;

    Handler handler = new Handler();

    Runnable runnable = null;

    ConstraintLayout layout = null;

    boolean isBlack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        immersionTitle(this);

//        tTest();
    }

    void tTest() {
        findViewById(R.id.text2).setVisibility(View.GONE);
        layout = findViewById(R.id.bg_ly);
        layout.setBackgroundColor(Color.BLACK);
        runnable = new Runnable() {
            @Override
            public void run() {
                Timber.i("TTAG SWITCH_TIME ");
                layout.setBackgroundColor(isBlack ? Color.WHITE : Color.BLACK);
                isBlack = !isBlack;
                handler.postDelayed(runnable, SWITCH_TIME);
            }
        };
        handler.postDelayed(runnable, SWITCH_TIME);
    }

    void heTest() {
        findViewById(R.id.text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setAction("android.provider.Telephony.SECRET_CODE");
                intent.setData( Uri.parse("android secret code://66"));
                intent.setComponent(new ComponentName("com.sc.hetest","com.sc.hetest.com.sc.nft.receiver.StartReceiver"));
                sendBroadcast(intent);
//                Toast.makeText(this, "打开测试app", Toast.LENGTH_LONG).show();
            }
        });
    }
}
