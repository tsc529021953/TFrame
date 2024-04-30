package com.illusory.tmp;

import android.app.Activity;
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
//import com.sc.lib_float.service.PaintServiceDelegate;
import com.illusory.tmp.service.TmpServiceDelegate;
import timber.log.Timber;

import static com.nbhope.lib_frame.utils.ViewUtil.immersionTitle;

public class MainActivity extends Activity {

//    private static final int SWITCH_TIME = 12000;

//    Handler handler = new Handler();
//
//    Runnable runnable = null;
//
//    ConstraintLayout layout = null;

//    boolean isBlack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        immersionTitle(this);
//        findViewById(R.id.text2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                TmpServiceDelegate.Companion.getInstance().showFloat();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        PaintServiceDelegate.Companion.getInstance().hideFloat(0);
        finish();
    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
////        PaintServiceDelegate.Companion.getInstance().showFloat();
//    }
}
