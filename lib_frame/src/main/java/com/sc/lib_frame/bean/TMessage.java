package com.sc.lib_frame.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.sc.lib_frame.bus.event.BaseEvent;

public class TMessage extends BaseEvent implements Parcelable {
    private String cmd;
    private String data;
    private byte[] byteData;

    public TMessage(String cmd, String data) {
        this.cmd = cmd;
        this.data = data;
    }

    public TMessage(String cmd, String data, byte[] byteData) {
        this.cmd = cmd;
        this.data = data;
        this.byteData = byteData;
    }


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte[] getByteData() {
        return byteData;
    }

    public void setByteData(byte[] byteData) {
        this.byteData = byteData;
    }

    protected TMessage(Parcel in) {
        cmd = in.readString();
        data = in.readString();
        byteData = in.createByteArray();
    }

    public static final Creator<TMessage> CREATOR = new Creator<TMessage>() {
        @Override
        public TMessage createFromParcel(Parcel in) {
            return new TMessage(in);
        }

        @Override
        public TMessage[] newArray(int size) {
            return new TMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cmd);
        parcel.writeString(data);
        parcel.writeByteArray(byteData);
    }
}
