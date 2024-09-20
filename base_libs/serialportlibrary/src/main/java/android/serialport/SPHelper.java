package android.serialport;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.widget.TextView;
//import androidx.core.app.ActivityCompat;
import com.serialportlibrary.service.impl.SerialPortBuilder;
import com.serialportlibrary.service.impl.SerialPortService;

import java.io.*;
import java.nio.ByteBuffer;

public class SPHelper {

    public static boolean sendFlag = false;
    SerialPort myPort = new SerialPort();           //用于发送串口同步信号
    private boolean SPstart= false;         //受控板接收到主控有效信号状态
    private boolean AutoRunFileExist=false;
    private int initfromcfg = 0;
    private String serial_dev="ttyS4";
    private int LeadTime=-200;
    private int LagTime=150;
    private int ratio=50,voltage=0;
    private boolean lockFlag=false;        //锁定，滑动左侧解锁
    //private byte hx,lx,hy,ly;
    private Handler mHandler;
    //    private boolean SyncFlag=false;
//    private String HostIP="192.168.1.105";
//    private int HostSocket=5780;
//    private boolean moveflag=false;
//    private boolean DataReceived=false;
//    TextView tv,label;
    private static final int POLYNOMIAL = 0xA001;
    public SerialPortService.OnDataReceiveListener onDataReceiveListener = null;
    public byte[] toByteArray(long value) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(value).array();
    }
    public static int calculate(byte[] data) {
        int crc = 0xFFFF;
        for (byte b : data) {
            crc ^= b & 0xFF;
            for (int i = 0; i < 8; i++) {
                if ((crc & 1) == 1) {
                    crc = (crc >>> 1) ^ POLYNOMIAL;
                } else {
                    crc = crc >>> 1;
                }
            }
        }
        return crc;
    }
    class SerialPort {
        private byte[] mBuffer;
        private Handler handler;
        SerialPortService serialPortService;

        //开启串口
        public void startPort() {
            serialPortService = new SerialPortBuilder()
                    .setTimeOut(100L)
                    .setBaudrate(115200)
                    .setDevicePath("dev/"+serial_dev)
                    .createService();
            SPstart=true;
            serialPortService.isOutputLog(true);
        }


        public long byteArrayToLong(byte[] bytes) {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.put(bytes, 0, bytes.length);
            buffer.flip();
            return buffer.getLong();
        }
        public byte[] hexStringToBytes(String hexString)
        {
            if ((hexString == null) || (hexString.equals(""))) {
                return null;
            }
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; ++i) {
                int pos = i * 2;
                d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)]));
            }
            return d;
        }
        private byte charToByte(char c)
        {
            return (byte)"0123456789ABCDEF".indexOf(c);
        }
        public String byte2HexStr(byte[] b, int length)
        {
            String hs = "";
            String stmp = "";
            for (int n = 0; n < length; ++n) {
                stmp = Integer.toHexString(b[n] & 0xFF);
                if (stmp.length() == 1)
                    hs = hs + "0" + stmp;
                else {
                    hs = hs + stmp;
                }
            }
            return hs.toUpperCase();
        }

        public void sendMessage() {
            //发数据的线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (sendFlag) {
                        byte[] sendData0=toByteArray(141);
                        byte[] sendData1=toByteArray(ratio);
                        byte[] sendData2={1,6,0,sendData0[7],0,sendData1[7]};
                        byte[] sendData3=toByteArray(calculate(sendData2));
                        byte[] sendData={1,6,0,sendData0[7],0,sendData1[7],sendData3[7],sendData3[6]};
                        //byte[] sendData={1,6,0,3};
                        try {
                            serialPortService.sendData(sendData);
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        public void sendMessageOnce(byte[] sendData){
            try {
                serialPortService.sendData(sendData);
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void receiveMesage() {

            handler = new Handler(); //创建主线程的handler  用于接收数据时更新UI
            serialPortService.setOnDataReceiveListener(new SerialPortService.OnDataReceiveListener() {
                @Override
                public void onDataReceive(byte[] buffer, int size) {
                    Log.d("tag", "进入数据监听事件 " );
                    mBuffer = buffer;
                    if (onDataReceiveListener != null) {
                        onDataReceiveListener.onDataReceive(buffer, size);
                    }
//                    handler.post(runnable);//利用handler将数据传递到主线程
                }

//                //开线程更新UI
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        byte trueDate[]=new byte[100];
//                        String str="";
//                        for(int i=0;i<mBuffer.length;i++){
//                            trueDate[i]=mBuffer[i];
//                            str+=Integer.toString(trueDate[i]);
//                            if(i!=mBuffer.length-1)str+=",";
//                        }
//                        tv.setText(str);
//                    }
//                };
            });
        }

        public void closePort() {
            serialPortService.close();
            sendFlag = false;//关闭串口以后还要把发送数据标志位置false，不然它还会发数据，而这个时候串口已经关闭了。。。
        }
    }


    public void read_cfg() {
        try {
            File urlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "THREDIM_MEDIA" + File.separator + "串口同步配置.txt");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            initfromcfg=Integer.parseInt(br.readLine());
            serial_dev=br.readLine();
            LeadTime=Integer.parseInt(br.readLine());
            LagTime=Integer.parseInt(br.readLine());
            ratio=Integer.parseInt(br.readLine());
            br.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write_cfg() {
        try {
            File urlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "THREDIM_MEDIA" + File.separator + "串口同步配置.txt");
            FileWriter writer = new FileWriter(urlFile);
            writer.write(Integer.toString(initfromcfg)+"\n");
            writer.write(serial_dev+"\n");
            writer.write(Integer.toString(LeadTime)+"\n");
            writer.write(Integer.toString(LagTime)+"\n");
            writer.write(ratio+"\n");
            writer.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void requestPermissions() {
//        try {
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//                String[] PERMISSIONS_STORAGE = {
//                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                };
//
//                int permission = ActivityCompat.checkSelfPermission(this,
//                        "android.permission.READ_EXTERNAL_STORAGE");
//                /***
//                 * checkSelfPermission返回两个值
//                 * 有权限: PackageManager.PERMISSION_GRANTED
//                 * 无权限: PackageManager.PERMISSION_DENIED
//                 */
//                if (permission != PackageManager.PERMISSION_GRANTED) {
//                    // 没有写的权限，去申请写的权限，会弹出对话框
//                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
//                }
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void setLevel() {
        byte[] sendData0 = toByteArray(141);
        byte[] sendData1 = toByteArray(ratio);
        byte[] sendData2 = {1, 6, 0, sendData0[7], 0, sendData1[7]};
        byte[] sendData3 = toByteArray(calculate(sendData2));
        byte[] sendData = {1, 6, 0, -115, 0, sendData1[7], sendData3[7], sendData3[6]};
        myPort.sendMessageOnce(sendData);
    }
//    public void getLevel(){
//        //tv.setText("");
//        String str,label1,label2;
//        String[] strs;
//        byte[] sendData1 = {1, 3, 0, -115, 0, 1, 20, 33};
//        try {
//            myPort.sendMessageOnce(sendData1);
//            Thread.sleep(500);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        str=tv.getText().toString();
//        strs=str.split(",");
//        if(strs.length==7&&(strs[0]+strs[1]+strs[2]).equals("132")){
//            ratio=Integer.parseInt(strs[4]);
//            label1="当前调光等级(%)："+Integer.toString(ratio);
//        }
//        else label1="当前调光等级(%)：NA";
//        byte[] sendData2 = {1, 3, 0, -117, 0, 1, -12, 32};
//        try {
//            myPort.sendMessageOnce(sendData2);
//            Thread.sleep(500);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        str=tv.getText().toString();
//        strs=str.split(",");
//        if(strs.length==7&&(strs[0]+strs[1]+strs[2]).equals("132")){
//            voltage=Integer.parseInt(strs[3])*256+Integer.parseInt(strs[4]);
//            label2="；输出电压幅度(mV)："+Integer.toString(voltage);
//        }
//        else label2="；输出电压幅度(mV)：NA";
////        label.setText(label1+label2);
//        System.out.println(label1 + " " + label2);
//    }

    /*
        请确定权限授予完成之后再调用
     */
    public void init() {
//        requestPermissions();
        read_cfg();
        try {
            Thread.sleep(500);
            myPort.startPort();
            myPort.receiveMesage();
            sendFlag = true;
            //myPort.sendMessage();                 //通过串口发送调光等级ratio
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if(initfromcfg==0)getLevel();           //0:从控制板读取初始ratio；-1:ratio初始值按配置文件设定，并随时保存；其它:按配置文件设定，不保存
//        else
            setLevel();
    }

    public void sendCommand(int ratio) {
        if (ratio > 100) this.ratio = 100;
        else if (ratio < 0) this.ratio = 0;
        else this.ratio = ratio;
        setLevel();
        //getLevel();
        if(initfromcfg==-1)write_cfg();
    }

    public void release() {
        myPort.closePort();
    }

}
