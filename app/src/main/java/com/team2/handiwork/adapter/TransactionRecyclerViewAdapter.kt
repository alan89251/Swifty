package com.team2.handiwork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewTransactionItemBinding
import com.team2.handiwork.models.Transaction

class TransactionRecyclerViewAdapter(var context: Context, var list: List<Transaction>) :
    RecyclerView.Adapter<TransactionRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemBinding: RecycleViewTransactionItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewTransactionItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewTransactionItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_transaction_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.transaction = item
        if (item.isExpense()) {
            holder.binding.tvCredit.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.strong_red_100
                )
            )
        }
        holder.binding.ivTransaction.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                item.getIcon(),
            )
        )
    }

    override fun getItemCount(): Int = list.size
}