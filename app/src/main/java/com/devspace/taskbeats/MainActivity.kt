package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
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

    private val taskDao by lazy {
        db.taskUiDao()
    }

    private lateinit var categoriesFromDB: List<CategoryUiData>
    private lateinit var tasksFromDB: List<TaskUiData>
    private val taskAdapter by lazy {
        TaskListAdapter()
    }
    private lateinit var categoryAdapter: CategoryListAdapter
    private lateinit var emptyView: LinearLayout
    private lateinit var rvCategory: RecyclerView
    private lateinit var rvTask: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        insertDefaultCategoryTasks(categories, tasks)
        // insertDefaultCategory(categories)
        // insertDefaultTasks(tasks)
        emptyView = findViewById(R.id.emptyView)
        rvCategory = findViewById(R.id.rv_categories)
        rvTask = findViewById(R.id.rv_tasks)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val createCategoryButtonEmpty = findViewById<Button>(R.id.emptyViewCreate)
       // taskAdapter = TaskListAdapter()
        categoryAdapter = CategoryListAdapter()

       // var categories: List<CategoryEntity> = categoryDao.getAll()

        fab.setOnClickListener {
            updateCreateTaskFunctionality()
        }

        taskAdapter.setOnClickListener { selected: TaskUiData ->
            updateCreateTaskFunctionality(selected)
        }

        getCategoriesTasksFromDB(categoryAdapter, taskAdapter)

        createCategoryButtonEmpty.setOnClickListener {
                val createCategoryBottomSheet = InsertCategoryDialog(
                    onCreateClicked = { categoryName ->
                        val categoryEntity = CategoryEntity(
                            name = categoryName,
                            isSelected = false)
                        insertCategory(categoryEntity)
                    },
                    onDeleteClicked = { categoryName ->
                        val categoryEntity = CategoryEntity(
                            name = categoryName,
                            isSelected = false)
                        deleteCategory(categoryEntity)
                    }
                    ,null
                )
                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")
        }


       // Log.i("TESTE", categories.toString())
        categoryAdapter.setOnClickListener { selected ->

            if (selected.name == "+") {
                val createCategoryBottomSheet = InsertCategoryDialog(
                    onCreateClicked = { categoryName ->
                        val categoryEntity = CategoryEntity(
                            name = categoryName,
                            isSelected = false)
                    insertCategory(categoryEntity)
                },
                    onDeleteClicked = { categoryName ->
                        val categoryEntity = CategoryEntity(
                            name = categoryName,
                            isSelected = false)
                        deleteCategory(categoryEntity)
                       }
                    ,null
                    )
                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")

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
                    Log.i("ITEM",item.name)
                    when {
                        item.name == selected.name && item.isSelected -> item.copy(
                            isSelected = true
                        )
                        item.name == selected.name && !item.isSelected -> item.copy(
                            isSelected = true
                        )
                        item.name != selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }

                }


                    if (selected.name != "ALL" && selected.name != "+") {
                        getTasksByCategory(category = selected.name)
                        //tasksFromDB.filter { it.category == selected.name }
                    } else {
                        getTasksFromDB(taskAdapter)
                       // getCategoriesTasksFromDB(categoryAdapter, taskAdapter) - faz reset e nao seleciona o all
                    }


                    categoryAdapter.submitList(categoryTemp)

            }
        }

        categoryAdapter.setOnLongClickListener { selected ->
            if (selected.name == "+" || selected.name == "ALL") {
                Snackbar.make(rvCategory, "Not allowed to delete", 2000).show()
            }
            else {
                val createCategoryBottomSheet = InsertCategoryDialog ({ categoryName ->
                    val categoryEntity = CategoryEntity(
                        name = categoryName,
                        isSelected = false
                    )

                    insertCategory(categoryEntity)
                } ,
                    onDeleteClicked = { categoryName ->
                    val categoryEntity = CategoryEntity(
                        name = categoryName,
                        isSelected = false)
                    deleteCategory(categoryEntity)
                },
                    selected

                )
                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")

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

            val newTasks = tasks.map { it -> TaskUiEntity(id = it.id, name=it.name, category = it.category) }

            taskDao.insertAll(newTasks) // data persistency
        }
    }

    private fun getCategoriesTasksFromDB(categoryAdapter: CategoryListAdapter, taskAdapter: TaskListAdapter) {
        GlobalScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAll()
            GlobalScope.launch(Dispatchers.Main) {
                if (categories.isNotEmpty()) {
                    emptyView.isVisible = false
                    rvCategory.isVisible = true
                    rvTask.isVisible = true
                }
                else {
                    emptyView.isVisible = true
                    rvCategory.isVisible = false
                    rvTask.isVisible = false
                }
            }
            val newCategories =
                categories.map { it -> CategoryUiData(name = it.name, isSelected = it.isSelected) }
                    .toMutableList()
            // Add fake + category (not to DB)
            newCategories.add(0,CategoryUiData(name = "ALL", true)) // always selecte onCreate
            newCategories.add(index = newCategories.size ,CategoryUiData(name = "+", false))
            categoriesFromDB = newCategories

            val tasks = taskDao.getAll()
            val newTasks = tasks.map { it -> TaskUiData(it.id, name=it.name, category = it.category) }
            tasksFromDB = newTasks

        GlobalScope.launch(Dispatchers.Main) { // voltar para a main thread para alterar UI
            categoryAdapter.submitList(categoriesFromDB)
            taskAdapter.submitList(tasksFromDB)
        }
        }
     }


    private fun insertCategory(category: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insertOne(category)
            getCategoriesTasksFromDB(categoryAdapter, taskAdapter)
        }

    }

    private fun insertTask(task: TaskUiEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.insertOne(task)
            getCategoriesTasksFromDB(categoryAdapter, taskAdapter)
        }

    }

    private fun updateTask(task: TaskUiEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.update(task)
            getCategoriesTasksFromDB(categoryAdapter, taskAdapter)
        }

    }

    private fun deleteTask(task: TaskUiEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.delete(task)
            getCategoriesTasksFromDB(categoryAdapter, taskAdapter)
        }

    }

    private fun deleteCategory(category: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.deleteByCategory(category.name)
            categoryDao.delete(category)
            getCategoriesTasksFromDB(categoryAdapter, taskAdapter)
        }
    }

    private fun getTasksByCategory(category: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val tasksFromDB = taskDao.getAllByCategoryName(category).map { it -> TaskUiData(it.id, it.name, category = it.category) }

            GlobalScope.launch(Dispatchers.Main) {
                taskAdapter.submitList((tasksFromDB))
            }
        }

    }


    private fun updateCreateTaskFunctionality(selected: TaskUiData? = null) {
        if (selected is TaskUiData) {
            Log.i("AGORA ", "AQUII")
            val updateTaskBottomSheet = InsertTaskDialog(
                fun(task: String, categorySelected: String) {
                    val taskE = TaskUiEntity(name = task, category = categorySelected)

                    //insertTask(taskE)
                },
                categoriesFromDB,
                onUpdateClicked = { task: String, categorySelected: String ->
                    val taskE =
                        TaskUiEntity(id = selected.id, name = task, category = categorySelected)
                    updateTask(taskE)
                },
                onDeleteClicked = { task: String, categorySelected: String ->
                    val taskE =
                        TaskUiEntity(id = selected.id, name = task, category = categorySelected)
                    deleteTask(taskE)
                },
                selected
            )
            updateTaskBottomSheet.show(supportFragmentManager, "createTaskBottomSheet")
        }
        else {

            Log.i("AGORA ", "AQUII 2")

            val createTaskBottomSheet = InsertTaskDialog(
                fun(task: String, categorySelected: String) {
                    val taskE = TaskUiEntity(name = task, category = categorySelected)

                    insertTask(taskE)
                },
                categoriesFromDB,
                fun(task: String, categorySelected: String) {
                    val taskE = TaskUiEntity(name = task, category = categorySelected)

                    //insertTask(taskE)
                },
                onDeleteClicked = { task: String, categorySelected: String ->
                    //val taskE = TaskUiEntity(id = selected.id, name = task, category = categorySelected)
                    //deleteTask(taskE)
                },
            )
            createTaskBottomSheet.show(supportFragmentManager, "createTaskBottomSheet")
        }
    }

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
            val newTasks = tasks.map { it -> TaskUiData(it.id, name=it.name, category = it.category) }
            tasksFromDB = newTasks

            GlobalScope.launch(Dispatchers.Main) {
                categoryAdapter.submitList(newTasks)
            }
        }
    }
}


val categories: List<CategoryUiData> = listOf()
    /*CategoryUiData(
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
    /*TaskUiData(
        name = "Ler 10 páginas do livro atual",
        category = "STUDY",
        id = 1
    ),
    TaskUiData(
        id = 2,
        name ="45 min de treino na academia",
        category = "HEALTH"
    ),
    TaskUiData(
        id = 3,
        "Correr 5km",
        "HEALTH"
    ),
    TaskUiData(
        id = 4,
        "Meditar por 10 min",
        "WELLNESS"
    ),
    TaskUiData(
        id =5,
        "Silêncio total por 5 min",
        "WELLNESS"
    ),
    TaskUiData(
        id = 6,
        "Descer o livo",
        "HOME"
    ),
    TaskUiData(
        id = 7,
        "Tirar caixas da garagem",
        "HOME"
    ),
    TaskUiData(
        id = 8,
        "Lavar o carro",
        "HOME"
    ),
    TaskUiData(
        id = 9,
        "Gravar aulas DevSpace",
        "WORK"
    ),
    TaskUiData(
        id = 10,
        "Criar planejamento de vídeos da semana",
        "WORK"
    ),
    TaskUiData(
        id = 11,
        "Soltar reels da semana",
        "WORK"
    ),
)*/