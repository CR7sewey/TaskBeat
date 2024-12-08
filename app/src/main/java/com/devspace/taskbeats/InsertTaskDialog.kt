package com.devspace.taskbeats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class InsertTaskDialog(
    private val onCreateClicked: (String,String) -> Unit,
    private val categoryList: List<CategoryUiData>
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.insert_task, container, false)

        val btnCreate = view.findViewById<Button>(R.id.btn_task_create)
        val tieTaskName = view.findViewById<TextInputEditText>(R.id.tie_task_name)
        val categories_list = view.findViewById<Spinner>(R.id.categories_list)

        val categoryStr: List<String> = categoryList.map { item -> item.name }.filter {item -> item != "ALL" && item != "+"}

        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            categoryStr
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            categories_list.adapter = adapter
        }

        var categorySelected: String = ""
        categories_list.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                    // An item is selected. You can retrieve the selected item using
                    categorySelected = parent.getItemAtPosition(pos).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback.
                }
            }

        btnCreate.setOnClickListener {
            val name = tieTaskName.text.toString()
           // val category = categories_list.toString()
            if (name.isNullOrBlank() || categorySelected.isNullOrBlank()) {
                Snackbar.make(categories_list, "Insert all the values", 3000).show()
            }
            else {
                onCreateClicked.invoke(name,categorySelected)
                dismiss()
            }
        }

        return view

    }
}