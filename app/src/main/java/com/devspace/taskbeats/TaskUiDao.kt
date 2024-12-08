package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskUiDao {

    @Query("SELECT * from taskuientity")
    fun getAll(): List<TaskUiEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(taskList: List<TaskUiEntity>)

}

