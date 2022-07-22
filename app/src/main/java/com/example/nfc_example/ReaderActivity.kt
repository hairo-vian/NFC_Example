package com.example.nfc_example

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.nfc_example.utils.Utils

class ReaderActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private val nfcAdapter by lazy { NfcAdapter.getDefaultAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            this, this,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        isoDep.connect()
        val response = isoDep.transceive(
            Utils.hexStringToByteArray(
                "00A4040007D00000D24F6331"
            )
        )
        runOnUiThread {
            val hexStringResponse = Utils.toHex(response)
            if (hexStringResponse == "9000") {
                Toast.makeText(
                    this,
                    "NO ID CARD DETECTED, HAVE YOU SAVED YOUR ID CARD BEFORE?",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if(hexStringResponse.length == 4){
                Toast.makeText(this, "ERROR, CODE : $hexStringResponse", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Success, TAG ID : $hexStringResponse", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        isoDep.close()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}