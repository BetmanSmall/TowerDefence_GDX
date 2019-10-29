package com.betmansmall.server.networking;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.Player;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TcpConnection {
    private final TcpSocketListener tcpSocketListener;
    private final Socket socket;
    private final Thread thread;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public TcpConnection(TcpSocketListener tcpSocketListener, String host, int port) throws IOException {
        this(tcpSocketListener, new Socket(host, port));
        Logger.logFuncEnd();
    }

    public TcpConnection(final TcpSocketListener tcpSocketListener, final Socket socket) throws IOException {
        Logger.logFuncStart();
        this.tcpSocketListener = tcpSocketListener;
        this.socket = socket;

        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpSocketListener.onConnectionReady(TcpConnection.this);
                    while (!thread.isInterrupted() && !socket.isClosed()) {
                        SendObject sendObject = (SendObject)objectInputStream.readObject();
                        tcpSocketListener.onReceiveObject(TcpConnection.this, sendObject);
                    }
                } catch (Exception e) {
                    tcpSocketListener.onException(TcpConnection.this, e);
                } finally {
                    tcpSocketListener.onDisconnect(TcpConnection.this);
                }
            }
        });
        thread.start();
        Logger.logFuncEnd();
    }

    public synchronized void sendObject(final SendObject sendObject) {
        Logger.logFuncStart("sendObject:" + sendObject);
        try {
            objectOutputStream.writeObject(sendObject);
            objectOutputStream.flush();
        } catch (IOException e) {
            tcpSocketListener.onException(TcpConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        Logger.logFuncStart();
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            tcpSocketListener.onException(TcpConnection.this, e);
        }
    }

    public String getSocketIP() {
        return socket.getRemoteSocketAddress().toString();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("TcpConnection[");
        if (full) {
            sb.append("tcpSocketListener:" + tcpSocketListener);
            sb.append(",socket:" + socket);
            sb.append(",thread:" + thread);
            sb.append(",objectInputStream:" + objectInputStream);
            sb.append(",objectOutputStream:" + objectOutputStream);
        } else {
            sb.append("inetAddress:" + socket.getInetAddress());
        }
        sb.append("]");
        return sb.toString();
    }
}
