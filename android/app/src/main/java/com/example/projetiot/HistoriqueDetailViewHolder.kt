package com.example.projetiot

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.linehistoriquedetail_layout.view.*

class HistoriqueDetailViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
        private val tvNameOtherSensor = itemView.name_otherSensor
        private val tvInfosOtherSensor = itemView.infos_otherSensor
        private val tvDateOtherSensor = itemView.date_otherSensor


        fun HistoriqueDetailViewHolder(@NonNull itemView:View){
            super.itemView;
        }

        fun updateWithHistoriqueDetail(historique: Historique){
            this.tvNameOtherSensor.text = historique.type;
            this.tvInfosOtherSensor.text = historique.value;
            this.tvDateOtherSensor.text = historique.date;
        }
}