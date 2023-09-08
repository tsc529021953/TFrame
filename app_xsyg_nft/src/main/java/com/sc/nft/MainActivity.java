package com.sc.nft;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setAction("android.provider.Telephony.SECRET_CODE");
                intent.setData( Uri.parse("android secret code://66"));
                intent.setComponent(new ComponentName("com.sc.hetest","com.sc.hetest.receiver.StartReceiver"));
                sendBroadcast(intent);
//                Toast.makeText(this, "打开测试app", Toast.LENGTH_LONG).show();
            }
        });
    }
}
