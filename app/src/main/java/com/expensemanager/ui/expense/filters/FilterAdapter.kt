package com.expensemanager.ui.expense.filters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.expensemanager.R
import com.expensemanager.databinding.ItemFilterBinding

class FilterAdapter(
    private val filterList: ArrayList<CategoryFilter>,
    val callback: FilterActions,
    val filterName: String
) : RecyclerView.Adapter<FilterAdapter.DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            ItemFilterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = filterList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(filterList[position])

    fun deselectAll() {
        filterList.forEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }

    inner class DataViewHolder(private val binding: ItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var filter: CategoryFilter

        init {
            binding.filterRoot.setOnClickListener {
                filter.isSelected = !filter.isSelected
                notifyItemChanged(layoutPosition)
                callback.selectFilters(filter.name)
            }
        }

        fun bind(filter: CategoryFilter) {
            this.filter = filter
            binding.name.text = filter.name
            if (filter.isSelected || filter.name == filterName) {
                binding.imageSelected.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.imageSelected.context, R.drawable.baseline_radio_button_checked_24
                    )
                )
            } else {
                binding.imageSelected.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.imageSelected.context, R.drawable.baseline_radio_button_unchecked_24
                    )
                )
            }
        }
    }
}

interface FilterActions {
    fun selectFilters(name: String)
    fun selectAll()
}