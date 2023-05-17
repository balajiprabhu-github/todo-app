package com.balajiprabhu.todo.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.balajiprabhu.todo.data.Todo
import com.balajiprabhu.todo.utils.TodoListEvent

@Composable
fun TodoItem(
    todo: Todo,
    onEvent:(TodoListEvent) -> Unit,
    modifier: Modifier = Modifier,
){
    val itemBackground = if (isSystemInDarkTheme()) Color.Black else Color.White
    val dividerBackground = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray

    Row(
        modifier = modifier.background(itemBackground),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.padding(
                    start = 12.dp, end = 4.dp
                )) {
                    Text(
                        text = todo.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        style = if (todo.isDone) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle(textDecoration = TextDecoration.None)
                    )

                    todo.description?.let { description ->
                        if (description.isNotBlank()) {
                            Text(
                                text = todo.description ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                style = if (todo.isDone) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle(textDecoration = TextDecoration.None)
                            )
                        }
                    }
                }
            }
        }

        Checkbox(
            checked = todo.isDone,
            onCheckedChange = { isChecked ->
                onEvent(TodoListEvent.OnDoneChange(todo,isChecked))
            }
        )
    }

    Divider(
        modifier = Modifier.fillMaxWidth().height(2.dp).background(dividerBackground)
    )
}