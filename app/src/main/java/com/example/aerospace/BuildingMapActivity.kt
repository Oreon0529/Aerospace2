package com.example.aerospace

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import android.view.View // View 클래스 import

class BuildingMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_map)

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val swipeHint: TextView = findViewById(R.id.swipeHint)

        // 이미지 리스트 (순서 조정 포함)
        val imageList = listOf(
            R.drawable.firstfloor,
            R.drawable.secondfloor,
            R.drawable.fivefloor, // fivefloor 추가
            R.drawable.sixfloor,
            R.drawable.sevenfloor,
            R.drawable.eightfloor
        )

        // 어댑터에 리스트 전달
        val adapter = ImagePagerAdapter(this, imageList)
        viewPager.adapter = adapter

        // 힌트 항상 표시
        swipeHint.visibility = View.VISIBLE
    }
}
