package com.example.aerospace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LockerAdapter(private val floorNumber: Int) : RecyclerView.Adapter<LockerAdapter.LockerViewHolder>() {
    private var lockerList: List<Locker> = listOf()

    // 클릭 리스너를 위한 변수
    private var onItemClickListener: ((Locker) -> Unit)? = null

    // ViewHolder 클래스 정의
    class LockerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lockerNumber: TextView = itemView.findViewById(R.id.lockerNumber)
        val lockerContainer: View = itemView // 전체 컨테이너 (배경색을 변경하기 위해 추가)
    }

    // ViewHolder를 생성하는 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_locker, parent, false)
        return LockerViewHolder(view)
    }

    // ViewHolder에 데이터를 바인딩하는 메서드
    override fun onBindViewHolder(holder: LockerViewHolder, position: Int) {
        val locker = lockerList[position]
        holder.lockerNumber.text = locker.number.toString()

        // 2층일 때만 25번과 31번 사물함을 회색 처리하고 클릭 비활성화
        if (floorNumber == 2 && (locker.number == 25 || locker.number == 31)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.gray)) // 회색 배경
            holder.itemView.isEnabled = false    // View 자체를 비활성화
            holder.itemView.setOnClickListener(null) // 클릭 리스너 제거
        } else {
            // 사용 여부에 따라 배경 색상 변경 (테두리는 유지됨)
            val backgroundColor = if (locker.isUsed) {
                ContextCompat.getColor(holder.itemView.context, R.color.colorWarning) // 분홍색
            } else {
                ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary) // 파란색
            }

            // View에 배경 색상만 적용 (테두리는 유지됨)
            holder.itemView.setBackgroundColor(backgroundColor)
            holder.itemView.isEnabled = true // 다시 클릭 활성화
            holder.itemView.setOnClickListener {
                onItemClickListener?.invoke(locker)
            }
        }
    }

    // 데이터 리스트의 크기 반환
    override fun getItemCount(): Int {
        return lockerList.size
    }

    // 외부에서 데이터를 설정할 수 있는 메서드
    fun setLockers(lockers: List<Locker>) {
        lockerList = lockers
        notifyDataSetChanged()
    }

    // 클릭 리스너 설정 메서드
    fun setOnItemClickListener(listener: (Locker) -> Unit) {
        onItemClickListener = listener
    }
}
