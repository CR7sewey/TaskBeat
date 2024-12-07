package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TaskUiDao {

    @Query("SELECT * from taskuientity")
    fun getAll(): List<TaskUiEntity>

}

