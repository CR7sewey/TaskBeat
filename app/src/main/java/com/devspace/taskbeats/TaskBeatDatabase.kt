package com.devspace.taskbeats

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CategoryEntity::class, TaskUiEntity::class], version = 3)
abstract class TaskBeatDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryUiDao
    abstract fun taskUiDao(): TaskUiDao
}