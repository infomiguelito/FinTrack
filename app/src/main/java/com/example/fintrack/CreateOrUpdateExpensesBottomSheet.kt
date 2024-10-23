package com.example.fintrack

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class CreateOrUpdateExpensesBottomSheet(
    private val categoryList: List<CategoryUiData>,
    private val expenses:ExpensesUiData ? = null,
    private val onCreateClicked: (ExpensesUiData) -> Unit,
    private val onUpdateClicked: (ExpensesUiData) -> Unit,
    private val onDeleteClicked: (ExpensesUiData) -> Unit,

) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_or_update_expenses_bottom_sheet, container, false)

        var expensesCategory: String? = null
        val categoryStr :List<String> = categoryList.map{it . name}


        val tvTitle = view.findViewById<TextView>(R.id.tv_title_expenses)
        val btnCreateOrUpdateExpenses = view.findViewById<Button>(R.id.btn_create_or_update_expenses)
        val btnDeleteOrUpdateExpenses = view.findViewById<Button>(R.id.btn_delete_or_update_expenses)
        val edtExpensesNumber = view.findViewById<EditText>(R.id.edt_expenses_number)

        val spinner: Spinner = view.findViewById(R.id.category_list)
        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            categoryStr.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long) {

                expensesCategory = categoryStr[position]
            }



            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        if (expenses == null ){
            btnDeleteOrUpdateExpenses.isVisible= false
            tvTitle.setText(R.string.insert_expenses)
            btnCreateOrUpdateExpenses.setText(R.string.add_btn)
        } else {
            tvTitle.setText(R.string.update_expenses)
            btnCreateOrUpdateExpenses.setText(R.string.update)
            edtExpensesNumber.setText(expenses.name)
            btnDeleteOrUpdateExpenses.isVisible= true

            val currentCategory = categoryList.first { it.name == expenses.category }
            val index = categoryList.indexOf(currentCategory)
            spinner.setSelection(index)
        }


        btnDeleteOrUpdateExpenses.setOnClickListener {
            if(expenses != null){
                onDeleteClicked.invoke(expenses)
                dismiss()
            }else{
                Log.d("CreateOrUpdateExpensesBottomSheet","Expenses not found")
            }

        }


        btnCreateOrUpdateExpenses.setOnClickListener {
            val number = edtExpensesNumber.text.toString().trim()
            if (expensesCategory != null && number.isNotEmpty()) {

                if (expenses == null){
                    onCreateClicked.invoke(
                        ExpensesUiData(
                            id = 0 ,
                            name = number,
                            category = requireNotNull(expensesCategory)
                        )
                    )
                } else {
                    onUpdateClicked.invoke(
                        ExpensesUiData(
                            id = expenses.id,
                            name = number,
                            category = requireNotNull(expensesCategory)
                        )
                    )
                }
                dismiss()
            } else {
                Snackbar.make(btnCreateOrUpdateExpenses, "Please select a category", Snackbar.LENGTH_LONG).show()
            }


        }



        return view
    }
}
