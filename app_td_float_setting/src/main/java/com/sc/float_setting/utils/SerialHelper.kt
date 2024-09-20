package com.sc.float_setting.utils

import android.os.Environment
import android.os.Handler
import android.serialport.SerialPort
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.serialtest.R
import com.sc.float_setting.inter.ISerial
import com.serialportlibrary.service.impl.SerialPortBuilder
import com.serialportlibrary.service.impl.SerialPortService
import com.serialportlibrary.service.impl.SerialPortService.OnDataReceiveListener
import java.io.*
import java.nio.ByteBuffer
import java.util.*

class SerialHelper {

    companion object {
        const val POLYNOMIAL: Int = 0xA001
    }

    var sendFlag: Boolean = false
    var myPort: SerialPort = SerialPort() //用于发送串口同步信号
    private var SPstart = false //受控板接收到主控有效信号状态
    private var AutoRunFileExist = false
    private var initfromcfg = 0
    private var serial_dev = "ttyS4"
    private var BaudRate = 9600 //DF:9600;BOE:115200
    private var LeadTime = -200
    private var LagTime = 150
    private var ratio = 50
    private var voltage:Int = 0
    private var lockFlag = false //锁定，滑动左侧解锁

    //private byte hx,lx,hy,ly;
    private var mHandler: Handler? = null

    private lateinit var iSerial: ISerial

    constructor(iSerial: ISerial) {
        this.iSerial = iSerial

    }

    fun init() {
        read_cfg()
        try {
            Thread.sleep(500)
            myPort.startPort(BaudRate)
            myPort.receiveMesage()
            sendFlag = true
            //myPort.sendMessage();                 //通过串口发送调光等级ratio
            Thread.sleep(500)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        tv = findViewById<TextView>(R.id.recvData)
        iv = findViewById<ImageView>(R.id.imageView)
        label = findViewById<TextView>(R.id.textView2)
        if (initfromcfg == 0) getLevel() //0:从控制板读取初始ratio；-1:ratio初始值按配置文件设定，并随时保存；其它:按配置文件设定，不保存
        else setLevel()
    }

    fun release() {
        myPort.closePort()
    }

    fun toByteArray(value: Long): ByteArray {
        return ByteBuffer.allocate(java.lang.Long.SIZE / java.lang.Byte.SIZE).putLong(value).array()
    }

    fun calculate(data: ByteArray): Int {
        var crc = 0xFFFF
        for (b in data) {
            crc = crc xor (b.toInt() and 0xFF)
            for (i in 0..7) {
                crc = if ((crc and 1) == 1) {
                    crc ushr 1 xor POLYNOMIAL
                } else {
                    crc ushr 1
                }
            }
        }
        return crc
    }

    fun read_cfg() {
        try {
            val urlFile = File(
                Environment.getExternalStorageDirectory().absolutePath
                        + File.separator + "THREDIM_MEDIA" + File.separator + "串口同步配置.txt"
            )
            val isr = InputStreamReader(FileInputStream(urlFile), "UTF-8")
            val br = BufferedReader(isr)
            initfromcfg = br.readLine().toInt()
            serial_dev = br.readLine()
            LeadTime = br.readLine().toInt()
            LagTime = br.readLine().toInt()
            ratio = br.readLine().toInt()
            br.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun write_cfg() {
        try {
            val urlFile = File(
                ((Environment.getExternalStorageDirectory().absolutePath
                        + File.separator + "THREDIM_MEDIA" + File.separator + "串口同步配置.txt"))
            )
            val writer = FileWriter(urlFile)
            writer.write(initfromcfg.toString() + "\n")
            writer.write(serial_dev + "\n")
            writer.write(LeadTime.toString() + "\n")
            writer.write(LagTime.toString() + "\n")
            writer.write(ratio.toString() + "\n")
            writer.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    //    protected void UDPClientReceiveAction(){
    //        new Thread(){
    //            public void run() {
    //                DatagramSocket ds;
    //                try {
    //                    ds = new DatagramSocket();
    //                    int i = 0;
    //                    while (SyncFlag) {
    //                        i++;
    //                        try {
    //                            Thread.sleep(30);
    //                        } catch (InterruptedException e) {
    //                            e.printStackTrace();
    //                        }
    //                        DataReceived=false;
    //                        byte[] buf = new byte[1024];
    //                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
    //                        ds.receive(packet);
    //                        DataReceived=true;
    //                        byte data[] = packet.getData();// 接收的数据
    //                        byte newArray[] = Arrays.copyOfRange(data, 0, packet.getLength());      //截取有效的部分
    //                    }
    //                    ds.close();
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //        }.start();
    //    }
    //    protected void UDPClientSendAction(){
    ////        new MyThread("my thread").start();
    //        new Thread(){
    //            public void run() {
    //                DatagramSocket ds;
    //                Log.d("Read IP configuration", HostIP);
    //                try {
    //                    ds = new DatagramSocket();
    //                    int i = 0;
    //                    while (SyncFlag) {
    //                        i++;
    //                        try {
    //                            Thread.sleep(30);
    //                        } catch (InterruptedException e) {
    //                            e.printStackTrace();
    //                        }
    //                        mHandler.sendEmptyMessage(0);
    ////                        String str = "Guest进度:"+Long.toString(mPvMediaPlayeView.getCurrentPosition());
    ////                        byte[] strbyte = str.getBytes("gbk");
    //                        byte[] strbyte={(byte)0x89,hx,lx,hy,ly,(byte)0x64};
    //                        //构建数据报（内容+地址）
    //                        DatagramPacket dp1 = new DatagramPacket(strbyte, strbyte.length, InetAddress.getByName(HostIP), HostSocket);
    //                        //把数据报发送出去
    //                        if(moveflag){
    //                            moveflag=false;
    //                            ds.send(dp1);
    //                        }
    ////                        Log.d(TAG,str);
    //                    }
    //                    ds.close();
    //                } catch (IOException e) {
    //                    // TODO Auto-generated catch block
    //                    e.printStackTrace();
    //                }
    //            }
    //        }.start();
    //    }
    //    private void readIPLAG(){
    //        try {
    //            File urlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
    //                    + File.separator + "THREDIM_MEDIA" + File.separator + "网络同步配置.txt");
    //            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
    //            BufferedReader br = new BufferedReader(isr);
    //            HostIP=br.readLine();
    //        }catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    fun setLevel() {
//        byte[] sendData0 = toByteArray(141);
//        byte[] sendData1 = toByteArray(ratio);
//        byte[] sendData2 = {1, 6, 0, sendData0[7], 0, sendData1[7]};
//        byte[] sendData3 = toByteArray(calculate(sendData2));
//        byte[] sendData = {1, 6, 0, -115, 0, sendData1[7], sendData3[7], sendData3[6]};
        ////BOE
        val sendData0 = toByteArray(85) //0x55
        val sendData1 = toByteArray(ratio.toLong())
        val sendData2 = toByteArray(170) //0xaa
        val sendData = byteArrayOf(sendData0[7], sendData2[7], 0, sendData1[7], 0)
        ////DF
        myPort.sendMessageOnce(sendData)
    }

    fun getLevel() {
        val label1: String
        val label2: String
        var strs: Array<String>
        val sendData1 = byteArrayOf(1, 3, 0, -115, 0, 1, 20, 33)
        try {
            myPort.sendMessageOnce(sendData1)
            Thread.sleep(500)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        //tv.setText("");
        var str: String = tv.getText().toString()
        strs = str.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if ((strs.size == 7) && (strs[0] + strs[1] + strs[2]) == "132") {
            ratio = strs[4].toInt()
            label1 = "当前调光等级(%)：$ratio"
        } else label1 = "当前调光等级(%)：NA"
        val sendData2 = byteArrayOf(1, 3, 0, -117, 0, 1, -12, 32)
        try {
            myPort.sendMessageOnce(sendData2)
            Thread.sleep(500)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        str = tv.getText().toString()
        strs = str.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if ((strs.size == 7) && (strs[0] + strs[1] + strs[2]) == "132") {
            voltage = strs[3].toInt() * 256 + strs[4].toInt()
            label2 = "；输出电压幅度(mV)：$voltage"
        } else label2 = "；输出电压幅度(mV)：NA"
        label.setText(label1 + label2) //BOE驱动板需要主动查询
    }

    inner class SerialPort {
        private lateinit var mBuffer: ByteArray
        private var handler: Handler? = null
        var serialPortService: SerialPortService? = null

        //开启串口
        fun startPort(br: Int) {
            serialPortService = SerialPortBuilder()
                .setTimeOut(100L)
                .setBaudrate(br)
                .setDevicePath("dev/$serial_dev")
                .createService()
            SPstart = true
            serialPortService?.isOutputLog(true)
        }


        fun byteArrayToLong(bytes: ByteArray): Long {
            val buffer = ByteBuffer.allocate(8)
            buffer.put(bytes, 0, bytes.size)
            buffer.flip()
            return buffer.getLong()
        }

        fun hexStringToBytes(hexString: String?): ByteArray? {
            var hexString = hexString
            if ((hexString == null) || (hexString == "")) {
                return null
            }
            hexString = hexString.uppercase(Locale.getDefault())
            val length = hexString.length / 2
            val hexChars = hexString.toCharArray()
            val d = ByteArray(length)
            for (i in 0 until length) {
                val pos = i * 2
                d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
            }
            return d
        }

        private fun charToByte(c: Char): Byte {
            return "0123456789ABCDEF".indexOf(c).toByte()
        }

        fun byte2HexStr(b: ByteArray, length: Int): String {
            var hs = ""
            var stmp = ""
            for (n in 0 until length) {
                stmp = Integer.toHexString(b[n].toInt() and 0xFF)
                hs = if (stmp.length == 1) hs + "0" + stmp
                else {
                    hs + stmp
                }
            }
            return hs.uppercase(Locale.getDefault())
        }

        fun sendMessageOnce(sendData: ByteArray?) {
            try {
                serialPortService!!.sendData(sendData)
                Thread.sleep(100)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun receiveMesage() {
            handler = Handler() //创建主线程的handler  用于接收数据时更新UI
            serialPortService!!.setOnDataReceiveListener(object : OnDataReceiveListener {
                override fun onDataReceive(buffer: ByteArray, size: Int) {
                    Log.d("tag", "进入数据监听事件 ")
                    mBuffer = buffer
                    handler!!.post(runnable) //利用handler将数据传递到主线程
                }

                //开线程更新UI
                var runnable: Runnable = Runnable {
//                    val trueDate = ByteArray(100)
//                    var str = ""
//                    for (i in mBuffer.indices) {
//                        trueDate[i] = mBuffer[i]
//                        str += trueDate[i].toString()
//                        if (i != mBuffer.size - 1) str += ","
//                    }
//                    tv.setText(str)
//                    val strs: Array<String> =
//                        tv.getText().toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }
//                            .toTypedArray()
//                    if (strs.size > 14) {
//                        val label1 = "温度(℃)：" + (strs[3].toInt() * 256 + strs[4].toInt() - 20).toString()
//                        val label2 = """
//                         ；
//                         输出电压(V)：${strs[13].toInt() * 256 + strs[14].toInt()}
//                         """.trimIndent()
//                        label.setText(label1 + label2)
//                    } // DF board
                }
            })
        }

        fun closePort() {
            serialPortService!!.close()
            sendFlag = false //关闭串口以后还要把发送数据标志位置false，不然它还会发数据，而这个时候串口已经关闭了。。。
        }
    }

}