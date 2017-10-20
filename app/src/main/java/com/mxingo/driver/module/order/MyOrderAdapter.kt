package com.mxingo.driver.module.order

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.ListDriverOrderEntity
import com.mxingo.driver.module.take.OrderType
import com.mxingo.driver.utils.TextUtil
import java.text.SimpleDateFormat

/**
 * Created by zhouwei on 2017/6/29.
 */
class MyOrderAdapter() : BaseAdapter() {

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater
    private lateinit var datas: ArrayList<ListDriverOrderEntity.OrderEntity>
    private var sdfMoth: SimpleDateFormat = SimpleDateFormat("MM月dd日")
    private var sdfDay: SimpleDateFormat = SimpleDateFormat("HH:mm")

    constructor(context: Context, datas: ArrayList<ListDriverOrderEntity.OrderEntity>) : this() {
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

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        var holder: ViewHolder?
        var v: View
        if (convertView == null) {
            v = inflater.inflate(R.layout.item_order, null)
            holder = ViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }

        var order = datas[position]
        holder.tvOrderNo.text = order.orderNo
        holder.tvOrderType.text = OrderType.getKey(order.orderType)
        holder.tvBookTime.text = sdfMoth.format(order.bookTime.toLong()) + " (" + TextUtil.getWeekDay(order.bookTime.toLong()) + ") " + sdfDay.format(order.bookTime.toLong())
        if (order.orderType == OrderType.DAY_RENTER_TYPE) {
            holder.llBusiness.visibility = View.VISIBLE
            holder.llNoBusiness.visibility = View.GONE
            holder.tvAddress.text = order.startAddr
        } else {
            holder.llBusiness.visibility = View.GONE
            holder.llNoBusiness.visibility = View.VISIBLE
            holder.tvStartAddress.text = order.startAddr
            holder.tvEndAddress.text = order.endAddr
        }
        holder.tvFee.text = "¥" + order.orderAmount / 100
        holder.tvType.text = FlowStatus.getKey(order.flowStatus)
        holder.tvType.setTextColor(ContextCompat.getColor(context, R.color.text_color_red))
        holder.tvType.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

        return v
    }

    inner class ViewHolder {

        var tvOrderType: TextView
        var tvBookTime: TextView
        var tvAddress: TextView
        var tvStartAddress: TextView
        var tvEndAddress: TextView
        var tvFee: TextView
        var tvType: TextView
        var llNoBusiness: LinearLayout
        var llBusiness: LinearLayout
        var tvOrderNo: TextView


        constructor(view: View) {
            tvOrderType = view.findViewById(R.id.tv_order_type)
            tvBookTime = view.findViewById(R.id.tv_book_time)
            tvStartAddress = view.findViewById(R.id.tv_start_address)
            tvEndAddress = view.findViewById(R.id.tv_end_address)
            tvFee = view.findViewById(R.id.tv_fee)
            tvType = view.findViewById(R.id.tv_type)
            llNoBusiness = view.findViewById(R.id.ll_no_business)
            llBusiness = view.findViewById(R.id.ll_business)
            tvAddress = view.findViewById(R.id.tv_address)
            tvOrderNo = view.findViewById(R.id.tv_order_no)

        }

    }

    fun addAll(datas: List<ListDriverOrderEntity.OrderEntity>) {
        this.datas.addAll(datas)
        notifyDataSetChanged()
    }

    fun clear() {
        this.datas.clear()
        notifyDataSetChanged()
    }

}