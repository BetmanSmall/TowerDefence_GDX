package com.betmansmall.game;

import com.betmansmall.enums.SessionState;
import com.betmansmall.server.networking.ProtoTcpConnection;
import com.betmansmall.server.networking.ProtoTcpSocketListener;

import protobuf.Proto;

public abstract class ProtoSessionThread extends Thread implements ProtoTcpSocketListener {
    public SessionState sessionState;

    public abstract void dispose();

    public abstract void sendObject(final Proto.SendObject sendObject, ProtoTcpConnection tcpConnection);

    public abstract void sendObject(final Proto.SendObject sendObject);
}