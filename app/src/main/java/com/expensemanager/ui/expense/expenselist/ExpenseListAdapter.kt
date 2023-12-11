package com.expensemanager.ui.expense.expenselist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensemanager.data.local.entity.ExpenseModel
import com.expensemanager.databinding.ItemLayoutBinding
import com.expensemanager.utils.Utils.Companion.getDate

class ExpenseListAdapter(
    private val expenseList: ArrayList<ExpenseModel>, val callback: ClickCallback
) : RecyclerView.Adapter<ExpenseListAdapter.DataViewHolder>() {

    inner class DataViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var expenseModel: ExpenseModel
        private var position: Int = -1

        init {
            binding.textEdit.setOnClickListener {
                callback.editExpense(expenseModel, layoutPosition)
            }
            binding.textDelete.setOnClickListener {
                callback.deleteExpense(expenseModel, layoutPosition)
            }
        }

        fun bind(user: ExpenseModel, position: Int) {
            expenseModel = user
            this@DataViewHolder.position = position
            binding.textViewCategory.text = user.category
            binding.textExpenseDate.text = getDate(user.date ?: 0L)
            binding.textAppliedExpenseDate.text = getDate(user.date ?: 0L)
            binding.textExpenseAmount.text = user.amount.toString()
            binding.textExpenseAmountApproved.text = user.amount.toString()
            binding.textViewDescription.text = user.description.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = expenseList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(expenseList[position], position)

    fun addData(list: List<ExpenseModel>) {
        expenseList.clear()
        expenseList.addAll(list)
    }

    fun clear() {
        expenseList.clear()
    }

    fun removeItem(position: Int) {
        if (expenseList.isNotEmpty()) {
            expenseList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}