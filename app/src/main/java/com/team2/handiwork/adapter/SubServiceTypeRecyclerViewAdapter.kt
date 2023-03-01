package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewSubServiceTypeItemBinding
import com.team2.handiwork.models.ServiceType
import io.reactivex.rxjava3.subjects.PublishSubject

class SubServiceTypeRecyclerViewAdapter(var list: List<ServiceType>) :
    RecyclerView.Adapter<SubServiceTypeRecyclerViewAdapter.ViewHolder>() {
    var selectServiceType: PublishSubject<ServiceType> = PublishSubject.create()

    class ViewHolder(itemBinding: RecycleViewSubServiceTypeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewSubServiceTypeItemBinding = itemBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewSubServiceTypeItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_sub_service_type_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.serviceType = item
        holder.binding.rvSubServiceType.layoutManager =
            LinearLayoutManager(holder.binding.root.context)
        val adapter = SubServiceTypeOptionRecyclerViewAdapter(item.subServiceTypeList)
        holder.binding.rvSubServiceType.adapter = adapter
        adapter.selectSubServiceType.subscribe { selectSubService ->
            item.subServiceTypeList.forEach {
                if (selectSubService.name == it.name ) {
                    it.selected = selectSubService.selected
                }
            }
            this.selectServiceType.onNext(item)
        }
    }

    override fun getItemCount(): Int = list.size
}