package com.hsvibe.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hsvibe.AppController
import com.hsvibe.R

/**
 * Created by Vincent on 2021/8/15.
 */
class MyBaseAdapter(private val pairList: MutableList<Pair<String, String?>>, private val hasHintHeader: Boolean) : BaseAdapter() {

    override fun getCount(): Int = pairList.size

    override fun getItem(position: Int): Any = pairList[position]

    override fun getItemId(position: Int): Long = 0

    fun getFirst(position: Int): String = pairList[position].first

    fun getSecond(position: Int): String? = pairList[position].second

    fun updateList(pairList: List<Pair<String, String?>>) {
        this.pairList.apply {
            clear()
            addAll(pairList)
        }
        notifyDataSetChanged()
    }

    fun clearList() {
        this.pairList.clear()
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.inflate_simple_spinner_text_align_center, parent, false)
            holder = ViewHolder(view.findViewById(R.id.text_spinnerText))
            view.tag = holder
        }
        else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        holder.textView.text = pairList[position].first

        val textColorRes = if (hasHintHeader && position == 0) {
            R.color.md_grey_600
        }
        else {
            R.color.md_grey_50
        }
        holder.textView.setTextColor(ContextCompat.getColor(AppController.getAppContext(), textColorRes))

        return view
    }

    inner class ViewHolder(val textView: TextView)
}