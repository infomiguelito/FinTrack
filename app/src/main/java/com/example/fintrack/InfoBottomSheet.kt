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

class InfoBottomSheet(
    private val title : String,
    private val description : String,
    private val btnText : String,
    private val onClick : () -> Unit

) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.info_bottom_sheet, container, false)

        val tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        val Desc = view.findViewById<TextView>(R.id.tv_info_description)
        val btnDeleteCategory = view.findViewById<Button>(R.id.btn_delete_category)

        tvTitle.setText(title)
        Desc.setText(description)
        btnDeleteCategory.setText(btnText)

        btnDeleteCategory.setOnClickListener {
            onClick.invoke()
            dismiss()
        }

        return view
    }
}
