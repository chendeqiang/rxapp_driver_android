package com.mxingo.driver.module

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.ListNoticeEntity
import com.mxingo.driver.model.NoticeEntity
import com.mxingo.driver.utils.TextUtil

/**
 * Created by chendeqiang on 2017/11/8 10:40
 */
class NoticeAdapter() : BaseAdapter() {

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater
    private lateinit var datas: ArrayList<NoticeEntity>

    constructor(context: Context, datas: ArrayList<NoticeEntity>) : this() {
        this.context = context
        this.inflater = LayoutInflater.from(context)
        this.datas = datas
    }

    fun addAll(datas: List<NoticeEntity>) {
        this.datas.addAll(datas)
        notifyDataSetChanged()
    }

    fun clear() {
        this.datas.clear()
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val holder: ViewHolder?
        val v: View
        var notice: NoticeEntity = datas[position]
        if (convertView == null) {
            v = inflater.inflate(R.layout.item_notice, null)
            holder = ViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }
        holder.tvTitle.text = notice.title
        holder.tvDate.text = notice.csendtime.subSequence(0, 10)
        return v
    }

    inner class ViewHolder {

        var tvTitle: TextView
        var tvDate: TextView

        constructor(view: View) {
            tvTitle = view.findViewById(R.id.tv_title_notice)
            tvDate = view.findViewById(R.id.tv_date_notice)
        }
    }

    override fun getItem(position: Int): Any {
        return datas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return datas.size
    }
}