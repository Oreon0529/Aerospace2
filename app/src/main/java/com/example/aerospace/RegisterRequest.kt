package com.example.aerospace

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class RegisterRequest(
    userID: String,
    userPassword: String,
    userGender: String,
    userMajor: String,
    userEmail: String,
    listener: Response.Listener<String>
) : StringRequest(Method.POST, URL, listener, null) {

    companion object {
        private const val URL = "https://dhwnsgur4449.mycafe24.com/Register.php"
    }

    private val parameters: Map<String, String> = hashMapOf(
        "userID" to userID,
        "userPassword" to userPassword,
        "userGender" to userGender,
        "userMajor" to userMajor,
        "userEmail" to userEmail
    )

    override fun getParams(): Map<String, String> {
        return parameters
    }
}