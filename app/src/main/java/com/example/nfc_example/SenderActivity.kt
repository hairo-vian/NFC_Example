package com.example.nfc_example

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SenderActivity : AppCompatActivity(), NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback  {
    private val nfcAdapter by lazy { NfcAdapter.getDefaultAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender)

        nfcAdapter.setNdefPushMessageCallback(this,this)
        nfcAdapter.setOnNdefPushCompleteCallback(this,this)
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        // creating outcoming NFC message with a helper method
        // you could as well create it manually and will surely need, if Android version is too low
        val outString = "D24F6331"
        val outBytes = outString.toByteArray()
        val outRecord = NdefRecord.createMime("text/plain", outBytes)

        return NdefMessage(outRecord)
    }

    override fun onNdefPushComplete(event: NfcEvent?) {
        runOnUiThread { Toast.makeText(this, "message sent", Toast.LENGTH_SHORT).show() }
        findViewById<TextView>(R.id.tvSend).text = "SENT"
    }
}