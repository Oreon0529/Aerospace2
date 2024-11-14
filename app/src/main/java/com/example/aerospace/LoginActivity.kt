package com.example.aerospace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerButton: TextView = findViewById<TextView>(R.id.registerButton)
        registerButton.setOnClickListener {
            val registerIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        val idText: EditText = findViewById(R.id.idText)
        val passwordText: EditText = findViewById(R.id.passwordText)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val userID = idText.text.toString()
            val userPassword = passwordText.text.toString()

            val responseListener = com.android.volley.Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if (success) {
                        val builder = AlertDialog.Builder(this@LoginActivity)
                        dialog = builder.setMessage("로그인에 성공했습니다.")
                            .setPositiveButton("확인", null)
                            .create()
                        dialog?.show()

                        // DashboardActivity로 이동
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val builder = AlertDialog.Builder(this@LoginActivity)
                        dialog = builder.setMessage("계정을 다시 확인하세요.")
                            .setNegativeButton("다시 시도", null)
                            .create()
                        dialog?.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val loginRequest = LoginRequest(userID, userPassword, responseListener)
            val queue: RequestQueue = Volley.newRequestQueue(this@LoginActivity)
            queue.add(loginRequest)
        }
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
        dialog = null
    }
}
