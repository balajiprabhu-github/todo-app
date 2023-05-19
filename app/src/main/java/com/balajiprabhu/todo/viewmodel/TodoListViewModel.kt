package com.balajiprabhu.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balajiprabhu.todo.data.Todo
import com.balajiprabhu.todo.data.TodoRepository
import com.balajiprabhu.todo.utils.Routes
import com.balajiprabhu.todo.events.TodoListEvent
import com.balajiprabhu.todo.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    val todos = todoRepository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    private var deletedTodo : Todo? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onEvent(event : TodoListEvent) {
        when(event) {

            is TodoListEvent.OnTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO + "?todoId=${event.todo.id}"))
            }

            is TodoListEvent.OnAddTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO))
            }

            is TodoListEvent.OnUndoDeleteClick -> {
                deletedTodo?.let { todo ->
                    viewModelScope.launch {
                        todoRepository.insertTodo(todo)
                    }
                }
            }

            is TodoListEvent.OnDeleteTodoClick -> {
                viewModelScope.launch {
                    deletedTodo = event.todo
                    todoRepository.deleteTodo(event.todo)
                    sendUiEvent(
                        UiEvent.ShowSnackBar(
                        message = "${event.todo.title} Deleted",
                        action = "Undo"
                    ))
                }
            }

            is TodoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    todoRepository.insertTodo(event.todo.copy(
                        isDone = event.isDone
                    ))

                    todos.mapLatest { list ->
                        list.filter { it.isDone }
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}