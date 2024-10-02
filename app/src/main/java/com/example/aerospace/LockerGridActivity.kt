package com.example.aerospace

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class LockerGridActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var lockerAdapter: LockerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locker_grid)

        val areaNumber = intent.getIntExtra("areaNumber", 1)
        val floorNumber = intent.getIntExtra("floorNumber", 2)

        Log.d("LockerGridActivity", "AreaNumber: $areaNumber, FloorNumber: $floorNumber")

        // 각 영역에 맞는 사물함 번호 범위 및 열 개수를 설정
        val (startLocker, endLocker, columnCount) = getLockerRangeAndColumnCount(areaNumber, floorNumber)
        val databasePath = if (floorNumber == 2) "Lockers" else "Lockers5"
        database = FirebaseDatabase.getInstance("https://locker-d8b7d-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference(databasePath)

        recyclerView = findViewById(R.id.lockerRecyclerView)
        recyclerView.setHasFixedSize(true)

        // 가로 스크롤을 가능하게 하는 GridLayoutManager 설정
        setupGridLayoutManager(recyclerView, columnCount)

        // 간격을 조정하기 위한 ItemDecoration 추가
        recyclerView.addItemDecoration(SpaceItemDecoration(8)) // 좌우 간격 8dp, 상하 간격 4dp 설정

        lockerAdapter = LockerAdapter(floorNumber)
        recyclerView.adapter = lockerAdapter

        // Firebase에서 사물함 데이터를 불러옴
        loadLockers(startLocker, endLocker)

        // 사물함을 클릭하면 LockerDetailActivity로 이동
        lockerAdapter.setOnItemClickListener { locker ->
            val intent = Intent(this, LockerDetailActivity::class.java)
            intent.putExtra("lockerNumber", locker.number)
            intent.putExtra("floorNumber", floorNumber)
            startActivity(intent)
        }
    }

    // 사물함 데이터 범위와 열 개수를 설정하는 함수
    private fun getLockerRangeAndColumnCount(areaNumber: Int, floorNumber: Int): Triple<Int, Int, Int> {
        return when (floorNumber) {
            2 -> when (areaNumber) {
                1 -> Triple(1, 42, 6)    // 42개 6줄
                2 -> Triple(43, 92, 5)   // 50개 5줄
                3 -> Triple(93, 122, 5)  // 30개 5줄
                4 -> Triple(123, 142, 5) // 20개 5줄
                5 -> Triple(143, 182, 5) // 40개 5줄
                else -> Triple(1, 48, 8) // 기본 설정
            }
            5 -> when (areaNumber) {
                1 -> Triple(1, 48, 6)    // 48개 6줄
                2 -> Triple(49, 98, 5)   // 50개 5줄
                3 -> Triple(99, 110, 6)  // 12개 6줄
                4 -> Triple(111, 158, 6) // 48개 6줄
                5 -> Triple(159, 173, 3) // 15개 3줄
                6 -> Triple(174, 209, 3) // 36개 3줄
                else -> Triple(1, 48, 8) // 기본 설정
            }
            else -> Triple(1, 48, 8) // 기본 설정
        }
    }



    // Firebase 데이터 리스너를 추가하여 실시간 업데이트 반영
    private fun loadLockers(startLocker: Int, endLocker: Int) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lockerList = mutableListOf<Locker>()
                for (i in startLocker..endLocker) {
                    val lockerData = dataSnapshot.child("Locker$i")
                    val isUsed = lockerData.child("isUsed").getValue(Boolean::class.java) ?: false
                    val useUntil = lockerData.child("useUntil").getValue(String::class.java) // useUntil 값 가져오기
                    lockerList.add(Locker(i, isUsed, useUntil, studentId = null)) // useUntil 값을 추가하여 Locker 객체 생성
                }
                lockerAdapter.setLockers(lockerList) // 사물함 목록만 전달
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("LockerGridActivity", "Failed to load lockers: ${databaseError.message}")
            }
        })
    }

    private fun setupGridLayoutManager(recyclerView: RecyclerView, columnCount: Int) {
        // 가로 방향으로 먼저 숫자가 채워지도록 설정 (열이 가로로 나열됨)
        val layoutManager = GridLayoutManager(this, columnCount, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
    }

    // ItemDecoration을 이용하여 사물함 간의 좌우, 상하 간격을 설정
    class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = space
            outRect.right = space

            // 상하 간격을 추가
            outRect.top = space   // 상하 간격을 좌우의 절반 정도로 설정
            outRect.bottom = space
        }
    }
}
