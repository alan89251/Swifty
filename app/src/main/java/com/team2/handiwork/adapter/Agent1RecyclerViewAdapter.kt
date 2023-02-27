package com.team2.handiwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewAgent1Binding
import com.team2.handiwork.models.Enrollment
import io.reactivex.rxjava3.subjects.PublishSubject

class Agent1RecyclerViewAdapter(var list: List<Enrollment>):
    RecyclerView.Adapter<Agent1RecyclerViewAdapter.ViewHolder>() {
    var selectedEnrollment: PublishSubject<Enrollment> = PublishSubject.create()

    class ViewHolder(itemBinding: RecycleViewAgent1Binding):
        RecyclerView.ViewHolder(itemBinding.root) {
            val binding: RecycleViewAgent1Binding = itemBinding
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewAgent1Binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_agent1,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvUsername.text = item.agent
        holder.binding.btnSelect.setOnClickListener {
            selectedEnrollment.onNext(item)
        }
    }

    override fun getItemCount(): Int = list.size
}