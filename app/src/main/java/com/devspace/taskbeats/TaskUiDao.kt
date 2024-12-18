package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskUiDao {

    @Query("SELECT * from taskuientity")
    fun getAll(): List<TaskUiEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(taskList: List<TaskUiEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(vararg taskList: TaskUiEntity)

    @Update
    fun update(taskEntity: TaskUiEntity)

    @Delete
    fun delete(taskEntity: TaskUiEntity)

    @Query("delete from taskuientity where category is :categoryName")
    fun deleteByCategory(categoryName: String)

    @Query("Select * From taskuientity where category is :categoryName")
    fun getAllByCategoryName(categoryName: String): List<TaskUiEntity>

}

