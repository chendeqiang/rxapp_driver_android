package com.mxingo.driver.module

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.BillEntity
import java.text.SimpleDateFormat

/**
 * Created by chendeqiang on 2017/11/21 15:43
 */
class MyBillAdapter() : BaseAdapter() {

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater
    private var datas: ArrayList<BillEntity> = arrayListOf()
    private var sdfMoth: SimpleDateFormat = SimpleDateFormat("MM-dd")
    private var sdfDay: SimpleDateFormat = SimpleDateFormat("HH:mm")

    constructor(context: Context) : this() {
        this.context = context
        inflater = LayoutInflater.from(context)
    }

    fun addAll(datas: List<BillEntity>) {
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
        var order: BillEntity = datas[position]
        if (convertView == null) {
            v = inflater.inflate(R.layout.item_bill, null)
            holder = ViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }
        holder.tvOrderNo.text = order.orderNo
        holder.tvOrderTime.text = sdfMoth.format(order.bookTime.toLong()) + "  " + sdfDay.format(order.bookTime.toLong())
        holder.tvOrderMoney.text = "¥" + order.cmoney.toString()
        return v
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

    inner class ViewHolder {
        var tvOrderNo: TextView
        var tvOrderTime: TextView
        var tvOrderMoney: TextView

        constructor(view: View) {
            tvOrderNo = view.findViewById(R.id.tv_order_no_bill)
            tvOrderTime = view.findViewById(R.id.tv_bill_time)
            tvOrderMoney = view.findViewById(R.id.tv_bill_money)
        }
    }
}