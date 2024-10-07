package com.example.aerospace
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var noticeListView: ListView
    private lateinit var adapter: NoticeListAdapter
    private lateinit var noticeList: MutableList<Notice>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
