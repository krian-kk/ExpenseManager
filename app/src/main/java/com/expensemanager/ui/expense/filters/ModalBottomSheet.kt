package com.expensemanager.ui.expense.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensemanager.databinding.ModalBottomSheetContentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet : BottomSheetDialogFragment(), FilterActions {

    private lateinit var binding: ModalBottomSheetContentBinding
    private lateinit var adapter: FilterAdapter
    private var filterList = arrayListOf<CategoryFilter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoryRecyclerView.layoutManager =
            LinearLayoutManager(binding.categoryRecyclerView.context)
        if (arguments?.getString("src").equals("duration")) {
            filterList = arrayListOf(
                CategoryFilter("All(Default)"),
                CategoryFilter("Last 1 month"),
                CategoryFilter("Last 3 month"),
                CategoryFilter("Last 6 month"),
            )
        } else if (arguments?.getString("src").equals("category")) {
            filterList = arrayListOf(
                CategoryFilter("All(Default)"),
                CategoryFilter("Fuel"),
                CategoryFilter("Food"),
                CategoryFilter("Subscription"),
                CategoryFilter("Travel"),
                CategoryFilter("Mobile")
            )
        } else {
            filterList = arrayListOf(
                CategoryFilter("Fuel"),
                CategoryFilter("Food"),
                CategoryFilter("Subscription"),
                CategoryFilter("Travel"),
                CategoryFilter("Mobile")
            )
        }
        adapter = FilterAdapter(filterList, this, arguments?.getString("filterName").toString())
        binding.categoryRecyclerView.addItemDecoration(
            DividerItemDecoration(
                binding.categoryRecyclerView.context,
                (binding.categoryRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        binding.categoryRecyclerView.adapter = adapter
        binding.layoutFilterAll.setOnClickListener {
            adapter.deselectAll()
        }
    }

    override fun selectFilters(name: String) {
        if (arguments?.getString("src").equals("duration")) {
            setFragmentResult("durationKey", bundleOf("durationName" to name))
        } else if (arguments?.getString("src").equals("category")) {
            setFragmentResult("requestKey", bundleOf("filterName" to name))
        } else {
            setFragmentResult("editKey", bundleOf("category" to name))
        }
        findNavController().popBackStack()
    }

    override fun selectAll() {

    }
}