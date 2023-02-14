package com.mxingo.driver.module.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.ListCarPoolOrderEntity

class CarPoolOrderAdapter():BaseAdapter() {
    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater
    private lateinit var datas: ArrayList<ListCarPoolOrderEntity.DataBean>

    constructor(context: Context, datas: ArrayList<ListCarPoolOrderEntity.DataBean>) : this() {
        this.context = context
        inflater = LayoutInflater.from(context)
        this.datas = datas
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder?
        var v: View
        if (convertView == null) {
            v = inflater.inflate(R.layout.item_carpool_order, null)
            holder = ViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }

        var order = datas[position]
        holder.tvCode.text=order.ccode
        holder.tvStartAddress.text=order.cstartcity+order.cstartarea
        holder.tvEndAddress.text=order.cendcity+order.cendarea
        holder.tvCpTime.text =order.cstarttime.subSequence(0,16)
        holder.tvCpCost.text="¥"+order.allprice.toString()
        holder.tvCpNum.text ="城际 | 拼车 "+ order.num+"人"
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
        var tvStartAddress: TextView
        var tvEndAddress: TextView
        var tvCode:TextView
        var tvCpTime:TextView
        var tvCpCost:TextView
        var tvCpNum:TextView

        constructor(view: View) {
            tvStartAddress =view.findViewById(R.id.tv_startdrs)
            tvEndAddress =view.findViewById(R.id.tv_enddrs)
            tvCode =view.findViewById(R.id.tv_ccode)
            tvCpTime =view.findViewById(R.id.tv_cp_time)
            tvCpCost =view.findViewById(R.id.tv_cp_cost)
            tvCpNum =view.findViewById(R.id.tv_a)
        }
    }

    fun addAll(datas: List<ListCarPoolOrderEntity.DataBean>) {
        this.datas.addAll(datas)
        notifyDataSetChanged()
    }

    fun clear() {
        this.datas.clear()
        notifyDataSetChanged()
    }
}