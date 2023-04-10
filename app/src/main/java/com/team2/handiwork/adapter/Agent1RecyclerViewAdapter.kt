package com.team2.handiwork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team2.handiwork.R
import com.team2.handiwork.databinding.RecycleViewAgent1Binding
import com.team2.handiwork.firebase.Storage
import com.team2.handiwork.models.Comment
import com.team2.handiwork.models.User
import io.reactivex.rxjava3.subjects.PublishSubject

class Agent1RecyclerViewAdapter(
    var agents: List<User>,
    val getCommentsFromDB: (User, (List<Comment>) -> Unit) -> Unit,
) :
    RecyclerView.Adapter<Agent1RecyclerViewAdapter.ViewHolder>() {
    var selectedAgent: PublishSubject<User> = PublishSubject.create()
    var chatAgent: PublishSubject<User> = PublishSubject.create()
    var viewAgent: PublishSubject<User> = PublishSubject.create()

    class ViewHolder(itemBinding: RecycleViewAgent1Binding, private val context: Context) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val binding: RecycleViewAgent1Binding = itemBinding

        fun getAgentIcon(url: String) {
            Storage().getImgUrl(url, onIconLoaded, onIconLoadFailed)
        }

        private val onIconLoaded: (mission: String) -> Unit = { imgUrl ->
            Glide.with(context)
                .load(imgUrl)
                .into(binding.ibtnUser)
        }

        private val onIconLoadFailed: () -> Unit = {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RecycleViewAgent1Binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycle_view_agent1,
            parent,
            false
        )
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val agent = agents[position]
        holder.binding.tvUsername.text = "${agent.firstName} ${agent.lastName}"
        if (agent.imageURi.isNotEmpty()) {
            holder.getAgentIcon(agent.imageURi)
        }
        holder.binding.btnSelect.setOnClickListener {
            selectedAgent.onNext(agent)
        }
        holder.binding.btnComm.setOnClickListener {
            chatAgent.onNext(agent)
        }

        holder.binding.ibtnUser.setOnClickListener {
            viewAgent.onNext(agent)
        }
        getCommentsFromDB(agent) {
            holder.binding.tvNumber.text = it.size.toString()
            if (it.isEmpty()) {
                holder.binding.ratingBar.rating = 0F
                return@getCommentsFromDB
            }

            var ratingSum = 0.0
            it.forEach {
                ratingSum += it.rating
            }
            val rating = ratingSum / it.size
            holder.binding.ratingBar.rating = rating.toFloat()
        }
    }

    override fun getItemCount(): Int = agents.size
}