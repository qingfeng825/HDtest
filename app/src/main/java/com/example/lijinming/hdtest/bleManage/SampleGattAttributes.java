package com.example.lijinming.hdtest.bleManage;

import java.util.UUID;

public class SampleGattAttributes {


    static UUID HeartService = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    static UUID PusleCharacteristic = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
    static UUID ECGCharacteristic = UUID.fromString("0000fff8-0000-1000-8000-00805f9b34fb");
    static UUID SoundCharacteristic = UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb");
    public static String CLIENT_PulseCHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_ECGCHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_SoundCHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

}
