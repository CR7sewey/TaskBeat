package com.devspace.taskbeats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class InsertCategoryDialog(
    private val onCreateClicked: (String) -> Unit,
    private val onDeleteClicked: (String) -> Unit,
    private val category: CategoryUiData? = null
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.insert_category, container, false)

        val btnCreate = view.findViewById<Button>(R.id.btn_category_create)
        val tieCategoryName = view.findViewById<TextInputEditText>(R.id.tie_category_name)
        val categoryLabel = view.findViewById<TextView>(R.id.tv_title)
        val textDelete = view.findViewById<TextView>(R.id.textDelete)


        if (category != null) {
            categoryLabel.text = "Delete Category"
            textDelete.text = "${textDelete.text} ${category.name}?"
            tieCategoryName.isVisible = false
            btnCreate.text = "delete"
        }
        else {
            textDelete.isVisible = false
        }

        btnCreate.setOnClickListener {
            if (category == null) {
                val name = tieCategoryName.text.toString()
                onCreateClicked.invoke(name)
                dismiss()
            }
            else {
                val name = category.name
                onDeleteClicked.invoke(name)
                dismiss()
            }
        }

        return view

    }
}