package com.mxingo.driver.module.order

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.ListCashEntity


class ListCashAdapter() : BaseAdapter() {

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater
    private lateinit var datas: ArrayList<ListCashEntity.DriverCashBean>

    constructor(context: Context, datas: ArrayList<ListCashEntity.DriverCashBean>) : this() {
        this.context = context
        inflater = LayoutInflater.from(context)
        this.datas = datas
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

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        var holder: ViewHolder?
        var v: View
        if (convertView == null) {
            v = inflater.inflate(R.layout.item_listcash, null)
            holder = ViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }

        var cash = datas[position]

        holder.tvCashTime.text = cash.createtime.toString().substring(5)
        holder.tvCashMoneys.text ="合计"+cash.price+"元"
        holder.tvCashAlipay.text ="支付宝("+cash.driveralino.toString().substring(0,3)+"****"+cash.driveralino.substring(cash.driveralino.length-4)+")"
        if (cash.state.equals(0)){
            holder.tvCashState.setTextColor(Color.parseColor("#ffb579"))
        }
        holder.tvCashState.text = cash.stateName
        holder.tvCashMoney.text= cash.price +"元"

        return v
    }

    inner class ViewHolder {

        var tvCashTime: TextView
        var tvCashMoneys: TextView
        var tvCashAlipay: TextView
        var tvCashMoney: TextView
        var tvCashState: TextView


        constructor(view: View) {
            tvCashTime = view.findViewById(R.id.tv_cash_time)
            tvCashMoneys = view.findViewById(R.id.tv_cash_moneys)
            tvCashAlipay = view.findViewById(R.id.tv_cash_alipay)
            tvCashMoney =view.findViewById(R.id.tv_cash_money)
            tvCashState =view.findViewById(R.id.tv_cash_state)
        }

    }

    fun addAll(datas: List<ListCashEntity.DriverCashBean>) {
        this.datas.addAll(datas)
        notifyDataSetChanged()
    }

    fun clear() {
        this.datas.clear()
        notifyDataSetChanged()
    }

}