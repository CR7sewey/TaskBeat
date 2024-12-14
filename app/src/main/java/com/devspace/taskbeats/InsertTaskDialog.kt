package com.devspace.taskbeats

import android.os.Bundle
import android.service.autofill.VisibilitySetterAction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.transition.Visibility
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class InsertTaskDialog(
    private val onCreateClicked: (String,String) -> Unit,
    private val categoryList: List<CategoryUiData>,
    private val onUpdateClicked: (String,String) -> Unit,
    private val onDeleteClicked: (String,String) -> Unit,
    private val task: TaskUiData? = null,
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.insert_task, container, false)

        val tv_title = view.findViewById<TextView>(R.id.tv_title)
        val btnCreateUpdate = view.findViewById<Button>(R.id.btn_task_create)
        val btnDelete = view.findViewById<Button>(R.id.btn_task_delete)
        val tieTaskName = view.findViewById<TextInputEditText>(R.id.tie_task_name)
        val categories_list = view.findViewById<Spinner>(R.id.categories_list)
        val categoryStr: MutableList<String> = categoryList.map { item -> item.name }.filter {item -> item != "ALL" && item != "+"}.toMutableList()
        if (task == null) {
            categoryStr.add(0, "Select a category please.")
        }
        var categorySelected: String = ""

        Log.i("AQUIIII","ERRO")

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


        if (task == null) {
            btnCreateUpdate.text = getString(R.string.create)
            tv_title.text = getString(R.string.add_task)
            btnDelete.isVisible = false
            categories_list.setSelection(0)
        }
        else {
            btnCreateUpdate.setText(getString(R.string.update))
            tieTaskName.setText(task.name)
            tv_title.setText(getString(R.string.update_task))
            btnDelete.visibility = View.VISIBLE


            val index = categoryStr.indexOf(task.category)
            Log.i("Index", index.toString())
            categories_list.setSelection(index)
        }

        btnCreateUpdate.setOnClickListener {
            if (task != null) {
                val name = tieTaskName.text.toString()
                // val category = categories_list.toString()
                if (name.isNullOrBlank() || categorySelected.isNullOrBlank()) {
                    Snackbar.make(categories_list, "Insert all the values", 3000).show()
                } else {
                    onUpdateClicked.invoke(name, categorySelected)
                    dismiss()
                }
            }
            else {
                val name = tieTaskName.text.toString()
                // val category = categories_list.toString()
                if (name.isNullOrBlank() || categorySelected.isNullOrBlank()) {
                    Snackbar.make(categories_list, "Insert all the values", 3000).show()
                }
                else if (categorySelected == categoryStr[0]) {
                    Snackbar.make(categories_list, "Please, insert a category!", 3000).show()

                }

                else {
                    onCreateClicked.invoke(name, categorySelected)
                    dismiss()
                }
            }
        }


        btnDelete.setOnClickListener {
            Log.i("AQUIIII",task.toString())

            if (task == null) {
                Snackbar.make(categories_list, "Please, select a task to delete", 3000).show()
            }
            else {
                val name = tieTaskName.text.toString()
                // val category = categories_list.toString()
                if (name.isNullOrBlank() || categorySelected.isNullOrBlank()) {
                    Snackbar.make(categories_list, "Insert all the values", 3000).show()
                } else {
                    onDeleteClicked.invoke(name, categorySelected)
                    dismiss()
                }
            }
        }

        return view

    }
}