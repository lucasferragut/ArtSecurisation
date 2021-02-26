package com.example.projetiot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoriqueAdapter(private val collection: ArrayList<Historique>) : RecyclerView.Adapter<HistoriqueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = HistoriqueViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.linehistorique_layout, parent, false)
    )

    override fun onBindViewHolder(holder: HistoriqueViewHolder, position: Int) {
        holder.updateWithHistorique(collection[position])
    }

    override fun getItemCount() = collection.size

}