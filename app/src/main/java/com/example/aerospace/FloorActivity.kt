package com.example.aerospace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FloorActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var database5: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor)

        // Firebase 초기화 (2층 및 5층 데이터)
        database = FirebaseDatabase.getInstance("https://locker-d8b7d-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Lockers")
        database5 = FirebaseDatabase.getInstance("https://locker-d8b7d-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Lockers5")

        // 데이터 변경 리스너 (2층)
        setupRealtimeDatabaseListener()

        // 데이터 변경 리스너 (5층)
        setupRealtimeDatabaseListenerFor5thFloor()

        // 2층 버튼 클릭 시 LockerAreaActivity로 이동 및 층 정보 전달
        findViewById<Button>(R.id.buttonFloor2).setOnClickListener {
            val intent = Intent(this, LockerAreaActivity::class.java)
            intent.putExtra("floorNumber", 2)  // 2층 정보 추가
            startActivity(intent)
        }

        // 5층 버튼 클릭 시 LockerArea5Activity로 이동 및 층 정보 전달
        findViewById<Button>(R.id.buttonFloor5).setOnClickListener {
            val intent = Intent(this, LockerArea5Activity::class.java)
            intent.putExtra("floorNumber", 5)  // 5층 정보 추가
            startActivity(intent)
        }
    }

    // Firebase에서 2층 실시간 데이터 변경 리스너 설정
    private fun setupRealtimeDatabaseListener() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 데이터가 변경될 때마다 호출됨
                val lockerList = mutableListOf<Locker>()
                for (snapshot in dataSnapshot.children) {
                    val isUsed = snapshot.child("isUsed").getValue(Boolean::class.java) ?: false
                    val lockerNumber = snapshot.key?.replace("Locker", "")?.toIntOrNull() ?: continue
                    val useUntil = snapshot.child("useUntil").getValue(String::class.java) ?: ""
                    val studentId = snapshot.child("studentId").getValue(String::class.java) ?: ""  // studentId 추가
                    val locker = Locker(lockerNumber, isUsed, useUntil, studentId)  // Locker에 studentId 추가
                    lockerList.add(locker)
                }
                // 데이터 업데이트를 UI에 반영하는 코드 (2층 사물함 상태 업데이트)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터 가져오기 실패 처리
            }
        })
    }

    // Firebase에서 5층 실시간 데이터 변경 리스너 설정
    private fun setupRealtimeDatabaseListenerFor5thFloor() {
        database5.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 5층 데이터가 변경될 때마다 호출됨
                val lockerList = mutableListOf<Locker>()
                for (snapshot in dataSnapshot.children) {
                    val isUsed = snapshot.child("isUsed").getValue(Boolean::class.java) ?: false
                    val lockerNumber = snapshot.key?.replace("Locker", "")?.toIntOrNull() ?: continue
                    val useUntil = snapshot.child("useUntil").getValue(String::class.java) ?: ""
                    val studentId = snapshot.child("studentId").getValue(String::class.java) ?: ""  // studentId 추가
                    val locker = Locker(lockerNumber, isUsed, useUntil, studentId)  // Locker에 studentId 추가
                    lockerList.add(locker)
                }
                // 5층 데이터 업데이트를 UI에 반영하는 코드 (5층 사물함 상태 업데이트)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터 가져오기 실패 처리
            }
        })
    }
}
