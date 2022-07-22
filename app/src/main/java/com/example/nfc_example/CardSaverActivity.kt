package com.example.nfc_example

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nfc_example.utils.PrefContracts
import com.example.nfc_example.utils.PrefUtil
import com.example.nfc_example.utils.Utils

class CardSaverActivity : AppCompatActivity() {
    private val nfcAdapter by lazy { NfcAdapter.getDefaultAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_saver)
    }

    override fun onResume() {
        super.onResume()
        enableForegroundDispatch(this,nfcAdapter)
        resolveIntent(intent)
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch(this, nfcAdapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        disableForegroundDispatch(this, nfcAdapter)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
//        setIntent(intent)
        resolveIntent(intent)
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = (intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?)!!
            findViewById<TextView>(R.id.tvCardSaver).text = detectTagData(tag)
//            val payload: ByteArray = detectTagData(tag).toByteArray()
        }
    }

    private fun detectTagData(tag: Tag): String {
        val sb = StringBuilder()
        val id = tag.id

        //Save information to sharedpreferences
        PrefUtil.getSharedPreference(this)
        PrefUtil.setString(PrefContracts.TAG_HEX_ID,Utils.toHex(id))
        PrefUtil.setString(PrefContracts.TAG_DEC_ID,Utils.toDec(id).toString())
        PrefUtil.setBoolean(PrefContracts.CARD_SAVED,true)

        sb.append("ID (hex): ").append(Utils.toHex(id)).append('\n')
//        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(Utils.toDec(id)).append('\n')
//        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        for (tech in tag.techList) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                var type = "Unknown"
                try {
                    val mifareTag = MifareClassic.get(tag)
                    when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> type = "Classic"
                        MifareClassic.TYPE_PLUS -> type = "Plus"
                        MifareClassic.TYPE_PRO -> type = "Pro"
                    }
                    sb.append("Mifare Classic type: ")
                    sb.append(type)
                    sb.append('\n')
                    sb.append("Mifare size: ")
                    sb.append(mifareTag.size.toString() + " bytes")
                    sb.append('\n')
                    sb.append("Mifare sectors: ")
                    sb.append(mifareTag.sectorCount)
                    sb.append('\n')
                    sb.append("Mifare blocks: ")
                    sb.append(mifareTag.blockCount)
                } catch (e: Exception) {
                    sb.append("Mifare classic error: " + e.message)
                }
            }
            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }
        Log.v("TAG", sb.toString())
        return sb.toString()
    }

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
        } catch (ex: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("Check your MIME type")
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, null, null)
    }

    private fun disableForegroundDispatch(activity: AppCompatActivity?, adapter: NfcAdapter) {
        adapter.disableForegroundDispatch(activity)
    }
}