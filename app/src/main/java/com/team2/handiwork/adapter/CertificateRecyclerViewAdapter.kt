package com.team2.handiwork.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team2.handiwork.R
import com.team2.handiwork.databinding.CertificateListItemBinding
import com.team2.handiwork.databinding.RecyclerViewMissionItemBinding
import com.team2.handiwork.enums.MissionStatusEnum
import com.team2.handiwork.models.Certification
import com.team2.handiwork.models.Mission
import com.team2.handiwork.utilities.Utility

class CertificateRecyclerViewAdapter(
    private val context: Context,
    private val onItemClicked: (Certification) -> Unit
) :
    RecyclerView.Adapter<CertificateRecyclerViewAdapter.ViewHolder>() {

    private val list = ArrayList<Certification>()

    inner class ViewHolder(itemBinding: CertificateListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val binding: CertificateListItemBinding = itemBinding
        fun bind(
            cert: Certification,
            clickListener: (Certification) -> Unit
        ) {
            binding.tvCertName.text = cert.name
            Log.d("hehehe", "bind: ${cert.name}")
            Glide.with(context)
                .load(cert.imgUrl)
                .into(binding.ivCertImg)

            binding.certItem.setOnClickListener {
                clickListener(cert)
            }
        }
    }

    fun setList(certs: List<Certification>) {
        list.clear()
        list.addAll(certs)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CertificateListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.certificate_list_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, onItemClicked)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}