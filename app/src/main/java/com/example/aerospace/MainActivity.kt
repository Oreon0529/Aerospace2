package com.example.aerospace

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

        // BackgroundTask 실행 (서버에서 공지사항 데이터 가져오기)
        BackgroundTask().execute()
    }

    inner class BackgroundTask : AsyncTask<Void, Void, String>() {

        private var target: String? = null

        override fun onPreExecute() {
            target = "http://dhwnsgur4449.mycafe24.com/NoticeList.php"
        }

        override fun doInBackground(vararg voids: Void): String? {
            return try {
                val url = URL(target)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                val inputStream = httpURLConnection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var temp: String?

                while (bufferedReader.readLine().also { temp = it } != null) {
                    stringBuilder.append(temp).append("\n")
                }

                bufferedReader.close()
                inputStream.close()
                httpURLConnection.disconnect()

                Log.d("MainActivity", "서버 응답: ${stringBuilder.toString()}")
                stringBuilder.toString().trim()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: String?) {
            try {
                val jsonObject = JSONObject(result)
                val jsonArray = jsonObject.getJSONArray("response")
                var count = 0
                while (count < jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(count)
                    val noticeContent = `object`.getString("noticeContent")
                    val noticeName = `object`.getString("noticeName")
                    val noticeDate = `object`.getString("noticeDate")
                    val notice = Notice(noticeContent, noticeName, noticeDate)
                    noticeList.add(notice)
                    adapter.notifyDataSetChanged()
                    count++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var pressedTime: Long = 0

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (System.currentTimeMillis() > pressedTime + 2000) {
            pressedTime = System.currentTimeMillis()
            Toast.makeText(applicationContext, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }
    }
}
