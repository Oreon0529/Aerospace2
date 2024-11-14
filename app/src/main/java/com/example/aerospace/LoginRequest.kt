package com.example.aerospace

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class LoginRequest(
    userID: String,
    userPassword: String,
    listener: Response.Listener<String>
) : StringRequest(Method.POST, URL, listener, null) {

    private val parameters: MutableMap<String, String> = HashMap()

    init {
        parameters["userID"] = userID
        parameters["userPassword"] = userPassword
    }

    override fun getParams(): Map<String, String> {
        return parameters
    }

    companion object {
        private const val URL = "https://dhwnsgur4449.mycafe24.com/Login.php"
    }
}