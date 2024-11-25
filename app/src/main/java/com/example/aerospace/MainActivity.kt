package com.example.aerospace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    private lateinit var noticeListView: ListView
    private lateinit var adapter: NoticeListAdapter
    private lateinit var noticeList: MutableList<Notice>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 공지사항 초기화
        noticeListView = findViewById(R.id.noticeListView)
        noticeList = mutableListOf()
        noticeList.add(Notice("공지사항입니다.", "오준혁", "2024-10-01"))
        noticeList.add(Notice("공지사항입니다.", "오준혁", "2024-10-02"))
        noticeList.add(Notice("공지사항입니다.", "오준혁", "2024-10-03"))
        noticeList.add(Notice("공지사항입니다.", "오준혁", "2024-10-04"))
        noticeList.add(Notice("공지사항입니다.", "오준혁", "2024-10-05"))
        noticeList.add(Notice("공지사항입니다.", "오준혁", "2024-10-07"))
        adapter = NoticeListAdapter(applicationContext, noticeList)
        noticeListView.adapter = adapter

        // 사물함 예약 버튼 클릭 시 동작
        val lockerButton: Button = findViewById(R.id.lockerButton)
        lockerButton.setOnClickListener {
            val intent = Intent(this, FloorActivity::class.java)
            startActivity(intent)
        }

        // 407동 지도 버튼 클릭 시 동작
        val mapButton: Button = findViewById(R.id.utilButton2)
        mapButton.setOnClickListener {
            val intent = Intent(this, BuildingMapActivity::class.java)
            startActivity(intent)
        }
    }
}
