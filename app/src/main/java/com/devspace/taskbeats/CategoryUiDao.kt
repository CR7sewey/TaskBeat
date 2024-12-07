package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryUiDao {

    @Query("Select * from categoryentity")
    fun getAll(): List<CategoryEntity>

    @Insert
    fun insertAll(vararg listCategories: List<CategoryEntity>)
}