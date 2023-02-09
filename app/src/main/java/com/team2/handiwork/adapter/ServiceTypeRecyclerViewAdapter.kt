package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewServiceTypeItemBinding
import com.team2.handiwork.models.ServiceType
import io.reactivex.rxjava3.subjects.PublishSubject

class ServiceTypeRecyclerViewAdapter(var list: List<ServiceType>) :
    RecyclerView.Adapter<ServiceTypeRecyclerViewAdapter.ViewHolder>() {
    var selectServiceType: PublishSubject<ServiceType> = PublishSubject.create()

    class ViewHolder(itemBinding: RecycleViewServiceTypeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewServiceTypeItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewServiceTypeItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_service_type_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.serviceType = item
        holder.binding.ibtnServiceType.setOnClickListener {
            item.selected = !item.selected
            if (item.selected) {
                holder.binding.ivSelected.visibility = View.VISIBLE
            } else {
                holder.binding.ivSelected.visibility = View.INVISIBLE
            }
            selectServiceType.onNext(item)
        }
    }

    override fun getItemCount(): Int = list.size
}