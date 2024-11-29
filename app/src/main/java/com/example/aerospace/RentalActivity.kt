package com.example.aerospace

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class RentalActivity : AppCompatActivity() {

    private lateinit var rentalRecyclerView: RecyclerView
    private lateinit var adapter: RentalListAdapter
    private lateinit var rentalList: MutableList<RentalItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rental)

        rentalRecyclerView = findViewById(R.id.rentalRecyclerView)
        rentalRecyclerView.layoutManager = LinearLayoutManager(this)

        rentalList = mutableListOf(
            RentalItem("독서대"),
            RentalItem("충전기"),
            RentalItem("노트북 스탠드"),
            RentalItem("HDMI 케이블")
        )

        adapter = RentalListAdapter(this, rentalList)
        rentalRecyclerView.adapter = adapter

        // 대여 확인 버튼 클릭 리스너 추가
        val confirmRentalButton: Button = findViewById(R.id.confirmRentalButton)
        confirmRentalButton.setOnClickListener {
            showRentalStatus()
        }
    }

    private fun showRentalStatus() {
        val rentedItems = rentalList.filter { !it.isAvailable }
        val message = if (rentedItems.isNotEmpty()) {
            rentedItems.joinToString("\n") { "${it.name} - 대여자: ${it.borrowerName}" }
        } else {
            "현재 대여 중인 물품이 없습니다."
        }

        AlertDialog.Builder(this)
            .setTitle("대여 상태 확인")
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show()
    }
}
