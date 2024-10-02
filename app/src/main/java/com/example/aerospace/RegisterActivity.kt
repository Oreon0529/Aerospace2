package com.example.aerospace

import android.os.Bundle
import android.webkit.WebView.FindListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import com.android.volley.Response
import com.android.volley.toolbox.Volley


class RegisterActivity : AppCompatActivity() {
    private lateinit var adapter: ArrayAdapter<CharSequence>
    private lateinit var spinner: Spinner
    private var userId: String? = null
    private var userPassword: String? = null
    private var userGender: String? = null
    private var userMajor: String? = null
    private var userEmail: String? = null
    private var dialog: AlertDialog? = null
    private var validate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinner = findViewById(R.id.majorSpinner)
        adapter = ArrayAdapter.createFromResource(this, R.array.major, android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val idText: EditText = findViewById(R.id.idText)
        val passwordText: EditText = findViewById(R.id.passwordText)
        val emailText: EditText = findViewById(R.id.emailText)

        val genderGroup: RadioGroup = findViewById(R.id.genderGroup)
        val genderGroupID = genderGroup.checkedRadioButtonId
        userGender = findViewById<RadioButton>(genderGroupID).text.toString()

        genderGroup.setOnCheckedChangeListener { _, i ->
            val genderButton: RadioButton = findViewById(i)
            userGender = genderButton.text.toString()
        }

        val validateButton: Button = findViewById(R.id.validateButton)
        validateButton.setOnClickListener {
            val userID = idText.text.toString()

            if (validate) {
                return@setOnClickListener
            }

            if (userID.isEmpty()) {
                val builder = AlertDialog.Builder(this@RegisterActivity)
                dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.").setPositiveButton("확인", null).create()
                dialog!!.show()
                return@setOnClickListener
            }

            val responseListener = Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if (success) {
                        val builder = AlertDialog.Builder(this@RegisterActivity)
                        dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create()
                        dialog!!.show()
                        idText.isEnabled = false
                        validate = true
                        idText.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray))
                        validateButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGray))
                    } else {
                        val builder = AlertDialog.Builder(this@RegisterActivity)
                        dialog = builder.setMessage("사용할 수 없는 아이디입니다.").setNegativeButton("확인", null).create()
                        dialog!!.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val validateRequest = ValidateRequest(userID, responseListener)
            val queue = Volley.newRequestQueue(this@RegisterActivity)
            queue.add(validateRequest)
        }

        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            val userID = idText.text.toString()
            val userPassword = passwordText.text.toString()
            val userMajor = spinner.selectedItem.toString()
            val userEmail = emailText.text.toString()

            if (!validate) {
                val builder = AlertDialog.Builder(this@RegisterActivity)
                dialog = builder.setMessage("먼저 중복 체크를 해주세요.").setPositiveButton("확인", null).create()
                dialog!!.show()
                return@setOnClickListener
            }

            if (userID.isEmpty() || userPassword.isEmpty() || userMajor.isEmpty() || userEmail.isEmpty() || userGender == null) {
                val builder = AlertDialog.Builder(this@RegisterActivity)
                dialog = builder.setMessage("빈 칸 없이 입력해주세요.").setPositiveButton("확인", null).create()
                dialog!!.show()
                return@setOnClickListener
            }

            val responseListener = Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if (success) {
                        val builder = AlertDialog.Builder(this@RegisterActivity)
                        dialog = builder.setMessage("회원 등록에 성공했습니다.").setPositiveButton("확인", null).create()
                        dialog!!.show()
                        finish()
                    } else {
                        val builder = AlertDialog.Builder(this@RegisterActivity)
                        dialog = builder.setMessage("회원 등록에 실패했습니다.").setNegativeButton("확인", null).create()
                        dialog!!.show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val registerRequest = RegisterRequest(userID, userPassword, userGender!!, userMajor, userEmail, responseListener)
            val queue = Volley.newRequestQueue(this@RegisterActivity)
            queue.add(registerRequest)
        }
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
        dialog = null
    }
}