package com.example.aerospace

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class RentalListAdapter(private val context: Context, private val itemList: MutableList<RentalItem>) :
    RecyclerView.Adapter<RentalListAdapter.ViewHolder>() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("RentalPrefs", Context.MODE_PRIVATE)

    init {
        loadItemStates()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val rentButton: Button = itemView.findViewById(R.id.rentButton)
        val returnButton: Button = itemView.findViewById(R.id.returnButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rental, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.name

        updateButtonVisibility(holder, item)

        holder.rentButton.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_rent, null)
            val studentIdInput = dialogView.findViewById<EditText>(R.id.studentIdInput)
            val studentNameInput = dialogView.findViewById<EditText>(R.id.studentNameInput)

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("대여 정보 입력")
                .setView(dialogView)
                .setPositiveButton("대여") { _, _ ->
                    val studentId = studentIdInput.text.toString()
                    val studentName = studentNameInput.text.toString()

                    if (studentId.isNotBlank() && studentName.isNotBlank()) {
                        item.isAvailable = false
                        item.borrowerId = studentId
                        item.borrowerName = studentName
                        updateButtonVisibility(holder, item)
                        saveItemState(item, position)
                        Toast.makeText(holder.itemView.context, "${item.name} 대여 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "학번과 이름을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }

        holder.returnButton.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_rent, null)
            val studentIdInput = dialogView.findViewById<EditText>(R.id.studentIdInput)
            val studentNameInput = dialogView.findViewById<EditText>(R.id.studentNameInput)

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("반납 확인")
                .setView(dialogView)
                .setPositiveButton("반납") { _, _ ->
                    val studentId = studentIdInput.text.toString()
                    val studentName = studentNameInput.text.toString()

                    if (studentId == item.borrowerId && studentName == item.borrowerName) {
                        item.isAvailable = true
                        item.borrowerId = null
                        item.borrowerName = null
                        updateButtonVisibility(holder, item)
                        saveItemState(item, position)
                        Toast.makeText(holder.itemView.context, "${item.name} 반납 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "학번과 이름이 대여 정보와 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    private fun updateButtonVisibility(holder: ViewHolder, item: RentalItem) {
        holder.rentButton.isVisible = item.isAvailable
        holder.returnButton.isVisible = !item.isAvailable
    }

    private fun saveItemState(item: RentalItem, position: Int) {
        with(sharedPreferences.edit()) {
            putBoolean("item_${position}_available", item.isAvailable)
            putString("item_${position}_borrowerId", item.borrowerId)
            putString("item_${position}_borrowerName", item.borrowerName)
            apply()
        }
    }

    private fun loadItemStates() {
        for (i in itemList.indices) {
            itemList[i].isAvailable = sharedPreferences.getBoolean("item_${i}_available", true)
            itemList[i].borrowerId = sharedPreferences.getString("item_${i}_borrowerId", null)
            itemList[i].borrowerName = sharedPreferences.getString("item_${i}_borrowerName", null)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
