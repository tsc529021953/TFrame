package com.lib.network.vo.response;

import java.io.Serializable;


public class SpeechSelfResponse implements Serializable {


    /**
     * memberId : 1343430718869270528
     * platformId : 999
     * refrenceId : 1365849496114483200
     * sceneId : 1655972626559
     * voiceMean : ["白班"]
     * voiceText : ["uScBayeqOA4QOyPW"]
     */

    private String memberId;
    private int platformId;
    private String refrenceId;
    private String sceneId;
    private String voiceMean;
    private String voiceText;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public String getRefrenceId() {
        return refrenceId;
    }

    public void setRefrenceId(String refrenceId) {
        this.refrenceId = refrenceId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getVoiceMean() {
        return voiceMean;
    }

    public void setVoiceMean(String voiceMean) {
        this.voiceMean = voiceMean;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public void setVoiceText(String voiceText) {
        this.voiceText = voiceText;
    }
}
