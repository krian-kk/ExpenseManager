package com.expensemanager.ui.expense.expenselist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensemanager.ui.viewmodels.ExpenseManagerViewModel
import com.expensemanager.R
import com.expensemanager.data.local.DatabaseHelperImpl
import com.expensemanager.data.local.entity.ExpenseModel
import com.expensemanager.databinding.ActivityRecyclerViewBinding
import com.expensemanager.ui.base.UiState
import com.expensemanager.ui.base.ViewModelFactory
import me.amitshekhar.learn.kotlin.coroutines.data.local.DatabaseBuilder

class ExpenseListFragment : Fragment(), ClickCallback {

    private lateinit var viewModel: ExpenseManagerViewModel
    private lateinit var adapter: ExpenseListAdapter
    private lateinit var binding: ActivityRecyclerViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ActivityRecyclerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentCallbacks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
        setupObserver()
    }

    private fun setupUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)
        adapter = ExpenseListAdapter(
            arrayListOf(), this
        )
        binding.recyclerView.adapter = adapter

        binding.filterCategory.setOnClickListener {
            findNavController().navigate(
                R.id.moveToBottomSheet,
                args = bundleOf("filterName" to viewModel.filterName, "src" to "category")
            )
        }
        binding.filterDuration.setOnClickListener {
            findNavController().navigate(
                R.id.moveToBottomSheet,
                args = bundleOf("filterName" to viewModel.durationName, "src" to "duration")
            )
        }
        binding.buttonAddExpense.setOnClickListener {
            findNavController().navigate(
                R.id.moveToAddExpense,
            )
        }
        binding.textClearAll.setOnClickListener {
            viewModel.fetchExpenses()
            binding.filterNav.visibility = View.GONE
        }
        binding.textFilterCategory.setOnClickListener {
            binding.textFilterCategory.visibility = View.GONE
            if (viewModel.durationName == "All(Default)") {
                binding.filterNav.visibility = View.GONE
            }
            viewModel.setFilter("All(Default)")
        }
        binding.textFilterDuration.setOnClickListener {
            if (viewModel.filterName == "All(Default)") {
                binding.filterNav.visibility = View.GONE
            }
            binding.textFilterDuration.visibility = View.INVISIBLE
            viewModel.setDurationFilter("All(Default)")
        }
    }

    private fun setupObserver() {
        viewModel.getUiState().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (it.data.isEmpty()) {
                        binding.textNoExpense.visibility = View.VISIBLE
                    } else {
                        binding.textNoExpense.visibility = View.GONE
                    }
                    renderList(it.data)
                    binding.recyclerView.visibility = View.VISIBLE
                }

                is UiState.Loading -> {
                    binding.textNoExpense.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }

                is UiState.Error -> {
                    binding.textNoExpense.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.fetchExpenses()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderList(users: List<ExpenseModel>) {
        if (users.isEmpty()) {
            adapter.clear()
        } else {
            adapter.addData(users)
        }
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {
        context?.let { mContext ->
            viewModel = ViewModelProvider(
                this, ViewModelFactory(
                    DatabaseHelperImpl(DatabaseBuilder.getInstance(mContext))
                )
            )[ExpenseManagerViewModel::class.java]
        }
    }

    override fun editExpense(expenseModel: ExpenseModel, position: Int) {
        val bundle = bundleOf()
        bundle.putInt("amount", expenseModel.amount ?: 0)
        bundle.putString("category", expenseModel.category)
        bundle.putLong("date", expenseModel.date ?: 0L)
        bundle.putString("description", expenseModel.description)
        bundle.putLong("id", expenseModel.id)
        findNavController().navigate(R.id.moveToAddExpense, args = bundle)
    }

    override fun deleteExpense(expenseModel: ExpenseModel, position: Int) {
        viewModel.getUiStateDeleteExpense().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    adapter.removeItem(position)
                    Toast.makeText(context, "Expense Deleted successfully", Toast.LENGTH_LONG)
                        .show()
                }

                is UiState.Error -> {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                }

                is UiState.Loading -> {

                }
            }
        }
        viewModel.deleteExpense(expenseModel)
    }

    private fun checkForFilterNav() {
        if (viewModel.durationName == "All(Default)" && viewModel.filterName == "All(Default)") {
            binding.filterNav.visibility = View.GONE
        } else {
            binding.filterNav.visibility = View.VISIBLE
        }
    }

    private fun setFragmentCallbacks() {
        setFragmentResultListener("requestKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported.
            val result = bundle.getString("bundleKey")
            val filterResult = bundle.getString("filterName")
            if (!filterResult.isNullOrEmpty() && filterResult != "All(Default)") {
                binding.filterNav.visibility = View.VISIBLE
                binding.textFilterCategory.text = filterResult
                binding.textFilterCategory.visibility = View.VISIBLE
            } else {
                binding.textFilterCategory.visibility = View.GONE
            }

            if (!filterResult.isNullOrEmpty()) {
                viewModel.setFilter(filterResult)
                checkForFilterNav()
                return@setFragmentResultListener
            }
            println(result)
            renderList(arrayListOf())
            Toast.makeText(context, "Expense Added Successfully", Toast.LENGTH_LONG).show()
            viewModel.fetchExpenses()
        }

        setFragmentResultListener("durationKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported.
            val durationResult = bundle.getString("durationName")
            if (!durationResult.isNullOrEmpty() && durationResult != "All(Default)") {
                binding.filterNav.visibility = View.VISIBLE
                binding.textFilterDuration.visibility = View.VISIBLE
                binding.textFilterDuration.text = durationResult
            } else {
                binding.textFilterDuration.visibility = View.INVISIBLE
            }
            viewModel.setDurationFilter(durationResult)
            checkForFilterNav()
        }
    }
}

interface ClickCallback {
    fun editExpense(expenseModel: ExpenseModel, position: Int)
    fun deleteExpense(expenseModel: ExpenseModel, position: Int)
}