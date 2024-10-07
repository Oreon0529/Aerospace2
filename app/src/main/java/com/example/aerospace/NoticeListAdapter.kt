package com.example.aerospace

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class NoticeListAdapter(
    private val context: Context,
    private val noticeList: List<Notice>
) : BaseAdapter() {

    override fun getCount(): Int {
        return noticeList.size
    }

    override fun getItem(i: Int): Any {
        return noticeList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
        val v: View = View.inflate(context, R.layout.notice, null)
        val noticeText = v.findViewById<TextView>(R.id.noticeText)
        val nameText = v.findViewById<TextView>(R.id.nameText)
        val dateText = v.findViewById<TextView>(R.id.dateText)

        val notice = noticeList[i]
        noticeText.text = notice.notice
        nameText.text = notice.name
        dateText.text = notice.date

        v.tag = notice

        return v
    }
}
