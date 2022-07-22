package com.example.nfc_example

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.tech.NfcA
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import java.io.Reader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val nfcAdater = NfcAdapter.getDefaultAdapter(this)
        val isNfcSupported = nfcAdater != null
        if (!isNfcSupported){
            Toast.makeText(this, "NFC Not Supported", Toast.LENGTH_SHORT).show()
        }

        if (!nfcAdater.isEnabled){
            Toast.makeText(this, "NFC Not Enabled", Toast.LENGTH_SHORT).show()
        }

        val enableButtons = isNfcSupported && nfcAdater.isEnabled

        findViewById<Button>(R.id.btReceive).isEnabled = enableButtons
        findViewById<Button>(R.id.btSend).isEnabled = enableButtons
        findViewById<Button>(R.id.btReader).isEnabled = enableButtons
        findViewById<Button>(R.id.btCardSaver).isEnabled = enableButtons
    }

    fun onClick(view:View){
        when(view.id){
            R.id.btReceive-> startActivity(Intent(this,ReceiverActivity::class.java))
            R.id.btSend -> startActivity(Intent(this,SenderActivity::class.java))
            R.id.btReader -> startActivity(Intent(this,ReaderActivity::class.java))
            R.id.btCardSaver -> startActivity(Intent(this,CardSaverActivity::class.java))
        }
    }
}