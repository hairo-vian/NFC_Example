package com.example.nfc_example;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.nfc_example.utils.UtilsJava;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class ReaderActivityJava extends AppCompatActivity implements ReaderCallback {
    private NfcAdapter nfcAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_reader);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(this, this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V, null);
        }
    }

    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        try {
            isoDep.connect();
            final byte[] response = isoDep.transceive(UtilsJava.hexStringToByteArray("00A4040007D00000D24F6331"));

            this.runOnUiThread(() -> {
                String hexStringResponse = UtilsJava.toHex(response);
                if (hexStringResponse.equals("9000")) {
                    Toast.makeText(
                            this,
                            "NO ID CARD DETECTED, HAVE YOU SAVED YOUR ID CARD BEFORE?",
                            Toast.LENGTH_SHORT
                    ).show();
                } else if (hexStringResponse.length() == 4) {
                    Toast.makeText(this, "ERROR, CODE : " + hexStringResponse, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Success, TAG ID : " + hexStringResponse, Toast.LENGTH_SHORT).show();
                }

            });
            isoDep.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
