package com.example.tradefunc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.tradefunc.ui.theme.TradeFuncTheme
import com.google.firebase.auth.FirebaseAuth

class RentalHistoryActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 익명 인증
        auth = FirebaseAuth.getInstance()
        authenticateUser()

        setContent {
            TradeFuncTheme {
                Scaffold { padding ->
                    RentalHistoryScreen(modifier = Modifier.padding(padding))
                }
            }
        }
    }

    private fun authenticateUser() {
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Authenticated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}
