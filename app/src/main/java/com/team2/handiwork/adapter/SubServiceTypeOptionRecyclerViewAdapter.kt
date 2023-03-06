package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewSubServiceTypeOptionItemBinding
import com.team2.handiwork.models.SubServiceType
import io.reactivex.rxjava3.subjects.PublishSubject

class SubServiceTypeOptionRecyclerViewAdapter(var list: List<SubServiceType>) :
    RecyclerView.Adapter<SubServiceTypeOptionRecyclerViewAdapter.ViewHolder>() {
    var selectSubServiceType: PublishSubject<SubServiceType> = PublishSubject.create()

    inner class ViewHolder(itemBinding: RecycleViewSubServiceTypeOptionItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewSubServiceTypeOptionItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewSubServiceTypeOptionItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_sub_service_type_option_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: SubServiceType = list[position]
        holder.binding.subServiceType = item
        if (item.selected) {
            holder.binding.ivSelected.visibility = View.VISIBLE
        } else {
            holder.binding.ivSelected.visibility = View.INVISIBLE
        }
        holder.binding.btnSubServiceType.setOnClickListener {
            item.selected = !item.selected
            if (item.selected) {
                holder.binding.ivSelected.visibility = View.VISIBLE
            } else {
                holder.binding.ivSelected.visibility = View.INVISIBLE
            }
            selectSubServiceType.onNext(item)
        }
    }

    override fun getItemCount(): Int = list.size
}