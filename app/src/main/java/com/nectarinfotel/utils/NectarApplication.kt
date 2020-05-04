package com.nectarinfotel.utils

import android.app.Application
import android.preference.PreferenceManager
import com.nectarinfotel.retrofit.RetroAPIInterface
import com.nectarinfotel.retrofit.Retrofit
import com.nectarinfotel.ui.login.LoginActivity
import java.util.*

class NectarApplication : Application(){

    companion object {
        var mRetroClient: RetroAPIInterface? = null
        var userID : String = ""
         const val UserName : String = ""
          var Password : String = ""

    }

    override fun onCreate() {
        super.onCreate()

        mRetroClient = Retrofit.run { getClient()!!.create(RetroAPIInterface::class.java) }

     /*   var change = ""
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val language = sharedPreferences.getString("language", "bak")
        if (language == "Turkish") {
            change="tr"
        } else if (language=="English" ) {
            change = "en"
        }else {
            change =""
        }

        LoginActivity.dLocale = Locale(change) //set any locale you want here*/
    }

}