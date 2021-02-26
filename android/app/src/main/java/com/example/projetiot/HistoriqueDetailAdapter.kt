package com.example.projetiot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoriqueDetailAdapter(private val collection: ArrayList<Historique>) : RecyclerView.Adapter<HistoriqueDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = HistoriqueDetailViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.linehistoriquedetail_layout, parent, false)
    )

    override fun onBindViewHolder(holder: HistoriqueDetailViewHolder, position: Int) {
        holder.updateWithHistoriqueDetail(collection[position])
    }

    override fun getItemCount() = collection.size

}