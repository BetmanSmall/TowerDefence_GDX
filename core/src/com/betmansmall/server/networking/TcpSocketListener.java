package com.betmansmall.server.networking;

import com.betmansmall.server.data.SendObject;

public interface TcpSocketListener {
    void onConnectionReady(TcpConnection tcpConnection);

    void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject);

    void onDisconnect(TcpConnection tcpConnection);

    void onException(TcpConnection tcpConnection, Exception exception);
}
