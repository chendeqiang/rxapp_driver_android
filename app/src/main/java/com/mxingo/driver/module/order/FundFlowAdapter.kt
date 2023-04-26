package com.mxingo.driver.module.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.WalletFundFlowEntity


class FundFlowAdapter() : BaseAdapter() {

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater
    private lateinit var datas: ArrayList<WalletFundFlowEntity.FundFlowBean>

    constructor(context: Context, datas: ArrayList<WalletFundFlowEntity.FundFlowBean>) : this() {
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
            v = inflater.inflate(R.layout.item_fundflow, null)
            holder = ViewHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as ViewHolder
        }

        var order = datas[position]

        holder.tvFundFlow.text = order.categoryName
        holder.tvTimeFundFlow.text = order.createTimeMD +" "+ order.createTimeHIS

        if (order.sort.equals("2")){//收入
            holder.tvMoneyFundFlow.text = "+"+order.driverPrice+"元"
        }else if (order.sort.equals("3")){//支出
            holder.tvMoneyFundFlow.text = order.driverPrice+"元"
        }

        return v
    }

    inner class ViewHolder {

        var tvFundFlow: TextView
        var tvTimeFundFlow: TextView
        var tvMoneyFundFlow: TextView


        constructor(view: View) {
            tvFundFlow = view.findViewById(R.id.tv_fund_flow)
            tvTimeFundFlow = view.findViewById(R.id.tv_time_fund_flow)
            tvMoneyFundFlow = view.findViewById(R.id.tv_money_fund_flow)
        }

    }

    fun addAll(datas: List<WalletFundFlowEntity.FundFlowBean>) {
        this.datas.addAll(datas)
        notifyDataSetChanged()
    }

    fun clear() {
        this.datas.clear()
        notifyDataSetChanged()
    }

}