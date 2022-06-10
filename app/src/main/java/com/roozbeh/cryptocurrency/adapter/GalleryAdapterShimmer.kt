package com.roozbeh.cryptocurrency.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.roozbeh.cryptocurrency.R
import com.roozbeh.cryptocurrency.databinding.CardViewDesignBinding
import com.roozbeh.cryptocurrency.databinding.CardViewDesignShimmerBinding
import java.util.ArrayList

class GalleryAdapterShimmer(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val model: ArrayList<Int> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: CardViewDesignShimmerBinding =
            DataBindingUtil.inflate(inflater, R.layout.card_view_design_shimmer, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
    override fun getItemCount(): Int {
        return model.size
    }

    inner class ViewHolder(var binding: CardViewDesignShimmerBinding)  :
    RecyclerView.ViewHolder(binding.root)

    init {
        model.add(1)
        model.add(2)
        model.add(3)
        model.add(4)
        model.add(5)
        model.add(6)
        model.add(7)
        model.add(8)
        model.add(9)
        model.add(10)
        model.add(11)
        model.add(12)
    }
}