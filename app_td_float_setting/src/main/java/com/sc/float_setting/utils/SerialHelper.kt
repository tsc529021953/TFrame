package com.sc.float_setting.utils

import android.os.Environment
import android.os.Handler
import android.serialport.SerialPort
import android.util.Log
import com.sc.float_setting.inter.ISerial
import com.serialportlibrary.service.impl.SerialPortBuilder
import com.serialportlibrary.service.impl.SerialPortService
import com.serialportlibrary.service.impl.SerialPortService.OnDataReceiveListener
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.*
import java.lang.Runnable
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
    var ratio = 50
    private var voltage:Int = 0
    private var lockFlag = false //锁定，滑动左侧解锁

    //private byte hx,lx,hy,ly;
    private var mHandler: Handler? = null

    private lateinit var iSerial: ISerial

    var mScope =  CoroutineScope(Dispatchers.IO + SupervisorJob())

    constructor(iSerial: ISerial) {
        this.iSerial = iSerial

    }

    fun init() {
        read_cfg()
        mScope.launch {
            try {
                delay(500)
                myPort.startPort(BaudRate)
                myPort.receiveMesage()
                sendFlag = true
                //myPort.sendMessage();                 //通过串口发送调光等级ratio
                delay(500)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            setLevel()
        }
    }

    fun sendBrightness(ratio: Int) {
        if (ratio > 100) {
            this.ratio = 100
        } else if (ratio < 0) {
            this.ratio = 0
        } else {
            this.ratio = ratio
        }
        setLevel()
        if (initfromcfg == -1) {
            write_cfg()
        }
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
                    val trueDate = ByteArray(100)
                    var str = ""
                    for (i in mBuffer.indices) {
                        trueDate[i] = mBuffer[i]
                        str += trueDate[i].toString()
                        if (i != mBuffer.size - 1) str += ","
                    }
//                    tv.setText(str)
                    Timber.i("onDataReceive tv: $str")
                    val strs: Array<String> =
                            str.toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                    if (strs.size > 14) {
                        val label1 = "温度(℃)：" + (strs[3].toInt() * 256 + strs[4].toInt() - 20).toString()
                        val dy = strs[13].toInt() * 256 + strs[14].toInt()
                        val label2 = """
                         ；
                         输出电压(V)：$dy
                         """.trimIndent()
//                        label.setText(label1 + label2)
                        Timber.i("onDataReceive label: $label1$label2")
                        iSerial?.onBrightnessChanged(dy)
                    } // DF board
                }
            })
        }

        fun closePort() {
            serialPortService!!.close()
            sendFlag = false //关闭串口以后还要把发送数据标志位置false，不然它还会发数据，而这个时候串口已经关闭了。。。
        }
    }

}
