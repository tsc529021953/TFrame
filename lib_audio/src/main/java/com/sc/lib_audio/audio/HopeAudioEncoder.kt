package com.sc.lib_audio.audio

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import timber.log.Timber
import java.io.IOException
import java.nio.ByteBuffer

/**
 *Created by ywr on 2021/11/10 15:38
 * aac 编码
 */
abstract class HopeAudioEncoder : AudioRecorderReceiver {

    private val sampleRate_interphone = 8000
    private val audioChannelNum_interphone = 1
    private val audioBitRate = 12200  //和sampleRate存在一定的对应关系谨慎修改



    private lateinit var mediaEncode: MediaCodec
    private lateinit var encodeInputBuffers: Array<ByteBuffer>
    private lateinit var encodeOutputBuffers: Array<ByteBuffer>
    private lateinit var encodeBufferInfo: MediaCodec.BufferInfo


    private var databytes = ByteArray(1024)

    //存之前数组的长度 当前长度,last1Databytes长度，last2Databytes长度，last3Databytes长度，last4Databytes长度
    private val lengthDatabytes = ByteArray(5)

    //存之前音频数组，1为上一条，2为上上一条...
    private var last1Databytes: ByteArray? = null
    private var last2Databytes: ByteArray? = null
    private var last3Databytes: ByteArray? = null
    private var last4Databytes: ByteArray? = null

    //真正要发送的包
    //前14位是长度，后面分别为 currentDatabytes,last1Databytes,last2Databytes,last3Databytes,last4Databytes
    private var trueDatabytes: ByteArray? = null


    override fun onData(data: ByteArray) {
        encodeData(data)
    }


    /**
     * 初始化AAC编码器
     */
    fun initAACMediaEncode() {
        try { //参数对应-> mime type、采样率、声道数
            val encodeFormat: MediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate_interphone, audioChannelNum_interphone)
            encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, audioBitRate) //比特率
            encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024)
            mediaEncode = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            mediaEncode.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.e(e)
        }
        mediaEncode.start()
        encodeInputBuffers = mediaEncode.inputBuffers
        encodeOutputBuffers = mediaEncode.outputBuffers
        encodeBufferInfo = MediaCodec.BufferInfo()
    }

    /**
     * 编码，得到[.encodeType]格式的音频文件
     * @param data
     */
    fun encodeData(data: ByteArray) { //dequeueInputBuffer（time）需要传入一个时间值，-1表示一直等待，0表示不等待有可能会丢帧，其他表示等待多少毫秒
        if (!this::mediaEncode.isInitialized) {
            return
        }
        val inputIndex = mediaEncode.dequeueInputBuffer(-1) //获取输入缓存的index
        if (inputIndex >= 0) {
            val inputByteBuf = encodeInputBuffers[inputIndex]
            inputByteBuf.clear()
            inputByteBuf.put(data) //添加数据
            inputByteBuf.limit(data.size) //限制ByteBuffer的访问长度
            mediaEncode.queueInputBuffer(inputIndex, 0, data.size, 0, 0) //把输入缓存塞回去给MediaCodec
        }
        var outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 0) //获取输出缓存的index
        while (outputIndex >= 0) { //获取缓存信息的长度
            val byteBufSize = encodeBufferInfo.size
            //添加ADTS头部后的长度
            val bytePacketSize = byteBufSize + 7
            //拿到输出Buffer
            val outPutBuf = encodeOutputBuffers[outputIndex]
            outPutBuf.position(encodeBufferInfo.offset)
            outPutBuf.limit(encodeBufferInfo.offset + encodeBufferInfo.size)
            databytes = ByteArray(bytePacketSize)
            //添加ADTS头部
            addADTStoPacket(databytes, bytePacketSize)
            /*
            get（byte[] dst,int offset,int length）:ByteBuffer从position位置开始读，读取length个byte，并写入dst下
            标从offset到offset + length的区域
             */outPutBuf[databytes, 7, byteBufSize]
            outPutBuf.position(encodeBufferInfo.offset)

            compositeRedundancyPackage()
            arrangeBytes()
            sendAudioData(trueDatabytes!!, trueDatabytes!!.size)
            //释放
            mediaEncode.releaseOutputBuffer(outputIndex, false)
            outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 0)
        }
    }
    abstract fun sendAudioData(data: ByteArray,len:Int)

    /**
     * 给编码出的aac裸流添加adts头字段
     *
     * @param packet    要空出前7个字节，否则会搞乱数据
     * @param packetLen
     *  下面信息为adts 格式的 sampleRate和freq_idx 的对应关系
     * uint8_t freq_idx = 0;    //0: 96000 Hz  3: 48000 Hz 4: 44100 Hz
    switch (ctx->sample_rate) {
    case 96000:
    freq_idx = 0;
    break;
    case 88200:
    freq_idx = 1;
    break;
    case 64000:
    freq_idx = 2;
    break;
    case 48000:
    freq_idx = 3;
    break;
    case 44100:
    freq_idx = 4;
    break;
    case 32000:
    freq_idx = 5;
    break;
    case 24000:
    freq_idx = 6;
    break;
    case 22050:
    freq_idx = 7;
    break;
    case 16000:
    freq_idx = 8;
    break;
    case 12000:
    freq_idx = 9;
    break;
    case 11025:
    freq_idx = 10;
    break;
    case 8000:
    freq_idx = 11;
    break;
    case 7350:
    freq_idx = 12;
    break;
    default:
    freq_idx = 4;
    break;
    }
     */
    private fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        val profile = 2  //AAC LC
        val freqIdx = 11  //8KHz
        val chanCfg = 1  //CPE
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }


    //合成冗余包
    private fun compositeRedundancyPackage() {
        lengthDatabytes[0] = databytes.size.toByte()
        if (last1Databytes != null) {
            lengthDatabytes[1] = last1Databytes!!.size.toByte()
            trueDatabytes = addBytes(databytes, last1Databytes!!)


            if (last2Databytes != null) {
                lengthDatabytes[2] = last2Databytes!!.size.toByte()
                trueDatabytes = addBytes(trueDatabytes!!, last2Databytes!!)

                if (last3Databytes != null) {
                    lengthDatabytes[3] = last3Databytes!!.size.toByte()
                    trueDatabytes = addBytes(trueDatabytes!!, last3Databytes!!)

                    if (last4Databytes != null) {
                        lengthDatabytes[4] = last4Databytes!!.size.toByte()
                        trueDatabytes = addBytes(trueDatabytes!!, last4Databytes!!)

                        trueDatabytes = addBytes(lengthDatabytes, trueDatabytes!!)
                    } else {
                        lengthDatabytes[4] = 0
                        trueDatabytes = addBytes(lengthDatabytes, trueDatabytes!!)
                    }
                } else {
                    lengthDatabytes[3] = 0
                    lengthDatabytes[4] = 0
                    trueDatabytes = addBytes(lengthDatabytes, trueDatabytes!!)
                }
            } else {
                lengthDatabytes[2] = 0
                lengthDatabytes[3] = 0
                lengthDatabytes[4] = 0
                trueDatabytes = addBytes(lengthDatabytes, trueDatabytes!!)
            }
        } else {
            lengthDatabytes[1] = 0
            lengthDatabytes[2] = 0
            lengthDatabytes[3] = 0
            lengthDatabytes[4] = 0
            trueDatabytes = addBytes(lengthDatabytes, databytes)
        }
    }

    //模仿队列进出
    private fun arrangeBytes() {
        if (last1Databytes == null) {
            last1Databytes = ByteArray(databytes.size)
            System.arraycopy(databytes, 0, last1Databytes!!, 0, databytes.size)
        } else {

            if (last2Databytes == null) {
                last2Databytes = ByteArray(last1Databytes!!.size)
                System.arraycopy(last1Databytes!!, 0, last2Databytes!!, 0, last1Databytes!!.size)

            } else {

                if (last3Databytes == null) {
                    last3Databytes = ByteArray(last2Databytes!!.size)
                    System.arraycopy(last2Databytes!!, 0, last3Databytes!!, 0, last2Databytes!!.size)

                } else {

                    if (last4Databytes == null) {
                        last4Databytes = ByteArray(last3Databytes!!.size)
                        System.arraycopy(last3Databytes!!, 0, last4Databytes!!, 0, last3Databytes!!.size)

                    } else {

                        last4Databytes = ByteArray(last3Databytes!!.size)
                        System.arraycopy(last3Databytes!!, 0, last4Databytes!!, 0, last3Databytes!!.size)
                    }
                    last3Databytes = ByteArray(last2Databytes!!.size)
                    System.arraycopy(last2Databytes!!, 0, last3Databytes!!, 0, last2Databytes!!.size)
                }
                last2Databytes = ByteArray(last1Databytes!!.size)
                System.arraycopy(last1Databytes!!, 0, last2Databytes!!, 0, last1Databytes!!.size)
            }
            last1Databytes = ByteArray(databytes.size)
            System.arraycopy(databytes, 0, last1Databytes!!, 0, databytes.size)
        }
    }

    private var data3: ByteArray? = null

    fun addBytes(data1: ByteArray, data2: ByteArray): ByteArray {
        data3 = ByteArray(data1.size + data2.size)
        System.arraycopy(data1, 0, data3, 0, data1.size)
        System.arraycopy(data2, 0, data3, data1.size, data2.size)
        return data3 as ByteArray
    }
}