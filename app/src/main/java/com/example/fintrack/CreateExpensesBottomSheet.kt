package com.example.fintrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class CreateExpensesBottomSheet(private val onCreateClicked: (ExpensesUiData) -> Unit, private val categoryList : List<CategoryUiData>)
     : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_expenses_bottom_sheet, container, false)

        val btnCreateExpenses = view.findViewById<Button>(R.id.btn_create_expenses)
        val edtExpensesCategory = view.findViewById<TextInputEditText>(R.id.edt_expenses_category)
        val edtExpensesNumber = view.findViewById<TextInputEditText>(R.id.edt_expenses_number)

        btnCreateExpenses.setOnClickListener {
            val name = edtExpensesCategory.text.toString()
            val number = edtExpensesNumber.text.toString()
            onCreateClicked.invoke(name)
            onCreateClicked.invoke(number)
            dismiss()
        }

        return view
    }
}