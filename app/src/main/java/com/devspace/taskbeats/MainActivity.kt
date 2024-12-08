package com.devspace.taskbeats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.time.Duration

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

    private val taskDao by lazy {
        db.taskUiDao()
    }

    private lateinit var categoriesFromDB: List<CategoryUiData>
    private lateinit var tasksFromDB: List<TaskUiData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        insertDefaultCategoryTasks(categories, tasks)
        // insertDefaultCategory(categories)
        // insertDefaultTasks(tasks)
        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()
        val categoryAdapter = CategoryListAdapter()
        getCategoriesTasksFromDB(categoryAdapter, taskAdapter)
       // var categories: List<CategoryEntity> = categoryDao.getAll()

       // Log.i("TESTE", categories.toString())
        categoryAdapter.setOnClickListener { selected ->

            if (selected.name == "+") {
                val intentImplicita: Intent = Intent().apply {
                    action = Intent.ACTION_INPUT_METHOD_CHANGED
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(intentImplicita, null)
                startActivity(shareIntent)
                val newC = CategoryUiData("teste 2",false)
               // insertCategory(newC)
                // getCategoriesTasksFromDB(categoryAdapter, taskAdapter)
            }
            else {


                val categoryTemp = categoriesFromDB.map { item ->

                    /*if (item.name == selected.name && !item.isSelected) {
                    return@map item.copy(isSelected = true)
                }
                if (item.name == selected.name && item.isSelected) {
                    return@map item.copy(isSelected = false)
                }
                return@map item*/

                    when {
                        /*item.name == "+" && item.isSelected -> {
                        val intentImplicita: Intent = Intent().apply {
                            action = Intent.ACTION_INSERT
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(intentImplicita, null)
                        startActivity(shareIntent)
                        val newC = CategoryUiData("teste",false)
                        insertCategory(newC)
                        return@map newC
                    }*/
                        item.name == selected.name && !item.isSelected && item.name != "+" -> item.copy(
                            isSelected = true
                        )

                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }

                val taskTemp =
                    if (selected.name != "ALL" && selected.name != "+") {
                        tasksFromDB.filter { it.category == selected.name }
                    } else {
                        tasksFromDB
                    }
                    taskAdapter.submitList(taskTemp)

                    categoryAdapter.submitList(categoryTemp)

            }
        }

        rvCategory.adapter = categoryAdapter
        // getCategoriesFromDB(categoryAdapter)

        rvTask.adapter = taskAdapter
        //taskAdapter.submitList(tasks)
        // getTasksFromDB(taskAdapter)

        getCategoriesTasksFromDB(categoryAdapter, taskAdapter)
    }

    private fun insertDefaultCategoryTasks(categories: List<CategoryUiData>, tasks: List<TaskUiData>) {
        GlobalScope.launch(Dispatchers.IO) {
            val newCategories = categories.map { it -> CategoryEntity(name=it.name, isSelected = it.isSelected) }

            categoryDao.insertAll(newCategories) // data persistency

            val newTasks = tasks.map { it -> TaskUiEntity(name=it.name, category = it.category) }

            taskDao.insertAll(newTasks) // data persistency
        }
    }

    private fun getCategoriesTasksFromDB(categoryAdapter: CategoryListAdapter, taskAdapter: TaskListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAll()
            val newCategories =
                categories.map { it -> CategoryUiData(name = it.name, isSelected = it.isSelected) }
                    .toMutableList()
            // Add fake + category (not to DB)
            newCategories.add(CategoryUiData(name = "+", false))
            categoriesFromDB = newCategories

            val tasks = taskDao.getAll()
            val newTasks = tasks.map { it -> TaskUiData(name=it.name, category = it.category) }
            tasksFromDB = newTasks

        GlobalScope.launch(Dispatchers.Main) {
            categoryAdapter.submitList(categoriesFromDB)
            taskAdapter.submitList(tasksFromDB)
        }
        }
     }


   /* private fun insertCategory(category: CategoryUiData) {
        GlobalScope.launch(Dispatchers.IO) {
            val newCat = CategoryEntity(category.name,category.isSelected)
            categoryDao.insertOne(newCat)
        }
    } */

    private fun insertDefaultCategory(categories: List<CategoryUiData>) {
        GlobalScope.launch(Dispatchers.IO) {
            val newCategories = categories.map { it -> CategoryEntity(name=it.name, isSelected = it.isSelected) }

            categoryDao.insertAll(newCategories) // data persistency
        }
    }

    private fun getCategoriesFromDB(categoryAdapter: CategoryListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAll()
            val categoriesFromDB = categories.map { it -> CategoryUiData(name=it.name, isSelected = it.isSelected) }
            categoryAdapter.submitList(categoriesFromDB)
        }
    }

   private fun insertDefaultTasks(tasks: List<TaskUiData>) {
        GlobalScope.launch(Dispatchers.IO) {
            val newTasks = tasks.map { it -> TaskUiEntity(name=it.name, category = it.category) }

            taskDao.insertAll(newTasks) // data persistency
        }
    }

    private fun getTasksFromDB(categoryAdapter: TaskListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val tasks = taskDao.getAll()
            val newTasks = tasks.map { it -> TaskUiData(name=it.name, category = it.category) }
            categoryAdapter.submitList(newTasks)
        }
    }
}


val categories: List<CategoryUiData> = listOf()
    /*listOf(
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
     CategoryUiData(
        name = "ADD +",
        isSelected = false
    )
)*/

val tasks: List<TaskUiData> = listOf()
/*= listOf(
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
)*/