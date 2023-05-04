package com.balajiprabhu.todo.dependency

import android.app.Application
import androidx.room.Room
import com.balajiprabhu.todo.data.TodoDatabase
import com.balajiprabhu.todo.data.TodoRepository
import com.balajiprabhu.todo.data.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesTodoDatabase(app: Application) : TodoDatabase {
        return Room.databaseBuilder(
            app,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesTodoRepository(database: TodoDatabase): TodoRepository {
        return TodoRepositoryImpl(database.dao)
    }
}