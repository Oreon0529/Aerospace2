package com.example.aerospace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class LockerAreaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locker_area)

        // 각 사물함 영역 버튼을 설정
        val area1Button: Button = findViewById(R.id.area1Button)
        val area2Button: Button = findViewById(R.id.area2Button)
        val area3Button: Button = findViewById(R.id.area3Button)
        val area4Button: Button = findViewById(R.id.area4Button)
        val area5Button: Button = findViewById(R.id.area5Button)

        // 각 버튼 클릭 시 해당 영역으로 이동
        area1Button.setOnClickListener {
            openLockerArea(1, 2)  // 2층으로 설정
        }

        area2Button.setOnClickListener {
            openLockerArea(2, 2)  // 2층으로 설정
        }

        area3Button.setOnClickListener {
            openLockerArea(3, 2)  // 2층으로 설정
        }

        area4Button.setOnClickListener {
            openLockerArea(4, 2)  // 2층으로 설정
        }

        area5Button.setOnClickListener {
            openLockerArea(5, 2)  // 2층으로 설정
        }
    }

    private fun openLockerArea(areaNumber: Int, floorNumber: Int) {
        val intent = Intent(this, LockerGridActivity::class.java)
        intent.putExtra("areaNumber", areaNumber)
        intent.putExtra("floorNumber", floorNumber) // 층 정보 추가
        startActivity(intent)
    }
}
