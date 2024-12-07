package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// https://developer.android.com/training/data-storage/room?hl=pt-br
class MainActivity : AppCompatActivity() {
    // cria db
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskBeatDatabase::class.java, "task-beat-database"
        ).build()
    }

    private val categoryDao by lazy {
        db.categoryDao()
    }

    // private lateinit var categories: List<CategoryEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        insertDefaultCategory(categories)
        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()
        val categoryAdapter = CategoryListAdapter()
       // var categories: List<CategoryEntity> = categoryDao.getAll()

       // Log.i("TESTE", categories.toString())
        categoryAdapter.setOnClickListener { selected ->
            val categoryTemp = categories.map { item ->

                /*if (item.name == selected.name && !item.isSelected) {
                    return@map item.copy(isSelected = true)
                }
                if (item.name == selected.name && item.isSelected) {
                    return@map item.copy(isSelected = false)
                }
                return@map item*/

                when {
                    item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                    item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                    else -> item
                }
            }

            val taskTemp =
                if (selected.name != "ALL") {
                    tasks.filter { it.category == selected.name }
                } else {
                    tasks
                }
            taskAdapter.submitList(taskTemp)

            categoryAdapter.submitList(categoryTemp)
        }

        rvCategory.adapter = categoryAdapter
        getCategoriesFromDB(categoryAdapter)

        rvTask.adapter = taskAdapter
        taskAdapter.submitList(tasks)
    }

    private fun insertDefaultCategory(categories: List<CategoryUiData>) {
        GlobalScope.launch(Dispatchers.IO) {
            val newCategories = categories.map { it -> CategoryEntity(name=it.name, isSelected = it.isSelected) }

            categoryDao.insertAll(newCategories)
        }
    }

    private fun getCategoriesFromDB(categoryAdapter: CategoryListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAll()
            val newCategories = categories.map { it -> CategoryUiData(name=it.name, isSelected = it.isSelected) }
            categoryAdapter.submitList(newCategories)
        }
    }
}


val categories = listOf(
    CategoryUiData(
        name = "ALL",
        isSelected = false
    ),
    CategoryUiData(
        name = "STUDY",
        isSelected = false
    ),
    CategoryUiData(
        name = "WORK",
        isSelected = false
    ),
    CategoryUiData(
        name = "WELLNESS",
        isSelected = false
    ),
    CategoryUiData(
        name = "HOME",
        isSelected = false
    ),
    CategoryUiData(
        name = "HEALTH",
        isSelected = false
    ),
)

val tasks = listOf(
    TaskUiData(
        "Ler 10 páginas do livro atual",
        "STUDY"
    ),
    TaskUiData(
        "45 min de treino na academia",
        "HEALTH"
    ),
    TaskUiData(
        "Correr 5km",
        "HEALTH"
    ),
    TaskUiData(
        "Meditar por 10 min",
        "WELLNESS"
    ),
    TaskUiData(
        "Silêncio total por 5 min",
        "WELLNESS"
    ),
    TaskUiData(
        "Descer o livo",
        "HOME"
    ),
    TaskUiData(
        "Tirar caixas da garagem",
        "HOME"
    ),
    TaskUiData(
        "Lavar o carro",
        "HOME"
    ),
    TaskUiData(
        "Gravar aulas DevSpace",
        "WORK"
    ),
    TaskUiData(
        "Criar planejamento de vídeos da semana",
        "WORK"
    ),
    TaskUiData(
        "Soltar reels da semana",
        "WORK"
    ),
)