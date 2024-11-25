package com.example.aerospace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager

class BuildingMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_map)

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val imageList = listOf(
            R.drawable.firstfloor,
            R.drawable.secondfloor,
            R.drawable.sixfloor,
            R.drawable.sevenfloor,
            R.drawable.eightfloor
        )
        val adapter = ImagePagerAdapter(this, imageList)
        viewPager.adapter = adapter
    }
}
