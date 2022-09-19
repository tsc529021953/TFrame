package com.lib.halo;

import com.ethanco.halo.turbo.mina.IMySession;
import com.nbhope.phmina.bean.data.ClientInfo;

/**
 * Created by ywr on 2021/6/26 12:37
 */
public class TcpSession {
    private transient IMySession session;
    private boolean isLinkState = false;
    private ClientInfo clientInfo=null;

    public boolean isLinkState() {
        return isLinkState;
    }

    public void setLinkState(boolean linkState) {
        isLinkState = linkState;
    }

    public TcpSession() {
    }

    public IMySession getSession() {
        return session;
    }

    public void setSession(IMySession session) {
        this.session = session;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }
}
