package com.mxingo.driver.module.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.mxingo.driver.OrderModel
import com.mxingo.driver.R
import com.mxingo.driver.model.OrderEntity
import com.mxingo.driver.module.take.CarLevel
import com.mxingo.driver.module.take.OrderType
import com.mxingo.driver.utils.TextUtil

/**
 * Created by zhouwei on 2017/6/29.
 */
class OrderAdapter() : BaseAdapter() {

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater
    private lateinit var datas: ArrayList<OrderEntity>


    constructor(context: Context, datas: ArrayList<OrderEntity>) : this() {
        this.context = context
        inflater = LayoutInflater.from(context)
        this.datas = datas
    }

    fun addAll(datas: List<OrderEntity>) {
        this.datas.addAll(datas)
        notifyDataSetChanged()
    }

    fun clear() {
        this.datas.clear()
        notifyDataSetChanged()
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
        val holder: ViewHolder?
        val v: View
        var order: OrderEntity = datas[position]
        if (convertView == null) {
            v = inflater.inflate(R.layout.item_order, null)
            holder = ViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }

        holder.tvOrderNo.text = order.orderNo
        holder.tv0rderFrom.text = OrderSource.getKey(order.source)

        if (order.orderType == OrderType.DAY_RENTER_TYPE) {
            holder.llBusiness.visibility = View.VISIBLE
            holder.llNoBusiness.visibility = View.GONE
            holder.tvAddress.text = order.startAddr
            holder.tvBookTime.text = TextUtil.getFormatString((order.bookTime)!!.toLong(), order.bookDays, "MM月dd号")

        } else {
            holder.llBusiness.visibility = View.GONE
            holder.llNoBusiness.visibility = View.VISIBLE
            holder.tvStartAddress.text = order.startAddr
            holder.tvEndAddress.text = order.endAddr

            holder.tvBookTime.text = TextUtil.getFormatWeek(order.bookTime.toLong())
        }
        if (order.orderModel == OrderModel.POINT_TYPE) {
            holder.tvOrderType.text = OrderType.getKey(order.orderType)
        } else {
            holder.tvOrderType.text = OrderType.getKey(order.orderType) + "(" + CarLevel.getKey(order.carLevel) + "）"
        }
//        holder.tvFee.text = "¥" + order.orderAmount / 100
        holder.tvFee.text = "¥" + String.format("%.2f",order.orderAmount.toDouble() / 100)
        holder.tvType.text = OrderModel.getKey(order.orderModel)
        return v
    }

    inner class ViewHolder {

        var tvOrderType: TextView
        var tvBookTime: TextView
        var tvAddress: TextView
        var tvStartAddress: TextView
        var tvEndAddress: TextView
        var tvOrderNo: TextView
        var tvFee: TextView
        var tvType: TextView
        var llNoBusiness: LinearLayout
        var llBusiness: LinearLayout
        var tv0rderFrom: TextView


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
            tv0rderFrom = view.findViewById(R.id.tv_order_from_list)

        }
    }
}