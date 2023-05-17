package com.balajiprabhu.todo.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.balajiprabhu.todo.utils.TodoListEvent
import com.balajiprabhu.todo.utils.UiEvent
import com.balajiprabhu.todo.viewmodel.TodoListViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TodoListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {

    val todos = viewModel.todos.collectAsState(initial = emptyList())
    val scaffoldState = rememberScaffoldState()
    val dividerBackground = if (isSystemInDarkTheme()) Color.White else Color.LightGray

    LaunchedEffect(key1 = true) {

        viewModel.uiEvent.collect { event ->

            when(event) {

                is UiEvent.ShowSnackBar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )

                    if(result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(TodoListEvent.OnUndoDeleteClick)
                    }

                }

                is UiEvent.Navigate -> {
                    onNavigate(event)
                }

                else -> Unit
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
                 TopAppBar {
                     Text(
                         modifier = Modifier.padding(start=4.dp),
                         text = "Todo"
                     )
                 }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(TodoListEvent.OnAddTodoClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Todo"
                )
            }
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
            ) {
            itemsIndexed(
                items = todos.value,
                key = {_, item -> item.id!!  }
                ) { index, todo ->
                val currentItem by rememberUpdatedState(todo)
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        println("Dismiss value -> ${it.name}")
                        if (it == DismissValue.DismissedToStart) {
                            viewModel.onEvent(TodoListEvent.OnDeleteTodoClick(todo = currentItem))
                            true
                        } else false
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier
                        .animateItemPlacement(),
                    directions = setOf(
                        DismissDirection.EndToStart
                    ),
                    dismissThresholds = { direction ->
                        FractionalThreshold(
                            if (direction == DismissDirection.StartToEnd) 0.66f else 0.50f
                        )
                    },
                    background = {
                        SwipeBackground(dismissState)
                    },
                    dismissContent = {
                        TodoItem(
                            todo = currentItem,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    viewModel.onEvent(TodoListEvent.OnTodoClick(todo))
                                }
                        )
                    }
                )

                if (index < todos.value.lastIndex)
                    Divider(color = dividerBackground, thickness = 0.2.dp)
            }
        }
    }
}