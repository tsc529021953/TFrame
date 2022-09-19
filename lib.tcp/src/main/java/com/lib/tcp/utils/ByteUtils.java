package com.lib.tcp.utils;


import com.lib.tcp.consts.MessageConst;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


/**
 * 作者：kelingqiu on 17/11/6 10:38
 * 邮箱：42747487@qq.com
 */

public class ByteUtils {
    public static final String HEX_STRING = "0123456789ABCDEF";
    public static final int REPAIR_INT = 0xFF;

    private ByteUtils() {
        super();
    }

    /**
     * list转byte[]
     *
     * @param list List<Byte>
     * @return byte[] byte[]
     */
    public static byte[] listToBytes(List<Byte> list) {
        if (null == list) {
            throw new NullPointerException();
        }
        int listSize = list.size();
        byte[] result = new byte[listSize];
        for (int j = 0; j < listSize; j++) {
            result[j] = list.get(j);
        }
        return result;
    }

    /**
     * SHA加密
     *
     * @param strSrc
     *            明文
     * @return 加密之后的密文
    */
    public static String shaEncrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-1");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);
            strDes = bytesToHexString(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes.toLowerCase();
    }

    /**
     * 将字节数组转化为十六进制字符串
     * 注：0xFF & bArray[i]与bArray[i] & 0xFF的结果一致
     *
     * @param bArray 要转化的字节数组
     * @return String 转化后的十六进制字符串
     */
    public static String bytesToHexString(byte[] bArray) {
        if (null == bArray) {
            throw new NullPointerException();
        }
        StringBuffer stringBuffer = new StringBuffer(bArray.length);
        for (int i = 0; i < bArray.length; i++) {
            stringBuffer.append(byteToHexString(bArray[i]));
        }
        return stringBuffer.toString();
    }

    /**
     * 将一个byte转换为十六进制字符串，不足两位时前面补零
     * int是32位，byte是8位，不进行&0xff时会出现高24位补位误差，产生0xffffff
     *
     * @param b byte
     * @return String 为十六进制字符串
     */
    public static String byteToHexString(byte b) {
        String hexString = Integer.toHexString(b & REPAIR_INT);
        if (hexString.length() == 1) {
            hexString = MessageConst.Companion.getREPAIR_BIT() + hexString;
        }
        return hexString.trim().toUpperCase();
    }

    /**
     * 将指定字节数组中的若干元素拷贝到新数组
     *
     * @param source     指定的源字节数组
     * @param startIndex 源字节数组拷贝的起始索引
     * @param length     需要拷贝的字节数组长度
     * @return byte[] 拷贝后得到的新数组
     */
    public static byte[] bytesCopy4Size(byte[] source, int startIndex, int length) {
        if (null == source || startIndex < 0 || length < 0) {
            throw new NullPointerException();
        }
        if (source.length < length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        byte[] dest = new byte[length];
        System.arraycopy(source, startIndex, dest, 0, length);
        return dest;
    }

    /**
     * 将指定字节数组中的若干元素拷贝到新数组
     *
     * @param source     指定的源字节数组
     * @param startIndex 数组开始位置，包含该索引元素
     * @param endIndex   数组结束位置，包含该索引元素
     * @return byte[] 产生的新数组
     */
    public static byte[] bytesCopy4Index(byte[] source, int startIndex, int endIndex) {
        if (null == source || startIndex < 0 || endIndex < 0 || startIndex > endIndex) {
            throw new NullPointerException();
        }
        int size = endIndex - startIndex;
        if (size > source.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return bytesCopy4Size(source, startIndex, size + 1);
    }

    /**
     * 计算字节数组的异或校验码
     *
     * @return byte 数组异或校验码
     */
    public static byte bytesXor(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            throw new NullPointerException();
        }
        byte byteValue = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            byteValue ^= bytes[i];
        }
        return byteValue;
    }

    /**
     * 解包Int算法，将byte数组转换为整数，1int=4byte
     *
     * @param bs 需要转化为整数的字节数组
     * @return int 转化后的整数
     */
    public static int bytesToInt(byte[] bs) {
        if (null == bs || bs.length == 0) {
            throw new NullPointerException();
        }
        int iOutcome = 0;
        for (int i = 0; i < 4; i++) {
            byte bLoop = bs[i];
            iOutcome += (bLoop & REPAIR_INT) << (8 * i);
        }
        return iOutcome;
    }

    /**
     * 将十六进制字符串转换成字节数组。
     *
     * @param hexString 需要转换的字符串。
     * @return 字节数组。
     */
    public static byte[] hexStringToBytes(String hexString) {
        char[] c = hexString.toCharArray();
        byte[] b = new byte[c.length / 2];
        int m = 0;
        for (int i = 0; i < c.length; i += 2) {
            int j = hexCharToInt(c[i]) << 4;
            int k = hexCharToInt(c[i + 1]);
            b[m] = (byte) (j | k);
            m++;
        }
        return b;
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String strToHexString(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr str Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    public static String hexStringToStr(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 将十六进制字符转换成数字。
     *
     * @param c 将转换的字符。
     * @return 转换成的数字。
     */
    private static int hexCharToInt(char c) {
        char cc = Character.toUpperCase(c);
        int value = HEX_STRING.indexOf(cc);
        if (value == -1) {
            throw new NullPointerException();
        }
        return value;
    }

    /**
     * 唯一编号编码
     *
     * @param longPrimary 唯一编号
     * @return String 编码后的唯一编号
     * @throws Exception Exception
     */
    public static String encodePrimary(Long longPrimary) throws Exception {
        if (null == longPrimary || longPrimary == 0) {
            throw new NullPointerException();
        }
        String primary = longPrimary.toString();
        if (primary.length() < MessageConst.Companion.getDEVICEID_LENGTH()) {
            throw new IllegalArgumentException("唯一编号长度不符！");
        }
        if (longPrimary > 0) {
            primary = String.format("%1$" + MessageConst.Companion.getPRIMARY_LENGTH() + "s", primary).replace(' ',MessageConst.Companion.getPLUS_REPAIR().charAt(0));// 补位操作，1byte=8bit
        } else {
            primary = String.format("%1$" + MessageConst.Companion.getPRIMARY_LENGTH() + "s", primary.substring(1)).replace(' ',MessageConst.Companion.getMINU_REPIAR().charAt(0));// 补位操作，1byte=8bit
        }
        return primary;
    }

    /**
     * 唯一编号解码
     *
     * @param stringPrimary 编码后的唯一编号字符串
     * @return long 解码后的唯一编号
     * @throws Exception Exception
     */
    public static long decodePrimary(String stringPrimary) throws Exception {
        if (stringPrimary.isEmpty()) {
            throw new NullPointerException();
        }
        if (!stringPrimary.startsWith(MessageConst.Companion.getPLUS_REPAIR()) && !stringPrimary.startsWith(MessageConst.Companion.getMINU_REPIAR())) {
            throw new IllegalArgumentException();
        }
        long primary = 0L;
        if (stringPrimary.startsWith(MessageConst.Companion.getPLUS_REPAIR())) {
            stringPrimary = stringPrimary.replace(MessageConst.Companion.getPLUS_REPAIR(), "");
            try {
                primary = Long.parseLong(stringPrimary);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        if (stringPrimary.startsWith(MessageConst.Companion.getMINU_REPIAR())) {
            stringPrimary = stringPrimary.replace(MessageConst.Companion.getMINU_REPIAR(), "");
            try {
                primary = Long.parseLong(stringPrimary);
                primary = -primary;
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return primary;
    }
}
