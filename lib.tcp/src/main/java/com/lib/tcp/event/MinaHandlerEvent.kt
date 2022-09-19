package com.lib.tcp.event

import com.lib.tcp.bean.TCPMessage
import com.lib.tcp.enums.MinaEventEnum

data class MinaHandlerEvent( val enum:MinaEventEnum,val  message:TCPMessage? = null)