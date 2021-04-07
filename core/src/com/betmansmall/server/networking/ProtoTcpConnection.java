package com.betmansmall.server.networking;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.utils.logging.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import protobuf.Proto;

public class ProtoTcpConnection {
    private final ProtoTcpSocketListener tcpSocketListener;
    private final Socket socket;
    private final Thread thread;

    private final OutputStream outputStream;
    private final InputStream inputStream;

    public ProtoTcpConnection(ProtoTcpSocketListener tcpSocketListener, String host, int port) throws IOException {
        this(tcpSocketListener, new Socket(host, port));
        Logger.logFuncEnd();
    }

    public ProtoTcpConnection(final ProtoTcpSocketListener tcpSocketListener, final Socket socket) throws IOException {
        Logger.logFuncStart();
        this.tcpSocketListener = tcpSocketListener;
        this.socket = socket;

        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpSocketListener.onConnectionReady(ProtoTcpConnection.this);
                    while (!thread.isInterrupted() && !socket.isClosed() && socket.isConnected()) {
                        Proto.SendObject sendObject = Proto.SendObject.parseDelimitedFrom(inputStream);
                        tcpSocketListener.onReceiveObject(ProtoTcpConnection.this, sendObject);
                    }
                } catch (EOFException e) { // AuthServerThread Client Disconnect
                    tcpSocketListener.onException(ProtoTcpConnection.this, e);
                } catch (SocketException e) { // ServerSessionThread Player Disconnect
                    tcpSocketListener.onException(ProtoTcpConnection.this, e);
                } catch (Exception e) { // Other Exceptions
                    tcpSocketListener.onException(ProtoTcpConnection.this, e);
                } finally {
                    tcpSocketListener.onDisconnect(ProtoTcpConnection.this);
                }
            }
        });
        thread.start();
        Logger.logFuncEnd();
    }

    public void sendObject(final Proto.SendObject sendObject) {
//        Logger.logFuncStart("sendObject:" + sendObject);
        try {
            sendObject.writeDelimitedTo(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            tcpSocketListener.onException(ProtoTcpConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        Logger.logFuncStart();
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            tcpSocketListener.onException(ProtoTcpConnection.this, e);
        }
    }

    public String getSocketIP() {
        return socket.getRemoteSocketAddress().toString();
    }

    public String getRemoteHost() {
        return socket.getInetAddress().getHostAddress();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("ProtoTcpConnection[");
        if (full) {
            sb.append("tcpSocketListener:" + tcpSocketListener);
            sb.append(",socket:" + socket);
            sb.append(",thread:" + thread);
            sb.append(",inputStream:" + inputStream);
            sb.append(",outputStream:" + outputStream);
        } else {
            sb.append("inetAddress:" + socket.getInetAddress());
        }
        sb.append("]");
        return sb.toString();
    }
}
