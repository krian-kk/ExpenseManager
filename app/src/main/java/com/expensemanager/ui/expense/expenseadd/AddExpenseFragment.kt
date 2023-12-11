package com.expensemanager.ui.expense.expenseadd

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.expensemanager.ui.viewmodels.ExpenseManagerViewModel
import com.expensemanager.R
import com.expensemanager.data.local.DatabaseHelperImpl
import com.expensemanager.data.local.entity.ExpenseModel
import com.expensemanager.databinding.FragmentAddExpenseBinding
import com.expensemanager.ui.base.UiState
import com.expensemanager.ui.base.ViewModelFactory
import com.expensemanager.utils.Utils.Companion.getDate
import com.google.android.material.datepicker.MaterialDatePicker
import me.amitshekhar.learn.kotlin.coroutines.data.local.DatabaseBuilder


class AddExpenseFragment : Fragment() {
    private lateinit var binding: FragmentAddExpenseBinding
    private lateinit var viewModel: ExpenseManagerViewModel
    private var timeInMillis: Long? = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("editKey") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported.
            val result = bundle.getString("category")
            binding.editTextCategory.setText(result)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setUpUi()
        setUpObservers()

    }


    private fun setUpUi() {
        if (arguments?.isEmpty == false) {
            timeInMillis = arguments?.getLong("date")
            binding.editTextAmount.setText(arguments?.getInt("amount").toString());
            binding.editTextCategory.setText(arguments?.getString("category").toString());
            binding.editTextTime.setText(getDate(arguments?.getLong("date") ?: 0L))
            binding.editTextDescription.setText(arguments?.getString("description").toString());
        }
        binding.editTextCategory.inputType = InputType.TYPE_NULL
        binding.editTextCategory.keyListener = null
        binding.editTextCategory.setOnClickListener { _ ->
            findNavController().navigate(R.id.moveToCategory, bundleOf("src" to "add"))
        }
        binding.editTextTime.inputType = InputType.TYPE_NULL;
        binding.editTextTime.keyListener = null
        binding.editTextTime.setOnClickListener { _ ->
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

            datePicker.addOnPositiveButtonClickListener {
                timeInMillis = it
                binding.editTextTime.setText(getDate(it))
            }
            datePicker.show(childFragmentManager, "tag");
        }
        binding.buttonSubmit.setOnClickListener {
            insertExpense()

        }
        binding.buttonCamera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                activity?.startActivityForResult(takePictureIntent, 51)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
        }
        binding.imageBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            try {
                activity?.startActivityForResult(intent, 52)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
        }
    }

    private fun insertExpense() {
        val category = binding.editTextCategory.text.toString()
        val description = binding.editTextDescription.text.toString()
        val amount = binding.editTextAmount.text.toString().toInt()
        if (arguments?.isEmpty == false) {
            viewModel.editExpense(
                ExpenseModel(
                    id = arguments?.getLong("id") ?: 0L,
                    date = timeInMillis,
                    category = category,
                    description = description,
                    amount = amount
                )
            )
        } else {
            viewModel.addExpense(
                ExpenseModel(
                    date = timeInMillis,
                    category = category,
                    description = description,
                    amount = amount
                )
            )
        }
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

    private fun setUpObservers() {
        viewModel.getUiStateAddExpense().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    val result = "result"
                    setFragmentResult("requestKey", bundleOf("bundleKey" to result))
                    findNavController().popBackStack()
                }

                else -> {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                }
            }
        }
        viewModel.getUiStateEditExpense().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    val result = "result"
                    setFragmentResult("requestKey", bundleOf("bundleKey" to result))
                    findNavController().popBackStack()
                }

                else -> {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                }
            }
        }
    }
}