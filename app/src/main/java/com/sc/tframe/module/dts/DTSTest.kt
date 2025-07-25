package com.sc.tframe.module.dts

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nbhope.lib_frame.utils.DateUtil
import com.sc.tframe.R
import com.sc.tframe.utils.*

/**
 * @author  tsc
 * @date  2025/4/16 14:57
 * @version 0.0.0-1
 * @description
 */
class DTSTest {

    private var path =
//            "/storage/emulated/0/Music/input.mkv"
//            "/storage/emulated/0/Music/qtbhl.dts"
            "/storage/emulated/0/Music/dfp.dts"
//            "/storage/emulated/0/Music/xxdty.dts"

    private var paths = arrayListOf(
            "/storage/emulated/0/Music/qtbhl.dts",
            "/storage/emulated/0/Music/dfp.dts",
            "/storage/emulated/0/Music/xxdty.dts"
    )
    private var playIndex = 0

    private var dtsPlayer: DTSPlayer? = null

    private var pcmPlayer: PCMPlayer? = null

    private var seekbar: SeekBar? = null
    private var timeTv: TextView? = null

    private val MSG_REFRESH_VIEW = 1
    private var mIsSeekBarTouching = false

    private var arcPlayer: ARCPlayer? = null

    private var duration = 0L;

    private val mMainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_REFRESH_VIEW -> {
                    if (!mIsSeekBarTouching) {
                        updatePosition()
                    }
                    this.removeMessages(MSG_REFRESH_VIEW)
                    sendEmptyMessageDelayed(MSG_REFRESH_VIEW, 500)
                }
                else -> {
                }
            }
        }
    }

    fun init(context: AppCompatActivity) {
        arcPlayer = ARCPlayer(object : IPlayListen {
            override fun onPlay() {
                mMainHandler.sendEmptyMessageDelayed(MSG_REFRESH_VIEW, 500)
            }

            override fun onPause() {
                mMainHandler.removeMessages(MSG_REFRESH_VIEW)
            }

            override fun onRelease() {
                mMainHandler.removeMessages(MSG_REFRESH_VIEW)
            }

            override fun onDuration(duration: Long) {
                // 获取总的进度  并显示出来
                val max = (duration / 1000).toInt()
                val time = DateUtil.toProgress(0, max)
                context?.runOnUiThread {
                    seekbar?.max = max
                    timeTv?.text = time
                    System.out.println("onDuration time $time $max")
                }
            }

        })
//        arcPlayer?.init()

        dtsPlayer = DTSPlayer()
//        dtsPlayer?.init()
        timeTv = context.findViewById<TextView>(R.id.time_tv)
        val zmTV = context.findViewById<TextView>(R.id.zm_tv)
        zmTV.setOnClickListener {
//            val max = dtsPlayer?.getDuration(path)
//            val max2 = MusicUtil.getAudioFileVoiceTime(path)
//            dtsPlayer?.getInfo(path)
//            System.out.println("max get2 $max $max2")
//            dtsPlayer?.play(path)
        }
        val playTv = context.findViewById<TextView>(R.id.play_tv)
        playTv.setOnClickListener {
            arcPlayer?.play(paths[playIndex])
            playIndex = 0
        }
        val pauseTv = context.findViewById<TextView>(R.id.pause_tv)
        pauseTv.setOnClickListener {
            arcPlayer?.playPause()
        }
        val stopTv = context.findViewById<TextView>(R.id.stop_tv)
        stopTv.setOnClickListener {
            arcPlayer?.release()
        }
        val nextTv = context.findViewById<TextView>(R.id.next_tv)
        nextTv.setOnClickListener {
//            arcPlayer?.release()
            playIndex++
            if (playIndex >= paths.size) playIndex = 0
            arcPlayer?.play(paths[playIndex])
        }
        seekbar = context.findViewById<SeekBar>(R.id.position_sb)
        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (fromUser) {
//                    viewModel.playStatus.currProgress.set(progress)
//                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mIsSeekBarTouching = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                pcmPlayer?.seekTo(seekBar.progress.toLong() * 1000, duration)
//                viewModel.player.seekTo(viewModel.playStatus.currProgress.get() * 1000)
                arcPlayer?.seek(seekBar.progress * 1000L)
                mIsSeekBarTouching = false
            }
        })
    }

    fun updatePosition() {
        val position = arcPlayer?.getPosition() ?: 0
        val duration = arcPlayer?.getDuration() ?: 0
        seekbar?.progress = position
        timeTv?.text = DateUtil.toProgress(position, duration)
//        try {
//            val process = pcmPlayer!!.playbackPositionMs.toInt()
//            var max2 = 0L
//            var value = 180
//            val position = if (pcmPlayer != null && pcmPlayer?.inS?.channel?.isOpen == true) {
//                max2 = pcmPlayer!!.inS.channel.size()
////            value = (max2 / (seekbar?.max ?: 1)).toInt() // 39919616 225740/226,816 176.838 // 55922688 316900/317,742 176.467  // 51560448 291760/292,957  176.72
//                value = (max2 / 225740L).toInt()
////            max2 /= value
//                pcmPlayer!!.inS.channel.position() / value
//            } else 0
//            System.out.println("process $process min ${seekbar?.min} $value max ${seekbar?.max} $max2 $position")
//            seekbar?.progress = process / 1000
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

    }



    fun release() {
        dtsPlayer?.release()
        pcmPlayer?.release()
        arcPlayer?.release()
    }

}
