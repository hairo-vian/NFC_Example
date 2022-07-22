package com.example.nfc_example.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.nfc_example.R

class PrefUtil(val context: Context) {
    companion object {
        private var sharedPreference: SharedPreferences? = null

        fun getSharedPreference(context: Context): SharedPreferences {
            if (sharedPreference == null) {
                synchronized(SharedPreferences::class.java) {
                    if (sharedPreference == null) {
                        sharedPreference = context.getSharedPreferences(
                            context.getString(R.string.app_name),
                            Context.MODE_PRIVATE
                        )
                    }
                }
            }
            return sharedPreference!!
        }

        fun setString(key: String, value: String) {
            val editor = sharedPreference!!.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getString(key: String): String {
            return sharedPreference!!.getString(key, "")!!
        }

        fun setBoolean(key:String,value:Boolean){
            val editor = sharedPreference!!.edit()
            editor.putBoolean(key,value)
            editor.apply()
        }

        fun getBoolean(key:String):Boolean{
            return sharedPreference!!.getBoolean(key,false)
        }
    }

    init {
        getSharedPreference(context)
    }
}