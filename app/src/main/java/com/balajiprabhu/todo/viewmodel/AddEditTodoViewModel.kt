package com.balajiprabhu.todo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balajiprabhu.todo.data.Todo
import com.balajiprabhu.todo.data.TodoRepository
import com.balajiprabhu.todo.utils.AddEditTodoEvent
import com.balajiprabhu.todo.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    savesStateHandle: SavedStateHandle
) : ViewModel() {

    var todo by mutableStateOf<Todo?>(null)
    private set

    var title by mutableStateOf("")
    private set

    var description by mutableStateOf("")
    private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    init {
        handleEditTodo(savesStateHandle)
    }

    fun onEvent(event: AddEditTodoEvent){
        when(event) {
            is AddEditTodoEvent.OnTitleChange -> {
                title = event.title
            }

            is AddEditTodoEvent.OnDescriptionChange -> {
                description = event.description
            }

            is AddEditTodoEvent.OnSaveTodoClick -> {
                viewModelScope.launch {
                    if(title.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackBar(
                            message = "The title cannot be empty",
                        ))
                        return@launch
                    }

                    todoRepository.insertTodo(
                        todo = Todo(
                            title = title,
                            description = description,
                            isDone = todo?.isDone ?: false,
                            id = todo?.id
                        )
                    )

                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun handleEditTodo(savesStateHandle: SavedStateHandle) {
        val todoId = savesStateHandle.get<Int>("todoId")
        todoId?.let { _todoId ->
            if (_todoId != -1) {
                viewModelScope.launch {
                    todoRepository.getTodoById(_todoId)?.let { todo ->
                        title = todo.title
                        description = todo.description ?: ""
                        this@AddEditTodoViewModel.todo = todo
                    }
                }
            }
        }
    }

}