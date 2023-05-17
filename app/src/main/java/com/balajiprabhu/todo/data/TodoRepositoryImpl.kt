package com.balajiprabhu.todo.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class TodoRepositoryImpl(private val todoDao: TodoDao) : TodoRepository {

    override suspend fun insertTodo(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }

    override suspend fun getTodoById(id: Int): Todo? {
        return todoDao.getTodoById(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getTodos(): Flow<List<Todo>> {
        return todoDao.getTodos().mapLatest { list ->
            list.sortedBy { todo -> todo.isDone }
        }
    }
}