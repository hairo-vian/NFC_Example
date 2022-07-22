package com.example.nfc_example

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReceiverActivity : AppCompatActivity() {
    private val nfcAdapter by lazy { NfcAdapter.getDefaultAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)
    }

    override fun onResume() {
        super.onResume()

        // foreground dispatch should be enabled here, as onResume is the guaranteed place where app
        // is in the foreground
        enableForegroundDispatch(this, nfcAdapter)
        receiveMessageFromDevice(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        receiveMessageFromDevice(intent!!)
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch(this, nfcAdapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        disableForegroundDispatch(this, nfcAdapter)
    }

    private fun receiveMessageFromDevice(intent: Intent) {
        val action = intent.action
        Log.d("TAG", "receiveMessageFromDevice: $action")
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val inNdefMessage = parcelables!![0] as NdefMessage
            Log.d("TAG", "receiveMessageFromDevice: $inNdefMessage")
            val inNdefRecords = inNdefMessage.records
            Log.d("TAG", "receiveMessageFromDevice: $inNdefRecords")
            val ndefRecord_0 = inNdefRecords[0]
            val inMessage = String(ndefRecord_0.payload)
            findViewById<TextView>(R.id.tvReceive).text = inMessage
        }
    }

    // Foreground dispatch holds the highest priority for capturing NFC intents
    // then go activities with these intent filters:
    // 1) ACTION_NDEF_DISCOVERED
    // 2) ACTION_TECH_DISCOVERED
    // 3) ACTION_TAG_DISCOVERED
    // always try to match the one with the highest priority, cause ACTION_TAG_DISCOVERED is the most
    // general case and might be intercepted by some other apps installed on your device as well
    // When several apps can match the same intent Android OS will bring up an app chooser dialog
    // which is undesirable, because user will most likely have to move his device from the tag or another
    // NFC device thus breaking a connection, as it's a short range

    private fun enableForegroundDispatch(activity: AppCompatActivity, adapter: NfcAdapter) {

        // here we are setting up receiving activity for a foreground dispatch
        // thus if activity is already started it will take precedence over any other activity or app
        // with the same intent filters
        val intent = Intent(activity.applicationContext, activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        //
        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)
        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()
        filters[0] = IntentFilter()
        filters[0]!!.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
        filters[0]!!.addCategory(Intent.CATEGORY_DEFAULT)
        try {
            filters[0]!!.addDataType("text/plain")
        } catch (ex: MalformedMimeTypeException) {
            throw RuntimeException("Check your MIME type")
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }

    private fun disableForegroundDispatch(activity: AppCompatActivity?, adapter: NfcAdapter) {
        adapter.disableForegroundDispatch(activity)
    }
}