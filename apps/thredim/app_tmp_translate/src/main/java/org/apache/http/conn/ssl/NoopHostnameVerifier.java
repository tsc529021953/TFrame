//package org.apache.http.conn.ssl;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLSession;
//
///**
// * 一个不校验主机名的 HostnameVerifier
// */
//public class NoopHostnameVerifier implements HostnameVerifier {
//
//    public static final NoopHostnameVerifier INSTANCE = new NoopHostnameVerifier();
//
//    @Override
//    public boolean verify(String hostname, SSLSession session) {
//        return true; // 永远返回 true，跳过校验
//    }
//}
