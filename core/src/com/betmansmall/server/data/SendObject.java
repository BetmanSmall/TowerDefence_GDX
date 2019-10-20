package com.betmansmall.server.data;

import java.util.ArrayList;

public class SendObject {
    public enum SendObjectEnum {
        CONNECT_BY_CLIENT,
        CONNECT_BY_SERVER
    }
    SendObjectEnum sendObjectEnum;
    ArrayList<NetworkPackage> networkPackages;
}
