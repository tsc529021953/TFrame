//package com.sc.tframe;
//
//import static com.nbhope.lib_frame.utils.ViewUtil.immersionTitle;
//
//import android.os.Handler;
//import android.view.View;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import com.sc.lib_system.util.CommandUtil;
////import com.sc.tframe.module.dts.DTSTest;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int SWITCH_TIME = 12000;
//
//    Handler handler = new Handler();
//
//    Runnable runnable = null;
//
//    ConstraintLayout layout = null;
//
//    boolean isBlack = false;
//
//    DTSTest dtsTest = new DTSTest();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        immersionTitle(this);
//        hideSystemUI();
//
////        tTest();
//        dtsTest.init(this);
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
//    }
//
//    private void hideSystemUI() {
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        dtsTest.release();
//    }
//
//    void tTest() {
//        try {
//            CommandUtil.CommandResult res = CommandUtil.runCommand("setprop service.adb.tcp.port 5555");
//            System.out.println("CommandResult1 " + res.toString());
//            Process process = Runtime.getRuntime().exec("su");
//            res = CommandUtil.runCommand("stop adbd", true);
//            System.out.println("CommandResult2 " + res.toString());
//            res = CommandUtil.runCommand("start adbd", true);
//            System.out.println("CommandResult3 " + res.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    void heTest() {
//        findViewById(R.id.text2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Intent intent= new Intent();
////                intent.setAction("android.provider.Telephony.SECRET_CODE");
////                intent.setData( Uri.parse("android secret code://66"));
////                intent.setComponent(new ComponentName("com.sc.hetest","com.sc.hetest.com.sc.nft.receiver.StartReceiver"));
////                sendBroadcast(intent);
////                Toast.makeText(this, "打开测试app", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}
