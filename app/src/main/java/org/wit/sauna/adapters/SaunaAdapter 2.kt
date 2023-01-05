package org.wit.sauna.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.sauna.databinding.CardSaunaBinding
import org.wit.sauna.models.SaunaModel

interface SaunaListener {
    fun onSaunaClick(sauna: SaunaModel)
}

class SaunaAdapter constructor(private var saunas: List<SaunaModel>,
                                   private val listener: SaunaListener) :
        RecyclerView.Adapter<SaunaAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardSaunaBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val sauna = saunas[holder.adapterPosition]
        holder.bind(sauna, listener)
    }

    override fun getItemCount(): Int = saunas.size

    class MainHolder(private val binding : CardSaunaBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(sauna: SaunaModel, listener: SaunaListener) {
            binding.saunaTitle.text = sauna.title
            binding.description.text = sauna.description
            Picasso.get().load(sauna.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onSaunaClick(sauna) }
        }
    }
}
