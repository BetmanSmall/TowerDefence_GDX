package com.betmansmall.server.networking;

import protobuf.Proto;

public interface ProtoTcpSocketListener {
    void onConnectionReady(ProtoTcpConnection tcpConnection);

    void onReceiveObject(ProtoTcpConnection tcpConnection, Proto.SendObject sendObject);

    void onDisconnect(ProtoTcpConnection tcpConnection);

    void onException(ProtoTcpConnection tcpConnection, Exception exception);
}
