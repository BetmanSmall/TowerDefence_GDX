package com.betmansmall.game.GameScreenInteface;

/**
 * Created by Descogle on 27.12.2016.
 */

//INCOSTYLATION IN ACTION
public class DeviceSettings {
    private static String RUNNING_DEVICE;

    public DeviceSettings() {
    }

    public void setDevice(String device) {
        RUNNING_DEVICE = device;
    }

    public String getDevice() {
        return  RUNNING_DEVICE;
    }
}