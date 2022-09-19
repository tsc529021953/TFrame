package com.nbhope.lib_frame.bus.event

/**
 * Created by zhouwentao on 2020/4/21.
 */
class AudioControlEvent constructor(val message: Int) : BaseEvent() {

    companion object {
        const val START_SPEEK = 0
        const val STOP_SPEEK = 1

//        const val CHANGE_SPEECH = 2
        const val CHANGE_INTERPHONE = 3
    }

}