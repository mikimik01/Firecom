package com.example.firekom

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

data class Field(var name: String, var msg: String, val ja: Boolean)

class MyAdapter(private val ctx: Activity, val arrayList:ArrayList<Field>) : ArrayAdapter<Field>(ctx, R.layout.list_fragment, arrayList) {

    @SuppressLint("MissingInflatedId", "UseCompatLoadingForDrawables", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater : LayoutInflater = LayoutInflater.from(ctx)
        val view: View = inflater.inflate(R.layout.list_fragment, null)

        val nameTv:TextView = view.findViewById(R.id.nameTv)
        val msgTv:TextView = view.findViewById(R.id.msgTv)
        val lin:LinearLayout = view.findViewById(R.id.linlayfrag)

        nameTv.text = arrayList[position].name
        msgTv.text = arrayList[position].msg

        if (arrayList[position].ja){
            nameTv.setTextColor(Color.parseColor("#0048FF"))
            lin.background = ctx.getDrawable(R.drawable.gradient3)
            nameTv.text = arrayList[position].name + " (ja)"
        }

        return view
    }
}