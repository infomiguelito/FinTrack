package com.example.fintrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class CreateOrUpdateExpensesBottomSheet(
    private val categoryList: List<CategoryUiData>,
    private val expenses:ExpensesUiData ? = null,
    private val onCreateClicked: (ExpensesUiData) -> Unit

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
        val btnCreateExpenses = view.findViewById<Button>(R.id.btn_create_expenses)
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
            tvTitle.setText(R.string.insert_expenses)
            btnCreateExpenses.setText(R.string.add_btn)
        } else {
            tvTitle.setText(R.string.update_expenses)
            btnCreateExpenses.setText(R.string.update)
            edtExpensesNumber.setText(expenses.name)

            val currentCategory = categoryList.first { it.name == expenses.category }
            val index = categoryList.indexOf(currentCategory)
            spinner.setSelection(index)
        }




        btnCreateExpenses.setOnClickListener {
            val number = edtExpensesNumber.text.toString()

            if (expensesCategory != null) {
                onCreateClicked.invoke(
                    ExpensesUiData(
                        id = 0 ,
                        name = number,
                        category = requireNotNull(expensesCategory)
                    )
                )
                dismiss()
            } else {
                Snackbar.make(btnCreateExpenses, "Please select a category", Snackbar.LENGTH_LONG).show()
            }


        }



        return view
    }
}
