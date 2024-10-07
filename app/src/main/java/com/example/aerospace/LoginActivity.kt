package com.example.aerospace

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

    var pressedTime: Long = 0 //'뒤로가기' 버튼 클릭했을 때의 시간

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        //마지막으로 누른 '뒤로가기' 버튼 클릭 시간이 이전의 '뒤로가기' 버튼 클릭 시간과의 차이가 2초보다 크면

        if (System.currentTimeMillis() > pressedTime + 2000) {
            //현재 시간을 pressedTime 에 저장
            pressedTime = System.currentTimeMillis()
            Toast.makeText(applicationContext, "한번 더 누르면 종료", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "종료 완료", Toast.LENGTH_SHORT).show()
            // 앱 종료
            finish()
        }
    }
}
