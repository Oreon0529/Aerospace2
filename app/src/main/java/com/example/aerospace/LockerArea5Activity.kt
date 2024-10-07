package com.example.aerospace

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton

class LockerArea5Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locker_area_5)

        // 5-1부터 5-6까지의 버튼 설정 (각 버튼 클릭 시 사물함 범위를 전달)
        findViewById<Button>(R.id.buttonArea5_1).setOnClickListener { openLockerGrid(1) }
        findViewById<Button>(R.id.buttonArea5_2).setOnClickListener { openLockerGrid(2) }
        findViewById<Button>(R.id.buttonArea5_3).setOnClickListener { openLockerGrid(3) }
        findViewById<Button>(R.id.buttonArea5_4).setOnClickListener { openLockerGrid(4) }
        findViewById<Button>(R.id.buttonArea5_5).setOnClickListener { openLockerGrid(5) }
        findViewById<Button>(R.id.buttonArea5_6).setOnClickListener { openLockerGrid(6) }
        val mapButton5: ImageButton = findViewById(R.id.mapButton5)

        mapButton5.setOnClickListener{
            val intent = Intent(this, ShowFloorMap_5F::class.java)
            startActivity(intent)
        }
    }

    // 각 영역에 맞는 LockerGridActivity로 이동
    private fun openLockerGrid(areaNumber: Int) {
        val intent = Intent(this, LockerGridActivity::class.java)
        intent.putExtra("floorNumber", 5)  // 5층임을 전달
        intent.putExtra("areaNumber", areaNumber)  // 5층의 영역 정보 전달
        startActivity(intent)
    }
}
