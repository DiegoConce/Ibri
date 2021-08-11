package com.ibri.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.Question
import com.ibri.utils.BASE_URL

class QuestionAnswerRepository {
    companion object {
        private val GET_QUESTION_N_ANSWER_ENDPOINT = Uri.parse("${BASE_URL}/questions")
        private val MAKE_NEW_QUESTION_ENDPOINT = Uri.parse("${BASE_URL}/questions/new")
        private val GET_UNCOMPLETED_QUESTIONS_ENDPOINT =
            Uri.parse("${BASE_URL}/questions/uncompleted")
        private val CANCEL_QUESTION_ENDPOINT = Uri.parse("${BASE_URL}/questions/cancel")
        private val ANSWER_QUESTION_ENDPOINT = Uri.parse("${BASE_URL}/questions/answer")

        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())

        fun MakeNewQuestion(
            mutableMedia: MutableLiveData<String>,
            question: String,
            eventId: String
        ) {
            val req = object : StringRequest(
                Method.POST, MAKE_NEW_QUESTION_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["question"] = question
                    map["eventId"] = eventId
                    return map
                }
            }
            volley.add(req)
        }

        fun getQna(
            mutableMediaList: MutableLiveData<ArrayList<Question>>,
            eventId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                GET_QUESTION_N_ANSWER_ENDPOINT.buildUpon().appendQueryParameter("eventId", eventId)
                    .build().toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<Question>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun getUncompletedQuestions(
            mutableMediaList: MutableLiveData<ArrayList<Question>>,
            userId: String
        ) {
            val req = StringRequest(
                Request.Method.GET, GET_UNCOMPLETED_QUESTIONS_ENDPOINT.buildUpon()
                    .appendQueryParameter("userId", userId).build().toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<Question>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun deleteQuestion(
            mutableMedia: MutableLiveData<String>,
            questionId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                CANCEL_QUESTION_ENDPOINT.buildUpon().appendQueryParameter("id", questionId).build()
                    .toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            )
            volley.add(req)
        }

        fun answerQuestion(
            mutableMedia: MutableLiveData<String>,
            questionId: String,
            answer: String
        ) {
            val req = object : StringRequest(
                Method.POST, ANSWER_QUESTION_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["id"] = questionId
                    map["answer"] = answer
                    return map
                }
            }
            volley.add(req)
        }
    }
}