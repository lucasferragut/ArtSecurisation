package com.example.projetiot

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.linehistorique_layout.view.*

class HistoriqueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvItemTitle = itemView.item_title1
    private val tvItemType = itemView.item_type

    fun HistoriqueViewHolder(@NonNull itemView: View){
        super.itemView;
    }

    fun updateWithHistorique(historique: Historique){
        this.tvItemTitle.text = historique.date;
        this.tvItemType.text = historique.type;
    }
}