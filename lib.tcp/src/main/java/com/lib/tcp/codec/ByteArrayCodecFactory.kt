package com.lib.tcp.codec

import com.lib.tcp.bean.TCPMessage
import com.lib.tcp.consts.MessageConst
import com.lib.tcp.utils.MessageBuilder
import com.lib.tcp.utils.MessageParser
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.*
import java.util.*

class ByteArrayCodecFactory : ProtocolCodecFactory {
    private val decoder = ByteArrayDecoder()
    private val encoder = ByteArrayEncoder()

    override fun getDecoder(session: IoSession?): ProtocolDecoder {
        return decoder
    }

    override fun getEncoder(session: IoSession?): ProtocolEncoder {
        return encoder
    }

    internal class ByteArrayDecoder : CumulativeProtocolDecoder() {
        override fun doDecode(session: IoSession, `in`: IoBuffer, out: ProtocolDecoderOutput): Boolean {
            val byteSize = `in`.remaining()// 消息频率过高时，该值可能为多条消息的长度，需要删除空字节
            if (byteSize < MessageConst.MESSAGE_MINLEN) {
                return false
            }
            var n = 0
            `in`.mark()
            val list = LinkedList<Byte>()
            while (`in`.hasRemaining()) {
                val b = `in`.get()
                list.add(b)
                if (b == MessageConst.MESSAGE_FLAG) {
                    n += 1
                }
                if (n == 2) {
                    break
                }
            }

            if (n != 2) {
                `in`.reset()
                return false
            }
            val bytes = ByteArray(list.size)
            for (i in list.indices) {
                bytes[i] = list[i]
            }
            val message = MessageParser.decode(bytes)
            out.write(message)
            return true//处理成功，让父类进行接收下个包
        }
    }

    internal class ByteArrayEncoder : ProtocolEncoderAdapter() {
        override fun encode(session: IoSession, message: Any, out: ProtocolEncoderOutput) {
            val bs = MessageBuilder.encode(message as TCPMessage)
            val buffer = IoBuffer.allocate(256)
            buffer.isAutoExpand = true
            buffer.put(bs)
            buffer.flip()
            out.write(buffer)
            out.flush()
            buffer.free()
        }

    }
}