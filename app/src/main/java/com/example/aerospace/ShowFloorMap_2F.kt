package com.example.aerospace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class ShowFloorMap_2F : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showing_2floor_map)

        // ImageView에 이미지를 설정할 수 있습니다.
        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.secondmap) // your_image는 보여줄 이미지의 리소스 ID
    }
}