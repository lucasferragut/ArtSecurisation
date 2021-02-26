package com.example.projetiot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OeuvreAdapter(private val collection: ArrayList<Oeuvre>) : RecyclerView.Adapter<OeuvreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = OeuvreViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.line_layout, parent, false)
    )

    override fun onBindViewHolder(holder: OeuvreViewHolder, position: Int) {
        holder.updateWithOeuvre(collection[position])
    }

    override fun getItemCount() = collection.size

}