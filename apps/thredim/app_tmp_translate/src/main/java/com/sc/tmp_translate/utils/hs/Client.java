// src/main/java/websocket/Client.java
package com.sc.tmp_translate.utils.hs;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Client extends WebSocketClient {



    private IClientListener listener;

    public Client(URI serverUri) {
        super(serverUri);
    }

    void setClientListener(IClientListener listener) {
        this.listener = listener;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("onopen");
    }

    @Override
    public void onMessage(String s) {
        System.out.println("onmessage " + s);
        if (listener != null) listener.onMessage(s);
    }
    @Override
    public void onMessage(ByteBuffer message) {
        String s = new String(message.array(), StandardCharsets.UTF_8);
        System.out.println("onmessage " + s);
        if (listener != null) listener.onMessage(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("onclose " + i + " " + b + " " + s);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    static String getConfig() {
        return getConfig("zh", "en");
    }

    static String getConfig(String source, String target) {
        return "{\n" +
                "    \"Configuration\": {\n" +
                "        \"SourceLanguage\": \"" + source + "\",\n" +
                "        \"TargetLanguages\": [\n" +
                "            \"" + target + "\"\n" +
                "        ],\n" +
                "        \"HotWordList\": [\n" +
                "            {\n" +
                "                \"Word\": \"hello\",\n" +
                "                \"Scale\": 1\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
    }

    static String getEnd() {
        return "{\n" +
                "    \"End\": true\n" +
                "}";
    }

    static String bytesToMessage(byte[] data) {
        String base64Data = Base64.getEncoder().encodeToString(data);
        return "{\n" +
                "    \"AudioData\": \"" +
                base64Data +
                "\"\n" +
                "}";
    }
}
