package com.example.lijinming.hdtest.bleManage;

import android.bluetooth.BluetoothGattCharacteristic;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/22.
 */
public class Characteristic implements Serializable {
    private BluetoothGattCharacteristic characteristic;
    public BluetoothGattCharacteristic getCharacteristic(){
        return characteristic;
    }
    public void setCharacteristic(BluetoothGattCharacteristic characteristic ){
        this.characteristic = characteristic;
    }
}
