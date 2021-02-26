package com.example.projetiot

import android.graphics.Color
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.line_layout.view.*

class OeuvreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvItemTitle = itemView.item_title
    private val tvItemAuthor = itemView.item_author
    private val tvItemArtMovement = itemView.item_artMovement
    private val tvItemDate = itemView.item_date
    private val tvItemTechnique = itemView.item_technique


    fun OeuvreViewHolder(@NonNull itemView:View){
        super.itemView;
    }

    fun updateWithOeuvre(oeuvre: Oeuvre){
        this.tvItemTitle.text = oeuvre.titre;
        this.tvItemAuthor.text = oeuvre.artist;
        this.tvItemArtMovement.text = oeuvre.artMovement;
        this.tvItemDate.text = oeuvre.date.toString().split(".")[0];
        this.tvItemTechnique.text = oeuvre.technique;
    }
}