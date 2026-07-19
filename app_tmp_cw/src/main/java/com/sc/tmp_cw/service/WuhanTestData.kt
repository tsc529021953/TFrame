package com.sc.tmp_cw.service

import timber.log.Timber

/**
 * 武汉协议 (FlavorB) 测试数据
 *
 * 使用方法: 在 TmpServiceImpl.test() 中调用 MessageHandler.handleMessage(TEST_XXX, this)
 * 或者在 MessageHandler.test() 中替换 testData 变量
 */
object WuhanTestData {

    // ============================================================
    // B1 站点信息测试帧
    // ============================================================

    /** 即将到站: 石桥(32) → 中一路(33), trainStatus=0x02 → boardStatus=0 (nextStation) */
    const val NEXT_STATION_1 = "75AA002EB1FF110000010C01001F004300200021000002101900000000000000000000000000000000000000000000000000D9F85F"

    /** 已到站: 中一路(33), trainStatus=0x00 → boardStatus=2 (arrived) */
    const val ARRIVED_1 = "75AA002EB1FF110000010C01001F00430021002200000010190000000000000000000000000000000000000000000000000077BE5F"

    /** 即将到站: 后湖四路(34) → 兴业路(35), trainStatus=0x02 → boardStatus=0 (nextStation) */
    const val NEXT_STATION_2 = "75AA002EB1FF110000010C01001F00430022002300000210180000000000000000000000000000000000000000000000000052535F"

    /** 已到站: 兴业路(35), trainStatus=0x00 → boardStatus=2 (arrived) */
    const val ARRIVED_2 = "75AA002EB1FF110000010C01001F0043002300240000001018000000000000000000000000000000000000000000000000001A0D5F"

    // ============================================================
    // B5 紧急通知测试帧
    // ============================================================

    /** 临时停车通知 */
    const val URGENT_TEMP_STOP = "75AA0095B5FF11FF01000001E59084E4BD8DE4B998E5AEA2E682A8E5A5BDEFBC8CE78EB0E59CA8E698AFE4B8B4E697B6E5819CE8BDA6EFBC8CE8AFB7E88090E5BF83E7AD89E5BE85EFBC8CE4B88DE8A681E8A7A6E58AA8E58897E8BDA6E8AEBEE5A487E38082E7BB99E682A8E980A0E68890E79A84E4B88DE4BEBFE695ACE8AFB7E8B085E8A7A3EFBC8CE8B0A2E8B0A2E59088E4BD9CEFBC81FE775F"

    /** 火警疏散通知 */
    const val URGENT_FIRE = "75AA00B9B5FF11FF01000000E59084E4BD8DE4B998E5AEA2E682A8E5A5BDEFBC8CE8BDA6E58EA2E58685E58F91E7949FE781ABE68385EFBC8CE8AFB7E4BF9DE68C81E99587E5AE9AEFBC8CE5B7A5E4BD9CE4BABAE59198E9A9ACE4B88AE588B0E78EB0E59CBAE5A484E79086EFBC8CE58FAFE58F96E587BAE8BDA6E58EA2E7ABAFE983A8E68896E5BAA7E6A485E4B88BE696B9E79A84E781ADE781ABE599A8E68991E781ADE781ABE6BA90EFBC8CE8B0A2E8B0A2E59088E4BD9CEFBC813CD75F"

    /** 清客退出服务通知 */
    const val URGENT_EVACUATE = "75AA008FB5FF11FF02000001E59084E4BD8DE4B998E5AEA2E682A8E5A5BDEFBC8CE69CACE6ACA1E58897E8BDA6E5B086E98080E587BAE69C8DE58AA1EFBC8CE8AFB7E68980E69C89E4B998E5AEA2E59CA8E69CACE7AB99E4B88BE8BDA6E38082E7BB99E682A8E980A0E68890E79A84E4B88DE4BEBFE695ACE8AFB7E8B085E8A7A3EFBC8CE8B0A2E8B0A2E59088E4BD9CEFBC81C9115F"

    /**
     * 测试入口：在 MessageHandler.test() 中按顺序播放所有测试场景
     */
    fun runAll(service: TmpServiceImpl) {
        val messages = listOf(
            "下一站(石桥→中一路)" to NEXT_STATION_1,
            "已到站(中一路)" to ARRIVED_1,
            "下一站(后湖四路→兴业路)" to NEXT_STATION_2,
            "已到站(兴业路)" to ARRIVED_2,
            "紧急通知(临时停车)" to URGENT_TEMP_STOP,
            "紧急通知(火警)" to URGENT_FIRE,
            "紧急通知(清客)" to URGENT_EVACUATE,
        )

        var index = 0
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (index >= messages.size) return
                val (name, hex) = messages[index]
                Timber.i("=== 测试: $name ===")
                MessageHandler.handleMessage(hex, service)
                index++
                handler.postDelayed(this, 4000) // 每4秒发一条
            }
        }
        handler.post(runnable)
    }
}
