package com.betmansmall.game;

import com.betmansmall.enums.SessionState;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;

public abstract class SessionThread extends Thread implements TcpSocketListener {
    public SessionState sessionState;

    public abstract void dispose();

    public abstract void sendObject(final SendObject sendObject, TcpConnection tcpConnection);

    public abstract void sendObject(final SendObject sendObject);
}
