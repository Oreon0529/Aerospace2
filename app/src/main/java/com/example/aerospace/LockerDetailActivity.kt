package com.example.aerospace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.Locale
import java.util.Calendar
import java.text.SimpleDateFormat

class LockerDetailActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private var studentId: String? = null  // 초기값을 null로 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locker_detail)

        // 사물함 정보 Intent로부터 받아오기
        val lockerNumber = intent.getIntExtra("lockerNumber", -1)
        val floorNumber = intent.getIntExtra("floorNumber", 2) // 층 정보 받아오기

        // Firebase 경로 설정
        val databasePath = if (floorNumber == 2) "Lockers" else "Lockers5"
        database = FirebaseDatabase.getInstance("https://locker-d8b7d-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference(databasePath)

        // Firebase에서 사물함 정보를 가져와 UI 업데이트
        updateLockerInfo(lockerNumber)

        // Firebase 리스너 추가 (데이터 변경 감지)
        database.child("Locker$lockerNumber").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 데이터가 변경될 때마다 UI 업데이트
                val isUsed = snapshot.child("isUsed").getValue(Boolean::class.java) ?: false
                val useUntil = snapshot.child("useUntil").getValue(String::class.java) ?: "No Data"

                // UI에 사물함 정보 표시
                val lockerNumberTextView: TextView = findViewById(R.id.lockerNumberTextView)
                val lockerStatusTextView: TextView = findViewById(R.id.lockerStatusTextView)
                val usagePeriodTextView: TextView = findViewById(R.id.usagePeriodTextView)

                lockerNumberTextView.text = "사물함 번호: $lockerNumber"
                lockerStatusTextView.text = if (isUsed) "사용 중" else "비어 있음"
                usagePeriodTextView.text = "사용 기간: $useUntil"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LockerDetailActivity, "사물함 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        // 신청 버튼 처리
        val applyButton: Button = findViewById(R.id.applyButton)
        applyButton.setOnClickListener {
            showStudentIdInputDialog(lockerNumber, floorNumber) // 학번 입력 창 띄우기
        }

        // 취소 버튼 처리
        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            checkLockerStatusAndCancel(lockerNumber, floorNumber)
        }
    }

    // 신청 버튼을 눌렀을 때 학번 입력 UI를 띄우는 함수
    private fun showStudentIdInputDialog(lockerNumber: Int, floorNumber: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("학번 입력")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("신청") { dialog, _ ->
            studentId = input.text.toString()
            if (!studentId.isNullOrEmpty()) {
                // 학번 입력 후 로직 진행
                checkLockerStatusAndApply(lockerNumber, floorNumber)
            } else {
                Toast.makeText(this, "학번을 입력하세요", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    // 신청 버튼 처리
    private fun checkLockerStatusAndApply(lockerNumber: Int, floorNumber: Int) {
        database.child("Locker$lockerNumber").get().addOnSuccessListener { dataSnapshot ->
            val isUsed = dataSnapshot.child("isUsed").getValue(Boolean::class.java) ?: false

            if (!isUsed) {
                checkStudentLockerLimit(lockerNumber, floorNumber)
            } else {
                Toast.makeText(this, "이미 사용 중인 사물함입니다.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "사물함 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 해당 학번이 1개 이상의 사물함을 사용 중인지 확인 (수정된 부분)
    private fun checkStudentLockerLimit(lockerNumber: Int, floorNumber: Int) {
        val databasePath = if (floorNumber == 2) "Lockers" else "Lockers5"
        val databaseRef = FirebaseDatabase.getInstance("https://locker-d8b7d-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference(databasePath)

        // 층 내에서 학번으로 사용 중인 사물함 개수 체크
        databaseRef.orderByChild("studentId").equalTo(studentId).get().addOnSuccessListener { snapshot ->
            var lockerCount = 0

            for (lockerSnapshot in snapshot.children) {
                val isUsed = lockerSnapshot.child("isUsed").getValue(Boolean::class.java) ?: false
                if (isUsed) {
                    lockerCount++
                }
            }
//허허
            if (lockerCount >= 1) {  // 층당 1개로 제한
                Toast.makeText(this, "해당 층에서 이미 1개의 사물함을 사용 중입니다.", Toast.LENGTH_SHORT).show()
            } else {
                applyForLocker(lockerNumber, studentId!!, floorNumber, this)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "사물함 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 사물함 신청 함수
    private fun applyForLocker(lockerNumber: Int, studentId: String, floorNumber: Int, context: Context) {
        val databasePath = if (floorNumber == 2) "Lockers" else "Lockers5"
        val database = FirebaseDatabase.getInstance("https://locker-d8b7d-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference(databasePath)

        // 현재 날짜를 기준으로 6개월 후 날짜 계산
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendar.add(Calendar.MONTH, 6)
        val useUntilDate = dateFormat.format(calendar.time)

        // Firebase에 업데이트할 데이터
        val lockerUpdate = mapOf(
            "isUsed" to true,
            "useUntil" to useUntilDate,
            "studentId" to studentId
        )

        // Firebase에 데이터 업데이트
        database.child("Locker$lockerNumber").updateChildren(lockerUpdate).addOnSuccessListener {
            Toast.makeText(context, "사물함 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            updateLockerInfo(lockerNumber)  // 신청 후 UI 업데이트
        }.addOnFailureListener {
            Toast.makeText(context, "사물함 신청에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 취소 버튼 처리
    private fun checkLockerStatusAndCancel(lockerNumber: Int, floorNumber: Int) {
        database.child("Locker$lockerNumber").get().addOnSuccessListener { dataSnapshot ->
            val isUsed = dataSnapshot.child("isUsed").getValue(Boolean::class.java) ?: false

            if (isUsed) {
                showStudentIdInputDialogForCancellation(this, lockerNumber, floorNumber)
            } else {
                Toast.makeText(this, "비어 있는 사물함은 취소할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "사물함 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 취소 버튼을 눌렀을 때 학번 입력 UI를 띄우는 함수
    private fun showStudentIdInputDialogForCancellation(context: Context, lockerNumber: Int, floorNumber: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("학번 입력 (취소)")

        val input = EditText(context)
        builder.setView(input)

        builder.setPositiveButton("확인") { dialog, _ ->
            studentId = input.text.toString()
            if (!studentId.isNullOrEmpty()) {
                cancelLocker(lockerNumber, studentId!!, floorNumber, context)
            } else {
                Toast.makeText(context, "학번을 입력하세요", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    // 사물함 취소 함수
    private fun cancelLocker(lockerNumber: Int, studentId: String, floorNumber: Int, context: Context) {
        val databasePath = if (floorNumber == 2) "Lockers" else "Lockers5"
        val database = FirebaseDatabase.getInstance("https://locker-d8b7d-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference(databasePath)

        database.child("Locker$lockerNumber").get().addOnSuccessListener { dataSnapshot ->
            val storedStudentId = dataSnapshot.child("studentId").getValue(String::class.java) ?: ""
            val isUsed = dataSnapshot.child("isUsed").getValue(Boolean::class.java) ?: false // 사용 중 여부 확인

            if (!isUsed) {
                Toast.makeText(context, "비어 있는 사물함은 취소할 수 없습니다.", Toast.LENGTH_SHORT).show()
            } else if (storedStudentId == studentId) {
                val lockerUpdate = mapOf(
                    "isUsed" to false,
                    "useUntil" to "",
                    "studentId" to ""
                )
                database.child("Locker$lockerNumber").updateChildren(lockerUpdate).addOnSuccessListener {
                    Toast.makeText(context, "사물함 취소가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    updateLockerInfo(lockerNumber)  // 취소 후 UI 업데이트
                }.addOnFailureListener {
                    Toast.makeText(context, "사물함 취소에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "입력한 학번이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "사물함 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 사물함 정보를 업데이트하는 함수
    private fun updateLockerInfo(lockerNumber: Int) {
        database.child("Locker$lockerNumber").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Firebase에서 데이터 가져오기
                val isUsed = dataSnapshot.child("isUsed").getValue(Boolean::class.java) ?: false
                val useUntil = dataSnapshot.child("useUntil").getValue(String::class.java) ?: "No Data"

                // UI에 사물함 정보 표시
                val lockerNumberTextView: TextView = findViewById(R.id.lockerNumberTextView)
                val lockerStatusTextView: TextView = findViewById(R.id.lockerStatusTextView)
                val usagePeriodTextView: TextView = findViewById(R.id.usagePeriodTextView)

                lockerNumberTextView.text = "사물함 번호: $lockerNumber"
                lockerStatusTextView.text = if (isUsed) "사용 중" else "비어 있음"
                usagePeriodTextView.text = "사용 기간: $useUntil"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LockerDetailActivity, "사물함 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}