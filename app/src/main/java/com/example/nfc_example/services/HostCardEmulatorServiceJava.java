package com.example.nfc_example.services;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.example.nfc_example.utils.PrefContracts;
import com.example.nfc_example.utils.PrefUtil;
import com.example.nfc_example.utils.UtilsJava;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class HostCardEmulatorServiceJava extends HostApduService {
    public static final String TAG = "Host Card Emulator";
    public static final String STATUS_SUCCESS = "9000";
    public static final String STATUS_FAILED = "6F00";
    public static final String CLA_NOT_SUPPORTED = "6E00";
    public static final String INS_NOT_SUPPORTED = "6D00";
    public static final String AID = "D00000D24F6331";
    public static final String SELECT_INS = "A4";
    public static final String DEFAULT_CLA = "00";
    public static final int MIN_APDU_LENGTH = 18;

    public void onDeactivated(int reason) {
        Log.d("Host Card Emulator", "Deactivated: " + reason);
    }

    @NotNull
    public byte[] processCommandApdu(byte[] commandApdu,Bundle extras) {
        if (commandApdu == null) {
            return UtilsJava.hexStringToByteArray(STATUS_FAILED);
        }
        String hexCommandApdu = UtilsJava.toHex(commandApdu);
        if (hexCommandApdu.length() < MIN_APDU_LENGTH) {
            return UtilsJava.hexStringToByteArray(STATUS_FAILED);
        }

        if (!hexCommandApdu.substring(0, 2).equals(DEFAULT_CLA)) {
            return UtilsJava.hexStringToByteArray(CLA_NOT_SUPPORTED);
        }

        if (!hexCommandApdu.substring(2, 4).equals(SELECT_INS)) {
            return UtilsJava.hexStringToByteArray("6D00");
        }

        if (hexCommandApdu.substring(10, 24).equals(AID)) {
            PrefUtil.getSharedPreference(getApplicationContext());
            return (PrefUtil.getBoolean(PrefContracts.CARD_SAVED)
                    ? UtilsJava.hexStringToByteArray(PrefUtil.getString(PrefContracts.TAG_HEX_ID))
                    :UtilsJava.hexStringToByteArray(STATUS_SUCCESS));
        } else {
            return UtilsJava.hexStringToByteArray(STATUS_FAILED);
        }
    }
}
