package com.example.aerospace

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class ValidateRequest(
    userID: String,
    listener: Response.Listener<String>
) : StringRequest(Method.POST, URL, listener, null) {

    companion object {
        private const val URL = "https://dhwnsgur4449.mycafe24.com/Validate.php"
    }

    private val parameters: Map<String, String> = hashMapOf(
        "userID" to userID
    )

    override fun getParams(): Map<String, String> {
        return parameters
    }
}