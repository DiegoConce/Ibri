package com.ibri.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {
    companion object {
        const val ACCOUNT_ID = "IBRIUSERID"
        const val ACCOUNT_ROLE = "IBRIUSERROLE"
        const val ACCOUNT_NAME = "IBRIUSERNAME"
        const val ACCOUNT_BIO = "IBRIUSERBIO"
        const val ACCOUNT_AVATAR = "IBRIUSERAVATAR"
        const val ACCOUNT_AVATAR_ID = "IBRIUSERAVATARID"
        const val ACCOUNT_NUM_OF_EVENTS = "IBRINUMOFEVENTS"

        const val USER_SURNAME = "IBRIUSERSURNAME"
        const val USER_BIRTHDAY = "IBRIUSERBBD"

        const val COMPANY_PIVA = "IBRIUSERBCMPIVA"


        fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        }
    }
}