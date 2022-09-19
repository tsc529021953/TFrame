package com.lib.tcp.enums

/**
 * 作者：kelingqiu on 17/11/6 16:30
 * 邮箱：42747487@qq.com
 */
enum class MessageIDEnum constructor(val messageId: Int, val messageMethod: String, val messageMark: String) :
        MessageIDDefinition {
    // 平台部份
    APP_COMMANSWER(0x8001, "commAnswer","平台通用应答！"),
    APP_REGISTERS(0x8100, "registerAnswer","终端注册应答！");

    override fun getIDMessageId(): Int {
        return this.messageId
    }

    override fun getIDMessageMethod(): String {
        return this.messageMethod
    }

    override fun getIDMessageMark(): String {
        return this.messageMark
    }

    companion object {
        fun getEnumByMessageId(messageId: Int): MessageIDEnum?{
            if (messageId <= 0) {
                throw NullPointerException()
            }
            var msgIdEnum: MessageIDEnum? = null
            val ids = MessageIDEnum.values()
            for (idEnum in ids) {
                if (idEnum.getIDMessageId() == messageId) {
                    msgIdEnum = idEnum
                    break
                }
            }
            return msgIdEnum
        }
    }
}