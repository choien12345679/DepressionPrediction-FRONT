package com.imp.presentation.view.pattern

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imp.presentation.databinding.ItemDepressionScoreBinding

data class DepressionScoreUi(
    val date: String,
    val score: Double
)

class DepressionScoreAdapter : RecyclerView.Adapter<DepressionScoreAdapter.ViewHolder>() {

    private val items: MutableList<DepressionScoreUi> = mutableListOf()

    fun submitList(list: List<DepressionScoreUi>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDepressionScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ItemDepressionScoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DepressionScoreUi) {
            binding.tvScoreDate.text = item.date
            binding.tvScoreValue.text = "우울 점수 ${item.score.toInt()}점"
        }
    }
}
