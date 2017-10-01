package com.betmansmall.game.server;

import com.badlogic.gdx.Gdx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by betma on 10.09.2017.
 */

public class Client {
    public Socket socket = null;
    public ObjectInputStream objectInputStream = null;
    public ObjectOutputStream objectOutputStream = null;
    public BufferedReader in = null;
    public PrintWriter out = null;

    public Client(Socket socket) throws Exception {;
        Gdx.app.log("Client::Client(" + socket + ")", "--");
        this.socket = socket;

//        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        Gdx.app.log("Client::Client()", "-- objectInputStream:" + (objectInputStream != null));
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        Gdx.app.log("Client::Client()", "-- objectOutputStream:" + (objectOutputStream != null));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Gdx.app.log("Client::Client()", "-- in:" + (in != null));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        Gdx.app.log("Client::Client()", "-- out:" + (out != null));

//        out.println("TTW::SERVER_01");
//        out.flush();

//        String dataFromClient, output;
//        System.out.println("Wait for messages");
//        while ((dataFromClient = in.readLine()) != null) {
//            if (dataFromClient.contains("TTW::GAme_01")) {
//                out.println("OK, igraem!");
//            }
//            if (dataFromClient.equalsIgnoreCase("exit")) {
//                break;
//            }
//            out.println("S ::: " + dataFromClient);
//            System.out.println(dataFromClient);
//        }
    }

    public void dispose() throws Exception {
        out.close();
        in.close();
        socket.close();
    }
}
