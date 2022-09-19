package com.lib.halo;

import android.util.Log;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.RecoverableProtocolDecoderException;
import org.apache.mina.filter.codec.textline.LineDelimiter;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;


/**
 * TextLineCodecWithPackageFactory
 * <p>
 * mina IoBuffer 常用方法
 * https://www.cnblogs.com/zzt-lovelinlin/p/5292608.html
 *
 * @author EthanCo
 * @since 2018/10/9
 */
public class HaloTextLineCodecFactory implements ProtocolCodecFactory {
    private static String TAG = "Z-TextLineCodec";

    private final TextLineEncoder encoder;
    private final TextLineDecoder decoder;

    public HaloTextLineCodecFactory() {
        this(Charset.forName("utf-8"));
    }

    public HaloTextLineCodecFactory(Charset charset) {
        encoder = new TextLineEncoder(charset, LineDelimiter.UNIX);
        decoder = new TextLineDecoder(charset, LineDelimiter.AUTO);
    }

    public HaloTextLineCodecFactory(Charset charset, String encodingDelimiter, String decodingDelimiter) {
        encoder = new TextLineEncoder(charset, encodingDelimiter);
        decoder = new TextLineDecoder(charset, decodingDelimiter);
    }

    public HaloTextLineCodecFactory(Charset charset, LineDelimiter encodingDelimiter, LineDelimiter decodingDelimiter) {
        encoder = new TextLineEncoder(charset, encodingDelimiter);
        decoder = new TextLineDecoder(charset, decodingDelimiter);
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }


    public int getEncoderMaxLineLength() {
        return encoder.getMaxLineLength();
    }

    public void setEncoderMaxLineLength(int maxLineLength) {
        encoder.setMaxLineLength(maxLineLength);
    }

    public int getDecoderMaxLineLength() {
        return decoder.getMaxLineLength();
    }

    public void setDecoderMaxLineLength(int maxLineLength) {
        decoder.setMaxLineLength(maxLineLength);
    }

    //编码
    public class TextLineEncoder extends ProtocolEncoderAdapter {
        private final AttributeKey ENCODER = new AttributeKey(TextLineEncoder.class, "encoder");
        private final Charset charset;
        private final LineDelimiter delimiter;
        private int maxLineLength;

        public TextLineEncoder() {
            this(Charset.defaultCharset(), LineDelimiter.UNIX);
        }

        public TextLineEncoder(String delimiter) {
            this(new LineDelimiter(delimiter));
        }

        public TextLineEncoder(LineDelimiter delimiter) {
            this(Charset.defaultCharset(), delimiter);
        }

        public TextLineEncoder(Charset charset) {
            this(charset, LineDelimiter.UNIX);
        }

        public TextLineEncoder(Charset charset, String delimiter) {
            this(charset, new LineDelimiter(delimiter));
        }

        public TextLineEncoder(Charset charset, LineDelimiter delimiter) {
            this.maxLineLength = 2147483647;
            if (charset == null) {
                throw new IllegalArgumentException("charset");
            } else if (delimiter == null) {
                throw new IllegalArgumentException("delimiter");
            } else if (LineDelimiter.AUTO.equals(delimiter)) {
                throw new IllegalArgumentException("AUTO delimiter is not allowed for encoder.");
            } else {
                this.charset = charset;
                this.delimiter = delimiter;
            }
        }

        public int getMaxLineLength() {
            return this.maxLineLength;
        }

        public void setMaxLineLength(int maxLineLength) {
            if (maxLineLength <= 0) {
                throw new IllegalArgumentException("maxLineLength: " + maxLineLength);
            } else {
                this.maxLineLength = maxLineLength;
            }
        }

        @Override
        public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
            CharsetEncoder encoder = (CharsetEncoder) session.getAttribute(ENCODER);
            if (encoder == null) {
                encoder = this.charset.newEncoder();
                session.setAttribute(ENCODER, encoder);
            }

            String value = message == null ? "" : message.toString();
            IoBuffer buf = IoBuffer.allocate(value.length()).setAutoExpand(true);

            buf = putData(encoder, value, buf);
            if (buf.position() > this.maxLineLength) {
                throw new IllegalArgumentException("Line length: " + buf.position());
            } else {
                putDelimiter(out, encoder, buf);
            }
        }

        private IoBuffer putData(CharsetEncoder encoder, String value, IoBuffer buf) throws CharacterCodingException {
            try {
                buf.putString(value, encoder);
            } catch (Exception e) {
                Log.e(TAG, "putString:" + value + " error:" + Log.getStackTraceString(e));
                try {
                    buf = IoBuffer.allocate(value.length()).setAutoExpand(true);
                    buf.putString(value, encoder);
                } catch (Exception e2) {
                    Log.e(TAG, "putString:" + value + " error2:" + Log.getStackTraceString(e));
                }
            }
            return buf;
        }

        private void putDelimiter(ProtocolEncoderOutput out, CharsetEncoder encoder, IoBuffer buf) {
            try {
                buf.putString(this.delimiter.getValue(), encoder);
            } catch (Exception e) {
                Log.e(TAG, "put delimiter error:" + Log.getStackTraceString(e));
            } finally {
                buf.flip();
                out.write(buf);
            }
        }

        public void dispose() throws Exception {
        }
    }

    //解码
    public class TextLineDecoder implements ProtocolDecoder {
        private final AttributeKey CONTEXT;
        private final Charset charset;
        private final LineDelimiter delimiter;
        private IoBuffer delimBuf;
        private int maxLineLength;
        private int bufferLength;

        public TextLineDecoder() {
            this(LineDelimiter.AUTO);
        }

        public TextLineDecoder(String delimiter) {
            this(new LineDelimiter(delimiter));
        }

        public TextLineDecoder(LineDelimiter delimiter) {
            this(Charset.defaultCharset(), delimiter);
        }

        public TextLineDecoder(Charset charset) {
            this(charset, LineDelimiter.AUTO);
        }

        public TextLineDecoder(Charset charset, String delimiter) {
            this(charset, new LineDelimiter(delimiter));
        }

        public TextLineDecoder(Charset charset, LineDelimiter delimiter) {
            this.CONTEXT = new AttributeKey(this.getClass(), "context");
            this.maxLineLength = 1024;
            this.bufferLength = 128;
            if (charset == null) {
                throw new IllegalArgumentException("charset parameter shuld not be null");
            } else if (delimiter == null) {
                throw new IllegalArgumentException("delimiter parameter should not be null");
            } else {
                this.charset = charset;
                this.delimiter = delimiter;
                if (this.delimBuf == null) {
                    IoBuffer tmp = IoBuffer.allocate(2).setAutoExpand(true);

                    try {
                        tmp.putString(delimiter.getValue(), charset.newEncoder());
                    } catch (CharacterCodingException var5) {
                        ;
                    }

                    tmp.flip();
                    this.delimBuf = tmp;
                }

            }
        }

        public int getMaxLineLength() {
            return this.maxLineLength;
        }

        public void setMaxLineLength(int maxLineLength) {
            if (maxLineLength <= 0) {
                throw new IllegalArgumentException("maxLineLength (" + maxLineLength + ") should be a positive value");
            } else {
                this.maxLineLength = maxLineLength;
            }
        }

        public void setBufferLength(int bufferLength) {
            if (bufferLength <= 0) {
                throw new IllegalArgumentException("bufferLength (" + this.maxLineLength + ") should be a positive value");
            } else {
                this.bufferLength = bufferLength;
            }
        }

        public int getBufferLength() {
            return this.bufferLength;
        }

        @Override
        public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
            Context ctx = getContext(session);
            if (LineDelimiter.AUTO.equals(this.delimiter)) {
                decodeAuto(ctx, session, in, out);
            } else {
               decodeNormal(ctx, session, in, out);
            }

        }

        private Context getContext(IoSession session) {
            Context ctx = (Context) session.getAttribute(this.CONTEXT);
            if (ctx == null) {
                ctx = new Context(this.bufferLength);
                session.setAttribute(this.CONTEXT, ctx);
            }

            return ctx;
        }

        public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        }

        public void dispose(IoSession session) throws Exception {
            Context ctx = (Context) session.getAttribute(this.CONTEXT);
            if (ctx != null) {
                session.removeAttribute(this.CONTEXT);
            }

        }

        private void decodeAuto(Context ctx, IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws CharacterCodingException, ProtocolDecoderException {
            int matchCount = ctx.getMatchCount();
            int oldPos = in.position();
            int oldLimit = in.limit();

            while (in.hasRemaining()) {
                byte b = in.get();
                boolean matched = false;
                switch (b) {
                    case 10:
                        ++matchCount;
                        matched = true;
                        break;
                    case 13:
                        ++matchCount;
                        break;
                    default:
                        matchCount = 0;
                }

                if (matched) {
                    int pos = in.position();
                    in.limit(pos);
                    in.position(oldPos);
                    ctx.append(in);
                    in.limit(oldLimit);
                    in.position(pos);
                    if (ctx.getOverflowPosition() != 0) {
                        int overflowPosition = ctx.getOverflowPosition();
                        ctx.reset();
                        throw new RecoverableProtocolDecoderException("Line is too long: " + overflowPosition);
                    }

                    IoBuffer buf = ctx.getBuffer();
                    buf.flip();
                    buf.limit(buf.limit() - matchCount);

                    try {
                        byte[] data = new byte[buf.limit()];
                        buf.get(data);
                        CharsetDecoder decoder = ctx.getDecoder();
                        CharBuffer buffer = decoder.decode(ByteBuffer.wrap(data));
                        String str = buffer.toString();
                        this.writeText(session, str, out);
                    } finally {
                        buf.clear();
                    }

                    oldPos = pos;
                    matchCount = 0;
                }
            }

            in.position(oldPos);
            ctx.append(in);
            ctx.setMatchCount(matchCount);
        }

        private void decodeNormal(Context ctx, IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws CharacterCodingException, ProtocolDecoderException {
            int matchCount = ctx.getMatchCount(); //比较的count
            int oldPos = in.position(); //获取位置
            int oldLimit = in.limit(); //获取limit

            while (in.hasRemaining()) { //存在读取的数据
                byte b = in.get(); //读取byte
                if (this.delimBuf.get(matchCount) == b) { //如果需要比较的byte和读取到的byte相等
                    ++matchCount; //比较的count ++
                    if (matchCount == this.delimBuf.limit()) { //如果比较的count等于delimBuf的limit，即是当前最后的字段
                        int pos = in.position();
                        in.limit(pos);
                        in.position(oldPos);
                        ctx.append(in);
                        in.limit(oldLimit);
                        in.position(pos);
                        if (ctx.getOverflowPosition() != 0) {
                            int overflowPosition = ctx.getOverflowPosition();
                            ctx.reset();
                            throw new RecoverableProtocolDecoderException("Line is too long: " + overflowPosition);
                        }

                        IoBuffer buf = ctx.getBuffer();
                        buf.flip();
                        buf.limit(buf.limit() - matchCount);

                        try {
                            this.writeText(session, buf.getString(ctx.getDecoder()), out);
                        } finally {
                            buf.clear();
                        }

                        oldPos = pos;
                        matchCount = 0;
                    }
                } else {
                    in.position(Math.max(0, in.position() - matchCount));
                    matchCount = 0;
                }
            }

            in.position(oldPos);
            ctx.append(in);
            ctx.setMatchCount(matchCount);
        }

        protected void writeText(IoSession session, String text, ProtocolDecoderOutput out) {
            out.write(text);
        }

        private class Context {
            private final CharsetDecoder decoder;
            private final IoBuffer buf;
            private int matchCount;
            private int overflowPosition;

            private Context(int bufferLength) {
                this.matchCount = 0;
                this.overflowPosition = 0;
                this.decoder = TextLineDecoder.this.charset.newDecoder();
                this.buf = IoBuffer.allocate(bufferLength).setAutoExpand(true);
            }

            public CharsetDecoder getDecoder() {
                return this.decoder;
            }

            public IoBuffer getBuffer() {
                return this.buf;
            }

            public int getOverflowPosition() {
                return this.overflowPosition;
            }

            public int getMatchCount() {
                return this.matchCount;
            }

            public void setMatchCount(int matchCount) {
                this.matchCount = matchCount;
            }

            public void reset() {
                this.overflowPosition = 0;
                this.matchCount = 0;
                this.decoder.reset();
            }

            public void append(IoBuffer in) {
                if (this.overflowPosition != 0) {
                    this.discard(in);
                } else if (this.buf.position() > TextLineDecoder.this.maxLineLength - in.remaining()) {
                    this.overflowPosition = this.buf.position();
                    this.buf.clear();
                    this.discard(in);
                } else {
                    this.getBuffer().put(in);
                }

            }

            private void discard(IoBuffer in) {
                if (2147483647 - in.remaining() < this.overflowPosition) {
                    this.overflowPosition = 2147483647;
                } else {
                    this.overflowPosition += in.remaining();
                }

                in.position(in.limit());
            }
        }
    }
}
