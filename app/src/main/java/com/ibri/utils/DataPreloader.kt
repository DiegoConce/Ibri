package com.ibri.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.Company
import com.ibri.model.User
import com.ibri.model.enum.Role
import java.text.SimpleDateFormat
import java.util.*

class DataPreloader {
    companion object {
        private val USER_INFO_ENDPOINT = Uri.parse("${BASE_URL}/user/get")
        private val COMPANY_INFO_ENDPOINT = Uri.parse("${BASE_URL}/company/get")
        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())
        private val pref: SharedPreferences =
            PreferenceManager.getSharedPreferences(MainApplication.applicationContext()!!)


        fun loadPersonalInfo() {
            if (pref.getString(PreferenceManager.ACCOUNT_ID, "") != "") {
                if (pref.getString(PreferenceManager.ACCOUNT_ROLE, "") != "") {
                    val role: Role? =
                        pref.getString(PreferenceManager.ACCOUNT_ROLE, "")
                            ?.let { Role.valueOf(it) }

                    if (role == Role.USER) {
                        loadUser(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
                    } else if (role == Role.COMPANY) {
                        loadCompany(pref.getString(PreferenceManager.ACCOUNT_ID, "")!!)
                    }
                }
            }
        }

        private fun loadUser(id: String) {
            val req =
                StringRequest(
                    Request.Method.GET, USER_INFO_ENDPOINT.buildUpon()
                        .appendQueryParameter("id", id).build().toString(),
                    {
                        registerUserResponse(it)
                    },
                    {

                    })
            volley.add(req)
        }


        private fun registerUserResponse(response: String?) {
            val user = Gson().fromJson(response, User::class.java)
            registerUser(user)
        }

        private fun loadCompany(id: String) {
            val req =
                StringRequest(Request.Method.GET, COMPANY_INFO_ENDPOINT.buildUpon()
                    .appendQueryParameter("id", id).build().toString(),
                    {
                        registerCompanyResponse(it)
                    },
                    {
                        it
                    })
            volley.add(req)
        }

        private fun registerCompanyResponse(response: String?) {
            val company = Gson().fromJson(response, Company::class.java)
            registerCompany(company)
        }


        fun registerCompany(company: Company?) {
            if (company != null) {
                with(pref.edit()) {
                    putString(PreferenceManager.ACCOUNT_ID, company.id)
                    putString(PreferenceManager.ACCOUNT_ROLE, Role.COMPANY.toString())
                    putString(PreferenceManager.ACCOUNT_NAME, company.name)
                    putString(PreferenceManager.COMPANY_PIVA, company.pIva)
                    putString(PreferenceManager.ACCOUNT_BIO, company.bio)
                    putString(PreferenceManager.ACCOUNT_NUM_OF_EVENTS, company.numOfEvents.toString())
                    if (company.avatar != null) {
                        putString(PreferenceManager.ACCOUNT_AVATAR, company.avatar!!.url)
                        putString(PreferenceManager.ACCOUNT_AVATAR_ID, company.avatar!!.id)
                    }
                    apply()
                }
            }
        }

        fun registerUser(user: User?) {
            if (user != null) {
                with(pref.edit()) {
                    putString(PreferenceManager.ACCOUNT_ID, user.id)
                    putString(PreferenceManager.ACCOUNT_ROLE, Role.USER.toString())
                    putString(PreferenceManager.ACCOUNT_BIO, user.bio)
                    putString(PreferenceManager.ACCOUNT_NAME, user.name)
                    putString(PreferenceManager.USER_SURNAME, user.surname)
                    putString(PreferenceManager.ACCOUNT_NUM_OF_EVENTS, user.numOfEvents.toString())
                    putString(
                        PreferenceManager.USER_BIRTHDAY,
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(user.birthday)
                    )
                    if (user.avatar != null) {
                        putString(PreferenceManager.ACCOUNT_AVATAR, user.avatar!!.url)
                        putString(PreferenceManager.ACCOUNT_AVATAR_ID, user.avatar!!.id)
                    }
                    apply()
                }
            }

        }
    }
}