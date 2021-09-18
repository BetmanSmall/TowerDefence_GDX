package com.betmansmall.server.networking;

import protobuf.ProtoObject;

public interface ProtoTcpSocketListener {
    void onConnectionReady(ProtoTcpConnection tcpConnection);

    void onReceiveObject(ProtoTcpConnection tcpConnection, ProtoObject sendObject);

    void onDisconnect(ProtoTcpConnection tcpConnection);

    void onException(ProtoTcpConnection tcpConnection, Exception exception);
}
